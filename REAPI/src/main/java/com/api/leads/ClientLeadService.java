package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	public ClientLeadDTO createLead(ClientLeadDTO dto) {
		log.info("createLead - Creating lead for userId={}, propertyId={}", dto.getUserId(), dto.getPropertyId());

		if (repository.existsByUserIdAndPropertyId(dto.getUserId(), dto.getPropertyId())) {
			log.warn("createLead - Duplicate lead rejected for userId={}, propertyId={}", dto.getUserId(), dto.getPropertyId());
			throw new RuntimeException("Lead already exists for this user and property");
		}

		ClientLead entity = mapper.map(dto, ClientLead.class);
		entity.setInquiryDate(LocalDateTime.now());

		ClientLead saved = repository.save(entity);
		log.info("createLead - Lead created with id={}", saved.getId());
		return mapper.map(saved, ClientLeadDTO.class);
	}

	public ClientLeadDTO updateLead(Long id, ClientLeadDTO dto) {
		log.info("updateLead - Updating lead id={}", id);
		ClientLead existing = getEntityById(id);
		mapper.map(dto, existing);
		ClientLead updated = repository.save(existing);
		log.info("updateLead - Lead id={} updated", id);
		return mapper.map(updated, ClientLeadDTO.class);
	}

	public ClientLeadDTO getById(Long id) {
		log.info("getById - Fetching lead id={}", id);
		return mapper.map(getEntityById(id), ClientLeadDTO.class);
	}

	public void delete(Long id) {
		log.info("delete - Deleting lead id={}", id);
		repository.deleteById(id);
		log.info("delete - Lead id={} deleted", id);
	}

	public List<ClientLeadDTO> getByUserId(Long userId) {
		log.info("getByUserId - Fetching leads for userId={}", userId);
		List<ClientLeadDTO> leads = repository.findByUserId(userId).stream()
				.map(e -> mapper.map(e, ClientLeadDTO.class)).collect(Collectors.toList());
		log.info("getByUserId - Returned {} leads for userId={}", leads.size(), userId);
		return leads;
	}

	public List<ClientLeadDTO> getByBrokerWithFilters(Long brokerId, List<String> status, LocalDateTime startDate,
			LocalDateTime endDate) {
		log.info("getByBrokerWithFilters - Fetching leads for brokerId={} [status={}, startDate={}, endDate={}]",
				brokerId, status, startDate, endDate);
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
			if (startDate == null) startDate = LocalDateTime.MIN;
			if (endDate == null) endDate = LocalDateTime.now();
			if (status == null || status.isEmpty()) {
				leads = repository.findByBrokerIdAndInquiryDateBetween(brokerId, startDate, endDate);
			} else {
				leads = repository.findByBrokerIdAndStatusInAndInquiryDateBetween(brokerId, status, startDate, endDate);
			}
		}

		List<ClientLeadDTO> result = leads.stream().map(e -> mapper.map(e, ClientLeadDTO.class)).toList();
		log.info("getByBrokerWithFilters - Returned {} leads for brokerId={}", result.size(), brokerId);
		return result;
	}

	public List<ClientLeadDTO> getByStatus(String status) {
		log.info("getByStatus - Fetching leads with status={}", status);
		List<ClientLeadDTO> leads = repository.findByStatus(status).stream()
				.map(e -> mapper.map(e, ClientLeadDTO.class)).collect(Collectors.toList());
		log.info("getByStatus - Returned {} leads with status={}", leads.size(), status);
		return leads;
	}

	private ClientLead getEntityById(Long id) {
		return repository.findById(id).orElseThrow(() -> {
			log.error("getEntityById - Lead not found for id={}", id);
			return new RuntimeException("Lead not found with id: " + id);
		});
	}
}
