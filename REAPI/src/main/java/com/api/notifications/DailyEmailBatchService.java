package com.api.notifications;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.api.user.User;
import com.api.user.UserRepository;

@Service
public class DailyEmailBatchService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Scheduled to run every day at 8 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyEmails() {
        List<User> users = userRepository.findAll();

        String subject = "Daily Update from Real Estate API";
        String body = "Dear User,\n\nThis is your daily update email.\n\nBest regards,\nReal Estate API Team";

        for (User user : users) {
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                try {
                    emailService.sendEmail(user.getEmail(), subject, body);
                    System.out.println("Email sent to: " + user.getEmail());
                } catch (Exception e) {
                    System.err.println("Failed to send email to: " + user.getEmail() + " - " + e.getMessage());
                }
            }
        }
    }
}