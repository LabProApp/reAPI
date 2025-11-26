package com.api.banks;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankLoanRepository extends JpaRepository<BankLoanRepresentative, Long> {
    // You can add custom query methods here if needed, for example:
    // List<BankLoanRepresentative> findByInterestRateLessThanEqual(Double maxRate);
    // List<BankLoanRepresentative> findByMinCibilScoreGreaterThanEqual(Integer minCibil);
}
