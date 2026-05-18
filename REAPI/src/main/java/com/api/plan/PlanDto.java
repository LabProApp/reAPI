package com.api.plan;

import java.util.Map;

import com.api.enums.MasterEnums;

/**
 * Response payload for {@code GET /api/plans/{plan}}.
 */
public class PlanDto {

	private MasterEnums.PackageEnum planName;
	private String displayName;
	private Double priceYearly;
	private Map<String, Boolean> featureFlags;

	public PlanDto() {}

	public PlanDto(MasterEnums.PackageEnum planName, String displayName, Double priceYearly,
			Map<String, Boolean> featureFlags) {
		this.planName = planName;
		this.displayName = displayName;
		this.priceYearly = priceYearly;
		this.featureFlags = featureFlags;
	}

	public MasterEnums.PackageEnum getPlanName() { return planName; }
	public void setPlanName(MasterEnums.PackageEnum planName) { this.planName = planName; }

	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }

	public Double getPriceYearly() { return priceYearly; }
	public void setPriceYearly(Double priceYearly) { this.priceYearly = priceYearly; }

	public Map<String, Boolean> getFeatureFlags() { return featureFlags; }
	public void setFeatureFlags(Map<String, Boolean> featureFlags) { this.featureFlags = featureFlags; }
}
