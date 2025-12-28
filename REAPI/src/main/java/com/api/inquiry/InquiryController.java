package com.api.inquiry;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/loan-inquiries")
public class InquiryController {

    private final InquiryService service;

    public InquiryController(InquiryService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<InquiryDto> create(
            @RequestBody InquiryDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<InquiryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<InquiryDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<InquiryDto> update(
            @PathVariable Long id,
            @RequestBody InquiryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
