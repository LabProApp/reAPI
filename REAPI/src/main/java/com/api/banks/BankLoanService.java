package com.api.banks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BankLoanService {

    private final BankLoanRepository bankLoanRepository;

    public BankLoanService(BankLoanRepository bankLoanRepository) {
        this.bankLoanRepository = bankLoanRepository;
    }

    // 🧾 Get all loans
    public List<BankLoanRepresentative> getAllLoans() {
        return bankLoanRepository.findAll();
    }

    // ➕ Add a new loan representative
    public BankLoanRepresentative addLoan(BankLoanRepresentative loan) {
        return bankLoanRepository.save(loan);
    }

  

    // 🔎 Advanced filtering (optional)
    public List<BankLoanRepresentative> advancedFilter(
            Double maxRate,
            Integer minCibil,
            Integer maxTenure,
            Double minIncome,
            String city,
            String state,
            String bank,
            String postalCode) {

        Specification<BankLoanRepresentative> spec = Specification
                .where(BankLoanSpecifications.hasMaxRate(maxRate))
                .and(BankLoanSpecifications.hasMinCibil(minCibil))
                .and(BankLoanSpecifications.hasMaxTenure(maxTenure))
                .and(BankLoanSpecifications.hasMinIncome(minIncome))
                .and(BankLoanSpecifications.hasCity(city))
                .and(BankLoanSpecifications.hasState(state))
                .and(BankLoanSpecifications.hasBank(bank))
                .and(BankLoanSpecifications.hasPostalCode(postalCode));

        return bankLoanRepository.findAll(spec);
    }

    // ✏️ Update loan representative
    public ResponseEntity<?> updateLoan(BankLoanRepresentative loan) {

        if (loan.getId() == null) {
            return ResponseEntity.badRequest().body("Loan ID is required for update");
        }

        Optional<BankLoanRepresentative> optionalLoan = bankLoanRepository.findById(loan.getId());

        if (optionalLoan.isPresent()) {
            BankLoanRepresentative existingLoan = optionalLoan.get();

            // Update String fields
            if (loan.getBankName() != null) existingLoan.setBankName(loan.getBankName());
            if (loan.getContactName() != null) existingLoan.setContactName(loan.getContactName());
            if (loan.getContactNumber() != null) existingLoan.setContactNumber(loan.getContactNumber());
            if (loan.getEmail() != null) existingLoan.setEmail(loan.getEmail());
            if (loan.getWebsiteUrl() != null) existingLoan.setWebsiteUrl(loan.getWebsiteUrl());
            if (loan.getBranchName() != null) existingLoan.setBranchName(loan.getBranchName());
            if (loan.getLocationAddress() != null) existingLoan.setLocationAddress(loan.getLocationAddress());
            if (loan.getEmploymentType() != null) existingLoan.setEmploymentType(loan.getEmploymentType());
            if (loan.getNationalityRequirement() != null) existingLoan.setNationalityRequirement(loan.getNationalityRequirement());
            if (loan.getSpecialOffers() != null) existingLoan.setSpecialOffers(loan.getSpecialOffers());
            if (loan.getRequiredDocuments() != null) existingLoan.setRequiredDocuments(loan.getRequiredDocuments());
            if (loan.getDetails() != null) existingLoan.setDetails(loan.getDetails());

            // Update numeric fields
            if (loan.getInterestRate() > 0) existingLoan.setInterestRate(loan.getInterestRate());
            if (loan.getTenureYears() > 0) existingLoan.setTenureYears(loan.getTenureYears());
            if (loan.getMinCibilScore() > 0) existingLoan.setMinCibilScore(loan.getMinCibilScore());
            if (loan.getMaxLoanAmount() > 0) existingLoan.setMaxLoanAmount(loan.getMaxLoanAmount());
            if (loan.getMinLoanAmount() > 0) existingLoan.setMinLoanAmount(loan.getMinLoanAmount());
            if (loan.getMinimumIncome() > 0) existingLoan.setMinimumIncome(loan.getMinimumIncome());
            if (loan.getMinimumAge() > 0) existingLoan.setMinimumAge(loan.getMinimumAge());
            if (loan.getMaximumAge() > 0) existingLoan.setMaximumAge(loan.getMaximumAge());
            if (loan.getProcessingFee() > 0) existingLoan.setProcessingFee(loan.getProcessingFee());

            // Update boolean fields
            existingLoan.setPrepaymentAllowed(loan.isPrepaymentAllowed());
            existingLoan.setPartPaymentAllowed(loan.isPartPaymentAllowed());
            existingLoan.setBalanceTransferAvailable(loan.isBalanceTransferAvailable());
            existingLoan.setInsuranceBundled(loan.isInsuranceBundled());

            bankLoanRepository.save(existingLoan);

            return ResponseEntity.ok(existingLoan);

        } else {
            return ResponseEntity.badRequest().body("Loan representative not found!");
        }
    }
}
