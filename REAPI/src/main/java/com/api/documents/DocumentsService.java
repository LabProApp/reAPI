package com.api.documents;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.enums.MasterEnums;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class DocumentsService {

	private static final Logger log = LoggerFactory.getLogger(DocumentsService.class);

	private static final Set<String> CUSTOMER_UPLOAD_TYPES = Set.of("LOAN_DOCUMENT", "LEGAL_DOCUMENT");

	private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
			"image/jpeg", "image/png", "image/gif", "image/webp",
			"application/pdf",
			"application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/vnd.ms-excel",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
	);
	private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10 MB

	private final DocumentRepository documentsRepository;
	private final S3Service s3Service;
	private final ModelMapper mapper;

	// Bounded pool for parallel S3 uploads. Daemon threads so they don't block JVM exit.
	private final ExecutorService uploadExecutor = Executors.newFixedThreadPool(8, r -> {
		Thread t = new Thread(r);
		t.setName("s3-upload-" + t.getId());
		t.setDaemon(true);
		return t;
	});

	@PreDestroy
	void shutdownExecutor() {
		uploadExecutor.shutdown();
		try {
			if (!uploadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
				uploadExecutor.shutdownNow();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			uploadExecutor.shutdownNow();
		}
	}

	public DocumentsService(DocumentRepository documentsRepository, S3Service s3Service, ModelMapper mapper) {
		this.documentsRepository = documentsRepository;
		this.s3Service = s3Service;
		this.mapper = mapper;
	}

	
	public List<DocumentDto> uploadDocuments(String objectType, Long objectId, List<MultipartFile> files,
			List<String> titles, List<String> captions, Long uploadedBy) throws IOException {
		log.info("uploadDocuments - Uploading {} file(s) for objectType={}, objectId={}", files.size(), objectType, objectId);

		// Validate up front so we don't start uploads we then have to roll back.
		for (MultipartFile file : files) {
			if (file.getSize() > MAX_FILE_SIZE_BYTES) {
				throw new IllegalArgumentException(
						"File '" + file.getOriginalFilename() + "' exceeds maximum allowed size of 10MB");
			}
			String contentType = file.getContentType();
			if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
				throw new IllegalArgumentException(
						"File type not allowed: '" + contentType + "'. Allowed types: PDF, Word, Excel, JPEG, PNG, GIF, WebP");
			}
		}

		boolean requiresVerification = CUSTOMER_UPLOAD_TYPES.contains(objectType.toUpperCase());
		String folder = objectType.toUpperCase();

		// Phase 1 — upload every file to S3 in parallel.
		List<CompletableFuture<String>> futures = new ArrayList<>(files.size());
		for (MultipartFile file : files) {
			futures.add(CompletableFuture.supplyAsync(() -> {
				try {
					return s3Service.uploadFile(file, folder);
				} catch (IOException e) {
					throw new RuntimeException("S3 upload failed for " + file.getOriginalFilename(), e);
				}
			}, uploadExecutor));
		}

		List<String> keys = new ArrayList<>(files.size());
		try {
			for (CompletableFuture<String> f : futures) keys.add(f.join());
		} catch (RuntimeException e) {
			// One upload failed — clean up the rest so we don't leak S3 objects.
			for (CompletableFuture<String> f : futures) {
				if (f.isDone() && !f.isCompletedExceptionally()) {
					try { s3Service.deleteFile(f.get()); } catch (Exception ignore) { }
				}
			}
			throw e;
		}

		// Phase 2 — build entities and persist in one batch insert.
		List<Documents> entities = new ArrayList<>(files.size());
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String key = keys.get(i);
			String title = (titles != null && titles.size() > i) ? titles.get(i) : null;
			String caption = (captions != null && captions.size() > i) ? captions.get(i) : null;
			String sanitizedFilename = file.getOriginalFilename() != null
					? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "") : "file";

			Documents doc = new Documents();
			doc.setS3key(key);
			doc.setFilename(sanitizedFilename);
			doc.setTitle(title);
			doc.setDocType(DocTypeDetector.detect(file.getContentType()));
			doc.setCaption(caption);
			doc.setObjectType(objectType);
			doc.setObjectId(objectId);
			doc.setUploadedBy(uploadedBy);
			doc.setDocumentStatus(requiresVerification
					? MasterEnums.DocumentStatus.NOT_VERIFIED
					: MasterEnums.DocumentStatus.VERIFIED);
			entities.add(doc);
		}

		List<Documents> saved = documentsRepository.saveAll(entities);
		log.info("uploadDocuments - Saved {} document(s) for objectType={}, objectId={}",
				saved.size(), objectType, objectId);

		return saved.stream().map(d -> mapper.map(d, DocumentDto.class)).collect(Collectors.toList());
	}

	
	// Delete a document and its S3 object
	public void deleteDocument(Long id) {
		log.info("deleteDocument - Deleting document id={}", id);
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> {
			log.error("deleteDocument - Document not found for id={}", id);
			return new RuntimeException("Document not found");
		});
		String s3Key = doc.getS3key();
		documentsRepository.delete(doc);
		log.info("deleteDocument - DB record deleted for id={}, s3key={}", id, s3Key);
		if (s3Key != null && !s3Key.isBlank()) {
			try {
				s3Service.deleteFile(s3Key);
				log.info("deleteDocument - S3 object deleted key={}", s3Key);
			} catch (Exception e) {
				log.error("deleteDocument - Failed to delete S3 object key={}: {}", s3Key, e.getMessage());
			}
		}
	}

	
	// Get a single document
	public DocumentDto get(Long id) {
		Documents doc = documentsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Document not found"));
		return mapper.map(doc, DocumentDto.class);
	}

	
	public String generatePresignedUrl(String key) {
		return s3Service.generatePresignedUrl(key);
	}

	
	// Get all documents by objectType and objectId
	public List<DocumentDto> getDocumentsByObject(String objectType, Long objectId) {
		log.info("getDocumentsByObject - Fetching documents for objectType={}, objectId={}", objectType, objectId);
		List<Documents> docs = documentsRepository.findByObjectTypeAndObjectId(objectType, objectId);
		log.debug("getDocumentsByObject - Found {} documents", docs.size());
		return docs.stream().map(doc -> mapWithPresignedUrl(doc, new DocumentDto())).collect(Collectors.toList());
	}

	
	// Get all documents by objectType and objectId (minimal DTO)
	public List<DocumentminDto> getminDocumentsByObject(String objectType, Long objectId) {
		if (objectType == null || objectId == null) {
			return Collections.emptyList();
		}
		List<Documents> docs = documentsRepository.findByObjectTypeAndObjectId(objectType, objectId);
		if (docs.isEmpty()) {
			return Collections.emptyList();
		}
		log.debug("getminDocumentsByObject - Found {} documents for objectType={}, objectId={}", docs.size(), objectType, objectId);
		return docs.stream().map(doc -> {
			DocumentminDto dto = mapper.map(doc, DocumentminDto.class);
			enrichWithPresignedUrl(doc, dto);
			return dto;
		}).collect(Collectors.toList());
	}

	
	// Get minimal documents for a batch of objectIds in a single query
	public Map<Long, List<DocumentminDto>> getminDocumentsByObjectIds(String objectType, List<Long> objectIds) {
		if (objectType == null || objectIds == null || objectIds.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Documents> docs = documentsRepository.findByObjectTypeAndObjectIdIn(objectType, objectIds);
		return docs.stream().collect(Collectors.groupingBy(
			Documents::getObjectId,
			Collectors.mapping(doc -> {
				DocumentminDto dto = mapper.map(doc, DocumentminDto.class);
				enrichWithPresignedUrl(doc, dto);
				return dto;
			}, Collectors.toList())
		));
	}

	// ─── Status update ────────────────────────────────────────────────────────

	
	public DocumentDto updateDocumentStatus(Long documentId, MasterEnums.DocumentStatus status,
			String rejectionReason, String comments, Long reviewedBy) {
		Documents document = documentsRepository.findById(documentId)
				.orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + documentId));

		document.setDocumentStatus(status);
		document.setComments(comments);
		document.setReviewedBy(reviewedBy);
		document.setReviewedTs(LocalDateTime.now());

		if (status == MasterEnums.DocumentStatus.REJECTED) {
			document.setRejectionReason(rejectionReason);
		} else {
			document.setRejectionReason(null);
		}

		Documents saved = documentsRepository.save(document);
		log.info("updateDocumentStatus - doc id={} status={}, reviewedBy={}", documentId, status, reviewedBy);
		return mapper.map(saved, DocumentDto.class);
	}

	// ─── Fetch by status ─────────────────────────────────────────────────────

	
	public List<DocumentDto> getPendingDocuments() {
		log.info("getPendingDocuments - Fetching all NOT_VERIFIED documents");
		List<Documents> docs = documentsRepository.findByDocumentStatus(MasterEnums.DocumentStatus.NOT_VERIFIED);
		log.info("getPendingDocuments - Found {} NOT_VERIFIED documents", docs.size());
		return docs.stream().map(doc -> mapWithPresignedUrl(doc, new DocumentDto())).collect(Collectors.toList());
	}

	
	public List<DocumentDto> getDocumentsByObjectAndStatus(String objectType, Long objectId,
			MasterEnums.DocumentStatus status) {
		log.info("getDocumentsByObjectAndStatus - objectType={}, objectId={}, status={}", objectType, objectId, status);
		List<Documents> docs = documentsRepository
				.findByObjectTypeAndObjectIdAndDocumentStatus(objectType, objectId, status);
		log.info("getDocumentsByObjectAndStatus - Found {} documents", docs.size());
		return docs.stream().map(doc -> mapWithPresignedUrl(doc, new DocumentDto())).collect(Collectors.toList());
	}

	// ─── Helpers ─────────────────────────────────────────────────────────────

	
	private DocumentDto mapWithPresignedUrl(Documents doc, DocumentDto dto) {
		dto = mapper.map(doc, DocumentDto.class);
		enrichWithPresignedUrl(doc, dto);
		return dto;
	}

	
	private void enrichWithPresignedUrl(Documents doc, Object dto) {
		if (doc.getS3key() == null || doc.getS3key().isBlank()) return;
		try {
			String url = s3Service.generatePresignedUrl(doc.getS3key());
			if (dto instanceof DocumentDto d) d.setDocUrl(url);
			else if (dto instanceof DocumentminDto d) d.setDocUrl(url);
		} catch (Exception e) {
			log.error("enrichWithPresignedUrl - Failed for key={}: {}", doc.getS3key(), e.getMessage());
		}
	}
}
