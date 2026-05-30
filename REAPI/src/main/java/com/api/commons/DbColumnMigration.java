package com.api.commons;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * One-shot ALTER TABLE migrations for columns that ddl-auto=update cannot fix
 * (e.g. changing a MySQL ENUM column to VARCHAR, or widening column length).
 * Each statement is idempotent — safe to re-run on every startup.
 */
@Component
public class DbColumnMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DbColumnMigration.class);

    private final DataSource dataSource;

    public DbColumnMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("DbColumnMigration: running column migrations");

        // client_lead.lead_type was created as a MySQL ENUM; new InquiryType values
        // added to the Java enum are rejected with "Data truncated for column lead_type".
        // Convert to VARCHAR(50) so all current and future enum values are accepted.
        runSql("ALTER TABLE client_lead MODIFY COLUMN lead_type VARCHAR(50) NOT NULL",
               "client_lead.lead_type → VARCHAR(50)");

        // Same risk exists for status columns stored as MySQL ENUM.
        runSql("ALTER TABLE client_lead MODIFY COLUMN status VARCHAR(30)",
               "client_lead.status → VARCHAR(30)");

        // property.rent was the original column name; Spring naming strategy now maps
        // the rentOrSale field to rent_or_sale. Rename so Hibernate validation passes.
        runSql("ALTER TABLE property RENAME COLUMN rent TO rent_or_sale",
               "property.rent → rent_or_sale");

        // idx_property_posted_by_user was created with column 'postedByUser' (Java field
        // name) instead of the actual DB column 'posted_by_user_id'. Drop and recreate.
        runSql("ALTER TABLE property DROP INDEX idx_property_posted_by_user",
               "drop idx_property_posted_by_user (wrong column)");
        runSql("ALTER TABLE property ADD INDEX idx_property_posted_by_user (posted_by_user_id)",
               "create idx_property_posted_by_user on posted_by_user_id");

        // Same issue for the rentOrSale index — column name is rent_or_sale, not rentOrSale.
        runSql("ALTER TABLE property DROP INDEX idx_property_rent_or_sale",
               "drop idx_property_rent_or_sale (wrong column)");
        runSql("ALTER TABLE property ADD INDEX idx_property_rent_or_sale (rent_or_sale)",
               "create idx_property_rent_or_sale on rent_or_sale");

        log.info("DbColumnMigration: done");
    }

    private void runSql(String sql, String description) {
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute(sql);
            log.info("DbColumnMigration OK: {}", description);
        } catch (Exception e) {
            // Log as warning — a failure here means the column is already correct
            // or the table doesn't exist yet (Hibernate will create it).
            log.warn("DbColumnMigration skipped [{}]: {}", description, e.getMessage());
        }
    }
}
