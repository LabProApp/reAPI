package com.api.banks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Bank Loan APIs", description = "Operations related to Bank Home Loan Representatives")
public class BankLoanController {

    @Autowired
    private BankLoanService bankLoanService;
 
    // 🧾 List all loan representatives
    @GetMapping("/list")
    public ResponseEntity<List<BankLoanRepresentative>> getAllLoans() {
        return ResponseEntity.ok(bankLoanService.getAllLoans());
    }

    // ➕ Add a new loan representative
    @PostMapping("/add")
    public ResponseEntity<BankLoanRepresentative> addLoan(@RequestBody BankLoanRepresentative loan) {
        return ResponseEntity.ok(bankLoanService.addLoan(loan));
    }

    // ✏️ Update an existing loan representative
    @PutMapping("/update")
    public ResponseEntity<?> updateLoan(@RequestBody BankLoanRepresentative loan) {
        return ResponseEntity.ok(bankLoanService.updateLoan(loan));
    }

    // ⚖️ Compare loan representatives by interest rate (ascending)
    @GetMapping("/compare")
    public ResponseEntity<List<BankLoanRepresentative>> compareLoans() {
        List<BankLoanRepresentative> sorted = bankLoanService.getAllLoans().stream()
                .sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate()))
                .toList();
        return ResponseEntity.ok(sorted);
    }

    // 🔍 Filter loans by interest rate and minimum CIBIL score
    @GetMapping("/filter")
    public ResponseEntity<List<BankLoanRepresentative>> filterLoans(
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) Integer minCibil) {
        return ResponseEntity.ok(bankLoanService.filterLoans(maxRate, minCibil));
    }

    // 🔎 Additional: Filter by tenure, max loan amount, or other criteria
    @GetMapping("/advanced-filter")
    public ResponseEntity<List<BankLoanRepresentative>> advancedFilter(
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) Integer minCibil,
            @RequestParam(required = false) Integer maxTenure,
            @RequestParam(required = false) Double minIncome) {
        return ResponseEntity.ok(bankLoanService.advancedFilter(maxRate, minCibil, maxTenure, minIncome));
    }
}
