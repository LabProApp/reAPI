package com.api.serviceprovider;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class DocumentLegalServiceProviderService {

	@Autowired
	private DocumentLegalServiceProviderRepository repo;

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

	public Optional<DocumentLegalServiceProvider> getById(Long id) {
		log.info("getById - Fetching provider id={}", id);
		Optional<DocumentLegalServiceProvider> result = repo.findById(id);
		if (result.isEmpty()) {
			log.warn("getById - Provider not found for id={}", id);
		}
		return result;
	}

	public DocumentLegalServiceProvider create(DocumentLegalServiceProvider req) {
		log.info("create - Creating provider: {}", req.getLegalname());
		DocumentLegalServiceProvider saved = repo.save(req);
		log.info("create - Provider saved with id={}", saved.getId());
		return saved;
	}

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

	public void delete(Long id) {
		log.info("delete - Deleting provider id={}", id);
		repo.deleteById(id);
		log.info("delete - Provider id={} deleted", id);
	}
}
