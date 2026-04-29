package com.api.serviceprovider;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for document and legal service provider operations.
 *
 * <p>Provides full CRUD capabilities for {@link DocumentLegalServiceProvider}
 * entities, along with a dynamic search method that composes JPA
 * {@link Specification} predicates from {@link ProviderSpecification} to
 * support free-text and location-based filtering. All mutating operations are
 * wrapped in a transaction.</p>
 */
@Slf4j
@Service
@Transactional
public class DocumentLegalServiceProviderService {

	@Autowired
	private DocumentLegalServiceProviderRepository repo;

	/**
	 * Searches for providers using optional free-text and location filters.
	 *
	 * <p>The {@code q} parameter performs a case-insensitive LIKE match against
	 * {@code legalname}, {@code contactname}, {@code email}, {@code phone1}, and
	 * {@code phone2}. Location filters ({@code city}, {@code state}, {@code country})
	 * perform exact case-insensitive matches. Null or blank values for any parameter
	 * are ignored.</p>
	 *
	 * @param q        free-text search term, or {@code null} / blank to skip
	 * @param city     city filter, or {@code null} / blank to skip
	 * @param state    state filter, or {@code null} / blank to skip
	 * @param country  country filter, or {@code null} / blank to skip
	 * @param pageable pagination and sorting descriptor
	 * @return a page of matching {@link DocumentLegalServiceProvider} entities
	 */
	public Page<DocumentLegalServiceProvider> search(String q, String city, String state, String country,
			Pageable pageable) {
		log.info("search - Searching providers [q={}, city={}, state={}, country={}]", q, city, state, country);

		Specification<DocumentLegalServiceProvider> spec = Specification
				.where(ProviderSpecification.containsText(q))
				.and(ProviderSpecification.hasCity(city))
				.and(ProviderSpecification.hasState(state))
				.and(ProviderSpecification.hasCountry(country));

		Page<DocumentLegalServiceProvider> result = repo.findAll(spec, pageable);
		log.info("search - Found {} providers (total={})", result.getNumberOfElements(), result.getTotalElements());
		return result;
	}

	/**
	 * Fetches a single provider by its primary key.
	 *
	 * @param id the provider ID to look up
	 * @return an {@link Optional} containing the provider if found,
	 *         or {@link Optional#empty()} if no provider exists with the given ID
	 */
	public Optional<DocumentLegalServiceProvider> getById(Long id) {
		log.info("getById - Fetching provider id={}", id);
		Optional<DocumentLegalServiceProvider> result = repo.findById(id);
		if (result.isEmpty()) {
			log.warn("getById - Provider not found for id={}", id);
		}
		return result;
	}

	/**
	 * Persists a new document and legal service provider.
	 *
	 * @param req the provider entity to create (ID should be {@code null})
	 * @return the saved provider entity with the generated ID populated
	 */
	public DocumentLegalServiceProvider create(DocumentLegalServiceProvider req) {
		log.info("create - Creating provider: {}", req.getLegalname());
		DocumentLegalServiceProvider saved = repo.save(req);
		log.info("create - Provider saved with id={}", saved.getId());
		return saved;
	}

	/**
	 * Updates an existing provider identified by the given ID.
	 *
	 * <p>All mutable fields from {@code req} are applied to the persisted entity.
	 * If {@code req.getStatus()} is non-null it also updates the status; otherwise
	 * the existing status is preserved.</p>
	 *
	 * @param id  the ID of the provider to update
	 * @param req the provider data containing updated field values
	 * @return the updated and persisted provider entity
	 * @throws IllegalArgumentException if no provider exists with the given {@code id}
	 */
	public DocumentLegalServiceProvider update(Long id, DocumentLegalServiceProvider req) {
		log.info("update - Updating provider id={}", id);
		DocumentLegalServiceProvider p = repo.findById(id).orElseThrow(() -> {
			log.error("update - Provider not found for id={}", id);
			return new IllegalArgumentException("Provider not found");
		});

		p.setLegalname(req.getLegalname());
		p.setContactname(req.getContactname());
		p.setCity(req.getCity());
		p.setState(req.getState());
		p.setCountry(req.getCountry());
		p.setAddress(req.getAddress());
		p.setPhone1(req.getPhone1());
		p.setPhone2(req.getPhone2());
		p.setEmail(req.getEmail());
		p.setServices(req.getServices());
		if (req.getStatus() != null)
			p.setStatus(req.getStatus());

		DocumentLegalServiceProvider updated = repo.save(p);
		log.info("update - Provider id={} updated", id);
		return updated;
	}

	/**
	 * Deletes the provider with the given ID.
	 *
	 * <p>No exception is thrown if the provider does not exist.</p>
	 *
	 * @param id the ID of the provider to delete
	 */
	public void delete(Long id) {
		log.info("delete - Deleting provider id={}", id);
		repo.deleteById(id);
		log.info("delete - Provider id={} deleted", id);
	}
}
