package com.api.notifications;

/**
 * Static factory class that produces formatted notification message strings for all
 * business events in the real estate platform.
 *
 * <p>Each method returns a ready-to-send string for a specific channel (SMS or email body/subject).
 * Messages are pre-formatted with real data values and do not use {@code {{placeholder}}} syntax —
 * for template-registry-based rendering use {@link TemplateRegistry} and {@link TemplateRenderer}.
 *
 * <p>All methods are static and this class is not intended to be instantiated.
 */
public class NotificationTemplates {

    private static final String APP = "KeyBricks";

    // ─── Property Share ──────────────────────────────────────────────────────

    /**
     * Builds the SMS body sent to a recipient when a property is shared with them.
     *
     * @param senderName    the name of the user sharing the property
     * @param title         the property title
     * @param location      the locality or area of the property
     * @param city          the city where the property is located
     * @param price         the listed price of the property
     * @param rentOrSale    whether the property is for rent or sale
     * @param bedrooms      the number of bedrooms (may be {@code null})
     * @param type          the property type (e.g. "Apartment", "Villa")
     * @param contactNumber the contact number for the listing (may be blank)
     * @return a formatted SMS string describing the shared property
     */
    public static String propertyShareSms(String senderName, String title, String location, String city,
            Double price, String rentOrSale, Integer bedrooms, String type, String contactNumber) {
        String bedroomPart = bedrooms != null ? bedrooms + "BHK " : "";
        String contactPart = contactNumber != null && !contactNumber.isBlank() ? " | " + contactNumber : "";
        return String.format("%s shared: %s | %s%s | %s, %s | ₹%,.0f (%s)%s - %s",
                senderName, title, bedroomPart, type, location, city, price, rentOrSale, contactPart, APP);
    }

    /**
     * Builds the email subject line for a property-share notification.
     *
     * @param senderName the name of the user sharing the property
     * @param title      the property title
     * @return the formatted email subject string
     */
    public static String propertyShareEmailSubject(String senderName, String title) {
        return senderName + " shared a property – " + title;
    }

    /**
     * Builds the email body for a property-share notification with full property details.
     *
     * @param senderName          the name of the user sharing the property
     * @param title               the property title
     * @param address             the full street address
     * @param location            the locality or area
     * @param city                the city
     * @param state               the state
     * @param price               the listed price
     * @param rentOrSale          whether the property is for rent or sale
     * @param bedrooms            number of bedrooms (may be {@code null})
     * @param bathrooms           number of bathrooms (may be {@code null})
     * @param type                the property type
     * @param carpetArea          the carpet area in sq.ft (may be {@code null})
     * @param constructionStatus  the construction status (may be blank)
     * @param contactNumber       the listing contact number (may be blank)
     * @param description         the property description (may be blank)
     * @return a formatted multi-line email body string
     */
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

    /**
     * Builds the SMS body alerting a broker or owner of a new property inquiry lead.
     *
     * @param customerName the customer's name
     * @param mobile       the customer's mobile number
     * @param propTitle    the title of the property inquired about
     * @param city         the city of the property
     * @param leadId       the unique ID of the created lead
     * @return a formatted SMS alert string
     */
    public static String brokerLeadSms(String customerName, String mobile, String propTitle, String city, Long leadId) {
        return String.format("[%s] New lead #%d: %s (%s) interested in '%s', %s. Log in to follow up.", APP, leadId, customerName, mobile, propTitle, city != null ? city : "N/A");
    }

    /**
     * Builds the email subject line for a broker/owner new-property-lead notification.
     *
     * @param customerName the customer's name
     * @param propTitle    the property title
     * @return the formatted email subject string
     */
    public static String brokerLeadEmailSubject(String customerName, String propTitle) {
        return String.format("[%s] New Inquiry – %s is interested in '%s'", APP, customerName, propTitle);
    }

