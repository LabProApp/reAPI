package com.api.banks;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankLoanRepository extends JpaRepository<BankLoan, Long> {
}
