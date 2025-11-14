package com.api.serviceprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

	@Autowired
	private ProviderService service;

	// public listing with pagination, filtering & sorting
	@GetMapping
	public ResponseEntity<Page<DocumentLegalServiceProvider>> list(@RequestParam(required = false) String q,
			@RequestParam(required = false) String city, @RequestParam(required = false) String serviceType,
			@RequestParam(required = false) Double minRating,
			@RequestParam(required = false) DocumentLegalServiceProvider.Status status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "name,asc") String[] sort) {
		// parse sort param like ?sort=name,desc
		Sort.Direction dir = Sort.Direction.fromString(sort[1]);
		Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort[0]));
		Page<DocumentLegalServiceProvider> result = service.search(q, city, serviceType, minRating, status, pageable);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<DocumentLegalServiceProvider> get(@PathVariable Long id) {
		return service.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Admin endpoints - protect with roles (see notes)
	@PostMapping
	public ResponseEntity<DocumentLegalServiceProvider> create(
			@Valid @RequestBody DocumentLegalServiceProvider request) {
		DocumentLegalServiceProvider created = service.create(request);
		return ResponseEntity.ok(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DocumentLegalServiceProvider> update(@PathVariable Long id,
			@Valid @RequestBody DocumentLegalServiceProvider req) {
		DocumentLegalServiceProvider updated = service.update(id, req);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}