    /**
     * Builds the email body for a broker/owner new-property-lead notification.
     *
     * @param customerName the customer's name
     * @param mobile       the customer's mobile number
     * @param email        the customer's email address (may be blank)
     * @param propTitle    the property title
     * @param city         the city of the property
     * @param price        the listed price (may be {@code null})
     * @param message      the customer's inquiry message (may be blank)
     * @param leadId       the unique ID of the created lead
     * @return a formatted multi-line email body string
     */
    public static String brokerLeadEmailBody(String customerName, String mobile, String email, String propTitle,
            String city, Double price, String message, Long leadId) {
        String emailLine = email != null && !email.isBlank() ? " | " + email : "";
        String priceLine = price != null ? String.format(" | ₹%,.0f", price) : "";
        String msgLine = message != null && !message.isBlank() ? "\nMessage: \"" + message + "\"" : "";
        return String.format("[%s] Lead #%d\nCustomer: %s | %s%s\nProperty: %s, %s%s%s\n\nLog in to respond. - %s Team",
                APP, leadId, customerName, mobile, emailLine, propTitle, city != null ? city : "N/A", priceLine, msgLine, APP);
    }

    // ─── Lead Created — Bank (Loan leads) ───────────────────────────────────

    /**
     * Builds the SMS body alerting a broker or bank contact of a new loan inquiry lead.
     *
     * @param customerName the applicant's name
     * @param mobile       the applicant's mobile number
     * @param loanAmount   the required loan amount (may be {@code null})
     * @param loanType     the loan type (e.g. "HOME_LOAN")
     * @param leadId       the unique ID of the created lead
     * @return a formatted SMS alert string
     */
    public static String bankLeadSms(String customerName, String mobile, Double loanAmount, String loanType, Long leadId) {
        String amt = loanAmount != null ? String.format(" | ₹%,.0f", loanAmount) : "";
        return String.format("[%s] Loan enquiry #%d: %s (%s) needs %s loan%s. - %s", APP, leadId, customerName, mobile, loanType != null ? loanType : "home", amt, APP);
    }

    /**
     * Builds the email subject line for a broker/bank new-loan-lead notification.
     *
     * @param customerName the applicant's name
     * @param leadId       the unique ID of the created lead
     * @return the formatted email subject string
     */
    public static String bankLeadEmailSubject(String customerName, Long leadId) {
        return String.format("[%s] Loan Enquiry #%d – %s", APP, leadId, customerName);
    }

    /**
     * Builds the email body for a broker/bank new-loan-lead notification.
     *
     * @param customerName  the applicant's name
     * @param mobile        the applicant's mobile number
     * @param email         the applicant's email address (may be blank)
     * @param loanAmount    the required loan amount (may be {@code null})
     * @param tenureYears   the requested loan tenure in years (may be {@code null})
     * @param loanType      the loan type string
     * @param preferredBank the applicant's preferred bank (may be blank)
     * @param leadId        the unique ID of the created lead
     * @return a formatted multi-line email body string
     */
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

    /**
     * Builds the SMS body alerting a broker or service provider of a new legal inquiry lead.
     *
     * @param customerName the client's name
     * @param mobile       the client's mobile number
     * @param services     the legal services requested (may be {@code null})
     * @param city         the city of the inquiry
     * @param leadId       the unique ID of the created lead
     * @return a formatted SMS alert string
     */
    public static String legalLeadSms(String customerName, String mobile, String services, String city, Long leadId) {
        return String.format("[%s] Legal enquiry #%d: %s (%s) needs '%s' in %s. - %s", APP, leadId, customerName, mobile, services != null ? services : "services", city != null ? city : "N/A", APP);
    }

    /**
     * Builds the email subject line for a broker/service-provider new-legal-lead notification.
     *
     * @param customerName the client's name
     * @param leadId       the unique ID of the created lead
     * @return the formatted email subject string
     */
    public static String legalLeadEmailSubject(String customerName, Long leadId) {
        return String.format("[%s] Legal Enquiry #%d – %s", APP, leadId, customerName);
    }

