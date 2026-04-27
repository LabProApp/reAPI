package com.api.inquiry;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.enums.MasterEnums;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class InquiryService {

	private final InquiryRepository repository;
	private final ModelMapper mapper;

	public InquiryService(InquiryRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	public InquiryDto create(InquiryDto dto) {
		log.info("create - Creating new inquiry");
		Inquiry entity = mapper.map(dto, Inquiry.class);

		entity.setId(null);
		entity.setInquiryStatus(MasterEnums.InquiryStatus.NEW);
		entity.setAssignedAgentId(null);
		entity.setAssignedAgentName(null);
		entity.setApprovedBank(null);
		entity.setApprovedLoanAmount(null);
		entity.setApprovedInterestRate(null);

		Inquiry saved = repository.save(entity);
		log.info("create - Inquiry created with id={}", saved.getId());
		return mapper.map(saved, InquiryDto.class);
	}

	public InquiryDto getById(Long id) {
		log.info("getById - Fetching inquiry id={}", id);
		Inquiry entity = repository.findById(id).orElseThrow(() -> {
			log.error("getById - Inquiry not found for id={}", id);
			return new RuntimeException("Inquiry not found with id: " + id);
		});
		return mapper.map(entity, InquiryDto.class);
	}

	public List<InquiryDto> getAll() {
		log.info("getAll - Fetching all inquiries");
		List<InquiryDto> inquiries = repository.findAll()
				.stream()
				.map(e -> mapper.map(e, InquiryDto.class))
				.collect(Collectors.toList());
		log.info("getAll - Returned {} inquiries", inquiries.size());
		return inquiries;
	}

	public InquiryDto update(Long id, InquiryDto dto) {
		log.info("update - Updating inquiry id={}, new status={}", id, dto.getInquiryStatus());
		Inquiry entity = repository.findById(id).orElseThrow(() -> {
			log.error("update - Inquiry not found for id={}", id);
			return new RuntimeException("Inquiry not found with id: " + id);
		});

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
		log.info("update - Inquiry id={} updated successfully", id);
		return mapper.map(updated, InquiryDto.class);
	}

	public void delete(Long id) {
		log.info("delete - Deleting inquiry id={}", id);
		if (!repository.existsById(id)) {
			log.error("delete - Inquiry not found for id={}", id);
			throw new RuntimeException("Inquiry not found with id: " + id);
		}
		repository.deleteById(id);
		log.info("delete - Inquiry id={} deleted", id);
	}
}
