package com.api.banks;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



public interface InterestRatesRepository extends JpaRepository<InterestRates, Long> {

	List<InterestRates> findByBankId(Long bankId);
}

