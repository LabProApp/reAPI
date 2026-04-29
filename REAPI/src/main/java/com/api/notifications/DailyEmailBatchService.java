package com.api.notifications;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.user.User;
import com.api.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DailyEmailBatchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommService commService;

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
