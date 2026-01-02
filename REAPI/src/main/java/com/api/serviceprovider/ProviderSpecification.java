package com.api.serviceprovider;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class ProviderSpecification {

	public static Specification<DocumentLegalServiceProvider> containsText(String q) {
		return (root, query, cb) -> {
			if (q == null || q.trim().isEmpty()) {
				return cb.conjunction();
			}

			String like = "%" + q.toLowerCase() + "%";

			return cb.or(cb.like(cb.lower(root.get("legalname")), like),
					cb.like(cb.lower(root.get("contactname")), like), cb.like(cb.lower(root.get("email")), like),
					cb.like(cb.lower(root.get("phone1")), like), cb.like(cb.lower(root.get("phone2")), like));
		};
	}

	public static Specification<DocumentLegalServiceProvider> hasCity(String city) {
		return (root, query, cb) -> city == null || city.isBlank() ? cb.conjunction()
				: cb.equal(cb.lower(root.get("city")), city.toLowerCase());
	}

	public static Specification<DocumentLegalServiceProvider> hasState(String state) {
		return (root, query, cb) -> state == null || state.isBlank() ? cb.conjunction()
				: cb.equal(cb.lower(root.get("state")), state.toLowerCase());
	}

	public static Specification<DocumentLegalServiceProvider> hasCountry(String country) {
		return (root, query, cb) -> country == null || country.isBlank() ? cb.conjunction()
				: cb.equal(cb.lower(root.get("country")), country.toLowerCase());
	}
}
