package com.api.documents;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Operation APIs", description = "Operations related to Maintain Documents on cloud")
public class DocumentsController {

	private static final Logger log = LoggerFactory.getLogger(DocumentsController.class);

	private final DocumentsService documentsService;

	public DocumentsController(DocumentsService documentsService) {
		this.documentsService = documentsService;
	}

	@Operation(summary = "Upload documents for any object type")
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

	@Operation(summary = "Upload loan application documents")
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

	@Operation(summary = "Upload legal verification documents")
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

	@Operation(summary = "Get documents by object type and ID")
	@GetMapping("/{objectType}/{objectId}")
	public ResponseEntity<List<DocumentDto>> getDocuments(
			@PathVariable String objectType,
			@PathVariable Long objectId) {
		log.info("GET /api/documents/{}/{} - Fetching documents", objectType, objectId);
		List<DocumentDto> dtos = documentsService.getDocumentsByObject(objectType, objectId);
		log.info("GET /api/documents/{}/{} - Returned {} documents", objectType, objectId, dtos.size());
		return ResponseEntity.ok(dtos);
	}

	@Operation(summary = "Get loan documents by object ID")
	@GetMapping("/loan/{objectId}")
	public ResponseEntity<List<DocumentDto>> getLoanDocuments(@PathVariable Long objectId) {
		log.info("GET /api/documents/loan/{} - Fetching loan documents", objectId);
		List<DocumentDto> docs = documentsService.getDocumentsByObject("LOAN_DOCUMENT", objectId);
		log.info("GET /api/documents/loan/{} - Returned {} document(s)", objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	@Operation(summary = "Get legal documents by object ID")
	@GetMapping("/legal/{objectId}")
	public ResponseEntity<List<DocumentDto>> getLegalDocuments(@PathVariable Long objectId) {
		log.info("GET /api/documents/legal/{} - Fetching legal documents", objectId);
		List<DocumentDto> docs = documentsService.getDocumentsByObject("LEGAL_DOCUMENT", objectId);
		log.info("GET /api/documents/legal/{} - Returned {} document(s)", objectId, docs.size());
		return ResponseEntity.ok(docs);
	}

	@Operation(summary = "Delete a document record")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info("DELETE /api/documents/{} - Deleting document", id);
		documentsService.deleteDocument(id);
		log.info("DELETE /api/documents/{} - Document deleted", id);
		return ResponseEntity.ok("Deleted successfully");
	}

	@Operation(summary = "Update document verification status")
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

	@Operation(summary = "Get all pending (unverified) documents")
	@GetMapping("/pending")
	public ResponseEntity<List<DocumentDto>> getPendingDocuments() {
		log.info("GET /api/documents/pending - Fetching all NOT_VERIFIED documents");
		List<DocumentDto> docs = documentsService.getPendingDocuments();
		log.info("GET /api/documents/pending - Returned {} document(s)", docs.size());
		return ResponseEntity.ok(docs);
	}

	@Operation(summary = "Get pending documents for a specific object")
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

	public static class DocumentStatusRequest {
		@NotNull(message = "status is required")
		private MasterEnums.DocumentStatus status;
		private String rejectionReason;
		private String comments;
		private Long reviewedBy;

		public MasterEnums.DocumentStatus getStatus() { return status; }
		public void setStatus(MasterEnums.DocumentStatus status) { this.status = status; }
		public String getRejectionReason() { return rejectionReason; }
		public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
		public String getComments() { return comments; }
		public void setComments(String comments) { this.comments = comments; }
		public Long getReviewedBy() { return reviewedBy; }
		public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }
	}
}
