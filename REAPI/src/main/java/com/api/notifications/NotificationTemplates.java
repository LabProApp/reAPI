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

    // ─── Lead Created — Broker / Owner (Property leads) ─────────────────────

    public static String brokerLeadSms(String customerName, String mobile, String propTitle, String city, Long leadId) {
        return String.format("[%s] New lead #%d: %s (%s) interested in '%s', %s. Log in to follow up.", APP, leadId, customerName, mobile, propTitle, city != null ? city : "N/A");
    }

    public static String brokerLeadEmailSubject(String customerName, String propTitle) {
        return String.format("[%s] New Inquiry – %s is interested in '%s'", APP, customerName, propTitle);
    }

    public static String brokerLeadEmailBody(String customerName, String mobile, String email, String propTitle,
            String city, Double price, String message, Long leadId) {
        String emailLine = email != null && !email.isBlank() ? " | " + email : "";
        String priceLine = price != null ? String.format(" | ₹%,.0f", price) : "";
        String msgLine = message != null && !message.isBlank() ? "\nMessage: \"" + message + "\"" : "";
        return String.format("[%s] Lead #%d\nCustomer: %s | %s%s\nProperty: %s, %s%s%s\n\nLog in to respond. - %s Team",
                APP, leadId, customerName, mobile, emailLine, propTitle, city != null ? city : "N/A", priceLine, msgLine, APP);
    }

    // ─── Lead Created — Bank (Loan leads) ───────────────────────────────────

    public static String bankLeadSms(String customerName, String mobile, Double loanAmount, String loanType, Long leadId) {
        String amt = loanAmount != null ? String.format(" | ₹%,.0f", loanAmount) : "";
        return String.format("[%s] Loan enquiry #%d: %s (%s) needs %s loan%s. - %s", APP, leadId, customerName, mobile, loanType != null ? loanType : "home", amt, APP);
    }

    public static String bankLeadEmailSubject(String customerName, Long leadId) {
        return String.format("[%s] Loan Enquiry #%d – %s", APP, leadId, customerName);
    }

    public static String bankLeadEmailBody(String customerName, String mobile, String email, Double loanAmount,
            Integer tenureYears, String loanType, String preferredBank, Long leadId) {
        String emailLine = email != null && !email.isBlank() ? " | " + email : "";
        String amt = loanAmount != null ? String.format("₹%,.0f", loanAmount) : "N/A";
        String tenure = tenureYears != null ? tenureYears + " yrs" : "N/A";
        return String.format("[%s] Loan Enquiry #%d\nApplicant: %s | %s%s\nLoan: %s | Amount: %s | Tenure: %s | Preferred Bank: %s\n\n- %s Team",
                APP, leadId, customerName, mobile, emailLine,
                loanType != null ? loanType : "HOME_LOAN", amt, tenure,
                preferredBank != null && !preferredBank.isBlank() ? preferredBank : "Any", APP);
    }

    // ─── Lead Created — Legal / Service Provider (Legal leads) ──────────────

    public static String legalLeadSms(String customerName, String mobile, String services, String city, Long leadId) {
        return String.format("[%s] Legal enquiry #%d: %s (%s) needs '%s' in %s. - %s", APP, leadId, customerName, mobile, services != null ? services : "services", city != null ? city : "N/A", APP);
    }

    public static String legalLeadEmailSubject(String customerName, Long leadId) {
        return String.format("[%s] Legal Enquiry #%d – %s", APP, leadId, customerName);
    }

    public static String legalLeadEmailBody(String customerName, String mobile, String email, String services,
            String specifications, String city, Long leadId) {
        String emailLine = email != null && !email.isBlank() ? " | " + email : "";
        String specLine = specifications != null && !specifications.isBlank() ? "\nDetails: " + specifications : "";
        return String.format("[%s] Legal Enquiry #%d\nClient: %s | %s%s\nServices: %s | City: %s%s\n\n- %s Team",
                APP, leadId, customerName, mobile, emailLine,
                services != null ? services : "N/A", city != null ? city : "N/A", specLine, APP);
    }

    // ─── Customer Confirmation ────────────────────────────────────────────────

    public static String customerConfirmationSms(String name, String propTitle, String city) {
        return String.format("Hi %s, your inquiry for '%s' in %s is sent! The broker will contact you within 24 hrs. - %s", name, propTitle, city != null ? city : "N/A", APP);
    }

    public static String customerLoanConfirmationSms(String name, String loanType) {
        return String.format("Hi %s, your %s loan enquiry is received! Our team will contact you shortly. - %s", name, loanType != null ? loanType : "loan", APP);
    }

    public static String customerLegalConfirmationSms(String name, String services) {
        return String.format("Hi %s, your enquiry for '%s' is received! Our team will contact you shortly. - %s", name, services != null ? services : "legal services", APP);
    }

    public static String customerConfirmationEmailSubject(String propTitle) {
        return String.format("Enquiry Confirmed – '%s' | %s", propTitle, APP);
    }

    public static String customerConfirmationEmailBody(String name, String propTitle, String city, Double price) {
        String priceLine = price != null ? String.format(" | ₹%,.0f", price) : "";
        return String.format("Hi %s,\n\nYour enquiry for '%s' in %s%s has been sent. The broker will reach you within 24 hrs.\n\n- %s Team",
                name, propTitle, city != null ? city : "N/A", priceLine, APP);
    }

    public static String customerLoanConfirmationEmailSubject(String loanType) {
        return String.format("Loan Enquiry Received – %s | %s", loanType != null ? loanType : "Home Loan", APP);
    }

    public static String customerLoanConfirmationEmailBody(String name, String loanType, Double loanAmount) {
        String amt = loanAmount != null ? String.format(" of ₹%,.0f", loanAmount) : "";
        return String.format("Hi %s,\n\nYour %s enquiry%s is received. Our team will contact you shortly to discuss next steps.\n\n- %s Team",
                name, loanType != null ? loanType : "loan", amt, APP);
    }

    public static String customerLegalConfirmationEmailSubject(String services) {
        return String.format("Enquiry Received – %s | %s", services != null ? services : "Legal Services", APP);
    }

    public static String customerLegalConfirmationEmailBody(String name, String services, String city) {
        return String.format("Hi %s,\n\nYour enquiry for '%s' in %s is received. Our team will contact you shortly.\n\n- %s Team",
                name, services != null ? services : "legal services", city != null ? city : "N/A", APP);
    }

    // ─── Lead Status Update (sent to customer) ───────────────────────────────

    public static String leadStatusUpdateSms(String customerName, String propTitle, String status) {
        return String.format("Hi %s, your inquiry for '%s' has been updated to: %s. - %s", customerName, propTitle, status, APP);
    }

    public static String leadStatusUpdateEmailSubject(String propTitle, String status) {
        return String.format("Inquiry Update – %s | Status: %s", propTitle, status);
    }

    public static String leadStatusUpdateEmailBody(String customerName, String propTitle, String city, String status, String remark) {
        String remarkLine = remark != null && !remark.isBlank() ? "\nNote: " + remark : "";
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
