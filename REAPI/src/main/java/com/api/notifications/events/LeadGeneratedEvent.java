package com.api.notifications.events;

import com.api.leads.ClientLead;

public class LeadGeneratedEvent {

    private final ClientLead lead;
    private final String brokerEmail;
    private final String brokerMobile;
    private final String ownerEmail;
    private final String ownerMobile;
    private final boolean sendWhatsApp;

    public LeadGeneratedEvent(ClientLead lead, String brokerEmail, String brokerMobile,
            String ownerEmail, String ownerMobile, boolean sendWhatsApp) {
        this.lead = lead;
        this.brokerEmail = brokerEmail;
        this.brokerMobile = brokerMobile;
        this.ownerEmail = ownerEmail;
        this.ownerMobile = ownerMobile;
        this.sendWhatsApp = sendWhatsApp;
    }

    public ClientLead getLead() { return lead; }
    public String getBrokerEmail() { return brokerEmail; }
    public String getBrokerMobile() { return brokerMobile; }
    public String getOwnerEmail() { return ownerEmail; }
    public String getOwnerMobile() { return ownerMobile; }
    public boolean isSendWhatsApp() { return sendWhatsApp; }
}
