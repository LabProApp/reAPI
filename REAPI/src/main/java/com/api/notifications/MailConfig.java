package com.api.notifications;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Spring {@code @Configuration} class that creates and configures the {@link JavaMailSender} bean.
 *
 * <p>Supports two mail providers selected via the {@code mail.provider} application property:
 * <ul>
 *   <li>{@code gmail} (default) — connects to {@code smtp.gmail.com:587}</li>
 *   <li>{@code ses} — connects to AWS SES SMTP endpoint in the region specified by
 *       {@code aws.ses.region} (default {@code ap-south-1})</li>
 * </ul>
 *
 * <p>SMTP credentials are read from {@code spring.mail.username} and
 * {@code spring.mail.password}. STARTTLS and authentication are always enabled.
 * Connection, read, and write timeouts are set to 5 seconds each for production safety.
 * Debug output is controlled by the {@code mail.debug} property (defaults to {@code false}).
 *
 * @throws IllegalArgumentException if {@code mail.provider} is set to an unsupported value
 */
@Configuration
public class MailConfig {

    @Value("${mail.provider:gmail}") // default gmail
    private String mailProvider;

    @Value("${aws.ses.region:ap-south-1}")
    private String sesRegion;

    /**
     * Creates and configures a {@link JavaMailSender} bean based on the active mail provider.
     *
     * @param env the Spring {@link Environment} used to resolve SMTP credentials and debug flag
     * @return a fully configured {@link JavaMailSender} instance
     * @throws IllegalArgumentException if the {@code mail.provider} property is not {@code gmail} or {@code ses}
     */
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
