-- =============================================================================
--  One-time migration to bring existing prod data in line with the
--  BASIC / DELUX / PREMIUM tier model introduced on 2026-05-18.
--
--  Safe to run on a database where ddl-auto=update has already created
--  the new `plans`, `plan_features`, and `users.subscription_*` columns.
--  Wrap in a transaction; verify SELECTs first; then commit.
--
--  Order matters. Read top-to-bottom before running.
-- =============================================================================

START TRANSACTION;

-- -----------------------------------------------------------------------------
-- 1. Migrate legacy enum values on the users table.
--
--    REGULAR was the old free tier  -> BASIC   (no entitlements).
--    ELITE   was the old top tier   -> PREMIUM (full entitlements).
--
--    PlanService.normalize() already maps these in-memory, but we update the
--    column so audits / admin tools see a consistent value.
-- -----------------------------------------------------------------------------

UPDATE users
SET user_package = 'BASIC'
WHERE user_package = 'REGULAR';

UPDATE users
SET user_package = 'PREMIUM'
WHERE user_package = 'ELITE';

-- New signups default to BASIC; backfill any nulls left from before that
-- default was added.
UPDATE users
SET user_package = 'BASIC'
WHERE user_package IS NULL;

-- -----------------------------------------------------------------------------
-- 2. Grandfather paying DELUX customers who already had Documentation.
--
--    Documentation moved from DELUX+PREMIUM to PREMIUM-only. Existing DELUX
--    customers who were sold Documentation as part of their bundle would lose
--    it silently on next boot when the seeder syncs the flag. Two options:
--
--      a) Promote them to PREMIUM for the remainder of their term (chosen
--         here — preserves the benefit they paid for; communicate to
--         customer support).
--      b) Leave them on DELUX and accept they lose Documentation. To go
--         this route, comment out the UPDATE below and notify customers.
--
--    Adjust the customer list to your prod data before running.
-- -----------------------------------------------------------------------------

-- Preview which DELUX users this affects:
-- SELECT id, name, email, mobile, user_package FROM users WHERE user_package = 'DELUX';

-- UPDATE users
-- SET user_package = 'PREMIUM'
-- WHERE user_package = 'DELUX'
--   AND id IN ( /* list of DELUX customer ids who paid for Documentation */ );

-- -----------------------------------------------------------------------------
-- 3. Backfill subscription_start_at / subscription_end_at for paying users.
--
--    The two columns were added by JPA's ddl-auto=update; existing rows have
--    NULL. effectivePackage() treats NULL end-date as "no expiry" — fine for
--    BASIC, but DELUX/PREMIUM rows must have an end-date for renewal flows
--    to work later. Default each paying user to a 1-year window ending one
--    year from today. Adjust per-customer if you have real billing data.
-- -----------------------------------------------------------------------------

UPDATE users
SET subscription_start_at = NOW(),
    subscription_end_at   = DATE_ADD(NOW(), INTERVAL 1 YEAR)
WHERE user_package IN ('DELUX', 'PREMIUM')
  AND subscription_start_at IS NULL;

-- -----------------------------------------------------------------------------
-- 4. Verification queries — run before COMMIT.
-- -----------------------------------------------------------------------------

-- Plan distribution after migration:
-- SELECT user_package, COUNT(*) AS users FROM users GROUP BY user_package;

-- Paid users with their subscription window:
-- SELECT id, email, user_package, subscription_start_at, subscription_end_at
-- FROM users
-- WHERE user_package IN ('DELUX', 'PREMIUM');

-- Plans + per-plan features the seeder populated (should already exist):
-- SELECT * FROM plans;
-- SELECT plan_name, feature_key, enabled FROM plan_features ORDER BY plan_name, feature_key;

COMMIT;
-- ROLLBACK;  -- swap COMMIT for this line if any verification looks wrong
