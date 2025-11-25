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
@RequestMapping("/api/banks")
@Tag(name = "Bank Vendor APIs", description = "Operations related to Maintain Bank & Loan Vendors")
public class BankVendorController {

	@Autowired
	private BankVendorService BankVendorService;

	// 🧾 List all vendors
	@GetMapping("/list")
	public ResponseEntity<List<BankVendor>> getAllvendors() {
		return ResponseEntity.ok(BankVendorService.getAllvendors());
	}

	// ➕ Add a new vendor
	@PostMapping("/add")
	public ResponseEntity<BankVendor> addvendor(@RequestBody BankVendor vendor) {
		return ResponseEntity.ok(BankVendorService.addvendor(vendor));
	}

	// ➕ Update a Bank Vendor
	@PutMapping("/update")
	public ResponseEntity<?> updatevendor(@RequestBody BankVendor vendor) {
		return ResponseEntity.ok(BankVendorService.updatevendor(vendor));
	}

	// ⚖️ Compare vendors by interest rate
	@GetMapping("/compare")
	public ResponseEntity<List<BankVendor>> comparevendors() {
		List<BankVendor> sorted = BankVendorService.getAllvendors().stream()
				.sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate())).toList();
		return ResponseEntity.ok(sorted);
	}

	// 🔍 Filter vendors by interest rate and CIBIL score
	@GetMapping("/filter")
	public ResponseEntity<List<BankVendor>> filtervendors(@RequestParam(required = false) Double maxRate,
			@RequestParam(required = false) Integer minCibil) {
		return ResponseEntity.ok(BankVendorService.filtervendors(maxRate, minCibil));
	}
}
