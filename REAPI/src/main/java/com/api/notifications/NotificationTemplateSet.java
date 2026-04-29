package com.api.notifications;

/**
 * Holds the three channel templates for a single notification event.
 * All body strings use {{varName}} placeholders rendered by TemplateRenderer.
 *
 * Available variables per template are documented in TemplateRegistry.
 */
public class NotificationTemplateSet {

    private final String sms;
    private final String emailSubject;
    private final String emailBody;
    private final String whatsApp;

    public NotificationTemplateSet(String sms, String emailSubject, String emailBody, String whatsApp) {
        this.sms = sms;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.whatsApp = whatsApp;
    }

    public String getSms() { return sms; }
    public String getEmailSubject() { return emailSubject; }
    public String getEmailBody() { return emailBody; }
    public String getWhatsApp() { return whatsApp != null ? whatsApp : sms; }
}
