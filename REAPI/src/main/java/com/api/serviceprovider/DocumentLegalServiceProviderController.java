package com.api.serviceprovider;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/providers")
@Tag(name = "Document Legal Vendor Services", description = "Operations for Document Legal Vendor Services")
public class DocumentLegalServiceProviderController {

	private static final Logger log = LoggerFactory.getLogger(DocumentLegalServiceProviderController.class);

	@Autowired
	private DocumentLegalServiceProviderService service;

	@Autowired
	private ModelMapper modelMapper;

	@Operation(summary = "Search document and legal service providers")
	@GetMapping("/search")
	public ResponseEntity<Page<DocumentLegalServiceProviderDto>> list(@RequestParam(required = false) String q,
			@RequestParam(required = false) String city, @RequestParam(required = false) String state,
			@RequestParam(required = false) String country, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "legalname,asc") String sort) {

		log.info("GET /api/providers/search - Searching providers [q={}, city={}, state={}, page={}, size={}]",
				q, city, state, page, size);

		String[] sortParts = sort.split(",");
		String sortField = sortParts[0];
		Sort.Direction direction = sortParts.length > 1 ? Sort.Direction.fromString(sortParts[1]) : Sort.Direction.ASC;

		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
		Page<DocumentLegalServiceProvider> result = service.search(q, city, state, country, pageable);
		Page<DocumentLegalServiceProviderDto> dtoPage = result
				.map(provider -> modelMapper.map(provider, DocumentLegalServiceProviderDto.class));

		log.info("GET /api/providers/search - Returned {} providers (page {}/{})",
				result.getNumberOfElements(), page, result.getTotalPages());
		return ResponseEntity.ok(dtoPage);
	}

	@Operation(summary = "Get provider by ID")
	@GetMapping("/id/{id}")
	public ResponseEntity<DocumentLegalServiceProviderDto> get(@PathVariable Long id) {
		log.info("GET /api/providers/id/{} - Fetching provider", id);
		return service.getById(id)
				.map(provider -> modelMapper.map(provider, DocumentLegalServiceProviderDto.class))
				.map(dto -> {
					log.info("GET /api/providers/id/{} - Provider fetched: {}", id, dto.getLegalname());
					return ResponseEntity.ok(dto);
				})
				.orElseGet(() -> {
					log.warn("GET /api/providers/id/{} - Provider not found", id);
					return ResponseEntity.notFound().build();
				});
	}

	@Operation(summary = "Create a new service provider")
	@PostMapping
	public ResponseEntity<DocumentLegalServiceProviderDto> create(
			@Valid @RequestBody DocumentLegalServiceProviderDto requestDto) {
		log.info("POST /api/providers - Creating provider: {}", requestDto.getLegalname());
		DocumentLegalServiceProvider entity = modelMapper.map(requestDto, DocumentLegalServiceProvider.class);
		DocumentLegalServiceProvider created = service.create(entity);
		DocumentLegalServiceProviderDto dto = modelMapper.map(created, DocumentLegalServiceProviderDto.class);
		log.info("POST /api/providers - Provider created with id={}", created.getId());
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Update an existing service provider")
	@PutMapping("/{id}")
	public ResponseEntity<DocumentLegalServiceProviderDto> update(@PathVariable Long id,
			@Valid @RequestBody DocumentLegalServiceProviderDto requestDto) {
		log.info("PUT /api/providers/{} - Updating provider", id);
		DocumentLegalServiceProvider entity = modelMapper.map(requestDto, DocumentLegalServiceProvider.class);
		DocumentLegalServiceProvider updated = service.update(id, entity);
		DocumentLegalServiceProviderDto dto = modelMapper.map(updated, DocumentLegalServiceProviderDto.class);
		log.info("PUT /api/providers/{} - Provider updated", id);
		return ResponseEntity.ok(dto);
	}

	@Operation(summary = "Delete a service provider")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("DELETE /api/providers/{} - Deleting provider", id);
		service.delete(id);
		log.info("DELETE /api/providers/{} - Provider deleted", id);
		return ResponseEntity.noContent().build();
	}
}
