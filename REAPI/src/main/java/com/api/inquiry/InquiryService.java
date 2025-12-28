package com.api.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;


@Service
@Transactional
public class InquiryService {

	private final InquiryRepository repository;
	private final ModelMapper mapper;

	public InquiryService(InquiryRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	/* =========================
	 * CREATE INQUIRY
	 * ========================= */
	public InquiryDto create(InquiryDto dto) {

		Inquiry entity = mapper.map(dto, Inquiry.class);

		// SYSTEM-CONTROLLED FIELDS
		entity.setId(null); // safety
		entity.setInquiryStatus(MasterEnums.InquiryStatus.NEW);
		entity.setAssignedAgentId(null);
		entity.setAssignedAgentName(null);
		entity.setApprovedBank(null);
		entity.setApprovedLoanAmount(null);
		entity.setApprovedInterestRate(null);

		Inquiry saved = repository.save(entity);
		return mapper.map(saved, InquiryDto.class);
	}

	/* =========================
	 * GET BY ID
	 * ========================= */
	public InquiryDto getById(Long id) {
		Inquiry entity = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Inquiry not found with id: " + id));

		return mapper.map(entity, InquiryDto.class);
	}

	/* =========================
	 * GET ALL
	 * ========================= */
	public List<InquiryDto> getAll() {
		return repository.findAll()
				.stream()
				.map(e -> mapper.map(e, InquiryDto.class))
				.collect(Collectors.toList());
	}

	/* =========================
	 * UPDATE (ADMIN / OPS ONLY)
	 * ========================= */
	public InquiryDto update(Long id, InquiryDto dto) {

		Inquiry entity = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Inquiry not found with id: " + id));

		// SAFE UPDATES ONLY
		entity.setComments(dto.getComments());
		entity.setPreferredBank(dto.getPreferredBank());
		entity.setInquiryStatus(dto.getInquiryStatus());
		entity.setAssignedAgentId(dto.getAssignedAgentId());
		entity.setAssignedAgentName(dto.getAssignedAgentName());
		entity.setApprovedBank(dto.getApprovedBank());
		entity.setApprovedLoanAmount(dto.getApprovedLoanAmount());
		entity.setApprovedInterestRate(dto.getApprovedInterestRate());
		entity.setExpectedPurchaseDate(dto.getExpectedPurchaseDate());

		Inquiry updated = repository.save(entity);
		return mapper.map(updated, InquiryDto.class);
	}

	/* =========================
	 * DELETE
	 * ========================= */
	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new RuntimeException("Inquiry not found with id: " + id);
		}
		repository.deleteById(id);
	}
}
