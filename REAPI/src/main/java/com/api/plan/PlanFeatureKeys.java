package com.api.plan;

/**
 * Canonical feature-flag keys consumed by the mobile client.
 *
 * <p>Add a new constant here, reference it in {@link PlanSeeder}, and the
 * frontend can switch the gate on without any other backend change.</p>
 */
public final class PlanFeatureKeys {

	public static final String BUY_SELL = "buy_sell";
	public static final String RENT_PG = "rent_pg";
	public static final String BANK_LOANS = "bank_loans";
	public static final String DOCUMENTATION = "documentation";
	public static final String EMI_CALCULATOR = "emi_calculator";
	public static final String POST_PROPERTY = "post_property";
	public static final String POST_REQUIREMENT = "post_requirement";
	public static final String JOURNEY = "journey";

	private PlanFeatureKeys() {}
}
