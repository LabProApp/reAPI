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

/**
 * REST controller that exposes unified lead management endpoints under the
 * {@code /api/leads} base path. Supports property, rental, loan, and legal
 * service lead types via a single set of endpoints. All business logic is
 * delegated to {@link ClientLeadService}.
 */
@Slf4j
@RestController
@RequestMapping("/api/leads")
@Tag(name = "Client Leads APIs", description = "Unified lead management — property, rental, loan, and legal service leads")
public class ClientLeadController {

	private final ClientLeadService service;

	/**
	 * Constructs a {@code ClientLeadController} with the required service.
	 *
	 * @param service the service handling lead business logic
	 */
	public ClientLeadController(ClientLeadService service) {
		this.service = service;
	}

	// ─── Create ───────────────────────────────────────────────────────────────

	/**
	 * Creates a new client lead. Client and property information is
	 * auto-populated from the database when {@code userId} or {@code propertyId}
	 * are supplied.
	 *
	 * @param dto the validated {@link ClientLeadDTO} from the request body
	 * @return 200 OK with the persisted {@link ClientLeadDTO} including its generated ID
	 */
	@PostMapping
	public ResponseEntity<ClientLeadDTO> create(@Valid @RequestBody ClientLeadDTO dto) {
		log.info("POST /api/leads - leadType={}, userId={}, propertyId={}", dto.getLeadType(), dto.getUserId(), dto.getPropertyId());
		ClientLeadDTO created = service.createLead(dto);
		log.info("POST /api/leads - Created id={}", created.getId());
		return ResponseEntity.ok(created);
	}

	// ─── Read by ID ───────────────────────────────────────────────────────────

	/**
	 * Retrieves a single lead by its surrogate ID.
	 *
	 * @param id the ID of the lead to retrieve
	 * @return 200 OK with the matching {@link ClientLeadDTO}
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ClientLeadDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getById(id));
	}

	// ─── Search (unified — all params optional) ───────────────────────────────
	// Combine any of: brokerId, ownerId, propertyId, userId, status (repeatable),
	// leadType, mobile, startDate, endDate. Returns enriched summary with
	// customer, owner, broker, property, and all lead attributes.
	// Results ordered newest first.

	/**
	 * Searches for leads using any combination of the supplied optional query
	 * parameters. All parameters are independently optional; omitted parameters
	 * are ignored. Results are returned ordered newest-first by inquiry date and
	 * include resolved owner and broker display names.
	 *
	 * @param brokerId   filter by broker ID
	 * @param ownerId    filter by property owner ID
	 * @param propertyId filter by property ID
	 * @param userId     filter by client user ID
	 * @param status     filter by one or more lead statuses (repeatable parameter)
	 * @param leadType   filter by inquiry type
	 * @param mobile     partial mobile number match
	 * @param startDate  include leads with inquiry date on or after this timestamp (ISO format)
	 * @param endDate    include leads with inquiry date on or before this timestamp (ISO format)
	 * @return 200 OK with the list of matching enriched {@link ClientLeadDTO} instances
	 */
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

	/**
	 * Returns an enriched summary of all leads associated with the specified
	 * property, including resolved owner and broker display names.
	 *
	 * @param propertyId the ID of the property whose leads are to be retrieved
	 * @return 200 OK with the list of enriched {@link ClientLeadDTO} instances
	 */
	@GetMapping("/property/{propertyId}/summary")
	public ResponseEntity<List<ClientLeadDTO>> getPropertyLeadSummary(@PathVariable Long propertyId) {
		log.info("GET /api/leads/property/{}/summary", propertyId);
		return ResponseEntity.ok(service.getLeadSummariesByPropertyId(propertyId));
	}

	// ─── Status transition ────────────────────────────────────────────────────

	/**
	 * Updates the status of a lead. If transitioning to {@code CONTACTED} and no
	 * contacted timestamp has been recorded yet, the current timestamp is set
	 * automatically.
	 *
	 * @param id  the ID of the lead to update
	 * @param req the request body containing the new status and an optional remark
	 * @return 200 OK with the updated {@link ClientLeadDTO}
	 */
	@PutMapping("/{id}/status")
	public ResponseEntity<ClientLeadDTO> updateStatus(
			@PathVariable Long id, @RequestBody LeadStatusRequest req) {
		log.info("PUT /api/leads/{}/status - {}", id, req.getStatus());
		return ResponseEntity.ok(service.updateStatus(id, req.getStatus(), req.getRemark()));
	}

	// ─── Full update ──────────────────────────────────────────────────────────

