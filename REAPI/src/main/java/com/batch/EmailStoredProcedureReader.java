package com.batch;
import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class EmailStoredProcedureReader extends JdbcCursorItemReader<EmailRecord> {

	public EmailStoredProcedureReader(DataSource dataSource) {
		
		System.out.println("Running batch finally for sending email in loop");
		/*
		 * setDataSource(dataSource); setSql("CALL get_users_for_daily_email()");
		 * setRowMapper((rs, rowNum) -> new EmailRecord( rs.getLong("id"),
		 * rs.getString("email"), rs.getString("subject"), rs.getString("message") ));
		 * setFetchSize(100);
		 */}
}
