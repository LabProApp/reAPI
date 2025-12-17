CREATE PROCEDURE get_users_for_daily_email()
BEGIN
    SELECT id, email, subject, message
    FROM daily_email_candidates
    WHERE ready_to_send = 1;
END;
