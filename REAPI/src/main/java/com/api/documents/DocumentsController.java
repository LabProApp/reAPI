package com.api.documents;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentsController {

	@Autowired
	private DocumentsService documentsService;

	@PostMapping("/uploadDocuments/{objectType}/{objectId}")
	public ResponseEntity<List<Documents>> uploadDocuments(@PathVariable String objectType, @PathVariable Long objectId,
			@RequestParam("files") List<MultipartFile> files,
			@RequestParam(value = "captions", required = false) List<String> captions) throws IOException {

		List<Documents> uploadedDocs = documentsService.uploadDocuments(objectType, objectId, files, captions);

		return ResponseEntity.ok(uploadedDocs);
	}

	@GetMapping("/{objectType}/{objectId}")
	public ResponseEntity<List<Documents>> getDocuments(@PathVariable String objectType, @PathVariable Long objectId) {

		List<Documents> docs = documentsService.getDocumentsByObject(objectType, objectId);
		return ResponseEntity.ok(docs);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		documentsService.deleteDocument(id);
		return ResponseEntity.ok("Deleted successfully");
	}
}
