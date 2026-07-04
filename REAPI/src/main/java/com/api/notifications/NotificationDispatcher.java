package com.api.notifications;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Tracked notification dispatcher.
 *
 * <p>Every send attempt is recorded to the {@code notifications} table so we have
 * a full audit trail and can retry FAILED records via {@link NotificationRetryScheduler}.</p>
 *
 * <p>Methods are {@code @Async} so callers return immediately; the record exists
 * with PENDING status before the actual send is attempted.</p>
 */
@Service
public class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final CommService comm;
    private final WhatsAppProvider whatsApp;
    private final NotificationRepository repo;

    public NotificationDispatcher(CommService comm, WhatsAppProvider whatsApp,
            NotificationRepository repo) {
        this.comm = comm;
        this.whatsApp = whatsApp;
        this.repo = repo;
    }

    // ─── HTML Template Email (primary path for new workflows) ─────────────────

    @Async
    public void sendHtmlEmail(String type, String recipientName, String recipientEmail,
            String subject, String template, Map<String, Object> variables) {
        // Render to HTML first so the payload column stores the actual content for retry
        String html = comm.renderEmailTemplate(template, variables);
        Notification n = repo.save(Notification.pending(type, "EMAIL",
                recipientName, recipientEmail, null, subject, html));
        try {
            comm.sendEmailNow(EmailMessage.to(recipientEmail).subject(subject).body(html).html(true).build());
            n.markSent();
            log.info("NotificationDispatcher - HTML email sent type={}, to={}", type, recipientEmail);
        } catch (Exception e) {
            n.markFailed(e.getMessage());
            log.error("NotificationDispatcher - HTML email FAILED type={}, to={}: {}", type, recipientEmail, e.getMessage());
        } finally {
            repo.save(n);
        }
    }

    // ─── Plain Text Email ─────────────────────────────────────────────────────

    @Async
    public void sendEmail(String type, String recipientName, String recipientEmail,
            String subject, String body) {
        Notification n = repo.save(Notification.pending(type, "EMAIL",
                recipientName, recipientEmail, null, subject, body));
        try {
            comm.sendEmailNow(EmailMessage.to(recipientEmail).subject(subject).body(body).build());
            n.markSent();
            log.info("NotificationDispatcher - email sent type={}, to={}", type, recipientEmail);
        } catch (Exception e) {
            n.markFailed(e.getMessage());
            log.error("NotificationDispatcher - email FAILED type={}, to={}: {}", type, recipientEmail, e.getMessage());
        } finally {
            repo.save(n);
        }
    }

    // ─── SMS ──────────────────────────────────────────────────────────────────

    @Async
    public void sendSms(String type, String recipientName, String recipientMobile, String body) {
        Notification n = repo.save(Notification.pending(type, "SMS",
                recipientName, null, recipientMobile, null, body));
        try {
            comm.sendSmsNow(recipientMobile, body);
            n.markSent();
            log.info("NotificationDispatcher - SMS sent type={}, mobile={}", type, recipientMobile);
        } catch (Exception e) {
            n.markFailed(e.getMessage());
            log.error("NotificationDispatcher - SMS FAILED type={}, mobile={}: {}", type, recipientMobile, e.getMessage());
        } finally {
            repo.save(n);
        }
    }

    // ─── WhatsApp ─────────────────────────────────────────────────────────────

    @Async
    public void sendWhatsApp(String type, String recipientName, String recipientMobile, String body) {
        if (recipientMobile == null || recipientMobile.isBlank()) {
            log.warn("NotificationDispatcher - sendWhatsApp skipped: recipientMobile is blank type={}", type);
            return;
        }
        String e164 = recipientMobile.startsWith("+") ? recipientMobile : "+91" + recipientMobile;
        Notification n = repo.save(Notification.pending(type, "WHATSAPP",
                recipientName, null, recipientMobile, null, body));
        try {
            String sid = whatsApp.send(e164, body);
            if (sid != null) {
                n.markSent();
                log.info("NotificationDispatcher - WhatsApp sent type={}, mobile={}", type, recipientMobile);
            } else {
                // Provider not configured or send returned no SID — mark FAILED so retry picks it up
                n.markFailed("WhatsApp provider returned null SID — not configured or send failed");
                log.warn("NotificationDispatcher - WhatsApp null SID type={}, mobile={}", type, recipientMobile);
            }
        } catch (Exception ex) {
            n.markFailed(ex.getMessage());
            log.error("NotificationDispatcher - WhatsApp FAILED type={}, mobile={}: {}", type, recipientMobile, ex.getMessage());
        } finally {
            repo.save(n);
        }
    }

    // ─── Retry (called by NotificationRetryScheduler) ────────────────────────

    /** Re-attempts a specific FAILED/RETRYING notification record. */
    public void retry(Notification n) {
        n.markRetrying();
        repo.save(n);
        try {
            switch (n.getChannel()) {
                case "EMAIL" -> {
                    // payload stores the body; subject stored in subject column
                    comm.sendEmailNow(EmailMessage.to(n.getRecipientEmail())
                            .subject(n.getSubject())
                            .body(n.getPayload())
                            .html(n.getPayload() != null && n.getPayload().startsWith("<"))
                            .build());
                }
                case "SMS" -> comm.sendSmsNow(n.getRecipientMobile(), n.getPayload());
                case "WHATSAPP" -> {
                    String e164 = CommService.toE164India(n.getRecipientMobile());
                    String sid = whatsApp.send(e164, n.getPayload());
                    if (sid == null) throw new RuntimeException(
                            "WhatsApp provider returned null SID — not configured or send failed");
                }
                default -> log.warn("retry - unknown channel={} for notificationId={}", n.getChannel(), n.getId());
            }
            n.markSent();
            log.info("retry - success notificationId={}, type={}", n.getId(), n.getNotificationType());
        } catch (Exception e) {
            n.markFailed(e.getMessage());
            log.warn("retry - failed notificationId={}: {}", n.getId(), e.getMessage());
        } finally {
            repo.save(n);
        }
    }
}
