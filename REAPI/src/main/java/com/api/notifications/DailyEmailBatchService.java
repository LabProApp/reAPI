package com.api.notifications;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.user.User;
import com.api.user.UserRepository;

/**
 * Scheduled service that sends a daily digest email to all registered users.
 *
 * <p>The batch job runs every day at 08:00 AM server time (cron: {@code 0 0 8 * * ?}).
 * It fetches all users from the database and dispatches an email to each user that has
 * a non-blank email address. Emails are sent asynchronously via {@link CommService}.
 *
 * <p>This service requires that Spring's {@code @EnableScheduling} is active on the
 * application context.
 */
@Service
public class DailyEmailBatchService {

    private static final Logger log = LoggerFactory.getLogger(DailyEmailBatchService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommService commService;

    /**
     * Sends the daily update email to all users with a valid email address.
     * Triggered automatically every day at 08:00 AM by the Spring task scheduler.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyEmails() {
        List<User> users = userRepository.findAll();
        log.info("sendDailyEmails - dispatching to {} users", users.size());
        String subject = "Daily Update from KeyBricks";
        String body = "Dear User,\n\nThis is your daily update.\n\nBest regards,\nKeyBricks Team";
        for (User user : users) {
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
                commService.sendEmail(user.getEmail(), body, subject);
                log.debug("sendDailyEmails - queued for {}", user.getEmail());
            }
        }
        log.info("sendDailyEmails - complete");
    }
}
