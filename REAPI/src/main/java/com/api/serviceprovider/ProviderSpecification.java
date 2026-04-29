package com.api.serviceprovider;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

/**
 * Factory class providing JPA {@link Specification} predicates for filtering
 * {@link DocumentLegalServiceProvider} entities.
 *
 * <p>Each static method returns a {@link Specification} that applies its
 * predicate only when the supplied value is non-null and non-blank; otherwise
 * a trivially true conjunction is returned so the predicate is effectively
 * ignored. These specifications are designed to be composed with
 * {@link Specification#where(Specification)} and
 * {@link Specification#and(Specification)} to build dynamic queries in
 * {@link DocumentLegalServiceProviderService}.</p>
 */
public class ProviderSpecification {

	/**
	 * Returns a specification that performs a case-insensitive LIKE search across
	 * the {@code legalname}, {@code contactname}, {@code email}, {@code phone1},
	 * and {@code phone2} fields.
	 *
	 * <p>If {@code q} is {@code null} or blank, a conjunction (no-op) is returned.</p>
	 *
	 * @param q the search term; may be {@code null} or blank
	 * @return a {@link Specification} applying the free-text predicate, or a conjunction
	 */
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

	/**
	 * Returns a specification that filters providers by exact case-insensitive city match.
	 *
	 * <p>If {@code city} is {@code null} or blank, a conjunction (no-op) is returned.</p>
	 *
	 * @param city the city to match; may be {@code null} or blank
	 * @return a {@link Specification} applying the city predicate, or a conjunction
	 */
	public static Specification<DocumentLegalServiceProvider> hasCity(String city) {
		return (root, query, cb) -> city == null || city.isBlank() ? cb.conjunction()
				: cb.equal(cb.lower(root.get("city")), city.toLowerCase());
	}

	/**
	 * Returns a specification that filters providers by exact case-insensitive state match.
	 *
	 * <p>If {@code state} is {@code null} or blank, a conjunction (no-op) is returned.</p>
	 *
	 * @param state the state to match; may be {@code null} or blank
	 * @return a {@link Specification} applying the state predicate, or a conjunction
	 */
	public static Specification<DocumentLegalServiceProvider> hasState(String state) {
		return (root, query, cb) -> state == null || state.isBlank() ? cb.conjunction()
				: cb.equal(cb.lower(root.get("state")), state.toLowerCase());
	}

	/**
	 * Returns a specification that filters providers by exact case-insensitive country match.
	 *
	 * <p>If {@code country} is {@code null} or blank, a conjunction (no-op) is returned.</p>
	 *
	 * @param country the country to match; may be {@code null} or blank
	 * @return a {@link Specification} applying the country predicate, or a conjunction
	 */
	public static Specification<DocumentLegalServiceProvider> hasCountry(String country) {
		return (root, query, cb) -> country == null || country.isBlank() ? cb.conjunction()
				: cb.equal(cb.lower(root.get("country")), country.toLowerCase());
	}
}
