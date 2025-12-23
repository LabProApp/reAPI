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
	public List<BankDto> getAllBanks() {
		return bankRepository.findAll().stream().map(bank -> mapper.map(bank, BankDto.class))
				.collect(Collectors.toList());
	}

	// ➕ Add a new loan representative
	public BankDto addBank(BankDto bankDto) {
		// 🔥 Map DTO → Entity
		Bank entity = mapper.map(bankDto, Bank.class);

		// 🔥 Save entity
		Bank saved = bankRepository.save(entity);

		// 🔥 Map Entity → DTO
		return mapper.map(saved, BankDto.class);
	}

	public InterestRatesDto addInterestRates(InterestRatesDto dto) {

	    if (dto.getMinCibil() == null || dto.getMaxCibil() == null) {
	        throw new IllegalArgumentException("Min and Max CIBIL cannot be null");
	    }

	    if (dto.getMinCibil() > dto.getMaxCibil()) {
	        throw new IllegalArgumentException("Min CIBIL cannot be greater than Max CIBIL");
	    }

	    Bank bank = bankRepository.findById(dto.getBankId())
	            .orElseThrow(() -> new RuntimeException("Bank not found"));

	    boolean overlapExists = interestRatesRepository.existsOverlappingRange(
	            dto.getBankId(),
	            dto.getMinCibil(),
	            dto.getMaxCibil()
	    );

	    if (overlapExists) {
	        throw new RuntimeException(
	            "CIBIL range overlaps with an existing interest rate range"
	        );
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
	        Map<Long, List<InterestRatesDto>> rateMap =
	                rates.stream()
	                     .map(this::toInterestDto)
	                     .collect(Collectors.groupingBy(
	                             r -> r.getBankId()
	                     ));

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

	            dto.setInterestRates(
	                    rateMap.getOrDefault(bank.getId(), List.of())
	            );

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
	public ResponseEntity<?> updateBank(BankDto dto) {

		if (dto.getId() == null) {
			return ResponseEntity.badRequest().body("Bank ID is required for update");
		}

		Bank existing = bankRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Bank not found"));
		// 🔥 ModelMapper updates ONLY non-null fields because skipNullEnabled = true
		mapper.map(dto, existing);
		Bank updated = bankRepository.save(existing);
		BankDto responseDto = mapper.map(updated, BankDto.class);

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

		return bankRepository.findAll(spec).stream().map(entity -> mapper.map(entity, BankDto.class)) // 🔥 replaced
																										// convertToDto()
				.toList(); // cleaner in Java 16+
	}

}
