package com.api.plan;

import java.time.LocalDateTime;

import com.api.enums.MasterEnums;

/**
 * Wire payload for {@link PlanChangeRequest}. Used for both submit
 * (request body, only {@code requestedPlan} + {@code notes} read) and
 * list responses (everything populated).
 */
public class PlanChangeRequestDto {

	private Long id;
	private Long userId;
	private MasterEnums.PackageEnum requestedPlan;
	private PlanChangeRequestStatus status;
	private String notes;
	private LocalDateTime requestedAt;
	private LocalDateTime reviewedAt;
	private Long reviewedBy;
	private String rejectionReason;

	public PlanChangeRequestDto() {}

	public static PlanChangeRequestDto from(PlanChangeRequest r) {
		PlanChangeRequestDto dto = new PlanChangeRequestDto();
		dto.id = r.getId();
		dto.userId = r.getUserId();
		dto.requestedPlan = r.getRequestedPlan();
		dto.status = r.getStatus();
		dto.notes = r.getNotes();
		dto.requestedAt = r.getRequestedAt();
		dto.reviewedAt = r.getReviewedAt();
		dto.reviewedBy = r.getReviewedBy();
		dto.rejectionReason = r.getRejectionReason();
		return dto;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Long getUserId() { return userId; }
	public void setUserId(Long userId) { this.userId = userId; }
	public MasterEnums.PackageEnum getRequestedPlan() { return requestedPlan; }
	public void setRequestedPlan(MasterEnums.PackageEnum requestedPlan) { this.requestedPlan = requestedPlan; }
	public PlanChangeRequestStatus getStatus() { return status; }
	public void setStatus(PlanChangeRequestStatus status) { this.status = status; }
	public String getNotes() { return notes; }
	public void setNotes(String notes) { this.notes = notes; }
	public LocalDateTime getRequestedAt() { return requestedAt; }
	public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
	public LocalDateTime getReviewedAt() { return reviewedAt; }
	public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
	public Long getReviewedBy() { return reviewedBy; }
	public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }
	public String getRejectionReason() { return rejectionReason; }
	public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
