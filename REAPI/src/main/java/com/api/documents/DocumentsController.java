package com.api.documents;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentsController {

	@Autowired
	private DocumentsService documentsService;

	@PostMapping("/upload")
	public ResponseEntity<Documents> upload(@RequestParam MultipartFile file, @RequestParam String docType,
			@RequestParam(required = false) String caption, @RequestParam(required = false) Long propertyId,
			@RequestParam(required = false) Long userId) throws IOException {

		Documents doc = documentsService.uploadDocument(file, docType, caption, propertyId, userId);

		return ResponseEntity.ok(doc);
	}

	@GetMapping("/property/{id}")
	public List<Documents> byProperty(@PathVariable Long id) {
		return documentsService.getByProperty(id);
	}

	@GetMapping("/user/{id}")
	public List<Documents> byUser(@PathVariable Long id) {
		return documentsService.getByUser(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		documentsService.deleteDocument(id);
		return ResponseEntity.ok("Deleted successfully");
	}
}
