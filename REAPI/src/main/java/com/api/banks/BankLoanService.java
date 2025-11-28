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
    public List<BankLoanRepresentativeDto> getAllLoans() {
        return bankLoanRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ➕ Add a new loan representative
    public BankLoanRepresentativeDto addLoan(BankLoanRepresentativeDto loanDto) {
        BankLoanRepresentative entity = convertToEntity(loanDto);
        BankLoanRepresentative saved = bankLoanRepository.save(entity);
        return convertToDto(saved);
    }

    // ✏️ Update loan representative
    public ResponseEntity<?> updateLoan(BankLoanRepresentativeDto loanDto) {
        if (loanDto.getId() == null) {
            return ResponseEntity.badRequest().body("Loan ID is required for update");
        }

        Optional<BankLoanRepresentative> optionalLoan = bankLoanRepository.findById(loanDto.getId());

        if (optionalLoan.isPresent()) {
            BankLoanRepresentative existingLoan = optionalLoan.get();

            // Update fields if present in DTO
            if (loanDto.getBankName() != null) existingLoan.setBankName(loanDto.getBankName());
            if (loanDto.getContactName() != null) existingLoan.setContactName(loanDto.getContactName());
            if (loanDto.getContactNumber() != null) existingLoan.setContactNumber(loanDto.getContactNumber());
            if (loanDto.getEmail() != null) existingLoan.setEmail(loanDto.getEmail());
            if (loanDto.getWebsiteUrl() != null) existingLoan.setWebsiteUrl(loanDto.getWebsiteUrl());
            if (loanDto.getBranchName() != null) existingLoan.setBranchName(loanDto.getBranchName());
            if (loanDto.getLocationAddress() != null) existingLoan.setLocationAddress(loanDto.getLocationAddress());
            if (loanDto.getEmploymentType() != null) existingLoan.setEmploymentType(loanDto.getEmploymentType());
            if (loanDto.getNationalityRequirement() != null) existingLoan.setNationalityRequirement(loanDto.getNationalityRequirement());
            if (loanDto.getSpecialOffers() != null) existingLoan.setSpecialOffers(loanDto.getSpecialOffers());
            if (loanDto.getRequiredDocuments() != null) existingLoan.setRequiredDocuments(loanDto.getRequiredDocuments());
            if (loanDto.getDetails() != null) existingLoan.setDetails(loanDto.getDetails());

            // Update numeric fields
            if (loanDto.getInterestRate() > 0) existingLoan.setInterestRate(loanDto.getInterestRate());
            if (loanDto.getTenureYears() > 0) existingLoan.setTenureYears(loanDto.getTenureYears());
            if (loanDto.getMinCibilScore() > 0) existingLoan.setMinCibilScore(loanDto.getMinCibilScore());
            if (loanDto.getMaxLoanAmount() > 0) existingLoan.setMaxLoanAmount(loanDto.getMaxLoanAmount());
            if (loanDto.getMinLoanAmount() > 0) existingLoan.setMinLoanAmount(loanDto.getMinLoanAmount());
            if (loanDto.getMinimumIncome() > 0) existingLoan.setMinimumIncome(loanDto.getMinimumIncome());
            if (loanDto.getMinimumAge() > 0) existingLoan.setMinimumAge(loanDto.getMinimumAge());
            if (loanDto.getMaximumAge() > 0) existingLoan.setMaximumAge(loanDto.getMaximumAge());
            if (loanDto.getProcessingFee() > 0) existingLoan.setProcessingFee(loanDto.getProcessingFee());

            // Update boolean fields
            existingLoan.setPrepaymentAllowed(loanDto.isPrepaymentAllowed());
            existingLoan.setPartPaymentAllowed(loanDto.isPartPaymentAllowed());
            existingLoan.setBalanceTransferAvailable(loanDto.isBalanceTransferAvailable());
            existingLoan.setInsuranceBundled(loanDto.isInsuranceBundled());

            BankLoanRepresentative updated = bankLoanRepository.save(existingLoan);
            return ResponseEntity.ok(convertToDto(updated));

        } else {
            return ResponseEntity.badRequest().body("Loan representative not found!");
        }
    }

    // 🔎 Advanced filtering
    public List<BankLoanRepresentativeDto> advancedFilter(
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

        return bankLoanRepository.findAll(spec)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // -----------------------------
    // Conversion helpers
    // -----------------------------
    private BankLoanRepresentativeDto convertToDto(BankLoanRepresentative entity) {
        BankLoanRepresentativeDto dto = new BankLoanRepresentativeDto();
        dto.setId(entity.getId());
        dto.setBankName(entity.getBankName());
        dto.setContactName(entity.getContactName());
        dto.setContactNumber(entity.getContactNumber());
        dto.setEmail(entity.getEmail());
        dto.setBranchName(entity.getBranchName());
        dto.setLocationAddress(entity.getLocationAddress());
        dto.setStreet(entity.getStreet());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostalCode(entity.getPostalCode());
        dto.setCountry(entity.getCountry());
        dto.setWebsiteUrl(entity.getWebsiteUrl());
        dto.setInterestRate(entity.getInterestRate());
        dto.setInterestType(entity.getInterestType());
        dto.setProcessingFee(entity.getProcessingFee());
        dto.setTenureYears(entity.getTenureYears());
        dto.setMaxLoanAmount(entity.getMaxLoanAmount());
        dto.setMinLoanAmount(entity.getMinLoanAmount());
        dto.setMinCibilScore(entity.getMinCibilScore());
        dto.setMinimumIncome(entity.getMinimumIncome());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setMinimumAge(entity.getMinimumAge());
        dto.setMaximumAge(entity.getMaximumAge());
        dto.setNationalityRequirement(entity.getNationalityRequirement());
        dto.setPrepaymentAllowed(entity.isPrepaymentAllowed());
        dto.setPartPaymentAllowed(entity.isPartPaymentAllowed());
        dto.setBalanceTransferAvailable(entity.isBalanceTransferAvailable());
        dto.setInsuranceBundled(entity.isInsuranceBundled());
        dto.setSpecialOffers(entity.getSpecialOffers());
        dto.setRequiredDocuments(entity.getRequiredDocuments());
        dto.setDetails(entity.getDetails());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private BankLoanRepresentative convertToEntity(BankLoanRepresentativeDto dto) {
        BankLoanRepresentative entity = new BankLoanRepresentative();
        entity.setId(dto.getId());
        entity.setBankName(dto.getBankName());
        entity.setContactName(dto.getContactName());
        entity.setContactNumber(dto.getContactNumber());
        entity.setEmail(dto.getEmail());
        entity.setBranchName(dto.getBranchName());
        entity.setLocationAddress(dto.getLocationAddress());
        entity.setStreet(dto.getStreet());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        entity.setWebsiteUrl(dto.getWebsiteUrl());
        entity.setInterestRate(dto.getInterestRate());
        entity.setInterestType(dto.getInterestType());
        entity.setProcessingFee(dto.getProcessingFee());
        entity.setTenureYears(dto.getTenureYears());
        entity.setMaxLoanAmount(dto.getMaxLoanAmount());
        entity.setMinLoanAmount(dto.getMinLoanAmount());
        entity.setMinCibilScore(dto.getMinCibilScore());
        entity.setMinimumIncome(dto.getMinimumIncome());
        entity.setEmploymentType(dto.getEmploymentType());
        entity.setMinimumAge(dto.getMinimumAge());
        entity.setMaximumAge(dto.getMaximumAge());
        entity.setNationalityRequirement(dto.getNationalityRequirement());
        entity.setPrepaymentAllowed(dto.isPrepaymentAllowed());
        entity.setPartPaymentAllowed(dto.isPartPaymentAllowed());
        entity.setBalanceTransferAvailable(dto.isBalanceTransferAvailable());
        entity.setInsuranceBundled(dto.isInsuranceBundled());
        entity.setSpecialOffers(dto.getSpecialOffers());
        entity.setRequiredDocuments(dto.getRequiredDocuments());
        entity.setDetails(dto.getDetails());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
