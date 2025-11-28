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
@RequestMapping("/api/bankloans")
@Tag(name = "Bank Loan APIs", description = "Operations related to Bank Home Loan Representatives")
public class BankLoanController {

	@Autowired
	private BankLoanService bankLoanService;

	// 🧾 List all loan representatives
	@GetMapping("/list")
	public ResponseEntity<List<BankLoanRepresentativeDto>> getAllLoans() {
		List<BankLoanRepresentativeDto> loans = bankLoanService.getAllLoans();
		return ResponseEntity.ok(loans);
	}

	// ➕ Add a new loan representative
	@PostMapping("/add")
	public ResponseEntity<BankLoanRepresentativeDto> addLoan(@RequestBody BankLoanRepresentativeDto loanDto) {
		BankLoanRepresentativeDto savedLoan = bankLoanService.addLoan(loanDto);
		return ResponseEntity.ok(savedLoan);
	}

	// ✏️ Update an existing loan representative
	@PutMapping("/update")
	public ResponseEntity<?> updateLoan(@RequestBody BankLoanRepresentativeDto loanDto) {
		ResponseEntity<?> response = bankLoanService.updateLoan(loanDto);
		return response;
	}

	// ⚖️ Compare loan representatives by interest rate (ascending)
	@GetMapping("/compare")
	public ResponseEntity<List<BankLoanRepresentativeDto>> compareLoans() {
		List<BankLoanRepresentativeDto> sortedLoans = bankLoanService.getAllLoans().stream()
				.sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate())).toList();
		return ResponseEntity.ok(sortedLoans);
	}

	// 🔎 Advanced filtering by multiple criteria
	@GetMapping("/advanced-filter")
	public ResponseEntity<List<BankLoanRepresentativeDto>> advancedFilter(
			@RequestParam(required = false) Double maxRate, @RequestParam(required = false) Integer minCibil,
			@RequestParam(required = false) Integer maxTenure, @RequestParam(required = false) Double minIncome,
			@RequestParam(required = false) String city, @RequestParam(required = false) String state,
			@RequestParam(required = false) String bank, @RequestParam(required = false) String postalcode) {

		List<BankLoanRepresentativeDto> filteredLoans = bankLoanService.advancedFilter(maxRate, minCibil, maxTenure,
				minIncome, city, state, bank, postalcode);
		return ResponseEntity.ok(filteredLoans);
	}
}
