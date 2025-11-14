package com.api.banks;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class BankLoanController {

    private final BankLoanService bankLoanService;

    public BankLoanController(BankLoanService bankLoanService) {
        this.bankLoanService = bankLoanService;
    }

    // 🧾 List all loans
    @GetMapping("/list")
    public ResponseEntity<List<BankLoan>> getAllLoans() {
        return ResponseEntity.ok(bankLoanService.getAllLoans());
    }

    // ➕ Add a new loan
    @PostMapping("/add")
    public ResponseEntity<BankLoan> addLoan(@RequestBody BankLoan loan) {
        return ResponseEntity.ok(bankLoanService.addLoan(loan));
    }

    // ⚖️ Compare loans by interest rate
    @GetMapping("/compare")
    public ResponseEntity<List<BankLoan>> compareLoans() {
        List<BankLoan> sorted = bankLoanService.getAllLoans().stream()
                .sorted((a, b) -> Double.compare(a.getInterestRate(), b.getInterestRate()))
                .toList();
        return ResponseEntity.ok(sorted);
    }

    // 🔍 Filter loans by interest rate and CIBIL score
    @GetMapping("/filter")
    public ResponseEntity<List<BankLoan>> filterLoans(
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) Integer minCibil) {
        return ResponseEntity.ok(bankLoanService.filterLoans(maxRate, minCibil));
    }
}
