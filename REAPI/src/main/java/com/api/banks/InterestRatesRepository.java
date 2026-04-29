package com.api.banks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for {@link InterestRates} entities. Provides
 * derived-query lookup by bank ID and a custom JPQL query to detect overlapping
 * CIBIL-score ranges within the same bank.
 */
public interface InterestRatesRepository extends JpaRepository<InterestRates, Long> {

	/**
	 * Returns all interest rate slabs that belong to the specified bank.
	 *
	 * @param bankId the ID of the owning bank
	 * @return a list of {@link InterestRates}; empty if none exist
	 */
	List<InterestRates> findByBankId(Long bankId);

	/**
	 * Checks whether any existing interest rate slab for the given bank overlaps
	 * with the proposed CIBIL range {@code [minCibil, maxCibil]}. Two ranges
	 * overlap when the new range's lower bound is less than an existing slab's
	 * upper bound AND the new range's upper bound is greater than the existing
	 * slab's lower bound.
	 *
	 * @param bankId   the ID of the bank to check
	 * @param minCibil the lower bound (inclusive) of the proposed CIBIL range
	 * @param maxCibil the upper bound (inclusive) of the proposed CIBIL range
	 * @return {@code true} if at least one overlapping slab already exists
	 */
	@Query("""
			    SELECT COUNT(ir) > 0
			    FROM InterestRates ir
			    WHERE ir.bank.id = :bankId
			      AND :minCibil < ir.maxCibil
			      AND :maxCibil > ir.minCibil
			""")
	boolean existsOverlappingRange(@Param("bankId") Long bankId, @Param("minCibil") Integer minCibil,
			@Param("maxCibil") Integer maxCibil);

}
