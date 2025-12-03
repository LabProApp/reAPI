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

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/banks")
@Tag(name = "Bank APIs", description = "Bank and Interest Rate Operations")
public class BankController {

	private final BankService bankService;

	public BankController(BankService bankService) {
		this.bankService = bankService;
	}

	// 📃 List all banks
	@GetMapping
	public ResponseEntity<List<BankDto>> getAllBanks() {
		return ResponseEntity.ok(bankService.getAllBanks());
	}

	// ➕ Add a new bank
	@PostMapping
	public ResponseEntity<BankDto> addBank(@RequestBody BankDto dto) {
		return ResponseEntity.ok(bankService.addBank(dto));
	}

	// ✏️ Update an existing bank
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBank(@PathVariable Long id, @RequestBody BankDto dto) {
		dto.setId(id);
		return bankService.updateBank(dto);
	}

	// ⚖️ Compare banks by interest rate
	@GetMapping("/compare")
	public ResponseEntity<List<BankDto>> compareBanks() {
		return ResponseEntity.ok(bankService.getAllBanks().stream()
				.sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate())).toList());
	}

	

	// ➕ Add interest rates for a bank
	@PostMapping("/{bankId}/interest-rates")
	public ResponseEntity<InterestRatesDto> addInterestRate(@PathVariable Long bankId,
			@RequestBody InterestRatesDto dto) {
		dto.setBankId(bankId);
		return ResponseEntity.ok(bankService.addInterestRates(dto));
	}

	// ✏️ Update interest rate
	@PutMapping("/interest-rates/{bankId}")
	public ResponseEntity<?> updateInterestRates(@PathVariable Long bankId, @RequestBody InterestRatesDto dto) {
		dto.setBankId(bankId);
		return bankService.updateInterestRates(dto);
	}

	// 📃 List interest rates for a bank
	@GetMapping("/{bankId}/interest-rates")
	public ResponseEntity<List<InterestRates>> listInterestRates(@PathVariable Long bankId) {
		return ResponseEntity.ok(bankService.getRatesByBank(bankId));
	}

	// 🔎 Advanced filtering
	@GetMapping("/filter")
	public ResponseEntity<List<BankDto>> advancedFilter(@RequestParam(required = false) Double maxRate,
			@RequestParam(required = false) Integer minCibil, @RequestParam(required = false) Integer maxTenure,
			@RequestParam(required = false) Double minIncome, @RequestParam(required = false) String city,
			@RequestParam(required = false) String state, @RequestParam(required = false) String bank,
			@RequestParam(required = false) String postalcode) {
		return ResponseEntity
				.ok(bankService.advancedFilter(maxRate, minCibil, maxTenure, minIncome, city, state, bank, postalcode));
	}
}
