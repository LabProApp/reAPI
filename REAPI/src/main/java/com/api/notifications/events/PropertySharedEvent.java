package com.api.notifications.events;

public class PropertySharedEvent {

    private final String senderName;
    private final String recipientEmail;
    private final String recipientMobile;
    private final boolean sendWhatsApp;

    private final String propertyTitle;
    private final String propertyAddress;
    private final String propertyLocation;
    private final String propertyCity;
    private final String propertyState;
    private final Double price;
    private final String rentOrSale;
    private final Integer bedrooms;
    private final Integer bathrooms;
    private final String type;
    private final Double carpetArea;
    private final String constructionStatus;
    private final String contactNumber;
    private final String description;

    public PropertySharedEvent(String senderName, String recipientEmail, String recipientMobile,
            boolean sendWhatsApp, String propertyTitle, String propertyAddress,
            String propertyLocation, String propertyCity, String propertyState,
            Double price, String rentOrSale, Integer bedrooms, Integer bathrooms,
            String type, Double carpetArea, String constructionStatus,
            String contactNumber, String description) {
        this.senderName = senderName;
        this.recipientEmail = recipientEmail;
        this.recipientMobile = recipientMobile;
        this.sendWhatsApp = sendWhatsApp;
        this.propertyTitle = propertyTitle;
        this.propertyAddress = propertyAddress;
        this.propertyLocation = propertyLocation;
        this.propertyCity = propertyCity;
        this.propertyState = propertyState;
        this.price = price;
        this.rentOrSale = rentOrSale;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.type = type;
        this.carpetArea = carpetArea;
        this.constructionStatus = constructionStatus;
        this.contactNumber = contactNumber;
        this.description = description;
    }

    public String getSenderName() { return senderName; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getRecipientMobile() { return recipientMobile; }
    public boolean isSendWhatsApp() { return sendWhatsApp; }
    public String getPropertyTitle() { return propertyTitle; }
    public String getPropertyAddress() { return propertyAddress; }
    public String getPropertyLocation() { return propertyLocation; }
    public String getPropertyCity() { return propertyCity; }
    public String getPropertyState() { return propertyState; }
    public Double getPrice() { return price; }
    public String getRentOrSale() { return rentOrSale; }
    public Integer getBedrooms() { return bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public String getType() { return type; }
    public Double getCarpetArea() { return carpetArea; }
    public String getConstructionStatus() { return constructionStatus; }
    public String getContactNumber() { return contactNumber; }
    public String getDescription() { return description; }
}
