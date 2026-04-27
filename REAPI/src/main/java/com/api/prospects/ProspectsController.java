package com.api.prospects;

import com.api.enums.MasterEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/prospects")
public class ProspectsController {

	private final ProspectsService service;

	public ProspectsController(ProspectsService service) {
		this.service = service;
	}

	@PostMapping("/create")
	public ResponseEntity<Prospects> createProspect(@RequestBody ProspectsDto dto) {
		log.info("POST /api/prospects/create - Creating prospect for name={}, email={}", dto.getName(), dto.getEmail());
		Prospects prospect = new Prospects();
		prospect.setName(dto.getName());
		prospect.setEmail(dto.getEmail());
		prospect.setPhone(dto.getPhone());
		prospect.setInquiryType(dto.getInquiryType());
		prospect.setProfession(dto.getProfession());
		prospect.setAnnualSalary(dto.getAnnualSalary());
		prospect.setMessage(dto.getMessage());
		prospect.setAddress(dto.getAddress());
		prospect.setStatus(dto.getStatus());
		prospect.setDocumentServicesRequired(dto.getDocumentServicesRequired());
		prospect.setPropertyType(dto.getPropertyType());
		prospect.setMinBudget(dto.getMinBudget());
		prospect.setMaxBudget(dto.getMaxBudget());
		prospect.setSpecifications(dto.getSpecifications());

		Prospects saved = service.saveProspect(prospect);
		log.info("POST /api/prospects/create - Prospect created with id={}", saved.getId());
		return ResponseEntity.ok(saved);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<Prospects>> getAllProspects() {
		log.info("GET /api/prospects/getall - Fetching all prospects");
		List<Prospects> prospects = service.getAllProspects();
		log.info("GET /api/prospects/getall - Returned {} prospects", prospects.size());
		return ResponseEntity.ok(prospects);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Prospects> getProspectById(@PathVariable Long id) {
		log.info("GET /api/prospects/{} - Fetching prospect", id);
		Prospects prospect = service.getProspectById(id);
		if (prospect == null) {
			log.warn("GET /api/prospects/{} - Prospect not found", id);
			return ResponseEntity.notFound().build();
		}
		log.info("GET /api/prospects/{} - Prospect fetched: name={}", id, prospect.getName());
		return ResponseEntity.ok(prospect);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Prospects> updateStatus(@PathVariable Long id,
			@RequestParam MasterEnums.InquiryStatus status) {
		log.info("PUT /api/prospects/{}/status - Updating status to {}", id, status);
		Prospects updated = service.updateProspectStatus(id, status);
		if (updated == null) {
			log.warn("PUT /api/prospects/{}/status - Prospect not found", id);
			return ResponseEntity.notFound().build();
		}
		log.info("PUT /api/prospects/{}/status - Status updated to {}", id, status);
		return ResponseEntity.ok(updated);
	}
}
