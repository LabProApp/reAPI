package com.api.documents;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.api.enums.MasterEnums;

import jakarta.persistence.EntityNotFoundException;

/**
 * Service layer for all document management operations in the real estate API.
 *
 * <p>Responsibilities include:
 * <ul>
 *   <li>Validating uploaded files (max 10 MB, allowlisted MIME types).</li>
 *   <li>Uploading files to AWS S3 via {@link S3Service} and persisting
 *       metadata to the database via {@link DocumentRepository}.</li>
 *   <li>Applying automatic verification status: {@code LOAN_DOCUMENT} and
 *       {@code LEGAL_DOCUMENT} objects default to
 *       {@link MasterEnums.DocumentStatus#NOT_VERIFIED}; all other object
 *       types default to {@link MasterEnums.DocumentStatus#VERIFIED}.</li>
 *   <li>Enriching returned DTOs with 60-minute presigned S3 download URLs.</li>
 *   <li>Batch-loading minimal document DTOs to avoid N+1 queries when
 *       fetching documents for a list of parent objects.</li>
 * </ul>
 */
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

	/**
	 * Constructs a {@code DocumentsService} with the required dependencies.
	 *
	 * @param documentsRepository JPA repository for {@link Documents} entities
	 * @param s3Service           service for S3 upload, delete, and presigning
	 * @param mapper              ModelMapper used to convert entities to DTOs
	 */
	public DocumentsService(DocumentRepository documentsRepository, S3Service s3Service, ModelMapper mapper) {
		this.documentsRepository = documentsRepository;
		this.s3Service = s3Service;
		this.mapper = mapper;
	}

	/**
	 * Unified upload method for all objectTypes.
	 * LOAN_DOCUMENT and LEGAL_DOCUMENT default to NOT_VERIFIED; all others default to VERIFIED.
	 *
	 * <p>Each file is validated for size (max 10 MB) and MIME type before
	 * being uploaded to S3. The parallel {@code titles} and {@code captions}
	 * lists are matched by index; missing entries are treated as {@code null}.
	 *
	 * @param objectType the owning entity category (e.g. {@code "PROPERTY"},
	 *                   {@code "LOAN_DOCUMENT"})
	 * @param objectId   the primary-key of the owning entity
	 * @param files      the multipart files to upload
	 * @param titles     optional per-file human-readable titles (index-aligned
	 *                   with {@code files})
	 * @param captions   optional per-file captions (index-aligned with
	 *                   {@code files})
	 * @param uploadedBy the id of the user performing the upload
	 * @return list of {@link DocumentDto} representing the saved documents,
	 *         each enriched with a presigned download URL
	 * @throws IOException              if reading a file's input stream fails
	 * @throws IllegalArgumentException if a file exceeds 10 MB or has a
	 *                                  disallowed MIME type
	 */
	public List<DocumentDto> uploadDocuments(String objectType, Long objectId, List<MultipartFile> files,
			List<String> titles, List<String> captions, Long uploadedBy) throws IOException {
		log.info("uploadDocuments - Uploading {} file(s) for objectType={}, objectId={}", files.size(), objectType, objectId);

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

	/**
	 * Deletes a document record from the database by id.
	 *
	 * <p>Note: this method removes the database record but does not delete the
	 * corresponding object from S3.
	 *
	 * @param id the id of the document to delete
	 * @throws RuntimeException if no document exists with the given id
	 */
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

	/**
	 * Retrieves a single document by its primary key.
	 *
	 * @param id the document id
	 * @return the {@link DocumentDto} for the found document
	 * @throws RuntimeException if no document exists with the given id
	 */
	// Get a single document
	public DocumentDto get(Long id) {
		Documents doc = documentsRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Document not found"));
		return mapper.map(doc, DocumentDto.class);
	}

	/**
	 * Generates a 60-minute presigned S3 URL for the given S3 object key.
	 *
	 * @param key the S3 object key
	 * @return the presigned download URL as a string
	 */
	public String generatePresignedUrl(String key) {
		return s3Service.generatePresignedUrl(key);
	}

	/**
	 * Returns all documents belonging to the specified object, each enriched
	 * with a presigned S3 download URL.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectId   the primary-key of the owning entity
	 * @return list of {@link DocumentDto}; empty if none found
	 */
	// Get all documents by objectType and objectId
	public List<DocumentDto> getDocumentsByObject(String objectType, Long objectId) {
		log.info("getDocumentsByObject - Fetching documents for objectType={}, objectId={}", objectType, objectId);
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectId(objectType, objectId);
		log.debug("getDocumentsByObject - Found {} documents", docs.size());
		return docs.stream().map(doc -> mapWithPresignedUrl(doc, new DocumentDto())).collect(Collectors.toList());
	}

	/**
	 * Returns minimal document DTOs for the specified object, each enriched
	 * with a presigned S3 download URL.
	 *
	 * <p>Returns an empty list if either argument is {@code null} or if no
	 * documents are found.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectId   the primary-key of the owning entity
	 * @return list of {@link DocumentminDto}; empty if none found
	 */
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

	/**
	 * Batch-fetches minimal document DTOs for a list of object ids in a single
	 * database query, avoiding N+1 queries when loading documents for a
	 * collection of parent objects.
	 *
	 * <p>Returns an empty map if any argument is {@code null} or if
	 * {@code objectIds} is empty.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectIds  the list of owning entity primary-keys to query
	 * @return a map from each {@code objectId} to its list of
	 *         {@link DocumentminDto}; ids with no documents are absent from
	 *         the map
	 */
	// Get minimal documents for a batch of objectIds in a single query
	public Map<Long, List<DocumentminDto>> getminDocumentsByObjectIds(String objectType, List<Long> objectIds) {
		if (objectType == null || objectIds == null || objectIds.isEmpty()) {
			return Collections.emptyMap();
		}
		List<Documents> docs = documentsRepository.findByObjectTypeIgnoreCaseAndObjectIdIn(objectType, objectIds);
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

	/**
	 * Updates the verification status of a document and records the reviewer
	 * details.
	 *
	 * <p>When {@code status} is {@link MasterEnums.DocumentStatus#REJECTED} the
	 * supplied {@code rejectionReason} is persisted; for any other status the
	 * rejection reason is cleared.
	 *
	 * @param documentId      the id of the document to update
	 * @param status          the new verification status
	 * @param rejectionReason reason for rejection (only used when
	 *                        {@code status} is {@code REJECTED})
	 * @param comments        optional reviewer comments
	 * @param reviewedBy      the id of the admin or agent performing the review
	 * @return the updated {@link DocumentDto}
	 * @throws EntityNotFoundException if no document exists with the given id
	 */
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

	/**
	 * Returns all documents currently in the
	 * {@link MasterEnums.DocumentStatus#NOT_VERIFIED} state, each enriched
	 * with a presigned S3 download URL.
	 *
	 * <p>Intended for admin dashboards that show a review queue.
	 *
	 * @return list of {@link DocumentDto} with status {@code NOT_VERIFIED};
	 *         empty if none exist
	 */
	public List<DocumentDto> getPendingDocuments() {
		log.info("getPendingDocuments - Fetching all NOT_VERIFIED documents");
		List<Documents> docs = documentsRepository.findByDocumentStatus(MasterEnums.DocumentStatus.NOT_VERIFIED);
		log.info("getPendingDocuments - Found {} NOT_VERIFIED documents", docs.size());
		return docs.stream().map(doc -> mapWithPresignedUrl(doc, new DocumentDto())).collect(Collectors.toList());
	}

	/**
	 * Returns documents for a specific object that match the given status,
	 * each enriched with a presigned S3 download URL.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectId   the primary-key of the owning entity
	 * @param status     the document status to filter by
	 * @return list of matching {@link DocumentDto}; empty if none found
	 */
	public List<DocumentDto> getDocumentsByObjectAndStatus(String objectType, Long objectId,
			MasterEnums.DocumentStatus status) {
		log.info("getDocumentsByObjectAndStatus - objectType={}, objectId={}, status={}", objectType, objectId, status);
		List<Documents> docs = documentsRepository
				.findByObjectTypeIgnoreCaseAndObjectIdAndDocumentStatus(objectType, objectId, status);
		log.info("getDocumentsByObjectAndStatus - Found {} documents", docs.size());
		return docs.stream().map(doc -> mapWithPresignedUrl(doc, new DocumentDto())).collect(Collectors.toList());
	}

	// ─── Helpers ─────────────────────────────────────────────────────────────

	/**
	 * Maps a {@link Documents} entity to a {@link DocumentDto} and enriches it
	 * with a presigned S3 download URL.
	 *
	 * @param doc the source entity
	 * @param dto an unused placeholder (overwritten by ModelMapper)
	 * @return the populated and URL-enriched {@link DocumentDto}
	 */
	private DocumentDto mapWithPresignedUrl(Documents doc, DocumentDto dto) {
		dto = mapper.map(doc, DocumentDto.class);
		enrichWithPresignedUrl(doc, dto);
		return dto;
	}

	/**
	 * Sets the {@code docUrl} field on the given DTO by generating a 60-minute
	 * presigned S3 URL from the document's S3 key.
	 *
	 * <p>Silently skips enrichment if the S3 key is null or blank, and logs
	 * an error without re-throwing if URL generation fails.
	 *
	 * @param doc the source entity providing the S3 key
	 * @param dto a {@link DocumentDto} or {@link DocumentminDto} whose
	 *            {@code docUrl} will be set
	 */
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
