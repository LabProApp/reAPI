package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/leads")
@CrossOrigin
public class ClientLeadController {

	private final ClientLeadService service;

	public ClientLeadController(ClientLeadService service) {
		this.service = service;
	}

	@PostMapping
	public ClientLeadDTO create(@RequestBody ClientLeadDTO dto) {
		log.info("POST /api/leads - Creating lead for userId={}", dto.getUserId());
		ClientLeadDTO created = service.createLead(dto);
		log.info("POST /api/leads - Lead created with id={}", created.getId());
		return created;
	}

	@PutMapping("/{id}")
	public ClientLeadDTO update(@PathVariable Long id, @RequestBody ClientLeadDTO dto) {
		log.info("PUT /api/leads/{} - Updating lead", id);
		ClientLeadDTO updated = service.updateLead(id, dto);
		log.info("PUT /api/leads/{} - Lead updated", id);
		return updated;
	}

	@GetMapping("/{id}")
	public ClientLeadDTO getById(@PathVariable Long id) {
		log.info("GET /api/leads/{} - Fetching lead", id);
		ClientLeadDTO lead = service.getById(id);
		log.info("GET /api/leads/{} - Lead fetched", id);
		return lead;
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		log.info("DELETE /api/leads/{} - Deleting lead", id);
		service.delete(id);
		log.info("DELETE /api/leads/{} - Lead deleted", id);
	}

	@GetMapping("/user/{userId}")
	public List<ClientLeadDTO> getByUser(@PathVariable Long userId) {
		log.info("GET /api/leads/user/{} - Fetching leads by user", userId);
		List<ClientLeadDTO> leads = service.getByUserId(userId);
		log.info("GET /api/leads/user/{} - Returned {} leads", userId, leads.size());
		return leads;
	}

	@GetMapping("/broker/{brokerId}")
	public List<ClientLeadDTO> getByBrokerWithFilters(
	        @PathVariable Long brokerId,
	        @RequestParam(required = false) List<String> status,
	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		log.info("GET /api/leads/broker/{} - Fetching leads [status={}, startDate={}, endDate={}]",
				brokerId, status, startDate, endDate);
		List<ClientLeadDTO> leads = service.getByBrokerWithFilters(brokerId, status, startDate, endDate);
		log.info("GET /api/leads/broker/{} - Returned {} leads", brokerId, leads.size());
	    return leads;
	}

	@GetMapping("/status/{status}")
	public List<ClientLeadDTO> getByStatus(@PathVariable String status) {
		log.info("GET /api/leads/status/{} - Fetching leads by status", status);
		List<ClientLeadDTO> leads = service.getByStatus(status);
		log.info("GET /api/leads/status/{} - Returned {} leads", status, leads.size());
		return leads;
	}
}