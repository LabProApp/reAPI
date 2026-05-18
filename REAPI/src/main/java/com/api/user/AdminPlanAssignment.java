package com.api.user;

import com.api.enums.MasterEnums;

/**
 * Admin payload for {@code POST /api/user/{userId}/plan} — assigns a paid
 * subscription to a user. Used by sales/support to flip a plan after an
 * offline payment confirmation. Locked to ROLE_ADMIN.
 *
 * <p>Distinct from {@link com.api.plan.PlanChangeRequest}, which is the
 * <em>user-side</em> request that an admin then reviews here.</p>
 */
public class AdminPlanAssignment {

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