	/**
	 * Fully updates all fields of an existing lead.
	 *
	 * @param id  the ID of the lead to update
	 * @param dto the updated lead data from the request body
	 * @return 200 OK with the updated {@link ClientLeadDTO}
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ClientLeadDTO> update(@PathVariable Long id, @RequestBody ClientLeadDTO dto) {
		log.info("PUT /api/leads/{}", id);
		return ResponseEntity.ok(service.updateLead(id, dto));
	}

	// ─── Record loan approval ─────────────────────────────────────────────────

	/**
	 * Records the outcome of a loan approval on an existing lead. Intended to be
	 * called by an admin once the bank has processed the application.
	 *
	 * @param id  the ID of the lead to update
	 * @param req the request body containing the approved bank, loan amount, and interest rate
	 * @return 200 OK with the updated {@link ClientLeadDTO}
	 */
	@PutMapping("/{id}/approval")
	public ResponseEntity<ClientLeadDTO> updateApproval(
			@PathVariable Long id, @RequestBody ApprovalRequest req) {
		log.info("PUT /api/leads/{}/approval - bank={}", id, req.getApprovedBank());
		return ResponseEntity.ok(service.updateApproval(
				id, req.getApprovedBank(), req.getApprovedLoanAmount(), req.getApprovedInterestRate()));
	}

	// ─── Follow-up scheduling ─────────────────────────────────────────────────

	/**
	 * Schedules or updates the next follow-up date for a lead.
	 *
	 * @param id  the ID of the lead to update
	 * @param req the request body containing the follow-up timestamp and an optional remark
	 * @return 200 OK with the updated {@link ClientLeadDTO}
	 */
	@PutMapping("/{id}/followup")
	public ResponseEntity<ClientLeadDTO> scheduleFollowUp(
			@PathVariable Long id, @RequestBody FollowUpRequest req) {
		log.info("PUT /api/leads/{}/followup - date={}", id, req.getFollowUpDate());
		return ResponseEntity.ok(service.scheduleFollowUp(id, req.getFollowUpDate(), req.getRemark()));
	}

	// ─── Delete ───────────────────────────────────────────────────────────────

	/**
	 * Deletes the lead with the given ID.
	 *
	 * @param id the ID of the lead to delete
	 * @return 204 No Content on successful deletion
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	// ─── Request bodies ───────────────────────────────────────────────────────

	/**
	 * Request body for the status-update endpoint. Carries the new lead status
	 * and an optional remark.
	 */
	public static class LeadStatusRequest {
		@NotNull(message = "status is required")
		private MasterEnums.LeadStatus status;
		private String remark;

		/** @return the new lead status */
		public MasterEnums.LeadStatus getStatus() { return status; }
		/** @param status the new lead status to set */
		public void setStatus(MasterEnums.LeadStatus status) { this.status = status; }
		/** @return the optional remark about this status change */
		public String getRemark() { return remark; }
		/** @param remark the remark to set */
		public void setRemark(String remark) { this.remark = remark; }
	}

	/**
	 * Request body for the loan-approval endpoint. Carries the approved bank
	 * name, loan amount, and interest rate.
	 */
	public static class ApprovalRequest {
		private String approvedBank;
		private Double approvedLoanAmount;
		private Double approvedInterestRate;

		/** @return the name of the bank that approved the loan */
		public String getApprovedBank() { return approvedBank; }
		/** @param approvedBank the approved bank name to set */
		public void setApprovedBank(String approvedBank) { this.approvedBank = approvedBank; }
		/** @return the approved loan amount */
		public Double getApprovedLoanAmount() { return approvedLoanAmount; }
		/** @param approvedLoanAmount the approved loan amount to set */
		public void setApprovedLoanAmount(Double approvedLoanAmount) { this.approvedLoanAmount = approvedLoanAmount; }
		/** @return the annual interest rate at which the loan was approved */
		public Double getApprovedInterestRate() { return approvedInterestRate; }
		/** @param approvedInterestRate the approved interest rate to set */
		public void setApprovedInterestRate(Double approvedInterestRate) { this.approvedInterestRate = approvedInterestRate; }
	}

	/**
	 * Request body for the follow-up scheduling endpoint. Carries the target
	 * follow-up timestamp and an optional remark.
	 */
	public static class FollowUpRequest {
		@NotNull(message = "followUpDate is required")
		private LocalDateTime followUpDate;
		private String remark;

		/** @return the timestamp scheduled for the next follow-up */
		public LocalDateTime getFollowUpDate() { return followUpDate; }
		/** @param followUpDate the follow-up timestamp to set */
		public void setFollowUpDate(LocalDateTime followUpDate) { this.followUpDate = followUpDate; }
		/** @return the optional remark about the follow-up */
		public String getRemark() { return remark; }
		/** @param remark the remark to set */
		public void setRemark(String remark) { this.remark = remark; }
	}
}
