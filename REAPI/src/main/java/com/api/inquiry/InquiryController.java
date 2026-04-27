package com.api.inquiry;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService service;

    public InquiryController(InquiryService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping("/add")
    public ResponseEntity<InquiryDto> create(@RequestBody InquiryDto dto) {
        log.info("POST /api/inquiries/add - Creating inquiry");
        InquiryDto created = service.create(dto);
        log.info("POST /api/inquiries/add - Inquiry created with id={}", created.getId());
        return ResponseEntity.ok(created);
    }

    // READ BY ID
    @GetMapping("/id/{id}")
    public ResponseEntity<InquiryDto> getById(@PathVariable Long id) {
        log.info("GET /api/inquiries/id/{} - Fetching inquiry", id);
        InquiryDto dto = service.getById(id);
        log.info("GET /api/inquiries/id/{} - Inquiry fetched", id);
        return ResponseEntity.ok(dto);
    }

    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<List<InquiryDto>> getAll() {
        log.info("GET /api/inquiries/all - Fetching all inquiries");
        List<InquiryDto> inquiries = service.getAll();
        log.info("GET /api/inquiries/all - Returned {} inquiries", inquiries.size());
        return ResponseEntity.ok(inquiries);
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<InquiryDto> update(@PathVariable Long id, @RequestBody InquiryDto dto) {
        log.info("PUT /api/inquiries/update/{} - Updating inquiry", id);
        InquiryDto updated = service.update(id, dto);
        log.info("PUT /api/inquiries/update/{} - Inquiry updated", id);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/inquiries/delete/{} - Deleting inquiry", id);
        service.delete(id);
        log.info("DELETE /api/inquiries/delete/{} - Inquiry deleted", id);
        return ResponseEntity.noContent().build();
    }
}
