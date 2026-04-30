package com.api.banks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/banks")
@Tag(name = "Bank APIs", description = "Bank and Interest Rate Operations")
public class BankController {

	private static final Logger log = LoggerFactory.getLogger(BankController.class);

	private final BankService bankService;

	public BankController(BankService bankService) {
		this.bankService = bankService;
	}

	@Operation(summary = "Get all banks")
	@GetMapping
	public ResponseEntity<List<BankDto>> getAllBanks() {
		log.info("GET /api/banks - Fetching all banks");
		List<BankDto> banks = bankService.getAllBanks();
		log.info("GET /api/banks - Returned {} banks", banks.size());
		return ResponseEntity.ok(banks);
	}

	@Operation(summary = "Add a new bank")
	@PostMapping
	public ResponseEntity<BankDto> addBank(@Valid @RequestBody BankDto dto) {
		log.info("POST /api/banks - Adding bank: {}", dto.getBankName());
		BankDto saved = bankService.addBank(dto);
		log.info("POST /api/banks - Bank created with id={}", saved.getId());
		return ResponseEntity.ok(saved);
	}

	@Operation(summary = "Update an existing bank")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBank(@PathVariable Long id, @Valid @RequestBody BankDto dto) {
		log.info("PUT /api/banks/{} - Updating bank", id);
		dto.setId(id);
		ResponseEntity<?> response = bankService.updateBank(dto);
		log.info("PUT /api/banks/{} - Update completed, status={}", id, response.getStatusCode());
		return response;
	}

	@Operation(summary = "Compare banks sorted by interest rate")
	@GetMapping("/compare")
	public ResponseEntity<List<BankDto>> compareBanks() {
		log.info("GET /api/banks/compare - Comparing banks by interest rate");
		List<BankDto> sorted = bankService.getAllBanks().stream()
				.sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate())).toList();
		log.info("GET /api/banks/compare - Returned {} banks sorted by rate", sorted.size());
		return ResponseEntity.ok(sorted);
	}

	@Operation(summary = "Add interest rate slab for a bank")
	@PostMapping("/{bankId}/interest-rates")
	public ResponseEntity<InterestRatesDto> addInterestRate(@PathVariable Long bankId,
			@Valid @RequestBody InterestRatesDto dto) {
		log.info("POST /api/banks/{}/interest-rates - Adding interest rate range [{}-{}]",
				bankId, dto.getMinCibil(), dto.getMaxCibil());
		dto.setBankId(bankId);
		InterestRatesDto saved = bankService.addInterestRates(dto);
		log.info("POST /api/banks/{}/interest-rates - Interest rate added with id={}", bankId, saved.getId());
		return ResponseEntity.ok(saved);
	}

	@Operation(summary = "Update interest rate slab for a bank")
	@PutMapping("/{bankId}/interest-rates")
	public ResponseEntity<?> updateInterestRates(@PathVariable Long bankId, @Valid @RequestBody InterestRatesDto dto) {
		log.info("PUT /api/banks/{}/interest-rates - Updating interest rate id={}", bankId, dto.getId());
		dto.setBankId(bankId);
		ResponseEntity<?> response = bankService.updateInterestRates(dto);
		log.info("PUT /api/banks/{}/interest-rates - Update completed, status={}", bankId, response.getStatusCode());
		return response;
	}

	@Operation(summary = "Get all interest rates for a bank")
	@GetMapping("/{bankId}/interest-rates")
	public ResponseEntity<List<InterestRatesDto>> getInterestRatesByBank(@PathVariable Long bankId) {
		log.info("GET /api/banks/{}/interest-rates - Fetching interest rates", bankId);
		List<InterestRatesDto> rates = bankService.getInterestRatesByBank(bankId);
		log.info("GET /api/banks/{}/interest-rates - Returned {} rates", bankId, rates.size());
		return ResponseEntity.ok(rates);
	}

	@Operation(summary = "Get all banks with their interest rates")
	@GetMapping("/with-interest-rates")
	public ResponseEntity<List<BankDto>> getAllBanksWithInterestRates() {
		log.info("GET /api/banks/with-interest-rates - Fetching all banks with interest rates");
		List<BankDto> banks = bankService.getAllBanksWithInterestRates();
		log.info("GET /api/banks/with-interest-rates - Returned {} banks", banks.size());
	    return ResponseEntity.ok(banks);
	}

	@Operation(summary = "Filter banks by multiple criteria")
	@GetMapping("/filter")
	public ResponseEntity<List<BankDto>> advancedFilter(@RequestParam(required = false) Double maxRate,
			@RequestParam(required = false) Integer minCibil, @RequestParam(required = false) Integer maxTenure,
			@RequestParam(required = false) Double minIncome, @RequestParam(required = false) String city,
			@RequestParam(required = false) String state, @RequestParam(required = false) String bank,
			@RequestParam(required = false) String postalcode) {
		log.info("GET /api/banks/filter - Filtering banks [maxRate={}, minCibil={}, city={}, state={}]",
				maxRate, minCibil, city, state);
		List<BankDto> results = bankService.advancedFilter(maxRate, minCibil, maxTenure, minIncome, city, state, bank, postalcode);
		log.info("GET /api/banks/filter - Filter returned {} results", results.size());
		return ResponseEntity.ok(results);
	}
}