    /**
     * Builds the email body for a broker/service-provider new-legal-lead notification.
     *
     * @param customerName   the client's name
     * @param mobile         the client's mobile number
     * @param email          the client's email address (may be blank)
     * @param services       the legal services requested (may be {@code null})
     * @param specifications additional specifications or notes from the client (may be blank)
     * @param city           the city of the inquiry
     * @param leadId         the unique ID of the created lead
     * @return a formatted multi-line email body string
     */
    public static String legalLeadEmailBody(String customerName, String mobile, String email, String services,
            String specifications, String city, Long leadId) {
        String emailLine = email != null && !email.isBlank() ? " | " + email : "";
        String specLine = specifications != null && !specifications.isBlank() ? "\nDetails: " + specifications : "";
        return String.format("[%s] Legal Enquiry #%d\nClient: %s | %s%s\nServices: %s | City: %s%s\n\n- %s Team",
                APP, leadId, customerName, mobile, emailLine,
                services != null ? services : "N/A", city != null ? city : "N/A", specLine, APP);
    }

    // ─── Customer Confirmation ────────────────────────────────────────────────

    /**
     * Builds the customer confirmation SMS after a property inquiry is submitted.
     *
     * @param name      the customer's name
     * @param propTitle the property title
     * @param city      the city of the property
     * @return a formatted confirmation SMS string
     */
    public static String customerConfirmationSms(String name, String propTitle, String city) {
        return String.format("Hi %s, your inquiry for '%s' in %s is sent! The broker will contact you within 24 hrs. - %s", name, propTitle, city != null ? city : "N/A", APP);
    }

    /**
     * Builds the customer confirmation SMS after a loan inquiry is submitted.
     *
     * @param name      the customer's name
     * @param loanType  the loan type string (may be {@code null})
     * @return a formatted loan confirmation SMS string
     */
    public static String customerLoanConfirmationSms(String name, String loanType) {
        return String.format("Hi %s, your %s loan enquiry is received! Our team will contact you shortly. - %s", name, loanType != null ? loanType : "loan", APP);
    }

    /**
     * Builds the customer confirmation SMS after a legal-services inquiry is submitted.
     *
     * @param name     the customer's name
     * @param services the legal services requested (may be {@code null})
     * @return a formatted legal-services confirmation SMS string
     */
    public static String customerLegalConfirmationSms(String name, String services) {
        return String.format("Hi %s, your enquiry for '%s' is received! Our team will contact you shortly. - %s", name, services != null ? services : "legal services", APP);
    }

    /**
     * Builds the email subject line for a customer property-inquiry confirmation.
     *
     * @param propTitle the property title
     * @return the formatted email subject string
     */
    public static String customerConfirmationEmailSubject(String propTitle) {
        return String.format("Enquiry Confirmed – '%s' | %s", propTitle, APP);
    }

    /**
     * Builds the email body for a customer property-inquiry confirmation.
     *
     * @param name      the customer's name
     * @param propTitle the property title
     * @param city      the city of the property
     * @param price     the listed price (may be {@code null})
     * @return a formatted confirmation email body string
     */
    public static String customerConfirmationEmailBody(String name, String propTitle, String city, Double price) {
        String priceLine = price != null ? String.format(" | ₹%,.0f", price) : "";
        return String.format("Hi %s,\n\nYour enquiry for '%s' in %s%s has been sent. The broker will reach you within 24 hrs.\n\n- %s Team",
                name, propTitle, city != null ? city : "N/A", priceLine, APP);
    }

    /**
     * Builds the email subject line for a customer loan-inquiry confirmation.
     *
     * @param loanType the loan type string (may be {@code null})
     * @return the formatted email subject string
     */
    public static String customerLoanConfirmationEmailSubject(String loanType) {
        return String.format("Loan Enquiry Received – %s | %s", loanType != null ? loanType : "Home Loan", APP);
    }

