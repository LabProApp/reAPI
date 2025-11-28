package com.api.banks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/bank")
@Tag(name = "Bank Loan APIs", description = "Operations related to Bank Home Loan Representatives")
public class BankController {

	@Autowired
	private BankService bankService;

	// 🧾 List all loan representatives
	@GetMapping("/list")
	public ResponseEntity<List<BankDto>> getAllLoans() {
		List<BankDto> loans = bankService.getAllBanks();
		return ResponseEntity.ok(loans);
	}

	// ➕ Add a new loan representative
	@PostMapping("/add")
	public ResponseEntity<BankDto> addBank(@RequestBody BankDto loanDto) {
		BankDto savedLoan = bankService.addBank(loanDto);
		return ResponseEntity.ok(savedLoan);
	}

	// ✏️ Update an existing loan representative
	@PutMapping("/update")
	public ResponseEntity<?> updateBank(@RequestBody BankDto loanDto) {
		ResponseEntity<?> response = bankService.updateBank(loanDto);
		return response;
	}

	// ⚖️ Compare loan representatives by interest rate (ascending)
	@GetMapping("/compare")
	public ResponseEntity<List<BankDto>> compareLoans() {
		List<BankDto> sortedLoans = bankService.getAllBanks().stream()
				.sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate())).toList();
		return ResponseEntity.ok(sortedLoans);
	}

	// 🔎 Advanced filtering by multiple criteria
	@GetMapping("/advanced-filter")
	public ResponseEntity<List<BankDto>> advancedFilter(
			@RequestParam(required = false) Double maxRate, @RequestParam(required = false) Integer minCibil,
			@RequestParam(required = false) Integer maxTenure, @RequestParam(required = false) Double minIncome,
			@RequestParam(required = false) String city, @RequestParam(required = false) String state,
			@RequestParam(required = false) String bank, @RequestParam(required = false) String postalcode) {

		List<BankDto> filteredLoans = bankService.advancedFilter(maxRate, minCibil, maxTenure,
				minIncome, city, state, bank, postalcode);
		return ResponseEntity.ok(filteredLoans);
	}
}
