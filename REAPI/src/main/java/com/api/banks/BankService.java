package com.api.banks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BankService {

	private final BankRepository bankRepository;
	private final InterestRatesRepository interestRatesRepository;
	private final ModelMapper mapper;

	public BankService(BankRepository bankRepository, ModelMapper mapper,
			InterestRatesRepository interestRatesRepository) {
		this.bankRepository = bankRepository;
		this.interestRatesRepository = interestRatesRepository;
		this.mapper = mapper;
	}

	// 🧾 Get all loans
	// 🧾 Get all banks
	public List<BankDto> getAllBanks() {
		return bankRepository.findAll().stream().map(this::toDto) // ✅ use service's toDto method
				.collect(Collectors.toList());
	}

	// ➕ Add a new loan representative
	public BankDto addBank(BankDto bankDto) {
		// 🔥 Map DTO → Entity
		Bank entity = toEntity(bankDto);

		// 🔥 Save entity
		Bank saved = bankRepository.save(entity);

		// 🔥 Map Entity → DTO
		return toDto(saved);
	}

	// Convert Bank entity to BankDto
	public BankDto toDto(Bank bank) {
	    if (bank == null) return null;

	    BankDto dto = new BankDto();
	    
	    // 🔹 Basic Info
	    dto.setId(bank.getId());
	    dto.setBankName(bank.getBankName());
	    dto.setContactName(bank.getContactName());
	    dto.setContactNumber(bank.getContactNumber());
	    dto.setEmail(bank.getEmail());
	    dto.setBranchName(bank.getBranchName());
	    
	    // 🔹 Address Info
	    dto.setLocationAddress(bank.getLocationAddress());
	    dto.setStreet(bank.getStreet());
	    dto.setCity(bank.getCity());
	    dto.setState(bank.getState());
	    dto.setPostalCode(bank.getPostalCode());
	    dto.setCountry(bank.getCountry());
	    dto.setWebsiteUrl(bank.getWebsiteUrl());

	    // 🔹 Loan Info
	    dto.setInterestRate(bank.getInterestRate());
	    dto.setInterestType(bank.getInterestType());
	    dto.setProcessingFee(bank.getProcessingFee());
	    dto.setTenureYears(bank.getTenureYears());
	    dto.setMaxLoanAmount(bank.getMaxLoanAmount());
	    dto.setMinLoanAmount(bank.getMinLoanAmount());
	    dto.setMinCibilScore(bank.getMinCibilScore());

	    // 🔹 Eligibility
	    dto.setMinimumIncome(bank.getMinimumIncome());
	    dto.setEmploymentType(bank.getEmploymentType());
	    dto.setMinimumAge(bank.getMinimumAge());
	    dto.setMaximumAge(bank.getMaximumAge());
	    dto.setNationalityRequirement(bank.getNationalityRequirement());

	    // 🔹 Additional Features
	    dto.setPrepaymentAllowed(bank.isPrepaymentAllowed());
	    dto.setPartPaymentAllowed(bank.isPartPaymentAllowed());
	    dto.setBalanceTransferAvailable(bank.isBalanceTransferAvailable());
	    dto.setInsuranceBundled(bank.isInsuranceBundled());
	    dto.setSpecialOffers(bank.getSpecialOffers());
	    dto.setRequiredDocuments(bank.getRequiredDocuments());
	    dto.setDetails(bank.getDetails());

	    // 🔹 Nested InterestRates collection (manual mapping)
	    if (bank.getInterestRates() != null) {
	        dto.setInterestRates(
	            bank.getInterestRates().stream()
	                .map(ir -> {
	                    InterestRatesDto irDto = new InterestRatesDto();
	                    irDto.setId(ir.getId());
	                    irDto.setMinCibil(ir.getMinCibil());
	                    irDto.setMaxCibil(ir.getMaxCibil());
	                    irDto.setInterestRate(ir.getInterestRate());
	                    return irDto;
	                })
	                .collect(Collectors.toList())
	        );
	    }

	    // 🔹 Metadata
	    dto.setCreatedAt(bank.getCreatedAt());
	    dto.setUpdatedAt(bank.getUpdatedAt());

	    return dto;
	}


	// Convert BankDto to Bank entity
	public Bank toEntity(BankDto dto) {
	    if (dto == null) return null;

	    Bank bank = new Bank();

	    // 🔹 Basic Info
	    bank.setId(dto.getId());
	    bank.setBankName(dto.getBankName());
	    bank.setContactName(dto.getContactName());
	    bank.setContactNumber(dto.getContactNumber());
	    bank.setEmail(dto.getEmail());
	    bank.setBranchName(dto.getBranchName());

	    // 🔹 Address Info
	    bank.setLocationAddress(dto.getLocationAddress());
	    bank.setStreet(dto.getStreet());
	    bank.setCity(dto.getCity());
	    bank.setState(dto.getState());
	    bank.setPostalCode(dto.getPostalCode());
	    bank.setCountry(dto.getCountry());
	    bank.setWebsiteUrl(dto.getWebsiteUrl());

	    // 🔹 Loan Info
	    bank.setInterestRate(dto.getInterestRate());
	    bank.setInterestType(dto.getInterestType());
	    bank.setProcessingFee(dto.getProcessingFee());
	    bank.setTenureYears(dto.getTenureYears());
	    bank.setMaxLoanAmount(dto.getMaxLoanAmount());
	    bank.setMinLoanAmount(dto.getMinLoanAmount());
	    bank.setMinCibilScore(dto.getMinCibilScore());

	    // 🔹 Eligibility
	    bank.setMinimumIncome(dto.getMinimumIncome());
	    bank.setEmploymentType(dto.getEmploymentType());
	    bank.setMinimumAge(dto.getMinimumAge());
	    bank.setMaximumAge(dto.getMaximumAge());
	    bank.setNationalityRequirement(dto.getNationalityRequirement());

	    // 🔹 Additional Features
	    bank.setPrepaymentAllowed(dto.isPrepaymentAllowed());
	    bank.setPartPaymentAllowed(dto.isPartPaymentAllowed());
	    bank.setBalanceTransferAvailable(dto.isBalanceTransferAvailable());
	    bank.setInsuranceBundled(dto.isInsuranceBundled());
	    bank.setSpecialOffers(dto.getSpecialOffers());
	    bank.setRequiredDocuments(dto.getRequiredDocuments());
	    bank.setDetails(dto.getDetails());

	    // 🔹 Nested InterestRates collection (manual mapping)
	    if (dto.getInterestRates() != null) {
	        List<InterestRates> rates = dto.getInterestRates().stream()
	            .map(irDto -> {
	                InterestRates ir = new InterestRates();
	                ir.setId(irDto.getId());
	                ir.setMinCibil(irDto.getMinCibil());
	                ir.setMaxCibil(irDto.getMaxCibil());
	                ir.setInterestRate(irDto.getInterestRate());
	                ir.setBank(bank); // important: set the parent bank
	                return ir;
	            })
	            .collect(Collectors.toList());
	        bank.setInterestRates(rates);
	    }

	    // 🔹 Metadata (optional, usually handled by @PrePersist/@PreUpdate)
	    bank.setCreatedAt(dto.getCreatedAt());
	    bank.setUpdatedAt(dto.getUpdatedAt());

	    return bank;
	}


	public InterestRatesDto addInterestRates(InterestRatesDto dto) {

		if (dto.getMinCibil() == null || dto.getMaxCibil() == null) {
			throw new IllegalArgumentException("Min and Max CIBIL cannot be null");
		}

		if (dto.getMinCibil() > dto.getMaxCibil()) {
			throw new IllegalArgumentException("Min CIBIL cannot be greater than Max CIBIL");
		}

		Bank bank = bankRepository.findById(dto.getBankId()).orElseThrow(() -> new RuntimeException("Bank not found"));

		boolean overlapExists = interestRatesRepository.existsOverlappingRange(dto.getBankId(), dto.getMinCibil(),
				dto.getMaxCibil());

		if (overlapExists) {
			throw new RuntimeException("CIBIL range overlaps with an existing interest rate range");
		}

		InterestRates rate = new InterestRates();
		rate.setBank(bank);
		rate.setMinCibil(dto.getMinCibil());
		rate.setMaxCibil(dto.getMaxCibil());
		rate.setInterestRate(dto.getInterestRate());

		rate = interestRatesRepository.save(rate);
		dto.setId(rate.getId());

		return dto;
	}

	// ✏️ UPDATE
	public ResponseEntity<?> updateInterestRates(InterestRatesDto dto) {
		InterestRates rate = interestRatesRepository.findById(dto.getId())
				.orElseThrow(() -> new RuntimeException("CIBIL rate not found"));

		rate.setMinCibil(dto.getMinCibil());
		rate.setMaxCibil(dto.getMaxCibil());
		rate.setInterestRate(dto.getInterestRate());

		interestRatesRepository.save(rate);
		return ResponseEntity.ok("CIBIL Rate updated successfully");
	}

	// 📃 LIST BY BANK ID
	/**
	 * List comparison of all loans showing interest rate, processing fee, min CIBIL
	 */
	public List<InterestRatesDto> getInterestRatesByBank(Long bankId) {

		// Validate bank exists
		if (!bankRepository.existsById(bankId)) {
			throw new RuntimeException("Bank not found with id: " + bankId);
		}

		return interestRatesRepository.findByBankId(bankId).stream().map(this::toInterestDto).toList();
	}

	public List<BankDto> getAllBanksWithInterestRates() {

		List<Bank> banks = bankRepository.findAll();

		// Fetch all interest rates in one query
		List<InterestRates> rates = interestRatesRepository.findAll();

		// Group interest rates by bankId
		Map<Long, List<InterestRatesDto>> rateMap = rates.stream().map(this::toInterestDto)
				.collect(Collectors.groupingBy(r -> r.getBankId()));

		// Map banks → BankDto
		return banks.stream().map(bank -> {
			BankDto dto = new BankDto();
			dto.setId(bank.getId());
			dto.setBankName(bank.getBankName());
			dto.setBranchName(bank.getBranchName());
			dto.setCity(bank.getCity());
			dto.setState(bank.getState());
			dto.setMinLoanAmount(bank.getMinLoanAmount());
			dto.setMaxLoanAmount(bank.getMaxLoanAmount());
			dto.setMinCibilScore(bank.getMinCibilScore());

			dto.setInterestRates(rateMap.getOrDefault(bank.getId(), List.of()));

			return dto;
		}).toList();
	}

	private InterestRatesDto toInterestDto(InterestRates rate) {
		InterestRatesDto dto = new InterestRatesDto();
		dto.setId(rate.getId());
		dto.setMinCibil(rate.getMinCibil());
		dto.setMaxCibil(rate.getMaxCibil());
		dto.setInterestRate(rate.getInterestRate());
		dto.setBankId(rate.getBank().getId());
		return dto;
	}

	public List<LoanComparisonDto> listComparison() {

		return bankRepository.findAll().stream()
				.sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate()))
				.map(entity -> mapper.map(entity, LoanComparisonDto.class)).collect(Collectors.toList());
	}

	// ✏️ Update loan representative
	public ResponseEntity<?> updateBank(BankDto requestDto) {

		if (requestDto.getId() == null) {
			return ResponseEntity.badRequest().body("Bank ID is required for update");
		}

		Bank existing = bankRepository.findById(requestDto.getId())
				.orElseThrow(() -> new RuntimeException("Bank not found"));
		// 🔥 ModelMapper updates ONLY non-null fields because skipNullEnabled = true

		existing = toEntity(requestDto);
		Bank updated = bankRepository.save(existing);
		BankDto responseDto = toDto(updated);

		return ResponseEntity.ok(responseDto);
	}

	// 🔎 Advanced filtering
	public List<BankDto> advancedFilter(Double maxRate, Integer minCibil, Integer maxTenure, Double minIncome,
			String city, String state, String bank, String postalCode) {

		Specification<Bank> spec = Specification.where(BankSpecifications.hasMaxRate(maxRate))
				.and(BankSpecifications.hasMinCibil(minCibil)).and(BankSpecifications.hasMaxTenure(maxTenure))
				.and(BankSpecifications.hasMinIncome(minIncome)).and(BankSpecifications.hasCity(city))
				.and(BankSpecifications.hasState(state)).and(BankSpecifications.hasBank(bank))
				.and(BankSpecifications.hasPostalCode(postalCode));

		return bankRepository.findAll(spec).stream().map(this::toDto) // ✅ use toDto()
				.toList();

	}

}
