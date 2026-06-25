package com.api.notifications;

/**
 * Plain-text WhatsApp message templates for each notification workflow.
 *
 * <p>WhatsApp messages must be concise (under 1600 chars), use line breaks for
 * readability, and avoid HTML. Each method returns a ready-to-send string.</p>
 */
public final class WhatsAppTemplates {

    private static final String APP = "KeyBricks";
    private static final String DIVIDER = "─────────────────";

    private WhatsAppTemplates() {}

    // ─── OTP ─────────────────────────────────────────────────────────────────

    public static String otp(String name, String otp, String reason) {
        String intro = "RESEND".equalsIgnoreCase(reason)
                ? "Here is your new OTP as requested."
                : "Thanks for signing up! Please verify your account.";
        return String.format("""
                🔐 *%s — Verification Code*

                Hi %s,
                %s

                Your OTP: *%s*

                ⏱ Valid for 10 minutes.
                🔒 Never share this code with anyone.

                — %s Team""",
                APP, name != null ? name : "User", intro, otp, APP);
    }

    // ─── Welcome (after OTP verified) ────────────────────────────────────────

    public static String welcome(String name) {
        return String.format("""
                🏡 *Welcome to %s!*

                Hi %s, your account is now active.

                ✅ Browse verified listings
                ✅ Submit inquiries instantly
                ✅ Post your own properties
                ✅ Save favourites

                Start exploring: keybricks.in

                — %s Team""",
                APP, name != null ? name : "User", APP);
    }

    // ─── Password Reset Success ───────────────────────────────────────────────

    public static String passwordResetSuccess(String name) {
        return String.format("""
                🔑 *%s — Password Updated*

                Hi %s,

                Your password has been reset successfully.
                You can now log in with your new password.

                ⚠️ If you did not make this change, contact support immediately.

                — %s Team""",
                APP, name != null ? name : "User", APP);
    }

    // ─── Property Inquiry — Customer Confirmation ─────────────────────────────

    public static String propertyInquiryCustomer(String name, String propertyTitle,
            String city, String inquiryType) {
        String icon = "RENT".equalsIgnoreCase(inquiryType) ? "🔑" : "🏡";
        return String.format("""
                %s *%s — %s Inquiry Confirmed*

                Hi %s,

                Your inquiry for:
                📍 *%s*, %s

                has been received! The owner/broker will contact you within 24 hours.

                — %s Team""",
                icon, APP, inquiryType, name != null ? name : "User",
                propertyTitle != null ? propertyTitle : "this property",
                city != null ? city : "", APP);
    }

    // ─── Property Inquiry — Owner / Agent Alert ───────────────────────────────

    public static String propertyInquiryAgent(String customerName, String customerMobile,
            String propertyTitle, String city, String inquiryType) {
        return String.format("""
                📣 *%s — New %s Inquiry*
                %s

                Customer: *%s*
                Mobile:   %s

                Property: *%s*
                City:     %s

                Log in to follow up promptly.

                — %s""",
                APP, inquiryType, DIVIDER,
                customerName != null ? customerName : "—",
                customerMobile != null ? customerMobile : "—",
                propertyTitle != null ? propertyTitle : "—",
                city != null ? city : "—",
                APP);
    }

    // ─── Property Share ───────────────────────────────────────────────────────

    public static String propertyShare(String senderName, String propertyTitle,
            String city, String location, Double price, String rentOrSale,
            Integer bedrooms, String type, String contactNumber) {
        String bedroomPart = bedrooms != null ? bedrooms + " BHK " : "";
        String priceStr = price != null ? String.format("₹%,.0f", price) : "N/A";
        String contactPart = contactNumber != null && !contactNumber.isBlank()
                ? "\n📞 Contact: " + contactNumber : "";
        return String.format("""
                🏠 *Property shared by %s*
                %s

                *%s*
                🏗 %s%s
                📍 %s, %s
                💰 %s (%s)%s

                View on *%s*: keybricks.in

                — %s""",
                senderName != null ? senderName : "a user", DIVIDER,
                propertyTitle != null ? propertyTitle : "—",
                bedroomPart, type != null ? type : "",
                location != null ? location : "", city != null ? city : "",
                priceStr, rentOrSale != null ? rentOrSale : "—",
                contactPart, APP, APP);
    }

    // ─── Lead Generated — Broker / Owner Alert ────────────────────────────────

    public static String leadGeneratedBroker(String customerName, String customerMobile,
            String propertyTitle, String city, Long leadId, String leadType) {
        String icon = leadType != null && leadType.contains("LOAN") ? "💰"
                : leadType != null && leadType.contains("LEGAL") ? "⚖️" : "🏡";
        return String.format("""
                %s *%s — New Lead #%s*
                %s

                Customer: *%s*
                Mobile:   %s

                Property: %s
                City:     %s

                Log in to respond and close the deal!

                — %s""",
                icon, APP, leadId != null ? leadId.toString() : "—", DIVIDER,
                customerName != null ? customerName : "—",
                customerMobile != null ? customerMobile : "—",
                propertyTitle != null ? propertyTitle : "N/A",
                city != null ? city : "N/A",
                APP);
    }

    // ─── Lead Generated — Customer Confirmation ───────────────────────────────

    public static String leadGeneratedCustomer(String name, String propertyTitle,
            String city, String leadType) {
        boolean isLoan = leadType != null && (leadType.contains("LOAN") || leadType.contains("LAP")
                || leadType.contains("TRANSFER"));
        boolean isLegal = leadType != null && (leadType.contains("REGISTRATION")
                || leadType.contains("AGREEMENT") || leadType.contains("DOCUMENT"));

        String serviceDesc = isLoan ? "loan enquiry"
                : isLegal ? "legal service enquiry"
                : "property inquiry for *" + (propertyTitle != null ? propertyTitle : "this property") + "*"
                  + (city != null && !city.isBlank() ? " in " + city : "");

        return String.format("""
                ✅ *%s — Inquiry Received*

                Hi %s,

                Your %s has been received!
                Our team will contact you shortly.

                — %s Team""",
                APP, name != null ? name : "User", serviceDesc, APP);
    }

    // ─── Lead Status Update ───────────────────────────────────────────────────

    public static String leadStatusUpdate(String customerName, String propertyTitle, String status) {
        return String.format("""
                📋 *%s — Inquiry Update*

                Hi %s,

                Your inquiry for *%s* has been updated.
                New Status: *%s*

                Log in to see the latest details.

                — %s Team""",
                APP, customerName != null ? customerName : "User",
                propertyTitle != null ? propertyTitle : "your property",
                status, APP);
    }
}
