package com.api.notifications;

import java.security.SecureRandom;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.mail.internet.MimeMessage;

@Service
public class CommService {

    private static final Logger log = LoggerFactory.getLogger(CommService.class);

    @Value("${twilio.accountSid:}")
    private String ACCOUNT_SID;

    @Value("${twilio.authToken:}")
    private String AUTH_TOKEN;

    @Value("${twilio.source.number:}")
    private String SMS_FROM;

    @Value("${spring.mail.username:}")
    private String MAIL_USERNAME;

    @Value("${mail.from:noreply@keybricks.in}")
    private String MAIL_FROM;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public boolean mailConfigured() {
        return MAIL_USERNAME != null && !MAIL_USERNAME.isBlank();
    }

    public boolean smsConfigured() {
        return ACCOUNT_SID != null && !ACCOUNT_SID.isBlank()
                && AUTH_TOKEN != null && !AUTH_TOKEN.isBlank()
                && SMS_FROM != null && !SMS_FROM.isBlank();
    }

    // ─── OTP ─────────────────────────────────────────────────────────────────

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static String mask(String mobile) {
        if (mobile == null || mobile.length() < 4) return "***";
        return mobile.substring(0, 2) + "****" + mobile.substring(mobile.length() - 2);
    }

    /**
     * Normalizes a mobile number to E.164 format for India (+91).
     * Numbers already starting with '+' are returned as-is (international format preserved).
     * A leading '0' (domestic trunk prefix) is stripped before adding the country code.
     */
    static String toE164India(String mobile) {
        if (mobile == null || mobile.isBlank()) return mobile;
        if (mobile.startsWith("+")) return mobile;
        if (mobile.startsWith("0")) mobile = mobile.substring(1);
        return "+91" + mobile;
    }

    public String generateOtp() {
        int otp = SECURE_RANDOM.nextInt(900000) + 100000;
        log.debug("generateOtp - Generated OTP");
        return String.valueOf(otp);
    }

    @Async
    public void sendOtpOnSms(String mobile, String otp) {
        if (!smsConfigured()) {
            log.warn("sendOtpOnSms - SMS not configured; skipping send to {}", mask(mobile));
            return;
        }
        log.info("sendOtpOnSms - mobile={}", mask(mobile));
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message msg = Message.creator(new PhoneNumber(toE164India(mobile)),
                    new PhoneNumber(SMS_FROM),
                    "Your OTP is: " + otp + " (Valid for 10 minutes)").create();
            log.info("sendOtpOnSms - sent mobile={}, sid={}", mask(mobile), msg.getSid());
        } catch (Exception e) {
            log.error("sendOtpOnSms - failed mobile={}: {}", mask(mobile), e.getMessage(), e);
        }
    }

    // ─── SMS ─────────────────────────────────────────────────────────────────

    @Async
    public void sendSMSMessage(String mobile, String txtMessage) {
        try {
            sendSmsNow(mobile, txtMessage);
        } catch (Exception e) {
            log.error("sendSMSMessage - failed mobile={}: {}", mask(mobile), e.getMessage(), e);
        }
    }

    /** Synchronous SMS send — throws on failure. Used by NotificationDispatcher for tracked sends. */
    public void sendSmsNow(String mobile, String txtMessage) throws Exception {
        if (!smsConfigured()) {
            log.warn("sendSmsNow - SMS not configured; skipping send to {}", mask(mobile));
            return;
        }
        if (mobile == null || mobile.isBlank()) {
            log.warn("sendSmsNow - recipientMobile is blank; skipping send");
            return;
        }
        log.info("sendSmsNow - mobile={}", mask(mobile));
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message msg = Message.creator(new PhoneNumber(toE164India(mobile)),
                new PhoneNumber(SMS_FROM),
                txtMessage).create();
        log.info("sendSmsNow - sent mobile={}, sid={}", mask(mobile), msg.getSid());
    }

    // ─── Email (plain text / raw HTML) ───────────────────────────────────────

    @Async
    public void sendEmail(EmailMessage msg) {
        try {
            sendEmailNow(msg);
        } catch (jakarta.mail.AuthenticationFailedException e) {
            log.warn("sendEmail - SMTP auth failed; check credentials: {}", e.getMessage());
        } catch (Exception e) {
            log.error("sendEmail - failed to={}: {}", msg.getTo(), e.getMessage(), e);
        }
    }

    @Async
    public void sendEmail(String to, String body, String subject) {
        sendEmail(EmailMessage.to(to).subject(subject).body(body).build());
    }

    /** Synchronous email send — throws on failure. Used by NotificationDispatcher for tracked sends. */
    public void sendEmailNow(EmailMessage msg) throws Exception {
        if (!mailConfigured()) {
            log.warn("sendEmailNow - SMTP not configured; skipping send to {}", msg.getTo());
            return;
        }
        log.info("sendEmailNow - to={}, subject={}", msg.getTo(), msg.getSubject());
        MimeMessage mimeMsg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, false, "UTF-8");
        helper.setFrom(MAIL_FROM);
        helper.setTo(msg.getTo());
        if (msg.getCc() != null && msg.getCc().length > 0) helper.setCc(msg.getCc());
        if (msg.getBcc() != null && msg.getBcc().length > 0) helper.setBcc(msg.getBcc());
        helper.setSubject(msg.getSubject());
        helper.setText(msg.getBody(), msg.isHtml());
        mailSender.send(mimeMsg);
        log.info("sendEmailNow - sent to={}", msg.getTo());
    }

    // ─── HTML Template Email (Thymeleaf) ─────────────────────────────────────

    /** Renders a Thymeleaf template and returns the HTML string without sending. */
    public String renderEmailTemplate(String template, Map<String, Object> variables) {
        Context ctx = new Context();
        if (variables != null) ctx.setVariables(variables);
        return templateEngine.process("email/" + template, ctx);
    }

    /**
     * Renders a Thymeleaf template from {@code templates/email/<template>.html}
     * and sends it as an HTML email. Synchronous — throws on failure.
     */
    public void sendTemplateEmailNow(String to, String subject, String template,
            Map<String, Object> variables) throws Exception {
        if (!mailConfigured()) {
            log.warn("sendTemplateEmailNow - SMTP not configured; skipping send to {}", to);
            return;
        }
        Context ctx = new Context();
        if (variables != null) ctx.setVariables(variables);
        String html = templateEngine.process("email/" + template, ctx);
        sendEmailNow(EmailMessage.to(to).subject(subject).body(html).html(true).build());
    }

    /**
     * Async variant of {@link #sendTemplateEmailNow} — fire-and-forget.
     * Prefer {@link #sendTemplateEmailNow} when tracking delivery status.
     */
    @Async
    public void sendTemplateEmail(String to, String subject, String template,
            Map<String, Object> variables) {
        try {
            sendTemplateEmailNow(to, subject, template, variables);
        } catch (Exception e) {
            log.error("sendTemplateEmail - failed to={}, template={}: {}", to, template, e.getMessage(), e);
        }
    }
}
