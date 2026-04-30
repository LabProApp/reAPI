package com.api.notifications;

public enum TemplateKey {

    // ─── Account ──────────────────────────────────────────────────────────────
    
    WELCOME,

    // ─── Property leads (broker / owner) ─────────────────────────────────────
    
    BROKER_PROPERTY_LEAD,

    // ─── Loan leads (broker / bank) ───────────────────────────────────────────
    
    BROKER_LOAN_LEAD,

    // ─── Legal leads (broker / service provider) ──────────────────────────────
    
    BROKER_LEGAL_LEAD,

    // ─── Customer confirmations ───────────────────────────────────────────────
    
    CUSTOMER_PROPERTY_CONFIRMATION,

    
    CUSTOMER_LOAN_CONFIRMATION,

    
    CUSTOMER_LEGAL_CONFIRMATION,

    // ─── Lead status update (to customer) ────────────────────────────────────
    
    LEAD_STATUS_UPDATE,

    // ─── Property share ───────────────────────────────────────────────────────
    
    PROPERTY_SHARE
}
