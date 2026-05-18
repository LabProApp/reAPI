package com.api.user;

import com.api.enums.MasterEnums;

/**
 * Admin payload for {@code POST /api/user/{userId}/plan} — assigns a paid
 * subscription to a user. Until a real payment integration exists this is
 * the only programmatic upgrade pathway and is locked to ROLE_ADMIN.
 */
public class PlanChangeRequest {

	private MasterEnums.PackageEnum plan;
	/** How long the subscription runs from now, in years. Defaults to 1 if null. */
	private Integer durationYears;
	/** Optional free-text reason captured for the audit trail (logs). */
	private String reason;

	public MasterEnums.PackageEnum getPlan() { return plan; }
	public void setPlan(MasterEnums.PackageEnum plan) { this.plan = plan; }

	public Integer getDurationYears() { return durationYears; }
	public void setDurationYears(Integer durationYears) { this.durationYears = durationYears; }

	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }
}
