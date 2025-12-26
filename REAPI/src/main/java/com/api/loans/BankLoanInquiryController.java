package com.api.loans;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/loan-inquiries")
public class BankLoanInquiryController {

    private final BankLoanInquiryService service;

    public BankLoanInquiryController(BankLoanInquiryService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<BankLoanInquiryDto> create(
            @RequestBody BankLoanInquiryDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<BankLoanInquiryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<BankLoanInquiryDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<BankLoanInquiryDto> update(
            @PathVariable Long id,
            @RequestBody BankLoanInquiryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
