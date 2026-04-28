package com.api.notifications;

import com.api.inquiry.Inquiry;
import com.api.inquiry.InquiryRepository;
import com.api.user.User;
import com.api.user.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification APIs", description = "Send SMS, Email, WhatsApp and trigger template-based notifications")
public class NotificationController {

    private final CommService commService;
    private final WhatsAppService whatsAppService;
    private final NotificationService notificationService;
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public NotificationController(CommService commService, WhatsAppService whatsAppService,
            NotificationService notificationService, InquiryRepository inquiryRepository,
            UserRepository userRepository) {
        this.commService = commService;
        this.whatsAppService = whatsAppService;
        this.notificationService = notificationService;
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
    }

    // ─── Direct channel endpoints ─────────────────────────────────────────────

    @PostMapping("/sms")
    public ResponseEntity<String> sendSms(@Valid @RequestBody SmsRequest req) {
        log.info("POST /api/notifications/sms - Sending SMS to mobile={}", req.getMobile());
        commService.sendSMSMessage(req.getMobile(), req.getMessage());
        return ResponseEntity.ok("SMS dispatched to " + req.getMobile());
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest req) {
        log.info("POST /api/notifications/email - Sending email to={}", req.getEmail());
        commService.sendEmail(req.getEmail(), req.getBody(), req.getSubject());
        return ResponseEntity.ok("Email dispatched to " + req.getEmail());
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<String> sendWhatsApp(@Valid @RequestBody WhatsAppRequest req) {
        log.info("POST /api/notifications/whatsapp - Sending WhatsApp to mobile={}", req.getMobile());
        whatsAppService.sendMessage("+91" + req.getMobile(), req.getMessage());
        return ResponseEntity.ok("WhatsApp dispatched to " + req.getMobile());
    }

    // ─── Inquiry template triggers ────────────────────────────────────────────

    @PostMapping("/inquiry/{id}/created")
    public ResponseEntity<String> notifyInquiryCreated(@PathVariable Long id) {
        log.info("POST /api/notifications/inquiry/{}/created - Re-triggering inquiry created notification", id);
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found: " + id));
        notificationService.notifyInquiryCreated(inquiry);
        return ResponseEntity.ok("Inquiry created notification dispatched for INQ-" + id);
    }

    @PostMapping("/inquiry/{id}/status")
    public ResponseEntity<String> notifyInquiryStatus(@PathVariable Long id) {
        log.info("POST /api/notifications/inquiry/{}/status - Re-triggering inquiry status notification", id);
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found: " + id));
        notificationService.notifyInquiryStatusUpdated(inquiry);
        return ResponseEntity.ok("Inquiry status notification dispatched for INQ-" + id);
    }

    // ─── User template triggers ───────────────────────────────────────────────

    @PostMapping("/user/{id}/welcome")
    public ResponseEntity<String> notifyWelcome(@PathVariable Long id) {
        log.info("POST /api/notifications/user/{}/welcome - Re-triggering welcome notification", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        notificationService.notifyWelcome(user);
        return ResponseEntity.ok("Welcome notification dispatched for userId=" + id);
    }

    // ─── Request DTOs ─────────────────────────────────────────────────────────

    public static class SmsRequest {
        @NotBlank(message = "mobile is required")
        private String mobile;
        @NotBlank(message = "message is required")
        private String message;

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class EmailRequest {
        @NotBlank @Email
        private String email;
        @NotBlank(message = "subject is required")
        private String subject;
        @NotBlank(message = "body is required")
        private String body;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }

    public static class WhatsAppRequest {
        @NotBlank(message = "mobile is required")
        private String mobile;
        @NotBlank(message = "message is required")
        private String message;

        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
