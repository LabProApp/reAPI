package com.api.commons;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Idempotent data migrations that run on every boot.
 *
 * <p>We intentionally avoid Flyway/Liquibase here — they're the right tool
 * long-term, but for now this runner gives us the one property that
 * matters: "deploying the app applies the data migrations." Every
 * statement below is a no-op if it has already been applied (e.g.
 * "UPDATE rows WHERE old_value", "INSERT IF NOT EXISTS"), so re-running
 * is safe.</p>
 *
 * <p>Schema DDL is still handled by Hibernate's {@code ddl-auto=update}
 * (entities → tables on boot). This runner only patches <em>data</em>:
 * legacy plan enums, feature-flag flips, subscription backfill, etc.</p>
 *
 * <p>Ordered after {@link com.api.plan.PlanSeeder} (which is unordered
 * and runs first by default) so that {@code plans} / {@code plan_features}
 * rows exist before we touch them.</p>
 */
@Component
@Order(100)
public class DataMigrationRunner implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DataMigrationRunner.class);

	private final JdbcTemplate jdbc;

	public DataMigrationRunner(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}

	@Override
	public void run(String... args) {
		log.info("DataMigrationRunner - starting");
		// Each step is wrapped so a single failure doesn't abort the whole
		// app boot. A migration that can't apply now will be retried on
		// next restart.
		safeRun("enum columns → varchar",   this::convertEnumColumnsToVarchar);
		safeRun("fix indexes",             this::fixIndexes);
		safeRun("legacy plan enums",       this::migrateLegacyPlanEnums);
		safeRun("subscription backfill",   this::backfillSubscriptionWindow);
		safeRun("post_requirement BASIC",  this::movePostRequirementToBasic);
		log.info("DataMigrationRunner - done");
	}

	// ── Steps ───────────────────────────────────────────────────────────────

	/**
	 * Drops and recreates indexes whose column references were wrong (Java field
	 * names used instead of DB column names). ddl-auto=update won't fix these
	 * because the index already exists under the wrong name.
	 */
	private void fixIndexes() {
		// client_lead: brokerId → broker_id, propertyOwnerId → property_owner_id
		jdbc.execute("ALTER TABLE client_lead DROP INDEX idx_client_lead_broker_id");
		jdbc.execute("ALTER TABLE client_lead ADD  INDEX idx_client_lead_broker_id (broker_id)");
		jdbc.execute("ALTER TABLE client_lead DROP INDEX idx_client_lead_owner_id");
		jdbc.execute("ALTER TABLE client_lead ADD  INDEX idx_client_lead_owner_id (property_owner_id)");
		log.info("DataMigrationRunner - client_lead indexes rebuilt on correct columns");
	}

	/**
	 * ddl-auto=update adds new enum constants to Java but never widens an
	 * existing MySQL ENUM column. Convert any ENUM columns to VARCHAR so new
	 * values can be stored and old rows can be updated. Safe to re-run — MySQL
	 * is a no-op if the column is already VARCHAR.
	 */
	private void convertEnumColumnsToVarchar() {
		// user_package was seeded as ENUM('REGULAR','ELITE'); new values BASIC/
		// DELUX/PREMIUM were added later and are not in the MySQL ENUM definition.
		jdbc.execute("ALTER TABLE users MODIFY COLUMN user_package VARCHAR(50)");

		// client_lead enum columns carry the same risk for new InquiryType / LeadStatus values.
		jdbc.execute("ALTER TABLE client_lead MODIFY COLUMN lead_type VARCHAR(50)");
		jdbc.execute("ALTER TABLE client_lead MODIFY COLUMN status VARCHAR(30)");

		log.info("DataMigrationRunner - enum columns converted to varchar");
	}

	/** REGULAR -> BASIC, ELITE -> PREMIUM, NULL -> BASIC on the users table. */
	private void migrateLegacyPlanEnums() {
		int regular = jdbc.update(
			"UPDATE users SET user_package = 'BASIC' WHERE user_package = 'REGULAR'");
		int elite = jdbc.update(
			"UPDATE users SET user_package = 'PREMIUM' WHERE user_package = 'ELITE'");
		int nulls = jdbc.update(
			"UPDATE users SET user_package = 'BASIC' WHERE user_package IS NULL");
		if (regular + elite + nulls > 0) {
			log.info("DataMigrationRunner - legacy plan enums: REGULAR->BASIC={}, ELITE->PREMIUM={}, NULL->BASIC={}",
					regular, elite, nulls);
		}
	}

	/**
	 * Any existing DELUX / PREMIUM user without a subscription_end_at gets
	 * a 1-year window starting now. Only touches NULL rows so re-runs are
	 * no-ops.
	 */
	private void backfillSubscriptionWindow() {
		int filled = jdbc.update("""
			UPDATE users
			SET subscription_start_at = COALESCE(subscription_start_at, NOW()),
			    subscription_end_at   = COALESCE(subscription_end_at, DATE_ADD(NOW(), INTERVAL 1 YEAR))
			WHERE user_package IN ('DELUX', 'PREMIUM')
			  AND subscription_end_at IS NULL
			""");
		if (filled > 0) {
			log.info("DataMigrationRunner - backfilled subscription window on {} paying user(s)", filled);
		}
	}

	/**
	 * Post Requirement was originally seeded PREMIUM-only; we moved it to
	 * BASIC. Seeder runs insert-only in prod, so any pre-existing
	 * BASIC/DELUX rows still have {@code enabled=0}. This flips them.
	 */
	private void movePostRequirementToBasic() {
		int flipped = jdbc.update("""
			UPDATE plan_features
			SET enabled = 1
			WHERE feature_key = 'post_requirement'
			  AND plan_name IN ('BASIC', 'DELUX')
			  AND enabled = 0
			""");
		if (flipped > 0) {
			log.info("DataMigrationRunner - enabled post_requirement on {} plan(s)", flipped);
		}
	}

	// ── helpers ─────────────────────────────────────────────────────────────

	@FunctionalInterface
	private interface Step {
		void run() throws DataAccessException;
	}

	private void safeRun(String name, Step step) {
		try {
			step.run();
		} catch (DataAccessException e) {
			// Don't abort the boot; just record and move on.
			log.warn("DataMigrationRunner - step '{}' failed: {}", name, e.getMostSpecificCause().getMessage());
		} catch (Exception e) {
			log.warn("DataMigrationRunner - step '{}' threw: {}", name, e.getMessage());
		}
	}
}
