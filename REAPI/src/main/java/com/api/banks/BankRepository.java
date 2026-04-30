package com.api.banks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository
		extends JpaRepository<Bank, Long>, JpaSpecificationExecutor<Bank> {
}
