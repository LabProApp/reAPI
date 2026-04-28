package com.api.notifications;

public class NotificationTemplates {

    private static final String APP = "KeyBricks";

    // ─── Inquiry Confirmed ───────────────────────────────────────────────────

    public static String inquiryConfirmationSms(String name, String type, Long id) {
        return String.format("Hi %s, your %s inquiry INQ-%d is confirmed. We'll contact you within 24 hrs. - %s", name, type, id, APP);
    }

    public static String inquiryConfirmationEmailSubject(String type, Long id) {
        return String.format("Inquiry Confirmed – INQ-%d | %s", id, type);
    }

    public static String inquiryConfirmationEmailBody(String name, String type, Long id, String city, Double budget, Double loanAmount) {
        String budgetLine = budget != null ? String.format(", Budget: ₹%,.0f", budget) : "";
        String loanLine = loanAmount != null ? String.format(", Loan: ₹%,.0f", loanAmount) : "";
        String cityLine = city != null && !city.isBlank() ? ", City: " + city : "";
        return String.format("Hi %s,\n\nYour %s inquiry (INQ-%d) is confirmed%s%s%s.\nOur team will reach out within 24 hours.\n\n- %s Team", name, type, id, cityLine, budgetLine, loanLine, APP);
    }

    // ─── Admin Alert ─────────────────────────────────────────────────────────

    public static String inquiryAdminAlertSms(String name, String mobile, String type, String city, Long id) {
        return String.format("[%s] New INQ-%d: %s (%s), Type: %s, City: %s", APP, id, name, mobile, type, city != null ? city : "N/A");
    }

    public static String inquiryAdminAlertEmailSubject(String name, Long id) {
        return String.format("[New Inquiry] INQ-%d | %s", id, name);
    }

    public static String inquiryAdminAlertEmailBody(String name, String mobile, String email, String type, String city, Double budget, Long id) {
        String budgetLine = budget != null ? String.format(" | Budget: ₹%,.0f", budget) : "";
        String emailLine = email != null && !email.isBlank() ? " | Email: " + email : "";
        return String.format("New inquiry received:\nRef: INQ-%d | Name: %s | Mobile: %s%s\nType: %s | City: %s%s\n\nLog in to assign.", id, name, mobile, emailLine, type, city != null ? city : "N/A", budgetLine);
    }

    // ─── Inquiry Status Update ───────────────────────────────────────────────

    public static String inquiryStatusSms(String name, Long id, String status) {
        return String.format("Hi %s, your inquiry INQ-%d status is now %s. - %s", name, id, status, APP);
    }

    public static String inquiryStatusEmailSubject(Long id) {
        return String.format("Inquiry Update – INQ-%d", id);
    }

    public static String inquiryStatusEmailBody(String name, Long id, String status, String agentName) {
        String agentLine = agentName != null && !agentName.isBlank() ? " Agent: " + agentName + "." : "";
        return String.format("Hi %s,\n\nYour inquiry INQ-%d has been updated: Status → %s.%s\n\n- %s Team", name, id, status, agentLine, APP);
    }

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
