package com.api.banks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Bank} entities. Extends
 * {@link JpaSpecificationExecutor} to support dynamic, criteria-based
 * filtering via {@link BankSpecifications}.
 */
@Repository
public interface BankRepository
		extends JpaRepository<Bank, Long>, JpaSpecificationExecutor<Bank> {
}
