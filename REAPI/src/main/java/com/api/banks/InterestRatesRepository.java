package com.api.banks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterestRatesRepository extends JpaRepository<InterestRates, Long> {

	List<InterestRates> findByBankId(Long bankId);

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
