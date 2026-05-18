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
import jakarta.persistence.UniqueConstraint;

/**
 * One feature flag for one plan.
 *
 * <p>The frontend reads this catalog (flattened into a {@code Map<String,
 * Boolean>}) to gate dashboard entry points: Buy/Sell, Rent/PG, Bank Loans,
 * Documentation, etc. Adding a new feature is a matter of inserting rows —
 * no schema change required.</p>
 */
@Entity
@Table(
	name = "plan_features",
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_plan_feature", columnNames = { "plan_name", "feature_key" })
	},
	indexes = { @Index(name = "idx_plan_feature_plan", columnList = "plan_name") }
)
public class PlanFeature extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "plan_name", nullable = false)
	private MasterEnums.PackageEnum planName;

	/** Stable key consumed by the frontend (e.g. {@code buy_sell}, {@code bank_loans}). */
	@Column(name = "feature_key", nullable = false)
	private String featureKey;

	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	public PlanFeature() {}

	public PlanFeature(MasterEnums.PackageEnum planName, String featureKey, Boolean enabled) {
		this.planName = planName;
		this.featureKey = featureKey;
		this.enabled = enabled;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public MasterEnums.PackageEnum getPlanName() { return planName; }
	public void setPlanName(MasterEnums.PackageEnum planName) { this.planName = planName; }

	public String getFeatureKey() { return featureKey; }
	public void setFeatureKey(String featureKey) { this.featureKey = featureKey; }

	public Boolean getEnabled() { return enabled; }
	public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
