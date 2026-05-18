package com.api.plan;

import java.time.LocalDateTime;

import com.api.commons.BaseEntity;
import com.api.enums.MasterEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * A user's request to move to a paid plan, queued for admin review.
 *
 * <p>Used as the MVP upgrade pathway before a real payment integration
 * exists: the user submits a request from the mobile app, sales confirms
 * payment offline, and an admin approves the row — at which point
 * {@code UserService.changePlan} is called to apply the plan.</p>
 *
 * <p>Each user may have at most one row in {@code PENDING} state at a
 * time (enforced in the service layer).</p>
 */
@Entity
@Table(
	name = "plan_change_requests",
	indexes = {
		@Index(name = "idx_pcr_user_id", columnList = "user_id"),
		@Index(name = "idx_pcr_status", columnList = "status"),
		@Index(name = "idx_pcr_user_status", columnList = "user_id, status")
	}
)
public class PlanChangeRequest extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "requested_plan", nullable = false)
	private MasterEnums.PackageEnum requestedPlan;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PlanChangeRequestStatus status = PlanChangeRequestStatus.PENDING;

	/** Free-text note from the user (optional). */
	@Column(name = "notes", length = 500)
	private String notes;

	@Column(name = "requested_at", nullable = false)
	private LocalDateTime requestedAt;

	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;

	/** userId of the admin who acted on this request. */
	@Column(name = "reviewed_by")
	private Long reviewedBy;

	/** Reason captured when the admin rejects the request. */
	@Column(name = "rejection_reason", length = 500)
	private String rejectionReason;

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
