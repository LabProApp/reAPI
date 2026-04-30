package com.api.banks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.documents.DocumentminDto;
import com.api.documents.DocumentsService;

@Service
public class BankService {

	private static final Logger log = LoggerFactory.getLogger(BankService.class);

	private final BankRepository bankRepository;
	private final InterestRatesRepository interestRatesRepository;
	private final ModelMapper mapper;
	@Autowired
	private DocumentsService documentsService;

	
	public BankService(BankRepository bankRepository, ModelMapper mapper,
			InterestRatesRepository interestRatesRepository) {
		this.bankRepository = bankRepository;
		this.interestRatesRepository = interestRatesRepository;
		this.mapper = mapper;
	}

	
	@Transactional(readOnly = true)
	public List<BankDto> getAllBanks() {
		log.info("Fetching all banks");
		List<Bank> banks = bankRepository.findAll();
		List<Long> ids = banks.stream().map(Bank::getId).collect(Collectors.toList());
		Map<Long, List<DocumentminDto>> docsMap = documentsService.getminDocumentsByObjectIds("BANK", ids);
		return banks.stream().map(bank -> {
			BankDto dto = toDto(bank);
			List<DocumentminDto> bankDocs = docsMap.getOrDefault(bank.getId(), List.of());
			bankDocs.stream()
					.filter(d -> d.getCaption() != null && d.getCaption().equalsIgnoreCase("LOGO"))
					.findFirst()
					.ifPresent(logo -> dto.setBankLogoUrl(logo.getDocUrl()));
			return dto;
		}).collect(Collectors.toList());
	}

	// ➕ Add a new loan representative
	
	public BankDto addBank(BankDto bankDto) {
		log.info("Adding bank: {}", bankDto.getBankName());
		Bank entity = toEntity(bankDto);
		Bank saved = bankRepository.save(entity);
		log.info("Bank saved with id={}", saved.getId());
		return toDto(saved);
	}

	
	// Convert Bank entity to BankDto
	public BankDto toDto(Bank bank) {
		if (bank == null)
			return null;

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
			dto.setInterestRates(bank.getInterestRates().stream().map(ir -> {
				InterestRatesDto irDto = new InterestRatesDto();
				irDto.setId(ir.getId());
				irDto.setMinCibil(ir.getMinCibil());
				irDto.setMaxCibil(ir.getMaxCibil());
				irDto.setInterestRate(ir.getInterestRate());
				return irDto;
			}).collect(Collectors.toList()));
		}

		// 🔹 Metadata
		dto.setCreatedAt(bank.getCreatedAt());
		dto.setUpdatedAt(bank.getUpdatedAt());

