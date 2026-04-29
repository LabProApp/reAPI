package com.api.banks;



import org.springframework.data.jpa.domain.Specification;

/**
 * Factory class providing reusable JPA {@link Specification} predicates for
 * filtering {@link Bank} entities. Each method returns {@code null} (i.e. no
 * restriction) when its corresponding filter value is {@code null}, making
 * all predicates safely composable with
 * {@link Specification#where(Specification)} and {@link Specification#and}.
 */
public class BankSpecifications {

    /**
     * Matches banks whose base interest rate is less than or equal to
     * {@code maxRate}.
     *
     * @param maxRate the upper bound for the interest rate; {@code null} returns
     *                no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code maxRate} is {@code null}
     */
    public static Specification<Bank> hasMaxRate(Double maxRate) {
        return (root, query, cb) -> maxRate == null ? null : cb.le(root.get("interestRate"), maxRate);
    }

    /**
     * Matches banks whose minimum CIBIL score requirement is greater than or
     * equal to {@code minCibil}.
     *
     * @param minCibil the minimum CIBIL score threshold; {@code null} returns
     *                 no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code minCibil} is {@code null}
     */
    public static Specification<Bank> hasMinCibil(Integer minCibil) {
        return (root, query, cb) -> minCibil == null ? null : cb.ge(root.get("minCibilScore"), minCibil);
    }

    /**
     * Matches banks whose maximum loan tenure is less than or equal to
     * {@code maxTenure} years.
     *
     * @param maxTenure the upper bound for tenure in years; {@code null} returns
     *                  no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code maxTenure} is {@code null}
     */
    public static Specification<Bank> hasMaxTenure(Integer maxTenure) {
        return (root, query, cb) -> maxTenure == null ? null : cb.le(root.get("tenureYears"), maxTenure);
    }

    /**
     * Matches banks whose minimum income requirement is greater than or equal to
     * {@code minIncome}.
     *
     * @param minIncome the minimum income threshold; {@code null} returns
     *                  no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code minIncome} is {@code null}
     */
    public static Specification<Bank> hasMinIncome(Double minIncome) {
        return (root, query, cb) -> minIncome == null ? null : cb.ge(root.get("minimumIncome"), minIncome);
    }

    /**
     * Matches banks located in the specified city (case-insensitive exact match).
     *
     * @param city the city name to filter by; {@code null} returns no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code city} is {@code null}
     */
    public static Specification<Bank> hasCity(String city) {
        return (root, query, cb) -> city == null ? null : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    /**
     * Matches banks located in the specified state (case-insensitive exact match).
     *
     * @param state the state name to filter by; {@code null} returns no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code state} is {@code null}
     */
    public static Specification<Bank> hasState(String state) {
        return (root, query, cb) -> state == null ? null : cb.equal(cb.lower(root.get("state")), state.toLowerCase());
    }

    /**
     * Matches banks whose name contains the given substring
     * (case-insensitive LIKE search).
     *
     * @param bank a partial bank name to search for; {@code null} returns no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code bank} is {@code null}
     */
    public static Specification<Bank> hasBank(String bank) {
        return (root, query, cb) -> bank == null ? null : cb.like(cb.lower(root.get("bankName")), "%" + bank.toLowerCase() + "%");
    }

    /**
     * Matches banks with the specified postal code (case-insensitive exact match).
     *
     * @param postalCode the postal code to filter by; {@code null} returns no restriction
     * @return a {@link Specification} predicate, or a no-op if {@code postalCode} is {@code null}
     */
    public static Specification<Bank> hasPostalCode(String postalCode) {
        return (root, query, cb) -> postalCode == null ? null : cb.equal(cb.lower(root.get("postalCode")), postalCode.toLowerCase());
    }
}
