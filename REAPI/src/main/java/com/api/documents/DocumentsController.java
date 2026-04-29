package com.api.documents;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.enums.MasterEnums;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller exposing document management endpoints under
 * {@code /api/documents}.
 *
 * <p>Supported operations:
 * <ul>
 *   <li>{@code POST /uploadDocuments/{objectType}/{objectId}} — generic
 *       multi-file upload for any object type.</li>
 *   <li>{@code POST /upload/loan/{objectId}} — upload loan application
 *       documents (defaults to {@code NOT_VERIFIED}).</li>
 *   <li>{@code POST /upload/legal/{objectId}} — upload legal verification
 *       documents (defaults to {@code NOT_VERIFIED}).</li>
 *   <li>{@code GET /{objectType}/{objectId}} — fetch all documents for an
 *       object.</li>
 *   <li>{@code GET /loan/{objectId}} — fetch loan documents.</li>
 *   <li>{@code GET /legal/{objectId}} — fetch legal documents.</li>
 *   <li>{@code DELETE /{id}} — delete a document record.</li>
 *   <li>{@code PUT /{id}/status} — admin: verify or reject a document.</li>
 *   <li>{@code GET /pending} — admin: list all {@code NOT_VERIFIED}
 *       documents.</li>
 *   <li>{@code GET /pending/{objectType}/{objectId}} — admin: list
 *       {@code NOT_VERIFIED} documents for a specific object.</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Operation APIs", description = "Operations related to Maintain Documents on cloud")
public class DocumentsController {

	private final DocumentsService documentsService;

	/**
	 * Constructs the controller with the required service dependency.
	 *
	 * @param documentsService the service handling document business logic
	 */
	public DocumentsController(DocumentsService documentsService) {
		this.documentsService = documentsService;
	}

	// ─── Generic upload (any objectType) ─────────────────────────────────────

