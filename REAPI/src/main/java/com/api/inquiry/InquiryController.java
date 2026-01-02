package com.api.inquiry;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService service;

    public InquiryController(InquiryService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping("/add")
    public ResponseEntity<InquiryDto> create(
            @RequestBody InquiryDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    // READ BY ID
    @GetMapping("/id/{id}")
    public ResponseEntity<InquiryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<List<InquiryDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<InquiryDto> update(
            @PathVariable Long id,
            @RequestBody InquiryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
