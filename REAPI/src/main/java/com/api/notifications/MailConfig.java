package com.api.notifications;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Value("${mail.provider:gmail}") // default gmail
    private String mailProvider;

    @Value("${aws.ses.region:ap-south-1}")
    private String sesRegion;

    
    @Bean
    public JavaMailSender javaMailSender(Environment env) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        if ("gmail".equalsIgnoreCase(mailProvider)) {

            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);

        } else if ("ses".equalsIgnoreCase(mailProvider)) {

            mailSender.setHost("email-smtp." + sesRegion + ".amazonaws.com");
            mailSender.setPort(587);

        } else {
            throw new IllegalArgumentException(
                "Invalid mail.provider value: " + mailProvider +
                " (allowed: gmail, ses)"
            );
        }

        mailSender.setUsername(env.getProperty("spring.mail.username"));
        mailSender.setPassword(env.getProperty("spring.mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Timeouts (important in production)
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        // Debug (turn on only for troubleshooting)
        props.put("mail.debug", env.getProperty("mail.debug", "false"));

        return mailSender;
    }
}
