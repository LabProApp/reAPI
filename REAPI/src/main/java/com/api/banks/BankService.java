package com.api.banks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    private final BankRepository bankRepository;
    private final ModelMapper mapper;

    public BankService(BankRepository bankRepository,ModelMapper mapper) {
        this.bankRepository = bankRepository;
        this.mapper = mapper;
    }

    // 🧾 Get all loans
    public List<BankDto> getAllBanks() {
        return bankRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ➕ Add a new loan representative
    public BankDto addBank(BankDto BankDto) {
        Bank entity = convertToEntity(BankDto);
        Bank saved = bankRepository.save(entity);
        return convertToDto(saved);
    }
    /**
     * List comparison of all loans showing interest rate, processing fee, min CIBIL
     */
    public List<LoanComparisonDto> listComparison() {

        return bankRepository.findAll().stream()
                .sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate()))
                .map(entity -> mapper.map(entity, LoanComparisonDto.class))
                .collect(Collectors.toList());
    }

    // ✏️ Update loan representative
    public ResponseEntity<?> updateBank(BankDto loanDto) {
        if (loanDto.getId() == null) {
            return ResponseEntity.badRequest().body("Loan ID is required for update");
        }

        Optional<Bank> optionalLoan = bankRepository.findById(loanDto.getId());

        if (optionalLoan.isPresent()) {
            Bank existingLoan = optionalLoan.get();

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

            Bank updated = bankRepository.save(existingLoan);
            return ResponseEntity.ok(convertToDto(updated));

        } else {
            return ResponseEntity.badRequest().body("Loan representative not found!");
        }
    }

    // 🔎 Advanced filtering
    public List<BankDto> advancedFilter(
            Double maxRate,
            Integer minCibil,
            Integer maxTenure,
            Double minIncome,
            String city,
            String state,
            String bank,
            String postalCode) {

        Specification<Bank> spec = Specification
                .where(BankSpecifications.hasMaxRate(maxRate))
                .and(BankSpecifications.hasMinCibil(minCibil))
                .and(BankSpecifications.hasMaxTenure(maxTenure))
                .and(BankSpecifications.hasMinIncome(minIncome))
                .and(BankSpecifications.hasCity(city))
                .and(BankSpecifications.hasState(state))
                .and(BankSpecifications.hasBank(bank))
                .and(BankSpecifications.hasPostalCode(postalCode));

        return bankRepository.findAll(spec)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // -----------------------------
    // Conversion helpers
    // -----------------------------
    private BankDto convertToDto(Bank entity) {
        BankDto dto = new BankDto();
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

    private Bank convertToEntity(BankDto dto) {
        Bank entity = new Bank();
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
