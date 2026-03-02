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
		return service.createLead(dto);
	}

	@PutMapping("/{id}")
	public ClientLeadDTO update(@PathVariable Long id, @RequestBody ClientLeadDTO dto) {
		return service.updateLead(id, dto);
	}

	@GetMapping("/{id}")
	public ClientLeadDTO getById(@PathVariable Long id) {
		return service.getById(id);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	@GetMapping("/user/{userId}")
	public List<ClientLeadDTO> getByUser(@PathVariable Long userId) {
		return service.getByUserId(userId);
	}

	@GetMapping("/broker/{brokerId}")
	public List<ClientLeadDTO> getByBrokerWithFilters(
	        @PathVariable Long brokerId,
	        @RequestParam(required = false) List<String> status,
	        @RequestParam(required = false) 
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
	        @RequestParam(required = false) 
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
	) {
	    return service.getByBrokerWithFilters(brokerId, status, startDate, endDate);
	}

	@GetMapping("/status/{status}")
	public List<ClientLeadDTO> getByStatus(@PathVariable String status) {
		return service.getByStatus(status);
	}

	@GetMapping("/contacted/{contacted}")
	public List<ClientLeadDTO> getByContacted(@PathVariable Boolean contacted) {
		return service.getByContacted(contacted);
	}
}