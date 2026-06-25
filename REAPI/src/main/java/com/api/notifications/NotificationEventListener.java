package com.api.notifications;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.api.notifications.events.LeadGeneratedEvent;
import com.api.notifications.events.OtpGeneratedEvent;
import com.api.notifications.events.PasswordResetEvent;
import com.api.notifications.events.PropertyInquiryEvent;
import com.api.notifications.events.PropertySharedEvent;
import com.api.notifications.events.UserRegisteredEvent;

/**
 * Listens to application events and dispatches tracked notifications asynchronously.
 *
 * <p>Each listener method is {@code @Async} so the publishing thread returns immediately
 * after {@code ApplicationEventPublisher.publishEvent(...)}.
 * Delivery tracking is handled by {@link NotificationDispatcher}.</p>
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private static final String APP = "KeyBricks";

    private final NotificationDispatcher dispatcher;
    private final NotificationService notificationService;

    @Value("${app.frontend.url:https://keybricks.in}")
    private String frontendUrl;

    public NotificationEventListener(NotificationDispatcher dispatcher,
            NotificationService notificationService) {
        this.dispatcher = dispatcher;
        this.notificationService = notificationService;
    }

    // ─── OTP ─────────────────────────────────────────────────────────────────

    @Async
    @EventListener
    public void onOtpGenerated(OtpGeneratedEvent event) {
        log.info("onOtpGenerated - name={}, reason={}", event.getName(), event.getReason());

        if (hasValue(event.getEmail())) {
            String subject = "Your " + APP + " OTP";
            dispatcher.sendHtmlEmail("OTP", event.getName(), event.getEmail(), subject,
                    "otp",
                    Map.of(
                        "name",   event.getName() != null ? event.getName() : "User",
                        "otp",    event.getOtp(),
                        "reason", event.getReason(),
                        "app",    APP
                    ));
        }

        if (hasValue(event.getMobile())) {
            String smsBody = "Your " + APP + " OTP is: " + event.getOtp() + ". Valid for 10 minutes. Do not share.";
            dispatcher.sendSms("OTP", event.getName(), event.getMobile(), smsBody);
        }
    }

    // ─── Welcome (after OTP verified) ────────────────────────────────────────

    @Async
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("onUserRegistered - userId={}", event.getUserId());

        if (hasValue(event.getEmail())) {
            String subject = "Welcome to " + APP + "!";
            dispatcher.sendHtmlEmail("WELCOME", event.getName(), event.getEmail(), subject,
                    "signup",
                    Map.of(
                        "name", event.getName() != null ? event.getName() : "User",
                        "app",  APP,
                        "url",  frontendUrl
                    ));
        }

        if (hasValue(event.getMobile())) {
            String smsBody = "Welcome to " + APP + ", " + event.getName() + "! Your account is active. Browse properties today.";
            dispatcher.sendSms("WELCOME", event.getName(), event.getMobile(), smsBody);
        }
    }

    // ─── Password Reset ───────────────────────────────────────────────────────

    @Async
    @EventListener
    public void onPasswordReset(PasswordResetEvent event) {
        log.info("onPasswordReset - name={}", event.getName());

        if (hasValue(event.getEmail())) {
            String subject = "Password Reset Successful | " + APP;
            dispatcher.sendHtmlEmail("PASSWORD_RESET", event.getName(), event.getEmail(), subject,
                    "password-reset-success",
                    Map.of(
                        "name",    event.getName() != null ? event.getName() : "User",
                        "app",     APP,
                        "loginUrl", frontendUrl + "/login"
                    ));
        }

        if (hasValue(event.getMobile())) {
            String smsBody = "Hi " + event.getName() + ", your " + APP + " password has been reset successfully.";
            dispatcher.sendSms("PASSWORD_RESET", event.getName(), event.getMobile(), smsBody);
        }
    }

    // ─── Property Inquiry ─────────────────────────────────────────────────────

    @Async
    @EventListener
    public void onPropertyInquiry(PropertyInquiryEvent event) {
        log.info("onPropertyInquiry - leadId={}, type={}", event.getLeadId(), event.getInquiryType());

        String templateName = "RENT".equalsIgnoreCase(event.getInquiryType()) ? "rent-interest" : "buy-interest";
        String inquiryLabel = "RENT".equalsIgnoreCase(event.getInquiryType()) ? "Rent" : "Buy";

        // Customer confirmation
        if (hasValue(event.getCustomerEmail())) {
            String subject = "Your " + inquiryLabel + " Inquiry Confirmed | " + APP;
            dispatcher.sendHtmlEmail("PROPERTY_INQUIRY", event.getCustomerName(),
                    event.getCustomerEmail(), subject, templateName,
                    Map.of(
                        "name",          event.getCustomerName() != null ? event.getCustomerName() : "User",
                        "propertyTitle", event.getPropertyTitle() != null ? event.getPropertyTitle() : "",
                        "propertyCity",  event.getPropertyCity() != null ? event.getPropertyCity() : "",
                        "leadId",        String.valueOf(event.getLeadId()),
                        "inquiryType",   inquiryLabel,
                        "app",           APP
                    ));
        }

        if (hasValue(event.getCustomerMobile())) {
            String smsBody = "Hi " + event.getCustomerName() + ", your " + inquiryLabel + " inquiry for '"
                    + event.getPropertyTitle() + "' has been sent. Expect a call within 24hrs. - " + APP;
            dispatcher.sendSms("PROPERTY_INQUIRY", event.getCustomerName(), event.getCustomerMobile(), smsBody);
        }

        // Agent/broker notification
        if (hasValue(event.getAgentEmail())) {
            String agentSubject = "[" + APP + "] New " + inquiryLabel + " Inquiry – " + event.getCustomerName();
            dispatcher.sendHtmlEmail("PROPERTY_INQUIRY_AGENT", "Agent",
                    event.getAgentEmail(), agentSubject, "property-inquiry",
                    Map.of(
                        "name",          "Agent",
                        "customerName",  event.getCustomerName() != null ? event.getCustomerName() : "",
                        "customerMobile", event.getCustomerMobile() != null ? event.getCustomerMobile() : "",
                        "propertyTitle", event.getPropertyTitle() != null ? event.getPropertyTitle() : "",
                        "propertyCity",  event.getPropertyCity() != null ? event.getPropertyCity() : "",
                        "leadId",        String.valueOf(event.getLeadId()),
                        "inquiryType",   inquiryLabel,
                        "app",           APP
                    ));
        }
    }

    // ─── Property Share ───────────────────────────────────────────────────────

    @Async
    @EventListener
    public void onPropertyShared(PropertySharedEvent event) {
        log.info("onPropertyShared - from='{}', toEmail={}, toMobile={}",
                event.getSenderName(), event.getRecipientEmail(), event.getRecipientMobile());

        if (hasValue(event.getRecipientEmail())) {
            String subject = event.getSenderName() + " shared a property – " + event.getPropertyTitle() + " | " + APP;
            dispatcher.sendHtmlEmail("PROPERTY_SHARE", null, event.getRecipientEmail(), subject,
                    "property-share",
                    Map.of(
                        "senderName",          event.getSenderName() != null ? event.getSenderName() : "Someone",
                        "propertyTitle",        event.getPropertyTitle() != null ? event.getPropertyTitle() : "",
                        "propertyCity",         event.getPropertyCity() != null ? event.getPropertyCity() : "",
                        "propertyLocation",     event.getPropertyLocation() != null ? event.getPropertyLocation() : "",
                        "price",                event.getPrice() != null ? String.format("₹%,.0f", event.getPrice()) : "N/A",
                        "rentOrSale",           event.getRentOrSale() != null ? event.getRentOrSale() : "",
                        "bedrooms",             event.getBedrooms() != null ? String.valueOf(event.getBedrooms()) : "",
                        "propertyType",         event.getType() != null ? event.getType() : "",
                        "contactNumber",        event.getContactNumber() != null ? event.getContactNumber() : "",
                        "app",                  APP
                    ));
        }

        if (hasValue(event.getRecipientMobile())) {
            String bedroomPart = event.getBedrooms() != null ? event.getBedrooms() + "BHK " : "";
            String smsBody = event.getSenderName() + " shared: " + event.getPropertyTitle()
                    + " | " + bedroomPart + event.getType()
                    + " | " + event.getPropertyCity()
                    + " | " + String.format("₹%,.0f", event.getPrice())
                    + " (" + event.getRentOrSale() + ") - " + APP;
            dispatcher.sendSms("PROPERTY_SHARE", null, event.getRecipientMobile(), smsBody);

            if (event.isSendWhatsApp()) {
                dispatcher.sendWhatsApp("PROPERTY_SHARE", null, event.getRecipientMobile(), smsBody);
            }
        }
    }

    // ─── Lead Generated ───────────────────────────────────────────────────────

    @Async
    @EventListener
    public void onLeadGenerated(LeadGeneratedEvent event) {
        log.info("onLeadGenerated - leadId={}, type={}", event.getLead().getId(), event.getLead().getLeadType());
        // Delegate to existing NotificationService for full lead routing logic
        // (broker/owner/bank/legal routing) — existing code handles this well
        notificationService.notifyLeadCreated(event.getLead(),
                event.getBrokerEmail(), event.getBrokerMobile(),
                event.getOwnerEmail(), event.getOwnerMobile(),
                event.isSendWhatsApp());
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}
