package com.batch;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class EmailStoredProcedureReader extends JdbcCursorItemReader<EmailRecord> {

    private static final Logger log =
            LoggerFactory.getLogger(EmailStoredProcedureReader.class);

    public EmailStoredProcedureReader(DataSource dataSource) {

        log.info("Initializing EmailStoredProcedureReader");
        log.debug("Using stored procedure: get_users_for_daily_email()");
        log.debug("Fetch size set to 100");

        setDataSource(dataSource);
        setSql("CALL get_users_for_daily_email()");
        setFetchSize(100);

        setRowMapper((rs, rowNum) -> {
            EmailRecord record = new EmailRecord(
                    rs.getLong("id"),
                    rs.getString("email"),
                    rs.getString("subject"),
                    rs.getString("message")
            );

            log.trace("Read row {} -> EmailRecord[id={}, email={}]",
                    rowNum, record.getId(), record.getEmail());

            return record;
        });
    }
}
