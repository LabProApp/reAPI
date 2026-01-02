package com.api.serviceprovider;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DocumentLegalServiceProviderService {
	@Autowired
	private DocumentLegalServiceProviderRepository repo;

	public Page<DocumentLegalServiceProvider> search(
	        String q,
	        String city,
	        String state,
	        String country,
	        Pageable pageable) {

	    Specification<DocumentLegalServiceProvider> spec = Specification
	            .where(ProviderSpecification.containsText(q))
	            .and(ProviderSpecification.hasCity(city))
	            .and(ProviderSpecification.hasState(state))
	            .and(ProviderSpecification.hasCountry(country));

	    return repo.findAll(spec, pageable);
	}

	public Optional<DocumentLegalServiceProvider> getById(Long id) {
		return repo.findById(id);
	}

	public DocumentLegalServiceProvider create(DocumentLegalServiceProvider req) {

		return repo.save(req);
	}

	public DocumentLegalServiceProvider update(Long id, DocumentLegalServiceProvider req) {
		DocumentLegalServiceProvider p = repo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Provider not found"));

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

		return repo.save(p);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}
}
