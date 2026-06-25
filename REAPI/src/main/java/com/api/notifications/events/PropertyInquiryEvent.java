package com.api.notifications.events;

public class PropertyInquiryEvent {

    private final Long leadId;
    private final String customerName;
    private final String customerEmail;
    private final String customerMobile;
    private final String propertyTitle;
    private final String propertyCity;
    private final Double propertyPrice;
    private final String message;
    private final String agentEmail;
    private final String agentMobile;
    private final String ownerEmail;
    private final String ownerMobile;
    private final String inquiryType; // BUY or RENT

    public PropertyInquiryEvent(Long leadId, String customerName, String customerEmail,
            String customerMobile, String propertyTitle, String propertyCity,
            Double propertyPrice, String message,
            String agentEmail, String agentMobile,
            String ownerEmail, String ownerMobile, String inquiryType) {
        this.leadId = leadId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerMobile = customerMobile;
        this.propertyTitle = propertyTitle;
        this.propertyCity = propertyCity;
        this.propertyPrice = propertyPrice;
        this.message = message;
        this.agentEmail = agentEmail;
        this.agentMobile = agentMobile;
        this.ownerEmail = ownerEmail;
        this.ownerMobile = ownerMobile;
        this.inquiryType = inquiryType;
    }

    public Long getLeadId() { return leadId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerMobile() { return customerMobile; }
    public String getPropertyTitle() { return propertyTitle; }
    public String getPropertyCity() { return propertyCity; }
    public Double getPropertyPrice() { return propertyPrice; }
    public String getMessage() { return message; }
    public String getAgentEmail() { return agentEmail; }
    public String getAgentMobile() { return agentMobile; }
    public String getOwnerEmail() { return ownerEmail; }
    public String getOwnerMobile() { return ownerMobile; }
    public String getInquiryType() { return inquiryType; }
}
