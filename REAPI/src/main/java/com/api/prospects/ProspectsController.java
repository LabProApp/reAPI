package com.api.prospects;

import com.api.enums.MasterEnums;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prospects")
public class ProspectsController {

	private final ProspectsService service;

	public ProspectsController(ProspectsService service) {
		this.service = service;
	}

	@PostMapping("/create")
	public ResponseEntity<Prospects> createProspect(@RequestBody ProspectsDto dto) {
		Prospects prospect = new Prospects();
		// Map DTO to Entity
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
		return ResponseEntity.ok(saved);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<Prospects>> getAllProspects() {
		return ResponseEntity.ok(service.getAllProspects());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Prospects> getProspectById(@PathVariable Long id) {
		Prospects prospect = service.getProspectById(id);
		if (prospect == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(prospect);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<Prospects> updateStatus(@PathVariable Long id,
			@RequestParam MasterEnums.InquiryStatus status) {
		Prospects updated = service.updateProspectStatus(id, status);
		if (updated == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(updated);
	}
}