	/**
	 * Uploads one or more files for any object type and persists their metadata.
	 *
	 * <p>Files are validated for size (max 10 MB) and MIME type. Documents
	 * belonging to {@code LOAN_DOCUMENT} or {@code LEGAL_DOCUMENT} object types
	 * are automatically set to {@code NOT_VERIFIED}; all others are set to
	 * {@code VERIFIED}.
	 *
	 * @param objectType the owning entity category (e.g. {@code "PROPERTY"})
	 * @param objectId   the primary-key of the owning entity
	 * @param files      the multipart files to upload
	 * @param titles     optional per-file human-readable titles
	 * @param captions   optional per-file captions
	 * @param uploadedBy optional id of the uploading user
	 * @return {@code 200 OK} containing a list of saved {@link DocumentDto}
	 * @throws IOException if reading a file's input stream fails
	 */
	@PostMapping("/uploadDocuments/{objectType}/{objectId}")
	public ResponseEntity<List<DocumentDto>> uploadDocuments(
			@PathVariable String objectType,
			@PathVariable Long objectId,
			@RequestParam("files") List<MultipartFile> files,
			@RequestParam(value = "titles", required = false) List<String> titles,
			@RequestParam(value = "captions", required = false) List<String> captions,
			@RequestParam(value = "uploadedBy", required = false) Long uploadedBy) throws IOException {
		log.info("POST /api/documents/uploadDocuments/{}/{} - Uploading {} file(s)", objectType, objectId, files.size());
		List<DocumentDto> docs = documentsService.uploadDocuments(objectType, objectId, files, titles, captions, uploadedBy);
		log.info("POST /api/documents/uploadDocuments/{}/{} - Uploaded {} document(s)", objectType, objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	// ─── Loan application document upload ────────────────────────────────────

	/**
	 * Uploads one or more loan application documents for the given object id.
	 *
	 * <p>All uploaded documents are assigned the {@code LOAN_DOCUMENT} object
	 * type and default to {@code NOT_VERIFIED} status pending admin review.
	 *
	 * @param objectId   the primary-key of the owning loan application entity
	 * @param files      the multipart files to upload
	 * @param titles     optional per-file human-readable titles
	 * @param captions   optional per-file captions
	 * @param uploadedBy optional id of the uploading user
	 * @return {@code 200 OK} containing a list of saved {@link DocumentDto}
	 * @throws IOException if reading a file's input stream fails
	 */
	@PostMapping("/upload/loan/{objectId}")
	public ResponseEntity<List<DocumentDto>> uploadLoanDocuments(
			@PathVariable Long objectId,
			@RequestParam("files") List<MultipartFile> files,
			@RequestParam(value = "titles", required = false) List<String> titles,
			@RequestParam(value = "captions", required = false) List<String> captions,
			@RequestParam(value = "uploadedBy", required = false) Long uploadedBy) throws IOException {
		log.info("POST /api/documents/upload/loan/{} - Uploading {} loan document(s)", objectId, files.size());
		List<DocumentDto> docs = documentsService.uploadDocuments("LOAN_DOCUMENT", objectId, files, titles, captions, uploadedBy);
		log.info("POST /api/documents/upload/loan/{} - Uploaded {} document(s)", objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	// ─── Legal verification document upload ──────────────────────────────────

	/**
	 * Uploads one or more legal verification documents for the given object id.
	 *
	 * <p>All uploaded documents are assigned the {@code LEGAL_DOCUMENT} object
	 * type and default to {@code NOT_VERIFIED} status pending admin review.
	 *
	 * @param objectId   the primary-key of the owning legal entity
	 * @param files      the multipart files to upload
	 * @param titles     optional per-file human-readable titles
	 * @param captions   optional per-file captions
	 * @param uploadedBy optional id of the uploading user
	 * @return {@code 200 OK} containing a list of saved {@link DocumentDto}
	 * @throws IOException if reading a file's input stream fails
	 */
	@PostMapping("/upload/legal/{objectId}")
	public ResponseEntity<List<DocumentDto>> uploadLegalDocuments(
			@PathVariable Long objectId,
			@RequestParam("files") List<MultipartFile> files,
			@RequestParam(value = "titles", required = false) List<String> titles,
			@RequestParam(value = "captions", required = false) List<String> captions,
			@RequestParam(value = "uploadedBy", required = false) Long uploadedBy) throws IOException {
		log.info("POST /api/documents/upload/legal/{} - Uploading {} legal document(s)", objectId, files.size());
		List<DocumentDto> docs = documentsService.uploadDocuments("LEGAL_DOCUMENT", objectId, files, titles, captions, uploadedBy);
		log.info("POST /api/documents/upload/legal/{} - Uploaded {} document(s)", objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	// ─── Fetch documents ──────────────────────────────────────────────────────

	/**
	 * Returns all documents associated with the specified object type and id,
	 * each containing a presigned S3 download URL.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectId   the primary-key of the owning entity
	 * @return {@code 200 OK} containing a list of {@link DocumentDto}
	 */
	@GetMapping("/{objectType}/{objectId}")
	public ResponseEntity<List<DocumentDto>> getDocuments(
			@PathVariable String objectType,
			@PathVariable Long objectId) {
		log.info("GET /api/documents/{}/{} - Fetching documents", objectType, objectId);
		List<DocumentDto> dtos = documentsService.getDocumentsByObject(objectType, objectId);
		log.info("GET /api/documents/{}/{} - Returned {} documents", objectType, objectId, dtos.size());
		return ResponseEntity.ok(dtos);
	}

	/**
	 * Returns all loan application documents for the given object id, each
	 * containing a presigned S3 download URL.
	 *
	 * @param objectId the primary-key of the owning loan application entity
	 * @return {@code 200 OK} containing a list of {@link DocumentDto}
	 */
	@GetMapping("/loan/{objectId}")
	public ResponseEntity<List<DocumentDto>> getLoanDocuments(@PathVariable Long objectId) {
		log.info("GET /api/documents/loan/{} - Fetching loan documents", objectId);
		List<DocumentDto> docs = documentsService.getDocumentsByObject("LOAN_DOCUMENT", objectId);
		log.info("GET /api/documents/loan/{} - Returned {} document(s)", objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	/**
	 * Returns all legal verification documents for the given object id, each
	 * containing a presigned S3 download URL.
	 *
	 * @param objectId the primary-key of the owning legal entity
	 * @return {@code 200 OK} containing a list of {@link DocumentDto}
	 */
	@GetMapping("/legal/{objectId}")
	public ResponseEntity<List<DocumentDto>> getLegalDocuments(@PathVariable Long objectId) {
		log.info("GET /api/documents/legal/{} - Fetching legal documents", objectId);
		List<DocumentDto> docs = documentsService.getDocumentsByObject("LEGAL_DOCUMENT", objectId);
		log.info("GET /api/documents/legal/{} - Returned {} document(s)", objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	// ─── Delete ───────────────────────────────────────────────────────────────

	/**
	 * Deletes the document database record with the given id.
	 *
	 * <p>Note: the corresponding S3 object is not removed by this endpoint.
	 *
	 * @param id the document id to delete
	 * @return {@code 200 OK} with the message {@code "Deleted successfully"}
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info("DELETE /api/documents/{} - Deleting document", id);
		documentsService.deleteDocument(id);
		log.info("DELETE /api/documents/{} - Document deleted", id);
		return ResponseEntity.ok("Deleted successfully");
	}

	// ─── Admin: verify / reject a document ───────────────────────────────────

	/**
	 * Updates the verification status of a document (verify or reject).
	 *
	 * <p>When the status is {@code REJECTED} the {@code rejectionReason} field
	 * in the request body is persisted; for other statuses it is cleared.
	 *
	 * @param id  the id of the document to update
	 * @param req the request body containing the new status, optional rejection
	 *            reason, optional comments, and the reviewer's user id
	 * @return {@code 200 OK} containing the updated {@link DocumentDto}
	 */
	@PutMapping("/{id}/status")
	public ResponseEntity<DocumentDto> updateStatus(
			@PathVariable Long id,
			@Valid @RequestBody DocumentStatusRequest req) {
		log.info("PUT /api/documents/{}/status - status={}, reviewedBy={}", id, req.getStatus(), req.getReviewedBy());
		DocumentDto updated = documentsService.updateDocumentStatus(
				id, req.getStatus(), req.getRejectionReason(), req.getComments(), req.getReviewedBy());
		log.info("PUT /api/documents/{}/status - Updated to {}", id, updated.getDocumentStatus());
		return ResponseEntity.ok(updated);
	}

	// ─── Admin: pending (NOT_VERIFIED) documents ──────────────────────────────

	/**
	 * Returns all documents currently awaiting review (status
	 * {@code NOT_VERIFIED}) across all object types.
	 *
	 * @return {@code 200 OK} containing the list of pending {@link DocumentDto}
	 */
	@GetMapping("/pending")
	public ResponseEntity<List<DocumentDto>> getPendingDocuments() {
		log.info("GET /api/documents/pending - Fetching all NOT_VERIFIED documents");
		List<DocumentDto> docs = documentsService.getPendingDocuments();
		log.info("GET /api/documents/pending - Returned {} document(s)", docs.size());
		return ResponseEntity.ok(docs);
	}

	/**
	 * Returns all {@code NOT_VERIFIED} documents for a specific object type
	 * and id.
	 *
	 * @param objectType the owning entity category (case-insensitive)
	 * @param objectId   the primary-key of the owning entity
	 * @return {@code 200 OK} containing the list of pending {@link DocumentDto}
	 */
	@GetMapping("/pending/{objectType}/{objectId}")
	public ResponseEntity<List<DocumentDto>> getPendingByObject(
			@PathVariable String objectType,
			@PathVariable Long objectId) {
		log.info("GET /api/documents/pending/{}/{} - Fetching NOT_VERIFIED documents", objectType, objectId);
		List<DocumentDto> docs = documentsService.getDocumentsByObjectAndStatus(
				objectType, objectId, MasterEnums.DocumentStatus.NOT_VERIFIED);
		log.info("GET /api/documents/pending/{}/{} - Returned {} document(s)", objectType, objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	// ─── Request body for status update ──────────────────────────────────────

	/**
	 * Request body used by the {@code PUT /{id}/status} endpoint to carry the
	 * new document status and associated review metadata.
	 */
	public static class DocumentStatusRequest {
		@NotNull(message = "status is required")
		private MasterEnums.DocumentStatus status;
		private String rejectionReason;
		private String comments;
		private Long reviewedBy;

		/** @return the new document verification status */
		public MasterEnums.DocumentStatus getStatus() { return status; }

		/**
		 * Sets the new document verification status.
		 *
		 * @param status the status to set
		 */
		public void setStatus(MasterEnums.DocumentStatus status) { this.status = status; }

		/** @return the reason for rejection, or {@code null} */
		public String getRejectionReason() { return rejectionReason; }

		/**
		 * Sets the reason for rejection.
		 *
		 * @param rejectionReason the rejection reason
		 */
		public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

		/** @return optional reviewer comments */
		public String getComments() { return comments; }

		/**
		 * Sets optional reviewer comments.
		 *
		 * @param comments the comments
		 */
		public void setComments(String comments) { this.comments = comments; }

		/** @return the id of the admin or agent performing the review */
		public Long getReviewedBy() { return reviewedBy; }

		/**
		 * Sets the id of the admin or agent performing the review.
		 *
		 * @param reviewedBy the reviewer's user id
		 */
		public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }
	}
}
