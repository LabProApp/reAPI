package com.api.documents;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Operation APIs", description = "Operations related to Maintain Documents on cloud")
public class DocumentsController {

	private final DocumentsService documentsService;

	public DocumentsController(DocumentsService documentsService) {
		this.documentsService = documentsService;
	}

	// Upload documents
	@PostMapping("/uploadDocuments/{objectType}/{objectId}")
	public ResponseEntity<List<DocumentDto>> uploadDocuments(@PathVariable String objectType,
			@PathVariable Long objectId, @RequestParam("files") List<MultipartFile> files,
			@RequestParam(value = "captions", required = false) List<String> captions) throws IOException {
		log.info("POST /api/documents/uploadDocuments/{}/{} - Uploading {} file(s)",
				objectType, objectId, files.size());
		List<DocumentDto> uploadedDocs = documentsService.uploadDocuments(objectType, objectId, files, captions);
		log.info("POST /api/documents/uploadDocuments/{}/{} - Successfully uploaded {} document(s)",
				objectType, objectId, uploadedDocs.size());
		return ResponseEntity.ok(uploadedDocs);
	}

	// Get documents by object
	@GetMapping("/{objectType}/{objectId}")
	public ResponseEntity<List<DocumentDto>> getDocuments(@PathVariable String objectType,
			@PathVariable Long objectId) {
		log.info("GET /api/documents/{}/{} - Fetching documents", objectType, objectId);
		List<DocumentDto> dtos = documentsService.getDocumentsByObject(objectType, objectId);
		log.info("GET /api/documents/{}/{} - Returned {} documents", objectType, objectId, dtos.size());
		return ResponseEntity.ok(dtos);
	}

	// Delete a document
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		log.info("DELETE /api/documents/{} - Deleting document", id);
		documentsService.deleteDocument(id);
		log.info("DELETE /api/documents/{} - Document deleted", id);
		return ResponseEntity.ok("Deleted successfully");
	}
}
