package com.api.loans;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankLoanInquiryRepository extends JpaRepository<BankLoanInquiry, Long> {
}
