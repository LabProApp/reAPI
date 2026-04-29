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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/leads")
@Tag(name = "Client Leads APIs", description = "Unified lead management — property, rental, loan, and legal service leads")
public class ClientLeadController {

	private final ClientLeadService service;

	public ClientLeadController(ClientLeadService service) {
		this.service = service;
	}

	// ─── Create ───────────────────────────────────────────────────────────────

	@PostMapping
	public ResponseEntity<ClientLeadDTO> create(@Valid @RequestBody ClientLeadDTO dto) {
		log.info("POST /api/leads - leadType={}, userId={}, propertyId={}", dto.getLeadType(), dto.getUserId(), dto.getPropertyId());
		ClientLeadDTO created = service.createLead(dto);
		log.info("POST /api/leads - Created id={}", created.getId());
		return ResponseEntity.ok(created);
	}

	// ─── Read by ID ───────────────────────────────────────────────────────────

	@GetMapping("/{id}")
	public ResponseEntity<ClientLeadDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}

	// ─── Search (unified — all params optional) ───────────────────────────────
	// Combine any of: brokerId, ownerId, propertyId, userId, status (repeatable),
	// leadType, mobile, startDate, endDate. Returns enriched summary with
	// customer, owner, broker, property, and all lead attributes.
	// Results ordered newest first.

	@GetMapping("/search")
	public ResponseEntity<List<ClientLeadDTO>> search(
			@RequestParam(required = false) Long brokerId,
			@RequestParam(required = false) Long ownerId,
			@RequestParam(required = false) Long propertyId,
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) List<MasterEnums.LeadStatus> status,
			@RequestParam(required = false) MasterEnums.InquiryType leadType,
			@RequestParam(required = false) String mobile,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		log.info("GET /api/leads/search brokerId={}, ownerId={}, propertyId={}, userId={}, leadType={}, status={}",
				brokerId, ownerId, propertyId, userId, leadType, status);
		return ResponseEntity.ok(service.search(brokerId, ownerId, propertyId, userId, status, leadType, mobile, startDate, endDate));
	}

	// ─── Enriched property interest list ─────────────────────────────────────

	@GetMapping("/property/{propertyId}/summary")
	public ResponseEntity<List<ClientLeadDTO>> getPropertyLeadSummary(@PathVariable Long propertyId) {
		log.info("GET /api/leads/property/{}/summary", propertyId);
		return ResponseEntity.ok(service.getLeadSummariesByPropertyId(propertyId));
	}

	// ─── Status transition ────────────────────────────────────────────────────

	@PutMapping("/{id}/status")
	public ResponseEntity<ClientLeadDTO> updateStatus(
			@PathVariable Long id, @RequestBody LeadStatusRequest req) {
		log.info("PUT /api/leads/{}/status - {}", id, req.getStatus());
		return ResponseEntity.ok(service.updateStatus(id, req.getStatus(), req.getRemark()));
	}

	// ─── Full update ──────────────────────────────────────────────────────────

	@PutMapping("/{id}")
	public ResponseEntity<ClientLeadDTO> update(@PathVariable Long id, @RequestBody ClientLeadDTO dto) {
		log.info("PUT /api/leads/{}", id);
		return ResponseEntity.ok(service.updateLead(id, dto));
	}

	// ─── Record loan approval ─────────────────────────────────────────────────

	@PutMapping("/{id}/approval")
	public ResponseEntity<ClientLeadDTO> updateApproval(
			@PathVariable Long id, @RequestBody ApprovalRequest req) {
		log.info("PUT /api/leads/{}/approval - bank={}", id, req.getApprovedBank());
		return ResponseEntity.ok(service.updateApproval(
				id, req.getApprovedBank(), req.getApprovedLoanAmount(), req.getApprovedInterestRate()));
	}

	// ─── Follow-up scheduling ─────────────────────────────────────────────────

	@PutMapping("/{id}/followup")
	public ResponseEntity<ClientLeadDTO> scheduleFollowUp(
			@PathVariable Long id, @RequestBody FollowUpRequest req) {
		log.info("PUT /api/leads/{}/followup - date={}", id, req.getFollowUpDate());
		return ResponseEntity.ok(service.scheduleFollowUp(id, req.getFollowUpDate(), req.getRemark()));
	}

	// ─── Delete ───────────────────────────────────────────────────────────────

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
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

	public static class ApprovalRequest {
		private String approvedBank;
		private Double approvedLoanAmount;
		private Double approvedInterestRate;

		public String getApprovedBank() { return approvedBank; }
		public void setApprovedBank(String approvedBank) { this.approvedBank = approvedBank; }
		public Double getApprovedLoanAmount() { return approvedLoanAmount; }
		public void setApprovedLoanAmount(Double approvedLoanAmount) { this.approvedLoanAmount = approvedLoanAmount; }
		public Double getApprovedInterestRate() { return approvedInterestRate; }
		public void setApprovedInterestRate(Double approvedInterestRate) { this.approvedInterestRate = approvedInterestRate; }
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
