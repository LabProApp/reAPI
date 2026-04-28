package com.api.notifications;

public class NotificationTemplates {

    private static final String APP = "KeyBricks";

    // ─── Property Share ──────────────────────────────────────────────────────

    public static String propertyShareSms(String senderName, String title, String location, String city,
            Double price, String rentOrSale, Integer bedrooms, String type, String contactNumber) {
        String bedroomPart = bedrooms != null ? bedrooms + "BHK " : "";
        String contactPart = contactNumber != null && !contactNumber.isBlank() ? " | " + contactNumber : "";
        return String.format("%s shared: %s | %s%s | %s, %s | ₹%,.0f (%s)%s - %s",
                senderName, title, bedroomPart, type, location, city, price, rentOrSale, contactPart, APP);
    }

    public static String propertyShareEmailSubject(String senderName, String title) {
        return senderName + " shared a property – " + title;
    }

    public static String propertyShareEmailBody(String senderName, String title, String address, String location,
            String city, String state, Double price, String rentOrSale, Integer bedrooms, Integer bathrooms,
            String type, Double carpetArea, String constructionStatus, String contactNumber, String description) {
        String bedroomPart = bedrooms != null ? bedrooms + " BHK " : "";
        String areaPart = carpetArea != null ? String.format(" | %.0f sq.ft", carpetArea) : "";
        String statusPart = constructionStatus != null && !constructionStatus.isBlank() ? " | " + constructionStatus : "";
        String contactPart = contactNumber != null && !contactNumber.isBlank() ? "\nContact: " + contactNumber : "";
        String descPart = description != null && !description.isBlank() ? "\n\n" + description : "";
        return String.format("Hi,\n\n%s shared this property with you:\n%s | %s%s | %s, %s, %s\nPrice: ₹%,.0f (%s)%s%s%s%s\n\n- %s Team",
                senderName, title, bedroomPart, type, location, city, state, price, rentOrSale, areaPart, statusPart, contactPart, descPart, APP);
    }

    // ─── Property Inquiry (customer → broker/owner) ──────────────────────────

    public static String propertyInquirySms(String customerName, String mobile, String propTitle, String city, Long leadId) {
        return String.format("[%s] New inquiry #%d: %s (%s) interested in '%s', %s. Reply to connect.", APP, leadId, customerName, mobile, propTitle, city != null ? city : "N/A");
    }

    public static String propertyInquiryEmailSubject(String customerName, String propTitle) {
        return String.format("New Inquiry – %s is interested in '%s'", customerName, propTitle);
    }

    public static String propertyInquiryEmailBody(String customerName, String mobile, String email, String propTitle,
            String city, Double price, String message, Long leadId) {
        String emailLine = email != null && !email.isBlank() ? " | Email: " + email : "";
        String priceLine = price != null ? String.format(" | Price: ₹%,.0f", price) : "";
        String msgLine = message != null && !message.isBlank() ? "\n\nCustomer message: \"" + message + "\"" : "";
        return String.format("You have a new inquiry (Lead #%d):\n\nCustomer: %s | Mobile: %s%s\nProperty: %s | City: %s%s%s\n\nLog in to follow up. - %s Team",
                leadId, customerName, mobile, emailLine, propTitle, city != null ? city : "N/A", priceLine, msgLine, APP);
    }

    // ─── Property Inquiry Confirmation (sent to customer) ────────────────────

    public static String propertyInquiryConfirmationSms(String name, String propTitle, String city) {
        return String.format("Hi %s, your inquiry for '%s' in %s is sent! The broker will contact you within 24 hrs. - %s", name, propTitle, city != null ? city : "N/A", APP);
    }

    public static String propertyInquiryConfirmationEmailSubject(String propTitle) {
        return String.format("Inquiry Sent – '%s' | %s", propTitle, APP);
    }

    public static String propertyInquiryConfirmationEmailBody(String name, String propTitle, String city, Double price) {
        String priceLine = price != null ? String.format(" | Price: ₹%,.0f", price) : "";
        return String.format("Hi %s,\n\nYour inquiry for '%s' in %s%s has been sent to the broker.\nThey will reach you within 24 hours.\n\n- %s Team",
                name, propTitle, city != null ? city : "N/A", priceLine, APP);
    }

    // ─── Lead Status Update (broker updates lead status) ─────────────────────

    public static String leadStatusUpdateSms(String customerName, String propTitle, String status) {
        return String.format("Hi %s, your inquiry for '%s' has been updated to: %s. - %s", customerName, propTitle, status, APP);
    }

    public static String leadStatusUpdateEmailSubject(String propTitle, String status) {
        return String.format("Inquiry Update – %s | Status: %s", propTitle, status);
    }

    public static String leadStatusUpdateEmailBody(String customerName, String propTitle, String city, String status, String remark) {
        String remarkLine = remark != null && !remark.isBlank() ? "\nBroker note: " + remark : "";
        return String.format("Hi %s,\n\nYour inquiry for '%s' in %s has been updated.\nStatus: %s%s\n\n- %s Team",
                customerName, propTitle, city != null ? city : "N/A", status, remarkLine, APP);
    }

    // ─── Welcome ─────────────────────────────────────────────────────────────

    public static String welcomeSms(String name) {
        return String.format("Welcome to %s, %s! Your account is active. Browse properties today. - %s", APP, name, APP);
    }

    public static String welcomeEmailSubject() {
        return "Welcome to " + APP + "!";
    }

    public static String welcomeEmailBody(String name) {
        return String.format("Hi %s,\n\nWelcome to %s! Your account is now active.\nBrowse listings, submit inquiries, and post your own properties.\n\n- %s Team", name, APP, APP);
    }
}
