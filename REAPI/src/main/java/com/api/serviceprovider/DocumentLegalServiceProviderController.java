package com.api.serviceprovider;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/providers")
@Tag(name = "Document Legal Vendor Services", description = "Operations for Document Legal Vendor Services")
public class DocumentLegalServiceProviderController {

	@Autowired
	private DocumentLegalServiceProviderService service;

	@Autowired
	private ModelMapper modelMapper;
	

	// Public listing with pagination, filtering & sorting
	@GetMapping("/search")

	public ResponseEntity<Page<DocumentLegalServiceProviderDto>> list(@RequestParam(required = false) String q,
			@RequestParam(required = false) String city, @RequestParam(required = false) String state,
			@RequestParam(required = false) String country, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "legalname,asc") String sort) {

		// Safe sort parsing
		String[] sortParts = sort.split(",");
		String sortField = sortParts[0];
		Sort.Direction direction = sortParts.length > 1 ? Sort.Direction.fromString(sortParts[1]) : Sort.Direction.ASC;

		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

		Page<DocumentLegalServiceProvider> result = service.search(q, city, state, country, pageable);

		// Entity → DTO
		Page<DocumentLegalServiceProviderDto> dtoPage = result
				.map(provider -> modelMapper.map(provider, DocumentLegalServiceProviderDto.class));

		return ResponseEntity.ok(dtoPage);
	}

	@GetMapping("/id/{id}")
	public ResponseEntity<DocumentLegalServiceProviderDto> get(@PathVariable Long id) {
		return service.getById(id).map(provider -> modelMapper.map(provider, DocumentLegalServiceProviderDto.class))
				.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Admin endpoints
	@PostMapping
	public ResponseEntity<DocumentLegalServiceProviderDto> create(
			@Valid @RequestBody DocumentLegalServiceProviderDto requestDto) {

		DocumentLegalServiceProvider entity = modelMapper.map(requestDto, DocumentLegalServiceProvider.class);
		DocumentLegalServiceProvider created = service.create(entity);
		DocumentLegalServiceProviderDto dto = modelMapper.map(created, DocumentLegalServiceProviderDto.class);

		return ResponseEntity.ok(dto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<DocumentLegalServiceProviderDto> update(@PathVariable Long id,
			@Valid @RequestBody DocumentLegalServiceProviderDto requestDto) {

		DocumentLegalServiceProvider entity = modelMapper.map(requestDto, DocumentLegalServiceProvider.class);
		DocumentLegalServiceProvider updated = service.update(id, entity);
		DocumentLegalServiceProviderDto dto = modelMapper.map(updated, DocumentLegalServiceProviderDto.class);

		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
