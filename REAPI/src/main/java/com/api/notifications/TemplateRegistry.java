package com.api.notifications;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central registry of all notification templates.
 *
 * Templates use {{varName}} placeholders. Use TemplateRenderer.render() to substitute.
 *
 * Variable reference per key:
 *
 *  WELCOME                        : name
 *  BROKER_PROPERTY_LEAD           : leadId, customerName, mobile, email, propertyTitle, city, price, message
 *  BROKER_LOAN_LEAD               : leadId, customerName, mobile, email, loanType, loanAmount, tenure, preferredBank
 *  BROKER_LEGAL_LEAD              : leadId, customerName, mobile, email, services, city, specifications
 *  CUSTOMER_PROPERTY_CONFIRMATION : name, propertyTitle, city, price
 *  CUSTOMER_LOAN_CONFIRMATION     : name, loanType, loanAmount
 *  CUSTOMER_LEGAL_CONFIRMATION    : name, services, city
 *  LEAD_STATUS_UPDATE             : customerName, propertyTitle, city, status, remark
 *  PROPERTY_SHARE                 : senderName, title, location, city, price, rentOrSale, bedrooms, type
 */
public class TemplateRegistry {

    private static final String APP = "KeyBricks";

    private static final Map<TemplateKey, NotificationTemplateSet> REGISTRY =
            new EnumMap<>(TemplateKey.class);

    static {
        // ─── Welcome ─────────────────────────────────────────────────────────
        REGISTRY.put(TemplateKey.WELCOME, new NotificationTemplateSet(
                "Welcome to " + APP + ", {{name}}! Your account is active. Browse properties today. - " + APP,
                "Welcome to " + APP + "!",
                "Hi {{name}},\n\nWelcome to " + APP + "! Your account is now active.\n" +
                "Browse listings, submit inquiries, and post your own properties.\n\n- " + APP + " Team",
                null));

        // ─── Broker / Owner — Property lead ──────────────────────────────────
        REGISTRY.put(TemplateKey.BROKER_PROPERTY_LEAD, new NotificationTemplateSet(
                "[" + APP + "] New lead #{{leadId}}: {{customerName}} ({{mobile}}) interested in '{{propertyTitle}}', {{city}}. Log in to follow up.",
                "[" + APP + "] New Inquiry – {{customerName}} is interested in '{{propertyTitle}}'",
                "[" + APP + "] Lead #{{leadId}}\n" +
                "Customer: {{customerName}} | {{mobile}}{{email}}\n" +
                "Property: {{propertyTitle}}, {{city}}{{price}}{{message}}\n\n" +
                "Log in to respond. - " + APP + " Team",
                null));

        // ─── Broker / Bank — Loan lead ────────────────────────────────────────
        REGISTRY.put(TemplateKey.BROKER_LOAN_LEAD, new NotificationTemplateSet(
                "[" + APP + "] Loan enquiry #{{leadId}}: {{customerName}} ({{mobile}}) needs {{loanType}} loan{{loanAmount}}. - " + APP,
                "[" + APP + "] Loan Enquiry #{{leadId}} – {{customerName}}",
                "[" + APP + "] Loan Enquiry #{{leadId}}\n" +
                "Applicant: {{customerName}} | {{mobile}}{{email}}\n" +
                "Loan: {{loanType}} | Amount: {{loanAmount}} | Tenure: {{tenure}} | Preferred Bank: {{preferredBank}}\n\n" +
                "- " + APP + " Team",
                null));

        // ─── Broker / Provider — Legal lead ───────────────────────────────────
        REGISTRY.put(TemplateKey.BROKER_LEGAL_LEAD, new NotificationTemplateSet(
                "[" + APP + "] Legal enquiry #{{leadId}}: {{customerName}} ({{mobile}}) needs '{{services}}' in {{city}}. - " + APP,
                "[" + APP + "] Legal Enquiry #{{leadId}} – {{customerName}}",
                "[" + APP + "] Legal Enquiry #{{leadId}}\n" +
                "Client: {{customerName}} | {{mobile}}{{email}}\n" +
                "Services: {{services}} | City: {{city}}{{specifications}}\n\n" +
                "- " + APP + " Team",
                null));

        // ─── Customer — Property confirmation ────────────────────────────────
        REGISTRY.put(TemplateKey.CUSTOMER_PROPERTY_CONFIRMATION, new NotificationTemplateSet(
                "Hi {{name}}, your enquiry for '{{propertyTitle}}' in {{city}} is sent! The broker will contact you within 24 hrs. - " + APP,
                "Enquiry Confirmed – '{{propertyTitle}}' | " + APP,
                "Hi {{name}},\n\nYour enquiry for '{{propertyTitle}}' in {{city}}{{price}} has been sent. " +
                "The broker will reach you within 24 hrs.\n\n- " + APP + " Team",
                null));

        // ─── Customer — Loan confirmation ─────────────────────────────────────
        REGISTRY.put(TemplateKey.CUSTOMER_LOAN_CONFIRMATION, new NotificationTemplateSet(
                "Hi {{name}}, your {{loanType}} loan enquiry{{loanAmount}} is received! Our team will contact you shortly. - " + APP,
                "Loan Enquiry Received – {{loanType}} | " + APP,
                "Hi {{name}},\n\nYour {{loanType}} enquiry{{loanAmount}} is received. " +
                "Our team will contact you shortly to discuss next steps.\n\n- " + APP + " Team",
                null));

        // ─── Customer — Legal confirmation ────────────────────────────────────
        REGISTRY.put(TemplateKey.CUSTOMER_LEGAL_CONFIRMATION, new NotificationTemplateSet(
                "Hi {{name}}, your enquiry for '{{services}}' in {{city}} is received! Our team will contact you shortly. - " + APP,
                "Enquiry Received – {{services}} | " + APP,
                "Hi {{name}},\n\nYour enquiry for '{{services}}' in {{city}} is received. " +
                "Our team will contact you shortly.\n\n- " + APP + " Team",
                null));

        // ─── Lead status update ───────────────────────────────────────────────
        REGISTRY.put(TemplateKey.LEAD_STATUS_UPDATE, new NotificationTemplateSet(
                "Hi {{customerName}}, your inquiry for '{{propertyTitle}}' has been updated to: {{status}}. - " + APP,
                "Inquiry Update – {{propertyTitle}} | Status: {{status}}",
                "Hi {{customerName}},\n\nYour inquiry for '{{propertyTitle}}' in {{city}} has been updated.\n" +
                "Status: {{status}}{{remark}}\n\n- " + APP + " Team",
                null));

        // ─── Property share ───────────────────────────────────────────────────
        REGISTRY.put(TemplateKey.PROPERTY_SHARE, new NotificationTemplateSet(
                "{{senderName}} shared: {{title}} | {{bedrooms}}{{type}} | {{location}}, {{city}} | ₹{{price}} ({{rentOrSale}}) - " + APP,
                "{{senderName}} shared a property – {{title}}",
                "Hi,\n\n{{senderName}} shared this property with you:\n" +
                "{{title}} | {{bedrooms}}{{type}} | {{location}}, {{city}}\n" +
                "Price: ₹{{price}} ({{rentOrSale}})\n\n- " + APP + " Team",
                null));
    }

    public static NotificationTemplateSet get(TemplateKey key) {
        return REGISTRY.get(key);
    }

    public static Map<TemplateKey, NotificationTemplateSet> all() {
        return java.util.Collections.unmodifiableMap(REGISTRY);
    }
}
