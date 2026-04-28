package com.api.documents;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.enums.MasterEnums;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class DocumentsService {

	private static final Set<String> CUSTOMER_UPLOAD_TYPES = Set.of("LOAN_DOCUMENT", "LEGAL_DOCUMENT");

	private final DocumentRepository documentsRepository;
	private final S3Service s3Service;
	private final ModelMapper mapper;

	public DocumentsService(DocumentRepository documentsRepository, S3Service s3Service, ModelMapper mapper) {
		this.documentsRepository = documentsRepository;
		this.s3Service = s3Service;
		this.mapper = mapper;
	}

	/**
	 * Unified upload method for all objectTypes.
	 * LOAN_DOCUMENT and LEGAL_DOCUMENT default to NOT_VERIFIED; all others default to VERIFIED.
	 */
	public List<DocumentDto> uploadDocuments(String objectType, Long objectId, List<MultipartFile> files,
			List<String> titles, List<String> captions, Long uploadedBy) throws IOException {
		log.info("uploadDocuments - Uploading {} file(s) for objectType={}, objectId={}", files.size(), objectType, objectId);
		boolean requiresVerification = CUSTOMER_UPLOAD_TYPES.contains(objectType.toUpperCase());
		List<DocumentDto> dtoList = new ArrayList<>();

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String title = (titles != null && titles.size() > i) ? titles.get(i) : null;
			String caption = (captions != null && captions.size() > i) ? captions.get(i) : null;
			String sanitizedFilename = file.getOriginalFilename() != null
					? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "") : "file";
			String docType = DocTypeDetector.detect(file.getContentType());
			log.debug("uploadDocuments - file[{}]: name={}, title={}, docType={}", i, file.getOriginalFilename(), title, docType);

			String key = s3Service.uploadFile(file, objectType.toUpperCase());

			Documents doc = new Documents();
			doc.setS3key(key);
			doc.setFilename(sanitizedFilename);
			doc.setTitle(title);
			doc.setDocType(docType);
			doc.setCaption(caption);
			doc.setObjectType(objectType);
			doc.setObjectId(objectId);
			doc.setUploadedBy(uploadedBy);
			doc.setDocumentStatus(requiresVerification
					? MasterEnums.DocumentStatus.NOT_VERIFIED
					: MasterEnums.DocumentStatus.VERIFIED);

			Documents saved = documentsRepository.save(doc);
			log.info("uploadDocuments - Saved doc id={}, title={}, key={}", saved.getId(), title, key);
			dtoList.add(mapper.map(saved, DocumentDto.class));
		}

		log.info("uploadDocuments - Successfully uploaded {} document(s) for objectType={}, objectId={}",
				dtoList.size(), objectType, objectId);
		return dtoList;
	}

	// Delete a document
	public void deleteDocument(Long id) {
		log.info("deleteDocument - Deleting document id={}", id);
		Documents doc = documentsRepository.findById(id).orElseThrow(() -> {
			log.error("deleteDocument - Document not found for id={}", id);
			return new RuntimeException("Document not found");
		});
		documentsRepository.delete(doc);
		log.info("deleteDocument - Document id={} deleted (s3key={})", id, doc.getS3key());
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
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);
		log.debug("getDocumentsByObject - Found {} documents", docs.size());
		return docs.stream().map(doc -> mapWithPresignedUrl(doc, new DocumentDto())).collect(Collectors.toList());
	}

	// Get all documents by objectType and objectId (minimal DTO)
	public List<DocumentminDto> getminDocumentsByObject(String objectType, Long objectId) {
		if (objectType == null || objectId == null) {
			return Collections.emptyList();
		}
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);
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
				.findByObjectTypeIgnoreCaseAndObjectIdAndDocumentStatus(objectType, objectId, status);
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
