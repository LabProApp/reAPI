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

/**
 * REST controller that exposes notification endpoints for the real estate platform.
 *
 * <p>Provides three categories of endpoints:
 * <ul>
 *   <li><b>Direct channel</b> — {@code POST /sms}, {@code POST /email}, {@code POST /whatsapp}:
 *       send a raw message on a single channel.</li>
 *   <li><b>Template-based</b> — {@code POST /template/send}: resolve a {@link TemplateKey},
 *       render {@code {{placeholder}}} variables via {@link TemplateRenderer}, and dispatch
 *       across the requested channels.</li>
 *   <li><b>Re-trigger</b> — lead and user notification re-triggers that reload entity data
 *       from the database and re-dispatch the standard notifications.</li>
 * </ul>
 *
 * <p>Base path: {@code /api/notifications}
 */
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

    /**
     * Constructs the {@code NotificationController} with all required service dependencies.
     *
     * @param commService          low-level email/SMS communication service
     * @param whatsAppService      WhatsApp messaging service
     * @param notificationService  orchestration service for business-level notifications
     * @param leadRepository       repository for retrieving lead entities
     * @param userRepository       repository for retrieving user entities
     */
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

    /**
     * Sends a plain-text SMS to the specified mobile number.
     *
     * @param req the request body containing the destination mobile and message text
     * @return a confirmation message indicating the SMS was dispatched
     */
    @PostMapping("/sms")
    public ResponseEntity<String> sendSms(@Valid @RequestBody SmsRequest req) {
        log.info("POST /api/notifications/sms - mobile={}", req.getMobile());
        commService.sendSMSMessage(req.getMobile(), req.getMessage());
        return ResponseEntity.ok("SMS dispatched to " + req.getMobile());
    }

    /**
     * Sends an email with optional CC, BCC, and HTML body.
     *
     * @param req the request body containing the email recipient, subject, body, and optional CC/BCC lists
     * @return a confirmation message indicating the email was dispatched
     */
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

    /**
     * Sends a WhatsApp message to the specified mobile number via Twilio.
     *
     * @param req the request body containing the destination mobile and message text
     * @return a confirmation message indicating the WhatsApp message was dispatched
     */
    @PostMapping("/whatsapp")
    public ResponseEntity<String> sendWhatsApp(@Valid @RequestBody WhatsAppRequest req) {
        log.info("POST /api/notifications/whatsapp - mobile={}", req.getMobile());
        whatsAppService.sendMessage("+91" + req.getMobile(), req.getMessage());
        return ResponseEntity.ok("WhatsApp dispatched to " + req.getMobile());
    }

    // ─── Template-based send ──────────────────────────────────────────────────

    /**
     * Resolves a named template from the {@link TemplateRegistry}, renders variable placeholders,
     * and dispatches the notification across all requested channels (SMS, EMAIL, WHATSAPP).
     *
     * @param req the request body specifying the template key, recipient contact details,
     *            channel set, variables map, and optional CC/BCC for email
     * @return a confirmation message listing the channels used and the template key,
     *         or a 400 response if the template key is unknown
     */
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

    /**
     * Returns the set of all registered {@link TemplateKey} values available for template-based sends.
     *
     * @return a set of all template keys defined in the {@link TemplateRegistry}
     */
    @GetMapping("/templates")
    public ResponseEntity<Set<TemplateKey>> listTemplates() {
        return ResponseEntity.ok(TemplateRegistry.all().keySet());
    }

    // ─── Lead notification re-triggers ───────────────────────────────────────

    /**
     * Re-triggers the full lead-created notification flow for an existing lead.
     * Resolves broker and owner contact details from the database and dispatches
     * the appropriate notifications based on the lead type.
     *
     * @param id the ID of the lead to re-notify
     * @return a confirmation message, or throws {@link RuntimeException} if the lead is not found
     */
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

    /**
     * Re-triggers the lead status-update notification for an existing lead.
     * Reads the current status and remark from the database and dispatches the notification to the customer.
     *
     * @param id the ID of the lead whose status notification should be re-sent
     * @return a confirmation message, or throws {@link RuntimeException} if the lead is not found
     */
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

    /**
     * Sends the welcome notification (SMS + email) to an existing user by their ID.
     *
     * @param id the ID of the user to welcome
     * @return a confirmation message, or throws {@link RuntimeException} if the user is not found
     */
    @PostMapping("/user/{id}/welcome")
    public ResponseEntity<String> notifyWelcome(@PathVariable Long id) {
        log.info("POST /api/notifications/user/{}/welcome", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        notificationService.notifyWelcome(user);
        return ResponseEntity.ok("Welcome notification dispatched for userId=" + id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Resolves the email and mobile for a user by their ID.
     *
     * @param userId the user's database ID, or {@code null} if unknown
     * @return a two-element array {@code [email, mobile]}; both elements are empty strings
     *         if the user ID is {@code null} or the user is not found
     */
    private String[] resolveContact(Long userId) {
        if (userId == null) return new String[]{"", ""};
        return userRepository.findById(userId)
                .map(u -> new String[]{
                        u.getEmail() != null ? u.getEmail() : "",
                        u.getMobile() != null ? u.getMobile() : ""})
                .orElse(new String[]{"", ""});
    }

    /**
     * Returns {@code true} if the given string is non-null and not blank.
     *
     * @param s the string to test
     * @return {@code true} if {@code s} has at least one non-whitespace character
     */
    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }

    // ─── Request DTOs ─────────────────────────────────────────────────────────

    /**
     * Request DTO for the {@code POST /sms} endpoint.
     */
    public static class SmsRequest {
        @NotBlank(message = "mobile is required")
        private String mobile;
        @NotBlank(message = "message is required")
        private String message;

        /** @return the destination mobile number */
        public String getMobile() { return mobile; }
        /** @param mobile the destination mobile number to set */
        public void setMobile(String mobile) { this.mobile = mobile; }
        /** @return the SMS message text */
        public String getMessage() { return message; }
        /** @param message the SMS message text to set */
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Request DTO for the {@code POST /email} endpoint.
     * Supports optional CC, BCC, and an HTML body flag.
     */
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

        /** @return the primary recipient email address */
        public String getTo() { return to; }
        /** @param to the primary recipient email address to set */
        public void setTo(String to) { this.to = to; }
        /** @return the email subject line */
        public String getSubject() { return subject; }
        /** @param subject the email subject line to set */
        public void setSubject(String subject) { this.subject = subject; }
        /** @return the email body content */
        public String getBody() { return body; }
        /** @param body the email body content to set */
        public void setBody(String body) { this.body = body; }
        /** @return the list of CC recipient email addresses */
        public List<String> getCc() { return cc; }
        /** @param cc the list of CC recipient email addresses to set */
        public void setCc(List<String> cc) { this.cc = cc; }
        /** @return the list of BCC recipient email addresses */
        public List<String> getBcc() { return bcc; }
        /** @param bcc the list of BCC recipient email addresses to set */
        public void setBcc(List<String> bcc) { this.bcc = bcc; }
        /** @return {@code true} if the body should be treated as HTML */
        public boolean isHtml() { return html; }
        /** @param html {@code true} to send the body as HTML */
        public void setHtml(boolean html) { this.html = html; }
    }

    /**
     * Request DTO for the {@code POST /whatsapp} endpoint.
     */
    public static class WhatsAppRequest {
        @NotBlank(message = "mobile is required")
        private String mobile;
        @NotBlank(message = "message is required")
        private String message;

        /** @return the destination mobile number */
        public String getMobile() { return mobile; }
        /** @param mobile the destination mobile number to set */
        public void setMobile(String mobile) { this.mobile = mobile; }
        /** @return the WhatsApp message text */
        public String getMessage() { return message; }
        /** @param message the WhatsApp message text to set */
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Request DTO for the {@code POST /template/send} endpoint.
     * Specifies which template to use, the recipient contact details, target channels,
     * and a variables map for placeholder substitution.
     */
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

        /** @return the template key identifying which notification template to use */
        public TemplateKey getTemplateKey() { return templateKey; }
        /** @param templateKey the template key to set */
        public void setTemplateKey(TemplateKey templateKey) { this.templateKey = templateKey; }
        /** @return the recipient's mobile number for SMS/WhatsApp delivery */
        public String getMobile() { return mobile; }
        /** @param mobile the recipient mobile number to set */
        public void setMobile(String mobile) { this.mobile = mobile; }
        /** @return the recipient's email address for email delivery */
        public String getEmail() { return email; }
        /** @param email the recipient email address to set */
        public void setEmail(String email) { this.email = email; }
        /** @return the list of CC email addresses */
        public List<String> getCc() { return cc; }
        /** @param cc the list of CC email addresses to set */
        public void setCc(List<String> cc) { this.cc = cc; }
        /** @return the list of BCC email addresses */
        public List<String> getBcc() { return bcc; }
        /** @param bcc the list of BCC email addresses to set */
        public void setBcc(List<String> bcc) { this.bcc = bcc; }
        /** @return {@code true} if the email body should be rendered as HTML */
        public boolean isHtml() { return html; }
        /** @param html {@code true} to send the email body as HTML */
        public void setHtml(boolean html) { this.html = html; }
        /** @return the set of channel identifiers (e.g. "SMS", "EMAIL", "WHATSAPP") */
        public Set<String> getChannels() { return channels; }
        /** @param channels the set of channel identifiers to set */
        public void setChannels(Set<String> channels) { this.channels = channels; }
        /** @return the map of variable names to values for placeholder substitution */
        public Map<String, String> getVariables() { return variables; }
        /** @param variables the variable map to set */
        public void setVariables(Map<String, String> variables) { this.variables = variables; }
    }
}
