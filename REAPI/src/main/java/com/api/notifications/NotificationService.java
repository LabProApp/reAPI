package com.api.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.api.inquiry.Inquiry;
import com.api.prop.PropertyDto;
import com.api.prop.SharePropertyRequest;
import com.api.user.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.mobile:}")
    private String adminMobile;

    private final CommService commService;
    private final WhatsAppService whatsAppService;

    public NotificationService(CommService commService, WhatsAppService whatsAppService) {
        this.commService = commService;
        this.whatsAppService = whatsAppService;
    }

    // ─── Inquiry Created ──────────────────────────────────────────────────────

    @Async
    public void notifyInquiryCreated(Inquiry inquiry) {
        String name = inquiry.getApplicantName();
        String mobile = inquiry.getMobileNumber();
        String email = inquiry.getEmail();
        String type = inquiry.getInquiryType() != null ? inquiry.getInquiryType().name() : "General";
        String city = inquiry.getPropertyCity();
        Double budget = inquiry.getBudget();
        Long id = inquiry.getId();

        log.info("notifyInquiryCreated - Dispatching notifications for inquiryId={}", id);

        if (hasValue(mobile)) {
            commService.sendSMSMessage(mobile,
                NotificationTemplates.inquiryConfirmationSms(name, type, id));
        }
        if (hasValue(email)) {
            commService.sendEmail(email,
                NotificationTemplates.inquiryConfirmationEmailBody(name, type, id, city, budget, inquiry.getRequiredLoanAmount()),
                NotificationTemplates.inquiryConfirmationEmailSubject(type, id));
        }

        if (hasValue(adminEmail)) {
            commService.sendEmail(adminEmail,
                NotificationTemplates.inquiryAdminAlertEmailBody(name, mobile, email, type, city, budget, id),
                NotificationTemplates.inquiryAdminAlertEmailSubject(name, id));
        }
        if (hasValue(adminMobile)) {
            commService.sendSMSMessage(adminMobile,
                NotificationTemplates.inquiryAdminAlertSms(name, mobile, type, city, id));
        }
    }

    // ─── Inquiry Status Updated ───────────────────────────────────────────────

    @Async
    public void notifyInquiryStatusUpdated(Inquiry inquiry) {
        String name = inquiry.getApplicantName();
        String mobile = inquiry.getMobileNumber();
        String email = inquiry.getEmail();
        String status = inquiry.getInquiryStatus() != null ? inquiry.getInquiryStatus().name() : "UPDATED";
        Long id = inquiry.getId();

        log.info("notifyInquiryStatusUpdated - Dispatching status notifications for inquiryId={}", id);

        if (hasValue(mobile)) {
            commService.sendSMSMessage(mobile,
                NotificationTemplates.inquiryStatusSms(name, id, status));
        }
        if (hasValue(email)) {
            commService.sendEmail(email,
                NotificationTemplates.inquiryStatusEmailBody(name, id, status, inquiry.getAssignedAgentName()),
                NotificationTemplates.inquiryStatusEmailSubject(id));
        }
    }

    // ─── Property Shared ─────────────────────────────────────────────────────

    @Async
    public void notifyPropertyShared(PropertyDto property, SharePropertyRequest request) {
        String senderName = request.getSenderName();
        String toEmail = request.getToEmail();
        String toMobile = request.getToMobile();

        log.info("notifyPropertyShared - Sharing propertyId={} from='{}' to email={}, mobile={}",
            property.getId(), senderName, toEmail, toMobile);

        if (hasValue(toMobile)) {
            String smsBody = NotificationTemplates.propertyShareSms(
                senderName, property.getTitle(), property.getLocation(),
                property.getCity(), property.getPrice(), property.getRentOrSale(),
                property.getBedrooms(), property.getType(), property.getContactNumber());
            commService.sendSMSMessage(toMobile, smsBody);

            if (request.isSendWhatsApp()) {
                try {
                    whatsAppService.sendMessage("+91" + toMobile, smsBody);
                } catch (Exception e) {
                    log.error("notifyPropertyShared - WhatsApp failed for mobile={}: {}", toMobile, e.getMessage());
                }
            }
        }

        if (hasValue(toEmail)) {
            commService.sendEmail(toEmail,
                NotificationTemplates.propertyShareEmailBody(
                    senderName, property.getTitle(), property.getAddress(),
                    property.getLocation(), property.getCity(), property.getState(),
                    property.getPrice(), property.getRentOrSale(), property.getBedrooms(),
                    property.getBathrooms(), property.getType(), property.getCarpetArea(),
                    property.getConstructionStatus(), property.getContactNumber(), property.getDescription()),
                NotificationTemplates.propertyShareEmailSubject(senderName, property.getTitle()));
        }
    }

    // ─── Welcome ─────────────────────────────────────────────────────────────

    @Async
    public void notifyWelcome(User user) {
        log.info("notifyWelcome - Sending welcome notifications to userId={}", user.getId());
        if (hasValue(user.getMobile())) {
            commService.sendSMSMessage(user.getMobile(),
                NotificationTemplates.welcomeSms(user.getName()));
        }
        if (hasValue(user.getEmail())) {
            commService.sendEmail(user.getEmail(),
                NotificationTemplates.welcomeEmailBody(user.getName()),
                NotificationTemplates.welcomeEmailSubject());
        }
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}
