package com.api.plan;

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
 * A subscription plan offered by the app (BASIC / DELUX / PREMIUM).
 *
 * <p>Pricing and display metadata live here; the actual feature set is held
 * by {@link PlanFeature}, joined on {@link #planName}. This split keeps the
 * feature catalog editable without schema changes.</p>
 */
@Entity
@Table(
	name = "plans",
	indexes = { @Index(name = "idx_plan_name", columnList = "plan_name", unique = true) }
)
public class Plan extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "plan_name", nullable = false, unique = true)
	private MasterEnums.PackageEnum planName;

	@Column(name = "display_name", nullable = false)
	private String displayName;

	/** Annual subscription cost in INR. 0 for free tiers. */
	@Column(name = "price_yearly", nullable = false)
	private Double priceYearly;

	/**
	 * Maximum number of property listings a user on this plan may post.
	 * 0 means no posting allowed; the {@code post_property} feature flag
	 * still mirrors this (false when limit == 0) for cheap UI gating.
	 */
	@Column(name = "property_limit", nullable = false)
	private Integer propertyLimit = 0;

	public Plan() {}

	public Plan(MasterEnums.PackageEnum planName, String displayName, Double priceYearly,
			Integer propertyLimit) {
		this.planName = planName;
		this.displayName = displayName;
		this.priceYearly = priceYearly;
		this.propertyLimit = propertyLimit;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public MasterEnums.PackageEnum getPlanName() { return planName; }
	public void setPlanName(MasterEnums.PackageEnum planName) { this.planName = planName; }

	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }

	public Double getPriceYearly() { return priceYearly; }
	public void setPriceYearly(Double priceYearly) { this.priceYearly = priceYearly; }

	public Integer getPropertyLimit() { return propertyLimit; }
	public void setPropertyLimit(Integer propertyLimit) { this.propertyLimit = propertyLimit; }
}
