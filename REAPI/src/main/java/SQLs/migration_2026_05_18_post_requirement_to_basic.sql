-- =============================================================================
--  Move the `post_requirement` feature down to the BASIC tier.
--
--  Originally seeded as PREMIUM-only. We've decided to expose it to all
--  paying and free users as a lead-collection hook. The seeder defaults
--  have been updated (PlanSeeder.java), but since the seeder runs
--  insert-only in prod (PLAN_SEEDER_SYNC=false), existing rows must be
--  flipped explicitly.
--
--  Safe to run multiple times — idempotent.
-- =============================================================================

START TRANSACTION;

-- Preview before running:
-- SELECT plan_name, feature_key, enabled
-- FROM plan_features
-- WHERE feature_key = 'post_requirement';

UPDATE plan_features
SET enabled = 1
WHERE feature_key = 'post_requirement'
  AND plan_name IN ('BASIC', 'DELUX')
  AND enabled = 0;

-- Verify:
-- SELECT plan_name, feature_key, enabled
-- FROM plan_features
-- WHERE feature_key = 'post_requirement';
-- Expect: BASIC=1, DELUX=1, PREMIUM=1.

COMMIT;
-- ROLLBACK;
