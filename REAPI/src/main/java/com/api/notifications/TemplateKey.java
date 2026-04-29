package com.api.notifications;

/**
 * Enumeration of all available notification template keys in the real estate platform.
 *
 * <p>Each constant identifies a distinct notification event and maps to a
 * {@link NotificationTemplateSet} in the {@link TemplateRegistry}. The key is used
 * by callers to resolve the correct set of channel templates (SMS, email, WhatsApp)
 * and to understand which variable placeholders must be provided to
 * {@link TemplateRenderer#render(String, java.util.Map)}.
 *
 * <p>Variable requirements per key are documented in {@link TemplateRegistry}.
 */
public enum TemplateKey {

    // ─── Account ──────────────────────────────────────────────────────────────
    /** Welcome notification sent to a newly registered user. Variables: {@code name}. */
    WELCOME,

    // ─── Property leads (broker / owner) ─────────────────────────────────────
    /** New property inquiry lead alert sent to the broker and/or property owner.
     *  Variables: {@code leadId, customerName, mobile, email, propertyTitle, city, price, message}. */
    BROKER_PROPERTY_LEAD,

    // ─── Loan leads (broker / bank) ───────────────────────────────────────────
    /** New loan inquiry lead alert sent to the broker and/or bank contact.
     *  Variables: {@code leadId, customerName, mobile, email, loanType, loanAmount, tenure, preferredBank}. */
    BROKER_LOAN_LEAD,

    // ─── Legal leads (broker / service provider) ──────────────────────────────
    /** New legal-services inquiry lead alert sent to the broker and/or service provider.
     *  Variables: {@code leadId, customerName, mobile, email, services, city, specifications}. */
    BROKER_LEGAL_LEAD,

    // ─── Customer confirmations ───────────────────────────────────────────────
    /** Confirmation sent to the customer after submitting a property inquiry.
     *  Variables: {@code name, propertyTitle, city, price}. */
    CUSTOMER_PROPERTY_CONFIRMATION,

    /** Confirmation sent to the customer after submitting a loan inquiry.
     *  Variables: {@code name, loanType, loanAmount}. */
    CUSTOMER_LOAN_CONFIRMATION,

    /** Confirmation sent to the customer after submitting a legal-services inquiry.
     *  Variables: {@code name, services, city}. */
    CUSTOMER_LEGAL_CONFIRMATION,

    // ─── Lead status update (to customer) ────────────────────────────────────
    /** Status-change notification sent to the customer when their lead is updated.
     *  Variables: {@code customerName, propertyTitle, city, status, remark}. */
    LEAD_STATUS_UPDATE,

    // ─── Property share ───────────────────────────────────────────────────────
    /** Notification sent to a recipient when a property is shared with them.
     *  Variables: {@code senderName, title, location, city, price, rentOrSale, bedrooms, type}. */
    PROPERTY_SHARE
}
