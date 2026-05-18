package com.api.plan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.api.enums.MasterEnums;

/**
 * Read-side helper for plans and their feature flags. The seed is owned by
 * {@link PlanSeeder}; this service is only responsible for assembling the
 * frontend-facing view (a flat {@code Map<featureKey, enabled>}).
 */
@Service
public class PlanService {

	private static final Logger log = LoggerFactory.getLogger(PlanService.class);

	private final PlanRepository planRepository;
	private final PlanFeatureRepository planFeatureRepository;

	public PlanService(PlanRepository planRepository, PlanFeatureRepository planFeatureRepository) {
		this.planRepository = planRepository;
		this.planFeatureRepository = planFeatureRepository;
	}

	/**
	 * Returns the effective feature map for the given plan. Unknown / legacy
	 * plans (REGULAR, ELITE) fall back to BASIC so existing users never see
	 * an empty dashboard. Returns an empty map only if the seed is missing.
	 */
	public Map<String, Boolean> getFeatureFlags(MasterEnums.PackageEnum plan) {
		MasterEnums.PackageEnum effective = normalize(plan);
		List<PlanFeature> rows = planFeatureRepository.findByPlanName(effective);
		Map<String, Boolean> flags = new HashMap<>();
		for (PlanFeature f : rows) {
			flags.put(f.getFeatureKey(), Boolean.TRUE.equals(f.getEnabled()));
		}
		return flags;
	}

	/** Annual price (in INR) for the given plan; 0 if unknown. */
	public double getPriceYearly(MasterEnums.PackageEnum plan) {
		MasterEnums.PackageEnum effective = normalize(plan);
		return planRepository.findByPlanName(effective)
				.map(Plan::getPriceYearly)
				.orElse(0.0);
	}

	/**
	 * Max number of property listings the plan allows. 0 = posting blocked.
	 * Unknown / legacy plans (REGULAR, ELITE) fall back to BASIC.
	 */
	public int getPropertyLimit(MasterEnums.PackageEnum plan) {
		MasterEnums.PackageEnum effective = normalize(plan);
		return planRepository.findByPlanName(effective)
				.map(Plan::getPropertyLimit)
				.orElse(0);
	}

	/**
	 * Maps any stored plan value to one of the three canonical tiers.
	 * Explicit mapping for legacy values so existing customers don't
	 * silently lose features.
	 */
	private MasterEnums.PackageEnum normalize(MasterEnums.PackageEnum plan) {
		if (plan == null) return MasterEnums.PackageEnum.BASIC;
		switch (plan) {
			case BASIC:
			case DELUX:
			case PREMIUM:
				return plan;
			case REGULAR:
				// REGULAR was the old free tier — equivalent to BASIC today.
				return MasterEnums.PackageEnum.BASIC;
			case ELITE:
				// ELITE was the old top tier — promote to PREMIUM so paying
				// customers keep their features until they're migrated in SQL.
				return MasterEnums.PackageEnum.PREMIUM;
			default:
				// We added a new PackageEnum value but forgot to map it here.
				// Loud-log so QA notices; fall back to BASIC to stay safe.
				log.warn("PlanService.normalize - unmapped plan value {} — falling back to BASIC. "
						+ "Add an explicit case for this value.", plan);
				return MasterEnums.PackageEnum.BASIC;
		}
	}
}
