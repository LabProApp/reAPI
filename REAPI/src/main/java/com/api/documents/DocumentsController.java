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

		List<DocumentDto> uploadedDocs = documentsService.uploadDocuments(objectType, objectId, files, captions);

		return ResponseEntity.ok(uploadedDocs);
	}

	// Get documents by object
	@GetMapping("/{objectType}/{objectId}")
	public ResponseEntity<List<DocumentDto>> getDocuments(@PathVariable String objectType,
			@PathVariable Long objectId) {

		List<DocumentDto> dtos = documentsService.getDocumentsByObject(objectType, objectId);

		return ResponseEntity.ok(dtos);
	}

	// Delete a document
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		documentsService.deleteDocument(id);
		return ResponseEntity.ok("Deleted successfully");
	}
}
