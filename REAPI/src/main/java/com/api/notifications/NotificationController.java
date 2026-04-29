package com.api.notifications;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.api.leads.ClientLead;
import com.api.leads.ClientLeadRepository;
import com.api.user.User;
import com.api.user.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
        log.info("POST /api/notifications/email - to={}, cc={}, bcc={}", req.getTo(),
                req.getCc() != null ? req.getCc().size() : 0,
                req.getBcc() != null ? req.getBcc().size() : 0);
        commService.sendEmail(EmailMessage.to(req.getTo())
                .subject(req.getSubject())
                .body(req.getBody())
                .html(req.isHtml())
                .cc(req.getCc() != null ? req.getCc().toArray(new String[0]) : new String[0])
                .bcc(req.getBcc() != null ? req.getBcc().toArray(new String[0]) : new String[0])
                .build());
        return ResponseEntity.ok("Email dispatched to " + req.getTo());
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<String> sendWhatsApp(@Valid @RequestBody WhatsAppRequest req) {
        log.info("POST /api/notifications/whatsapp - mobile={}", req.getMobile());
        whatsAppService.sendMessage("+91" + req.getMobile(), req.getMessage());
        return ResponseEntity.ok("WhatsApp dispatched to " + req.getMobile());
    }

    // ─── Template-based send ──────────────────────────────────────────────────

    @PostMapping("/template/send")
    public ResponseEntity<String> sendByTemplate(@Valid @RequestBody TemplateSendRequest req) {
        log.info("POST /api/notifications/template/send - key={}, channels={}", req.getTemplateKey(), req.getChannels());

        NotificationTemplateSet tpl = TemplateRegistry.get(req.getTemplateKey());
        if (tpl == null) return ResponseEntity.badRequest().body("Unknown template key: " + req.getTemplateKey());

        Map<String, String> vars = req.getVariables() != null ? req.getVariables() : Collections.emptyMap();
        Set<String> channels = req.getChannels() != null ? req.getChannels() : Set.of("EMAIL");

        if (channels.contains("SMS") && hasValue(req.getMobile())) {
            String smsBody = TemplateRenderer.render(tpl.getSms(), vars);
            commService.sendSMSMessage(req.getMobile(), smsBody);
        }

        if (channels.contains("WHATSAPP") && hasValue(req.getMobile())) {
            String waBody = TemplateRenderer.render(tpl.getWhatsApp(), vars);
            try { whatsAppService.sendMessage("+91" + req.getMobile(), waBody); }
            catch (Exception e) { log.error("template/send - WhatsApp failed: {}", e.getMessage()); }
        }

        if (channels.contains("EMAIL") && hasValue(req.getEmail())) {
            String subject = TemplateRenderer.render(tpl.getEmailSubject(), vars);
            String body = TemplateRenderer.render(tpl.getEmailBody(), vars);
            commService.sendEmail(EmailMessage.to(req.getEmail())
                    .subject(subject)
                    .body(body)
                    .html(req.isHtml())
                    .cc(req.getCc() != null ? req.getCc().toArray(new String[0]) : new String[0])
                    .bcc(req.getBcc() != null ? req.getBcc().toArray(new String[0]) : new String[0])
                    .build());
        }

        return ResponseEntity.ok("Notification dispatched via " + channels + " for template=" + req.getTemplateKey());
    }

    // ─── List available templates ─────────────────────────────────────────────

    @GetMapping("/templates")
    public ResponseEntity<Set<TemplateKey>> listTemplates() {
        return ResponseEntity.ok(TemplateRegistry.all().keySet());
    }

    // ─── Lead notification re-triggers ───────────────────────────────────────

    @PostMapping("/lead/{id}/inquiry")
    public ResponseEntity<String> retriggerLeadInquiry(@PathVariable Long id) {
        log.info("POST /api/notifications/lead/{}/inquiry", id);
        ClientLead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + id));
        String[] brokerContact = resolveContact(lead.getBrokerId());
        String[] ownerContact = resolveContact(lead.getPropertyOwnerId());
        notificationService.notifyLeadCreated(lead,
                brokerContact[0], brokerContact[1],
                ownerContact[0], ownerContact[1], false);
        return ResponseEntity.ok("Lead notification re-triggered for lead #" + id);
    }

    @PostMapping("/lead/{id}/status")
    public ResponseEntity<String> retriggerLeadStatus(@PathVariable Long id) {
        log.info("POST /api/notifications/lead/{}/status", id);
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

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
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
        private String to;
        @NotBlank(message = "subject is required")
        private String subject;
        @NotBlank(message = "body is required")
        private String body;
        private List<String> cc;
        private List<String> bcc;
        private boolean html;

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public List<String> getCc() { return cc; }
        public void setCc(List<String> cc) { this.cc = cc; }
        public List<String> getBcc() { return bcc; }
        public void setBcc(List<String> bcc) { this.bcc = bcc; }
        public boolean isHtml() { return html; }
        public void setHtml(boolean html) { this.html = html; }
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

    public static class TemplateSendRequest {
        @NotNull(message = "templateKey is required")
        private TemplateKey templateKey;
        private String mobile;
        @Email
        private String email;
        private List<String> cc;
        private List<String> bcc;
        private boolean html;
        private Set<String> channels; // "SMS", "EMAIL", "WHATSAPP"
        private Map<String, String> variables;

        public TemplateKey getTemplateKey() { return templateKey; }
        public void setTemplateKey(TemplateKey templateKey) { this.templateKey = templateKey; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public List<String> getCc() { return cc; }
        public void setCc(List<String> cc) { this.cc = cc; }
        public List<String> getBcc() { return bcc; }
        public void setBcc(List<String> bcc) { this.bcc = bcc; }
        public boolean isHtml() { return html; }
        public void setHtml(boolean html) { this.html = html; }
        public Set<String> getChannels() { return channels; }
        public void setChannels(Set<String> channels) { this.channels = channels; }
        public Map<String, String> getVariables() { return variables; }
        public void setVariables(Map<String, String> variables) { this.variables = variables; }
    }
}