    /**
     * Builds the email body for a customer loan-inquiry confirmation.
     *
     * @param name       the customer's name
     * @param loanType   the loan type string (may be {@code null})
     * @param loanAmount the required loan amount (may be {@code null})
     * @return a formatted loan confirmation email body string
     */
    public static String customerLoanConfirmationEmailBody(String name, String loanType, Double loanAmount) {
        String amt = loanAmount != null ? String.format(" of ₹%,.0f", loanAmount) : "";
        return String.format("Hi %s,\n\nYour %s enquiry%s is received. Our team will contact you shortly to discuss next steps.\n\n- %s Team",
                name, loanType != null ? loanType : "loan", amt, APP);
    }

    /**
     * Builds the email subject line for a customer legal-services inquiry confirmation.
     *
     * @param services the legal services requested (may be {@code null})
     * @return the formatted email subject string
     */
    public static String customerLegalConfirmationEmailSubject(String services) {
        return String.format("Enquiry Received – %s | %s", services != null ? services : "Legal Services", APP);
    }

    /**
     * Builds the email body for a customer legal-services inquiry confirmation.
     *
     * @param name     the customer's name
     * @param services the legal services requested (may be {@code null})
     * @param city     the city of the inquiry
     * @return a formatted legal-services confirmation email body string
     */
    public static String customerLegalConfirmationEmailBody(String name, String services, String city) {
        return String.format("Hi %s,\n\nYour enquiry for '%s' in %s is received. Our team will contact you shortly.\n\n- %s Team",
                name, services != null ? services : "legal services", city != null ? city : "N/A", APP);
    }

    // ─── Lead Status Update (sent to customer) ───────────────────────────────

    /**
     * Builds the SMS body sent to a customer when their lead status is updated.
     *
     * @param customerName the customer's name
     * @param propTitle    the property title
     * @param status       the new status string
     * @return a formatted status-update SMS string
     */
    public static String leadStatusUpdateSms(String customerName, String propTitle, String status) {
        return String.format("Hi %s, your inquiry for '%s' has been updated to: %s. - %s", customerName, propTitle, status, APP);
    }

    /**
     * Builds the email subject line for a lead status-update notification.
     *
     * @param propTitle the property title
     * @param status    the new status string
     * @return the formatted email subject string
     */
    public static String leadStatusUpdateEmailSubject(String propTitle, String status) {
        return String.format("Inquiry Update – %s | Status: %s", propTitle, status);
    }

    /**
     * Builds the email body for a lead status-update notification sent to the customer.
     *
     * @param customerName the customer's name
     * @param propTitle    the property title
     * @param city         the city of the property
     * @param status       the new status string
     * @param remark       an optional remark or note accompanying the status update (may be blank)
     * @return a formatted status-update email body string
     */
    public static String leadStatusUpdateEmailBody(String customerName, String propTitle, String city, String status, String remark) {
        String remarkLine = remark != null && !remark.isBlank() ? "\nNote: " + remark : "";
        return String.format("Hi %s,\n\nYour inquiry for '%s' in %s has been updated.\nStatus: %s%s\n\n- %s Team",
                customerName, propTitle, city != null ? city : "N/A", status, remarkLine, APP);
    }

    // ─── Welcome ─────────────────────────────────────────────────────────────

    /**
     * Builds the welcome SMS sent to a newly registered user.
     *
     * @param name the user's display name
     * @return a formatted welcome SMS string
     */
    public static String welcomeSms(String name) {
        return String.format("Welcome to %s, %s! Your account is active. Browse properties today. - %s", APP, name, APP);
    }

    /**
     * Returns the standard welcome email subject line.
     *
     * @return the welcome email subject string
     */
    public static String welcomeEmailSubject() {
        return "Welcome to " + APP + "!";
    }

    /**
     * Builds the welcome email body sent to a newly registered user.
     *
     * @param name the user's display name
     * @return a formatted welcome email body string
     */
    public static String welcomeEmailBody(String name) {
        return String.format("Hi %s,\n\nWelcome to %s! Your account is now active.\nBrowse listings, submit inquiries, and post your own properties.\n\n- %s Team", name, APP, APP);
    }
}
