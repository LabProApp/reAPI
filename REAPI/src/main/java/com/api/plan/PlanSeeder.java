package com.api.plan;

import static com.api.plan.PlanFeatureKeys.BANK_LOANS;
import static com.api.plan.PlanFeatureKeys.BUY_SELL;
import static com.api.plan.PlanFeatureKeys.DOCUMENTATION;
import static com.api.plan.PlanFeatureKeys.EMI_CALCULATOR;
import static com.api.plan.PlanFeatureKeys.JOURNEY;
import static com.api.plan.PlanFeatureKeys.POST_PROPERTY;
import static com.api.plan.PlanFeatureKeys.POST_REQUIREMENT;
import static com.api.plan.PlanFeatureKeys.RENT_PG;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.api.enums.MasterEnums;

/**
 * Seeds the {@code plans} and {@code plan_features} tables on startup.
 *
 * <p>Idempotent: only inserts a plan or a feature row if the same
 * (plan, featureKey) tuple is missing. Existing rows (including manual
 * tweaks) are left untouched so ops can edit prices/flags directly in
 * the DB without the seeder undoing the change on next boot.</p>
 */
@Component
public class PlanSeeder implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(PlanSeeder.class);

	private final PlanRepository planRepository;
	private final PlanFeatureRepository planFeatureRepository;

	public PlanSeeder(PlanRepository planRepository, PlanFeatureRepository planFeatureRepository) {
		this.planRepository = planRepository;
		this.planFeatureRepository = planFeatureRepository;
	}

	@Override
	public void run(String... args) {
		seedPlans();
		seedFeatures();
	}

	private void seedPlans() {
		upsertPlan(MasterEnums.PackageEnum.BASIC, "Basic", 0.0);
		upsertPlan(MasterEnums.PackageEnum.DELUX, "Delux", 9999.0);
		upsertPlan(MasterEnums.PackageEnum.PREMIUM, "Premium", 19999.0);
	}

	private void upsertPlan(MasterEnums.PackageEnum name, String displayName, double price) {
		if (planRepository.findByPlanName(name).isPresent()) return;
		planRepository.save(new Plan(name, displayName, price));
		log.info("PlanSeeder - inserted plan {} ({} INR/yr)", name, price);
	}

	private void seedFeatures() {
		// All feature keys with their per-plan defaults. Edit the value
		// triples here to change the tier matrix.
		Map<String, boolean[]> defaults = Map.of(
			//                            BASIC,  DELUX, PREMIUM
			BUY_SELL,         new boolean[] { true,  true,  true  },
			RENT_PG,          new boolean[] { true,  true,  true  },
			EMI_CALCULATOR,   new boolean[] { true,  true,  true  },
			JOURNEY,          new boolean[] { true,  true,  true  },
			BANK_LOANS,       new boolean[] { false, true,  true  },
			DOCUMENTATION,    new boolean[] { false, true,  true  },
			POST_PROPERTY,    new boolean[] { false, true,  true  },
			POST_REQUIREMENT, new boolean[] { false, false, true  }
		);

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
		boolean present = existing.stream().anyMatch(f -> key.equals(f.getFeatureKey()));
		if (present) return;
		planFeatureRepository.save(new PlanFeature(plan, key, enabled));
		log.info("PlanSeeder - inserted feature {} for plan {} = {}", key, plan, enabled);
	}
}
