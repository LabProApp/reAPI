package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientLeadService {

	private final ClientLeadRepository repository;
	private final ModelMapper mapper;

	@Autowired
	public ClientLeadService(ClientLeadRepository repository, ModelMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	// ➕ CREATE
	// ➕ CREATE (DUPLICATE SAFE)
	public ClientLeadDTO createLead(ClientLeadDTO dto) {

	    // 🚫 DUPLICATE CHECK
	    if (repository.existsByUserIdAndPropertyId(dto.getUserId(), dto.getPropertyId())) {
	        throw new RuntimeException("Lead already exists for this user and property");
	    }

	    ClientLead entity = mapper.map(dto, ClientLead.class);
	    entity.setInquiryDate(LocalDateTime.now()); // force server time

	    ClientLead saved = repository.save(entity);
	    return mapper.map(saved, ClientLeadDTO.class);
	}

	// ✏ UPDATE
	public ClientLeadDTO updateLead(Long id, ClientLeadDTO dto) {
		ClientLead existing = getEntityById(id);

		mapper.map(dto, existing); // maps only fields from DTO

		ClientLead updated = repository.save(existing);
		return mapper.map(updated, ClientLeadDTO.class);
	}

	// 🔍 GET BY ID
	public ClientLeadDTO getById(Long id) {
		return mapper.map(getEntityById(id), ClientLeadDTO.class);
	}

	// ❌ DELETE
	public void delete(Long id) {
		repository.deleteById(id);
	}

	// 🔎 FILTERS
	public List<ClientLeadDTO> getByUserId(Long userId) {
		return repository.findByUserId(userId).stream().map(e -> mapper.map(e, ClientLeadDTO.class))
				.collect(Collectors.toList());
	}

	public List<ClientLeadDTO> getByBrokerWithFilters(Long brokerId, List<String> status, LocalDateTime startDate,
			LocalDateTime endDate) {

		List<ClientLead> leads;

		if ((status == null || status.isEmpty()) && startDate == null && endDate == null) {
			leads = repository.findByBrokerId(brokerId);
		} else if ((status == null || status.isEmpty()) && startDate != null && endDate != null) {
			leads = repository.findByBrokerIdAndInquiryDateBetween(brokerId, startDate, endDate);
		} else if ((status != null && !status.isEmpty()) && startDate == null && endDate == null) {
			leads = repository.findByBrokerIdAndStatusIn(brokerId, status);
		} else if ((status != null && !status.isEmpty()) && startDate != null && endDate != null) {
			leads = repository.findByBrokerIdAndStatusInAndInquiryDateBetween(brokerId, status, startDate, endDate);
		} else {
			// partial date given → treat missing as open-ended
			if (startDate == null)
				startDate = LocalDateTime.MIN;
			if (endDate == null)
				endDate = LocalDateTime.now();

			if (status == null || status.isEmpty()) {
				leads = repository.findByBrokerIdAndInquiryDateBetween(brokerId, startDate, endDate);
			} else {
				leads = repository.findByBrokerIdAndStatusInAndInquiryDateBetween(brokerId, status, startDate, endDate);
			}
		}

		return leads.stream().map(e -> mapper.map(e, ClientLeadDTO.class)).toList();
	}

	public List<ClientLeadDTO> getByStatus(String status) {
		return repository.findByStatus(status).stream().map(e -> mapper.map(e, ClientLeadDTO.class))
				.collect(Collectors.toList());
	}

	// 🔒 INTERNAL ENTITY FETCH
	private ClientLead getEntityById(Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));
	}
}