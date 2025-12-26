package com.api.loans;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;



@Service
public class BankLoanInquiryService {

    private final BankLoanInquiryRepository repository;
    private final ModelMapper mapper;

    public BankLoanInquiryService(BankLoanInquiryRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // CREATE
    public BankLoanInquiryDto create(BankLoanInquiryDto dto) {
        BankLoanInquiry entity = mapper.map(dto, BankLoanInquiry.class);

        // default status
        if (entity.getInquiryStatus() == null) {
            entity.setInquiryStatus("NEW");
        }

        BankLoanInquiry saved = repository.save(entity);
        return mapper.map(saved, BankLoanInquiryDto.class);
    }

    // READ BY ID
    public BankLoanInquiryDto getById(Long id) {
        BankLoanInquiry entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan inquiry not found"));

        return mapper.map(entity, BankLoanInquiryDto.class);
    }

    // READ ALL
    public List<BankLoanInquiryDto> getAll() {
        return repository.findAll()
                .stream()
                .map(e -> mapper.map(e, BankLoanInquiryDto.class))
                .collect(Collectors.toList());
    }

    // UPDATE
    public BankLoanInquiryDto update(Long id, BankLoanInquiryDto dto) {
        BankLoanInquiry entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan inquiry not found"));

        mapper.map(dto, entity); // updates fields
        BankLoanInquiry updated = repository.save(entity);

        return mapper.map(updated, BankLoanInquiryDto.class);
    }

    // DELETE
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Loan inquiry not found");
        }
        repository.deleteById(id);
    }
}
