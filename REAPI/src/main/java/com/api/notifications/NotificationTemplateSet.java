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

    /**
     * Constructs a {@code NotificationTemplateSet} with templates for all three channels.
     *
     * @param sms          the SMS template string with {@code {{placeholder}}} variables
     * @param emailSubject the email subject template string
     * @param emailBody    the email body template string
     * @param whatsApp     the WhatsApp message template string; if {@code null}, falls back to {@code sms}
     */
    public NotificationTemplateSet(String sms, String emailSubject, String emailBody, String whatsApp) {
        this.sms = sms;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.whatsApp = whatsApp;
    }

    /** @return the SMS template string */
    public String getSms() { return sms; }

    /** @return the email subject template string */
    public String getEmailSubject() { return emailSubject; }

    /** @return the email body template string */
    public String getEmailBody() { return emailBody; }

    /**
     * Returns the WhatsApp template string. Falls back to the SMS template if no
     * dedicated WhatsApp template was provided at construction time.
     *
     * @return the WhatsApp template string, or the SMS template if WhatsApp is {@code null}
     */
    public String getWhatsApp() { return whatsApp != null ? whatsApp : sms; }
}
