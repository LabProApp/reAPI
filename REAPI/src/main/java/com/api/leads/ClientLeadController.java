package com.api.leads;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.enums.MasterEnums;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/leads")
@Tag(name = "Client Leads APIs", description = "Customer-property engagement and broker CRM")
public class ClientLeadController {

	private final ClientLeadService service;

	public ClientLeadController(ClientLeadService service) {
		this.service = service;
	}

	// ─── Create inquiry (customer → property) ────────────────────────────────

	@PostMapping
	public ResponseEntity<ClientLeadDTO> create(@RequestBody ClientLeadDTO dto) {
		log.info("POST /api/leads - userId={}, propertyId={}", dto.getUserId(), dto.getPropertyId());
		ClientLeadDTO created = service.createLead(dto);
		log.info("POST /api/leads - Lead created id={}", created.getId());
		return ResponseEntity.ok(created);
	}

	// ─── Update full lead (broker CRM update) ────────────────────────────────

	@PutMapping("/{id}")
	public ResponseEntity<ClientLeadDTO> update(@PathVariable Long id, @RequestBody ClientLeadDTO dto) {
		log.info("PUT /api/leads/{} - Updating lead", id);
		ClientLeadDTO updated = service.updateLead(id, dto);
		log.info("PUT /api/leads/{} - Lead updated", id);
		return ResponseEntity.ok(updated);
	}

	// ─── Update status only ───────────────────────────────────────────────────

	@PutMapping("/{id}/status")
	public ResponseEntity<ClientLeadDTO> updateStatus(
			@PathVariable Long id,
			@RequestBody LeadStatusRequest req) {
		log.info("PUT /api/leads/{}/status - status={}", id, req.getStatus());
		ClientLeadDTO updated = service.updateStatus(id, req.getStatus(), req.getRemark());
		log.info("PUT /api/leads/{}/status - Updated to {}", id, updated.getStatus());
		return ResponseEntity.ok(updated);
	}

	// ─── Schedule follow-up ───────────────────────────────────────────────────

	@PutMapping("/{id}/followup")
	public ResponseEntity<ClientLeadDTO> scheduleFollowUp(
			@PathVariable Long id,
			@RequestBody FollowUpRequest req) {
		log.info("PUT /api/leads/{}/followup - followUpDate={}", id, req.getFollowUpDate());
		ClientLeadDTO updated = service.scheduleFollowUp(id, req.getFollowUpDate(), req.getRemark());
		log.info("PUT /api/leads/{}/followup - Follow-up scheduled", id);
		return ResponseEntity.ok(updated);
	}

	// ─── Fetch by ID ─────────────────────────────────────────────────────────

	@GetMapping("/{id}")
	public ResponseEntity<ClientLeadDTO> getById(@PathVariable Long id) {
		log.info("GET /api/leads/{}", id);
		return ResponseEntity.ok(service.getById(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		log.info("DELETE /api/leads/{}", id);
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	// ─── Fetch by customer ────────────────────────────────────────────────────

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<ClientLeadDTO>> getByUser(@PathVariable Long userId) {
		log.info("GET /api/leads/user/{}", userId);
		List<ClientLeadDTO> leads = service.getByUserId(userId);
		log.info("GET /api/leads/user/{} - Returned {} leads", userId, leads.size());
		return ResponseEntity.ok(leads);
	}

	// ─── Fetch by property ────────────────────────────────────────────────────

	@GetMapping("/property/{propertyId}")
	public ResponseEntity<List<ClientLeadDTO>> getByProperty(@PathVariable Long propertyId) {
		log.info("GET /api/leads/property/{}", propertyId);
		List<ClientLeadDTO> leads = service.getByPropertyId(propertyId);
		log.info("GET /api/leads/property/{} - Returned {} leads", propertyId, leads.size());
		return ResponseEntity.ok(leads);
	}

	// ─── Fetch by owner (all inquiries across owner's properties) ────────────

	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<List<ClientLeadDTO>> getByOwner(@PathVariable Long ownerId) {
		log.info("GET /api/leads/owner/{}", ownerId);
		List<ClientLeadDTO> leads = service.getByPropertyOwnerId(ownerId);
		log.info("GET /api/leads/owner/{} - Returned {} leads", ownerId, leads.size());
		return ResponseEntity.ok(leads);
	}

	// ─── Broker CRM view with filters ────────────────────────────────────────

	@GetMapping("/broker/{brokerId}")
	public ResponseEntity<List<ClientLeadDTO>> getByBrokerWithFilters(
			@PathVariable Long brokerId,
			@RequestParam(required = false) List<MasterEnums.LeadStatus> status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		log.info("GET /api/leads/broker/{} [status={}, start={}, end={}]", brokerId, status, startDate, endDate);
		List<ClientLeadDTO> leads = service.getByBrokerWithFilters(brokerId, status, startDate, endDate);
		log.info("GET /api/leads/broker/{} - Returned {} leads", brokerId, leads.size());
		return ResponseEntity.ok(leads);
	}

	// ─── Admin: all leads by status ───────────────────────────────────────────

	@GetMapping("/status/{status}")
	public ResponseEntity<List<ClientLeadDTO>> getByStatus(@PathVariable MasterEnums.LeadStatus status) {
		log.info("GET /api/leads/status/{}", status);
		List<ClientLeadDTO> leads = service.getByStatus(status);
		log.info("GET /api/leads/status/{} - Returned {} leads", status, leads.size());
		return ResponseEntity.ok(leads);
	}

	// ─── Request bodies ───────────────────────────────────────────────────────

	public static class LeadStatusRequest {
		@NotNull(message = "status is required")
		private MasterEnums.LeadStatus status;
		private String remark;

		public MasterEnums.LeadStatus getStatus() { return status; }
		public void setStatus(MasterEnums.LeadStatus status) { this.status = status; }
		public String getRemark() { return remark; }
		public void setRemark(String remark) { this.remark = remark; }
	}

	public static class FollowUpRequest {
		@NotNull(message = "followUpDate is required")
		private LocalDateTime followUpDate;
		private String remark;

		public LocalDateTime getFollowUpDate() { return followUpDate; }
		public void setFollowUpDate(LocalDateTime followUpDate) { this.followUpDate = followUpDate; }
		public String getRemark() { return remark; }
		public void setRemark(String remark) { this.remark = remark; }
	}
}
