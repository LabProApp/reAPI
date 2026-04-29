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
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for document and legal service provider endpoints.
 *
 * <p>Exposes CRUD and search APIs under {@code /api/providers}. The search
 * endpoint supports free-text and location-based filtering with full pagination
 * and sorting. All request/response bodies use {@link DocumentLegalServiceProviderDto}
 * to decouple the API contract from the persistence model.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/providers")
@Tag(name = "Document Legal Vendor Services", description = "Operations for Document Legal Vendor Services")
public class DocumentLegalServiceProviderController {

	@Autowired
	private DocumentLegalServiceProviderService service;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Searches for providers using optional free-text and location filters with pagination and sorting.
	 *
	 * <p>The {@code q} parameter searches across legal name, contact name, email,
	 * and phone numbers. Location parameters ({@code city}, {@code state},
	 * {@code country}) apply exact case-insensitive matches. The {@code sort}
	 * parameter accepts a field name and optional direction separated by a comma
	 * (e.g., {@code legalname,asc}).</p>
	 *
	 * @param q       optional free-text search term
	 * @param city    optional city filter
	 * @param state   optional state filter
	 * @param country optional country filter
	 * @param page    zero-based page index (default {@code 0})
	 * @param size    page size (default {@code 20})
	 * @param sort    sort field and direction, e.g., {@code legalname,asc} (default)
	 * @return a {@link ResponseEntity} containing a page of {@link DocumentLegalServiceProviderDto} objects
	 */
	// Public listing with pagination, filtering & sorting
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

	/**
	 * Fetches a single provider by its primary key.
	 *
	 * @param id the provider ID
	 * @return {@code 200 OK} with the {@link DocumentLegalServiceProviderDto} if found,
	 *         or {@code 404 Not Found} if no provider exists with the given ID
	 */
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

	/**
	 * Creates a new document and legal service provider.
	 *
	 * @param requestDto the provider data to persist; must pass validation constraints
	 * @return {@code 200 OK} with the created {@link DocumentLegalServiceProviderDto}
	 *         (including the generated ID)
	 */
	// Admin endpoints
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

	/**
	 * Updates an existing provider identified by the given ID.
	 *
	 * @param id         the ID of the provider to update
	 * @param requestDto the updated provider data; must pass validation constraints
	 * @return {@code 200 OK} with the updated {@link DocumentLegalServiceProviderDto}
	 * @throws IllegalArgumentException if no provider exists with the given {@code id}
	 */
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

	/**
	 * Deletes the provider with the given ID.
	 *
	 * @param id the ID of the provider to delete
	 * @return {@code 204 No Content} on successful deletion
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("DELETE /api/providers/{} - Deleting provider", id);
		service.delete(id);
		log.info("DELETE /api/providers/{} - Provider deleted", id);
		return ResponseEntity.noContent().build();
	}
}