		return dto;
	}

	
	// Convert BankDto to Bank entity
	public Bank toEntity(BankDto dto) {
		if (dto == null)
			return null;

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
			List<InterestRates> rates = dto.getInterestRates().stream().map(irDto -> {
				InterestRates ir = new InterestRates();
				ir.setId(irDto.getId());
				ir.setMinCibil(irDto.getMinCibil());
				ir.setMaxCibil(irDto.getMaxCibil());
				ir.setInterestRate(irDto.getInterestRate());
				ir.setBank(bank); // important: set the parent bank
				return ir;
			}).collect(Collectors.toList());
			bank.setInterestRates(rates);
		}

		// 🔹 Metadata (optional, usually handled by @PrePersist/@PreUpdate)
		bank.setCreatedAt(dto.getCreatedAt());
		bank.setUpdatedAt(dto.getUpdatedAt());

		return bank;
	}

	
	public InterestRatesDto addInterestRates(InterestRatesDto dto) {
		log.info("Adding interest rate for bankId={}, CIBIL range [{}-{}]",
				dto.getBankId(), dto.getMinCibil(), dto.getMaxCibil());

		if (dto.getMinCibil() == null || dto.getMaxCibil() == null) {
			log.warn("addInterestRates - Rejected: Min/Max CIBIL is null for bankId={}", dto.getBankId());
			throw new IllegalArgumentException("Min and Max CIBIL cannot be null");
		}

		if (dto.getMinCibil() > dto.getMaxCibil()) {
			log.warn("addInterestRates - Rejected: minCibil {} > maxCibil {} for bankId={}",
					dto.getMinCibil(), dto.getMaxCibil(), dto.getBankId());
			throw new IllegalArgumentException("Min CIBIL cannot be greater than Max CIBIL");
		}

		Bank bank = bankRepository.findById(dto.getBankId())
				.orElseThrow(() -> {
					log.error("addInterestRates - Bank not found for id={}", dto.getBankId());
					return new RuntimeException("Bank not found");
				});

		boolean overlapExists = interestRatesRepository.existsOverlappingRange(dto.getBankId(), dto.getMinCibil(),
				dto.getMaxCibil());

		if (overlapExists) {
			log.warn("addInterestRates - CIBIL range overlap detected for bankId={}, range [{}-{}]",
					dto.getBankId(), dto.getMinCibil(), dto.getMaxCibil());
			throw new RuntimeException("CIBIL range overlaps with an existing interest rate range");
		}

		InterestRates rate = new InterestRates();
		rate.setBank(bank);
		rate.setMinCibil(dto.getMinCibil());
		rate.setMaxCibil(dto.getMaxCibil());
		rate.setInterestRate(dto.getInterestRate());

		rate = interestRatesRepository.save(rate);
		dto.setId(rate.getId());
		log.info("Interest rate saved with id={} for bankId={}", rate.getId(), dto.getBankId());
		return dto;
	}

	// ✏️ UPDATE
	
	public ResponseEntity<?> updateInterestRates(InterestRatesDto dto) {
		log.info("Updating interest rate id={}", dto.getId());
		InterestRates rate = interestRatesRepository.findById(dto.getId())
				.orElseThrow(() -> {
					log.error("updateInterestRates - Rate not found for id={}", dto.getId());
					return new RuntimeException("CIBIL rate not found");
				});

		rate.setMinCibil(dto.getMinCibil());
		rate.setMaxCibil(dto.getMaxCibil());
		rate.setInterestRate(dto.getInterestRate());

		interestRatesRepository.save(rate);
		log.info("Interest rate id={} updated successfully", dto.getId());
		return ResponseEntity.ok("CIBIL Rate updated successfully");
	}

	// 📃 LIST BY BANK ID
	
	
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
		log.info("Updating bank id={}", requestDto.getId());

		if (requestDto.getId() == null) {
			log.warn("updateBank - Rejected: Bank ID is null");
			return ResponseEntity.badRequest().body("Bank ID is required for update");
		}

		Bank existing = bankRepository.findById(requestDto.getId())
				.orElseThrow(() -> {
					log.error("updateBank - Bank not found for id={}", requestDto.getId());
					return new RuntimeException("Bank not found");
				});

		existing.setBankName(requestDto.getBankName());
		existing.setContactName(requestDto.getContactName());
		existing.setContactNumber(requestDto.getContactNumber());
		existing.setEmail(requestDto.getEmail());
		existing.setBranchName(requestDto.getBranchName());
		existing.setLocationAddress(requestDto.getLocationAddress());
		existing.setStreet(requestDto.getStreet());
		existing.setCity(requestDto.getCity());
		existing.setState(requestDto.getState());
		existing.setPostalCode(requestDto.getPostalCode());
		existing.setCountry(requestDto.getCountry());
		existing.setWebsiteUrl(requestDto.getWebsiteUrl());
		existing.setInterestRate(requestDto.getInterestRate());
		existing.setInterestType(requestDto.getInterestType());
		existing.setProcessingFee(requestDto.getProcessingFee());
		existing.setTenureYears(requestDto.getTenureYears());
		existing.setMaxLoanAmount(requestDto.getMaxLoanAmount());
		existing.setMinLoanAmount(requestDto.getMinLoanAmount());
		existing.setMinCibilScore(requestDto.getMinCibilScore());
		existing.setMinimumIncome(requestDto.getMinimumIncome());
		existing.setEmploymentType(requestDto.getEmploymentType());
		existing.setMinimumAge(requestDto.getMinimumAge());
		existing.setMaximumAge(requestDto.getMaximumAge());
		existing.setNationalityRequirement(requestDto.getNationalityRequirement());
		existing.setPrepaymentAllowed(requestDto.isPrepaymentAllowed());
		existing.setPartPaymentAllowed(requestDto.isPartPaymentAllowed());
		existing.setBalanceTransferAvailable(requestDto.isBalanceTransferAvailable());
		existing.setInsuranceBundled(requestDto.isInsuranceBundled());
		existing.setSpecialOffers(requestDto.getSpecialOffers());
		existing.setRequiredDocuments(requestDto.getRequiredDocuments());
		existing.setDetails(requestDto.getDetails());
		if (requestDto.getBankLogoUrl() != null) {
			existing.setBankLogoUrl(requestDto.getBankLogoUrl());
		}

		Bank updated = bankRepository.save(existing);
		log.info("Bank id={} updated successfully", requestDto.getId());
		BankDto responseDto = toDto(updated);

		return ResponseEntity.ok(responseDto);
	}

	// 🔎 Advanced filtering
	
	@Transactional(readOnly = true)
	public List<BankDto> advancedFilter(Double maxRate, Integer minCibil, Integer maxTenure, Double minIncome,
			String city, String state, String bank, String postalCode) {
		log.info("Advanced bank filter [maxRate={}, minCibil={}, city={}, state={}]", maxRate, minCibil, city, state);

		Specification<Bank> spec = Specification.where(BankSpecifications.hasMaxRate(maxRate))
				.and(BankSpecifications.hasMinCibil(minCibil)).and(BankSpecifications.hasMaxTenure(maxTenure))
				.and(BankSpecifications.hasMinIncome(minIncome)).and(BankSpecifications.hasCity(city))
				.and(BankSpecifications.hasState(state)).and(BankSpecifications.hasBank(bank))
				.and(BankSpecifications.hasPostalCode(postalCode));

		List<BankDto> results = bankRepository.findAll(spec).stream().map(this::toDto).toList();
		log.info("Advanced bank filter returned {} results", results.size());
		return results;
	}

}
