package com.api.notifications;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.api.enums.MasterEnums;
import com.api.leads.ClientLead;
import com.api.prop.PropertyDto;
import com.api.prop.SharePropertyRequest;
import com.api.user.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {

    private static final Set<MasterEnums.InquiryType> LOAN_TYPES = EnumSet.of(
            MasterEnums.InquiryType.HOME_LOAN,
            MasterEnums.InquiryType.LAP,
            MasterEnums.InquiryType.BALANCE_TRANSFER,
            MasterEnums.InquiryType.LOAN_TRANSFER);

    private static final Set<MasterEnums.InquiryType> LEGAL_TYPES = EnumSet.of(
            MasterEnums.InquiryType.PROPERTY_REGISTRATION,
            MasterEnums.InquiryType.RENT_AGREEMENT,
            MasterEnums.InquiryType.DOCUMENT_SERVICES);

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

    // ─── Property Share ──────────────────────────────────────────────────────

    @Async
    public void notifyPropertyShared(PropertyDto property, SharePropertyRequest request) {
        String senderName = request.getSenderName();
        String toEmail = request.getToEmail();
        String toMobile = request.getToMobile();

        log.info("notifyPropertyShared - propertyId={} from='{}' to email={}, mobile={}",
            property.getId(), senderName, toEmail, toMobile);

        if (hasValue(toMobile)) {
            String smsBody = NotificationTemplates.propertyShareSms(
                senderName, property.getTitle(), property.getLocation(),
                property.getCity(), property.getPrice(), property.getRentOrSale(),
                property.getBedrooms(), property.getType(), property.getContactNumber());
            commService.sendSMSMessage(toMobile, smsBody);
            if (request.isSendWhatsApp()) {
                try { whatsAppService.sendMessage("+91" + toMobile, smsBody); }
                catch (Exception e) { log.error("notifyPropertyShared - WhatsApp failed mobile={}: {}", toMobile, e.getMessage()); }
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

    // ─── Lead Created (routes by lead type) ──────────────────────────────────

    /**
     * Dispatches creation notifications to all relevant parties based on leadType:
     *  - Property leads  → broker, owner, customer confirmation
     *  - Loan leads      → broker, bank, customer confirmation
     *  - Legal leads     → broker, service provider, customer confirmation
     */
    @Async
    public void notifyLeadCreated(ClientLead lead, String brokerEmail, String brokerMobile,
            String ownerEmail, String ownerMobile, boolean sendWhatsApp) {

        log.info("notifyLeadCreated - leadId={}, leadType={}, customer='{}'",
                lead.getId(), lead.getLeadType(), lead.getClientName());

        if (isLoanLead(lead.getLeadType())) {
            notifyLoanLead(lead, brokerEmail, brokerMobile, sendWhatsApp);
        } else if (isLegalLead(lead.getLeadType())) {
            notifyLegalLead(lead, brokerEmail, brokerMobile, sendWhatsApp);
        } else {
            notifyPropertyLead(lead, brokerEmail, brokerMobile, ownerEmail, ownerMobile, sendWhatsApp);
        }
    }

    private void notifyPropertyLead(ClientLead lead, String brokerEmail, String brokerMobile,
            String ownerEmail, String ownerMobile, boolean sendWhatsApp) {

        String sms = NotificationTemplates.brokerLeadSms(
                lead.getClientName(), lead.getMobile(), lead.getPropertyTitle(), lead.getPropertyCity(), lead.getId());
        String emailBody = NotificationTemplates.brokerLeadEmailBody(
                lead.getClientName(), lead.getMobile(), lead.getEmail(),
                lead.getPropertyTitle(), lead.getPropertyCity(), lead.getPropertyPrice(), lead.getMessage(), lead.getId());
        String emailSubject = NotificationTemplates.brokerLeadEmailSubject(lead.getClientName(), lead.getPropertyTitle());

        sendToRecipient("broker", brokerMobile, brokerEmail, sms, emailBody, emailSubject, sendWhatsApp);

        // Notify owner only if different from broker
        if (hasValue(ownerMobile) && !ownerMobile.equals(brokerMobile))
            commService.sendSMSMessage(ownerMobile, sms);
        if (hasValue(ownerEmail) && !ownerEmail.equals(brokerEmail))
            commService.sendEmail(ownerEmail, emailBody, emailSubject);

        // Customer confirmation
        if (hasValue(lead.getMobile()))
            commService.sendSMSMessage(lead.getMobile(),
                    NotificationTemplates.customerConfirmationSms(lead.getClientName(), lead.getPropertyTitle(), lead.getPropertyCity()));
        if (hasValue(lead.getEmail()))
            commService.sendEmail(lead.getEmail(),
                    NotificationTemplates.customerConfirmationEmailBody(lead.getClientName(), lead.getPropertyTitle(), lead.getPropertyCity(), lead.getPropertyPrice()),
                    NotificationTemplates.customerConfirmationEmailSubject(lead.getPropertyTitle()));
    }

    private void notifyLoanLead(ClientLead lead, String brokerEmail, String brokerMobile, boolean sendWhatsApp) {
        String loanTypeName = lead.getLoanType() != null ? lead.getLoanType().name() : "HOME_LOAN";

        // Alert broker
        String brokerSms = NotificationTemplates.bankLeadSms(
                lead.getClientName(), lead.getMobile(), lead.getRequiredLoanAmount(), loanTypeName, lead.getId());
        String brokerEmailBody = NotificationTemplates.bankLeadEmailBody(
                lead.getClientName(), lead.getMobile(), lead.getEmail(),
                lead.getRequiredLoanAmount(), lead.getLoanTenureYears(), loanTypeName, lead.getPreferredBank(), lead.getId());
        String brokerEmailSubject = NotificationTemplates.bankLeadEmailSubject(lead.getClientName(), lead.getId());
        sendToRecipient("broker", brokerMobile, brokerEmail, brokerSms, brokerEmailBody, brokerEmailSubject, sendWhatsApp);

        // Alert bank contact if provided
        sendToRecipient("bank", lead.getBankContactMobile(), lead.getBankContactEmail(),
                brokerSms, brokerEmailBody, brokerEmailSubject, false);

        // Customer confirmation
        if (hasValue(lead.getMobile()))
            commService.sendSMSMessage(lead.getMobile(),
                    NotificationTemplates.customerLoanConfirmationSms(lead.getClientName(), loanTypeName));
        if (hasValue(lead.getEmail()))
            commService.sendEmail(lead.getEmail(),
                    NotificationTemplates.customerLoanConfirmationEmailBody(lead.getClientName(), loanTypeName, lead.getRequiredLoanAmount()),
                    NotificationTemplates.customerLoanConfirmationEmailSubject(loanTypeName));
    }

    private void notifyLegalLead(ClientLead lead, String brokerEmail, String brokerMobile, boolean sendWhatsApp) {
        // Alert broker
        String brokerSms = NotificationTemplates.legalLeadSms(
                lead.getClientName(), lead.getMobile(), lead.getDocumentServicesRequired(), lead.getPropertyCity(), lead.getId());
        String brokerEmailBody = NotificationTemplates.legalLeadEmailBody(
                lead.getClientName(), lead.getMobile(), lead.getEmail(),
                lead.getDocumentServicesRequired(), lead.getSpecifications(), lead.getPropertyCity(), lead.getId());
        String brokerEmailSubject = NotificationTemplates.legalLeadEmailSubject(lead.getClientName(), lead.getId());
        sendToRecipient("broker", brokerMobile, brokerEmail, brokerSms, brokerEmailBody, brokerEmailSubject, sendWhatsApp);

        // Alert service provider if provided
        sendToRecipient("serviceProvider", lead.getServiceProviderMobile(), lead.getServiceProviderEmail(),
                brokerSms, brokerEmailBody, brokerEmailSubject, false);

        // Customer confirmation
        if (hasValue(lead.getMobile()))
            commService.sendSMSMessage(lead.getMobile(),
                    NotificationTemplates.customerLegalConfirmationSms(lead.getClientName(), lead.getDocumentServicesRequired()));
        if (hasValue(lead.getEmail()))
            commService.sendEmail(lead.getEmail(),
                    NotificationTemplates.customerLegalConfirmationEmailBody(lead.getClientName(), lead.getDocumentServicesRequired(), lead.getPropertyCity()),
                    NotificationTemplates.customerLegalConfirmationEmailSubject(lead.getDocumentServicesRequired()));
    }

    // ─── Lead Status Updated ──────────────────────────────────────────────────

    @Async
    public void notifyLeadStatusUpdated(
            String customerName, String customerMobile, String customerEmail,
            String propTitle, String propCity, String status, String remark) {

        log.info("notifyLeadStatusUpdated - customer='{}', property='{}', status={}", customerName, propTitle, status);
        if (hasValue(customerMobile))
            commService.sendSMSMessage(customerMobile,
                    NotificationTemplates.leadStatusUpdateSms(customerName, propTitle, status));
        if (hasValue(customerEmail))
            commService.sendEmail(customerEmail,
                    NotificationTemplates.leadStatusUpdateEmailBody(customerName, propTitle, propCity, status, remark),
                    NotificationTemplates.leadStatusUpdateEmailSubject(propTitle, status));
    }

    // ─── Welcome ─────────────────────────────────────────────────────────────

    @Async
    public void notifyWelcome(User user) {
        log.info("notifyWelcome - userId={}", user.getId());
        if (hasValue(user.getMobile()))
            commService.sendSMSMessage(user.getMobile(),
                NotificationTemplates.welcomeSms(user.getName()));
        if (hasValue(user.getEmail()))
            commService.sendEmail(user.getEmail(),
                NotificationTemplates.welcomeEmailBody(user.getName()),
                NotificationTemplates.welcomeEmailSubject());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void sendToRecipient(String role, String mobile, String email,
            String sms, String emailBody, String emailSubject, boolean sendWhatsApp) {
        sendToRecipient(role, mobile, email, sms, emailBody, emailSubject, sendWhatsApp,
                new String[0], new String[0]);
    }

    private void sendToRecipient(String role, String mobile, String email,
            String sms, String emailBody, String emailSubject, boolean sendWhatsApp,
            String[] cc, String[] bcc) {
        if (hasValue(mobile)) {
            commService.sendSMSMessage(mobile, sms);
            if (sendWhatsApp) {
                try { whatsAppService.sendMessage("+91" + mobile, sms); }
                catch (Exception e) { log.error("notifyLeadCreated - WhatsApp failed for {}: {}", role, e.getMessage()); }
            }
        }
        if (hasValue(email)) {
            commService.sendEmail(EmailMessage.to(email)
                    .subject(emailSubject)
                    .body(emailBody)
                    .cc(cc)
                    .bcc(hasValue(adminEmail) ? new String[]{adminEmail} : bcc)
                    .build());
        }
    }

    private boolean isLoanLead(MasterEnums.InquiryType type) {
        return type != null && LOAN_TYPES.contains(type);
    }

    private boolean isLegalLead(MasterEnums.InquiryType type) {
        return type != null && LEGAL_TYPES.contains(type);
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}
