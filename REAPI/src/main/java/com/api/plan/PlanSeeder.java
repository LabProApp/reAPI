package com.api.plan;

import static com.api.plan.PlanFeatureKeys.BANK_LOANS;
import static com.api.plan.PlanFeatureKeys.BUY_SELL;
import static com.api.plan.PlanFeatureKeys.DOCUMENTATION;
import static com.api.plan.PlanFeatureKeys.EMI_CALCULATOR;
import static com.api.plan.PlanFeatureKeys.JOURNEY;
import static com.api.plan.PlanFeatureKeys.POST_PROPERTY;
import static com.api.plan.PlanFeatureKeys.POST_REQUIREMENT;
import static com.api.plan.PlanFeatureKeys.RENT_PG;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.api.enums.MasterEnums;

/**
 * Seeds (and optionally keeps in sync) the {@code plans} and
 * {@code plan_features} tables on startup.
 *
 * <p><b>Modes (controlled by {@code app.plan.seeder.sync}):</b>
 * <ul>
 *   <li><b>false</b> (default) — Insert-only. Missing rows are inserted
 *       with the canonical defaults; existing rows are left untouched so
 *       ops can edit prices / flags directly in the DB without the
 *       seeder undoing the change on next boot. <strong>Use this in
 *       production.</strong></li>
 *   <li><b>true</b> — Sync. Existing rows whose value differs from the
 *       canonical default are <em>updated</em>. Use during development
 *       when you're iterating on the tier matrix and want changes here
 *       to flow through to existing seeded rows.</li>
 * </ul>
 */
@Component
public class PlanSeeder implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(PlanSeeder.class);

	private final PlanRepository planRepository;
	private final PlanFeatureRepository planFeatureRepository;

	@Value("${app.plan.seeder.sync:false}")
	private boolean syncMode;

	public PlanSeeder(PlanRepository planRepository, PlanFeatureRepository planFeatureRepository) {
		this.planRepository = planRepository;
		this.planFeatureRepository = planFeatureRepository;
	}

	@Override
	public void run(String... args) {
		log.info("PlanSeeder - starting (syncMode={})", syncMode);
		seedPlans();
		seedFeatures();
	}

	private void seedPlans() {
		//                                                       priceYearly  propertyLimit
		upsertPlan(MasterEnums.PackageEnum.BASIC,   "Basic",     0.0,         0);
		upsertPlan(MasterEnums.PackageEnum.DELUX,   "Delux",     9999.0,      250);
		upsertPlan(MasterEnums.PackageEnum.PREMIUM, "Premium",   19999.0,     500);
	}

	private void upsertPlan(MasterEnums.PackageEnum name, String displayName,
			double price, int propertyLimit) {
		Plan existing = planRepository.findByPlanName(name).orElse(null);
		if (existing == null) {
			planRepository.save(new Plan(name, displayName, price, propertyLimit));
			log.info("PlanSeeder - inserted plan {} ({} INR/yr, propertyLimit={})", name, price, propertyLimit);
			return;
		}
		if (!syncMode) return;

		boolean changed = false;
		if (!displayName.equals(existing.getDisplayName())) {
			existing.setDisplayName(displayName); changed = true;
		}
		if (existing.getPriceYearly() == null || existing.getPriceYearly() != price) {
			existing.setPriceYearly(price); changed = true;
		}
		if (existing.getPropertyLimit() == null || existing.getPropertyLimit() != propertyLimit) {
			existing.setPropertyLimit(propertyLimit); changed = true;
		}
		if (changed) {
			planRepository.save(existing);
			log.info("PlanSeeder - synced plan {} (priceYearly={}, propertyLimit={})", name, price, propertyLimit);
		}
	}

	private void seedFeatures() {
		// All feature keys with their per-plan defaults.
		// LinkedHashMap preserves declared order for readable logs.
		Map<String, boolean[]> defaults = new LinkedHashMap<>();
		//                              BASIC, DELUX, PREMIUM
		defaults.put(BUY_SELL,         new boolean[] { true,  true,  true  });
		defaults.put(RENT_PG,          new boolean[] { true,  true,  true  });
		defaults.put(EMI_CALCULATOR,   new boolean[] { true,  true,  true  });
		defaults.put(JOURNEY,          new boolean[] { true,  true,  true  });
		defaults.put(BANK_LOANS,       new boolean[] { false, true,  true  });
		defaults.put(POST_PROPERTY,    new boolean[] { false, true,  true  });
		defaults.put(POST_REQUIREMENT, new boolean[] { false, false, true  });
		// Documentation is a PREMIUM-only feature.
		defaults.put(DOCUMENTATION,    new boolean[] { false, false, true  });

		MasterEnums.PackageEnum[] plans = {
			MasterEnums.PackageEnum.BASIC,
			MasterEnums.PackageEnum.DELUX,
			MasterEnums.PackageEnum.PREMIUM,
		};

		for (Map.Entry<String, boolean[]> entry : defaults.entrySet()) {
			final String key = entry.getKey();
			final boolean[] enabled = entry.getValue();
			for (int i = 0; i < plans.length; i++) {
				upsertFeature(plans[i], key, enabled[i]);
			}
		}
	}

	private void upsertFeature(MasterEnums.PackageEnum plan, String key, boolean enabled) {
		List<PlanFeature> existing = planFeatureRepository.findByPlanName(plan);
		PlanFeature row = existing.stream()
			.filter(f -> key.equals(f.getFeatureKey()))
			.findFirst()
			.orElse(null);

		if (row == null) {
			planFeatureRepository.save(new PlanFeature(plan, key, enabled));
			log.info("PlanSeeder - inserted feature {} for plan {} = {}", key, plan, enabled);
			return;
		}
		if (!syncMode) return;

		if (!Boolean.valueOf(enabled).equals(row.getEnabled())) {
			row.setEnabled(enabled);
			planFeatureRepository.save(row);
			log.info("PlanSeeder - synced feature {} for plan {} -> {}", key, plan, enabled);
		}
	}
}
