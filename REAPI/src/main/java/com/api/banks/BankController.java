package com.api.banks;

import java.util.List;

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

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller that exposes bank and interest-rate management endpoints
 * under the {@code /api/banks} base path. Delegates all business logic to
 * {@link BankService}.
 */
@Slf4j
@RestController
@RequestMapping("/api/banks")
@Tag(name = "Bank APIs", description = "Bank and Interest Rate Operations")
public class BankController {

	private final BankService bankService;

	/**
	 * Constructs a {@code BankController} with the required service dependency.
	 *
	 * @param bankService the service that handles bank business logic
	 */
	public BankController(BankService bankService) {
		this.bankService = bankService;
	}

	// 📃 List all banks
	/**
	 * Retrieves all bank records, each enriched with its logo URL resolved from
	 * the document store.
	 *
	 * @return 200 OK with the list of all {@link BankDto} instances
	 */
	@GetMapping
	public ResponseEntity<List<BankDto>> getAllBanks() {
		log.info("GET /api/banks - Fetching all banks");
		List<BankDto> banks = bankService.getAllBanks();
		log.info("GET /api/banks - Returned {} banks", banks.size());
		return ResponseEntity.ok(banks);
	}

	// ➕ Add a new bank
	/**
	 * Creates a new bank record from the supplied request body.
	 *
	 * @param dto the validated {@link BankDto} containing the new bank's details
	 * @return 200 OK with the persisted {@link BankDto} including its generated ID
	 */
	@PostMapping
	public ResponseEntity<BankDto> addBank(@Valid @RequestBody BankDto dto) {
		log.info("POST /api/banks - Adding bank: {}", dto.getBankName());
		BankDto saved = bankService.addBank(dto);
		log.info("POST /api/banks - Bank created with id={}", saved.getId());
		return ResponseEntity.ok(saved);
	}

	// ✏️ Update an existing bank
	/**
	 * Updates an existing bank identified by its path-variable ID. The ID from the
	 * path is injected into the DTO before delegating to the service.
	 *
	 * @param id  the ID of the bank to update
	 * @param dto the validated {@link BankDto} containing updated field values
	 * @return 200 OK with the updated {@link BankDto}, or an appropriate error response
	 */
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBank(@PathVariable Long id, @Valid @RequestBody BankDto dto) {
		log.info("PUT /api/banks/{} - Updating bank", id);
		dto.setId(id);
		ResponseEntity<?> response = bankService.updateBank(dto);
		log.info("PUT /api/banks/{} - Update completed, status={}", id, response.getStatusCode());
		return response;
	}

	// ⚖️ Compare banks by interest rate
	/**
	 * Returns all banks sorted in ascending order by their base interest rate,
	 * providing a side-by-side comparison view.
	 *
	 * @return 200 OK with banks sorted by ascending interest rate
	 */
	@GetMapping("/compare")
	public ResponseEntity<List<BankDto>> compareBanks() {
		log.info("GET /api/banks/compare - Comparing banks by interest rate");
		List<BankDto> sorted = bankService.getAllBanks().stream()
				.sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate())).toList();
		log.info("GET /api/banks/compare - Returned {} banks sorted by rate", sorted.size());
		return ResponseEntity.ok(sorted);
	}



	// ➕ Add interest rates for a bank
	/**
	 * Adds a new CIBIL-tiered interest rate slab for the specified bank.
	 * The {@code bankId} from the path is injected into the DTO before processing.
	 *
	 * @param bankId the ID of the bank to which the interest rate slab belongs
	 * @param dto    the validated {@link InterestRatesDto} with CIBIL range and rate
	 * @return 200 OK with the persisted {@link InterestRatesDto} including its generated ID
	 */
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

	// ✏️ Update interest rate
	/**
	 * Updates an existing CIBIL-tiered interest rate slab for the specified bank.
	 * The {@code bankId} from the path is injected into the DTO before processing.
	 *
	 * @param bankId the ID of the owning bank
	 * @param dto    the validated {@link InterestRatesDto} containing updated values
	 * @return 200 OK with a success message, or an error response if the slab is not found
	 */
	@PutMapping("/{bankId}/interest-rates")
	public ResponseEntity<?> updateInterestRates(@PathVariable Long bankId, @Valid @RequestBody InterestRatesDto dto) {
		log.info("PUT /api/banks/{}/interest-rates - Updating interest rate id={}", bankId, dto.getId());
		dto.setBankId(bankId);
		ResponseEntity<?> response = bankService.updateInterestRates(dto);
		log.info("PUT /api/banks/{}/interest-rates - Update completed, status={}", bankId, response.getStatusCode());
		return response;
	}

	// 📃 List interest rates for a bank
	/**
	 * Lists all CIBIL-tiered interest rate slabs for the specified bank.
	 *
	 * @param bankId the ID of the bank whose rate slabs are to be retrieved
	 * @return 200 OK with the list of {@link InterestRatesDto} for the bank
	 */
	@GetMapping("/{bankId}/interest-rates")
	public ResponseEntity<List<InterestRatesDto>> getInterestRatesByBank(@PathVariable Long bankId) {
		log.info("GET /api/banks/{}/interest-rates - Fetching interest rates", bankId);
		List<InterestRatesDto> rates = bankService.getInterestRatesByBank(bankId);
		log.info("GET /api/banks/{}/interest-rates - Returned {} rates", bankId, rates.size());
		return ResponseEntity.ok(rates);
	}

	// 📃 Get all banks with their interest rates
	/**
	 * Retrieves all banks together with their full CIBIL-tiered interest rate slabs
	 * in a single response, optimised to avoid N+1 queries.
	 *
	 * @return 200 OK with the list of {@link BankDto} instances each containing
	 *         their associated interest rate slabs
	 */
	@GetMapping("/with-interest-rates")
	public ResponseEntity<List<BankDto>> getAllBanksWithInterestRates() {
		log.info("GET /api/banks/with-interest-rates - Fetching all banks with interest rates");
		List<BankDto> banks = bankService.getAllBanksWithInterestRates();
		log.info("GET /api/banks/with-interest-rates - Returned {} banks", banks.size());
	    return ResponseEntity.ok(banks);
	}


	// 🔎 Advanced filtering
	/**
	 * Filters banks using any combination of the supplied query parameters.
	 * All parameters are optional; omitted parameters are ignored in the query.
	 *
	 * @param maxRate    the upper bound for the base interest rate
	 * @param minCibil   the minimum CIBIL score the bank must accept
	 * @param maxTenure  the upper bound for loan tenure in years
	 * @param minIncome  the minimum required monthly income
	 * @param city       the city to filter by (case-insensitive)
	 * @param state      the state to filter by (case-insensitive)
	 * @param bank       a partial bank name to search for (case-insensitive)
	 * @param postalcode the postal code to filter by (case-insensitive)
	 * @return 200 OK with the list of matching {@link BankDto} instances
	 */
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
