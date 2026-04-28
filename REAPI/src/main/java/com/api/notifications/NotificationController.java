package com.api.notifications;

import com.api.leads.ClientLead;
import com.api.leads.ClientLeadRepository;
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
    private final ClientLeadRepository leadRepository;
    private final UserRepository userRepository;

    public NotificationController(CommService commService, WhatsAppService whatsAppService,
            NotificationService notificationService, ClientLeadRepository leadRepository,
            UserRepository userRepository) {
        this.commService = commService;
        this.whatsAppService = whatsAppService;
        this.notificationService = notificationService;
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    // ─── Direct channel endpoints ─────────────────────────────────────────────

    @PostMapping("/sms")
    public ResponseEntity<String> sendSms(@Valid @RequestBody SmsRequest req) {
        log.info("POST /api/notifications/sms - mobile={}", req.getMobile());
        commService.sendSMSMessage(req.getMobile(), req.getMessage());
        return ResponseEntity.ok("SMS dispatched to " + req.getMobile());
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest req) {
        log.info("POST /api/notifications/email - to={}", req.getEmail());
        commService.sendEmail(req.getEmail(), req.getBody(), req.getSubject());
        return ResponseEntity.ok("Email dispatched to " + req.getEmail());
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<String> sendWhatsApp(@Valid @RequestBody WhatsAppRequest req) {
        log.info("POST /api/notifications/whatsapp - mobile={}", req.getMobile());
        whatsAppService.sendMessage("+91" + req.getMobile(), req.getMessage());
        return ResponseEntity.ok("WhatsApp dispatched to " + req.getMobile());
    }

    // ─── Lead notification re-triggers ───────────────────────────────────────

    @PostMapping("/lead/{id}/inquiry")
    public ResponseEntity<String> retriggerLeadInquiry(@PathVariable Long id) {
        log.info("POST /api/notifications/lead/{}/inquiry - Re-triggering inquiry notification", id);
        ClientLead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + id));
        String[] brokerContact = resolveContact(lead.getBrokerId());
        String[] ownerContact = resolveContact(lead.getPropertyOwnerId());
        notificationService.notifyPropertyInquiry(
                lead.getId(),
                lead.getClientName(), lead.getMobile(), lead.getEmail(),
                lead.getPropertyTitle(), lead.getPropertyCity(), lead.getPropertyPrice(),
                brokerContact[0], brokerContact[1],
                ownerContact[0], ownerContact[1],
                lead.getBudget(), lead.getMessage(), false);
        return ResponseEntity.ok("Inquiry notification re-triggered for lead #" + id);
    }

    @PostMapping("/lead/{id}/status")
    public ResponseEntity<String> retriggerLeadStatus(@PathVariable Long id) {
        log.info("POST /api/notifications/lead/{}/status - Re-triggering status notification", id);
        ClientLead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + id));
        notificationService.notifyLeadStatusUpdated(
                lead.getClientName(), lead.getMobile(), lead.getEmail(),
                lead.getPropertyTitle(), lead.getPropertyCity(),
                lead.getStatus() != null ? lead.getStatus().name() : "UPDATED",
                lead.getRemark());
        return ResponseEntity.ok("Status notification re-triggered for lead #" + id);
    }

    // ─── User notification triggers ───────────────────────────────────────────

    @PostMapping("/user/{id}/welcome")
    public ResponseEntity<String> notifyWelcome(@PathVariable Long id) {
        log.info("POST /api/notifications/user/{}/welcome", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        notificationService.notifyWelcome(user);
        return ResponseEntity.ok("Welcome notification dispatched for userId=" + id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String[] resolveContact(Long userId) {
        if (userId == null) return new String[]{"", ""};
        return userRepository.findById(userId)
                .map(u -> new String[]{
                        u.getEmail() != null ? u.getEmail() : "",
                        u.getMobile() != null ? u.getMobile() : ""})
                .orElse(new String[]{"", ""});
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
