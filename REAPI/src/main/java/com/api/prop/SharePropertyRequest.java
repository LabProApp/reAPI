package com.api.prop;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request payload for sharing a property listing via email, SMS, or WhatsApp.
 *
 * <p>At least one recipient channel must be provided: either {@code toEmail}
 * or {@code toMobile} (or both). This constraint is enforced by the Bean
 * Validation method {@link #isAtLeastOneRecipient()}. When {@code sendWhatsApp}
 * is {@code true} and {@code toMobile} is supplied, a WhatsApp message will
 * also be dispatched to that number.</p>
 */
public class SharePropertyRequest {

    /**
     * The display name of the person sharing the property.
     * Must not be blank.
     */
    @NotBlank(message = "Sender name is required")
    private String senderName;

    /**
     * The email address of the recipient.
     * Must be a valid email format if provided.
     */
    @Email(message = "Invalid email format")
    private String toEmail;

    /**
     * The mobile number of the recipient, including optional country code prefix.
     * Must match the pattern {@code ^[+]?[0-9]{7,15}$} if provided.
     */
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number format")
    private String toMobile;

    /**
     * When {@code true}, a WhatsApp message is sent to {@code toMobile} in
     * addition to (or instead of) an SMS.
     */
    private boolean sendWhatsApp;

    /**
     * Bean Validation cross-field constraint that ensures at least one recipient
     * channel has been supplied.
     *
     * @return {@code true} if either {@code toEmail} or {@code toMobile} is
     *         non-blank; {@code false} otherwise (triggers a validation failure)
     */
    @AssertTrue(message = "Either toEmail or toMobile must be provided")
    public boolean isAtLeastOneRecipient() {
        return (toEmail != null && !toEmail.isBlank()) || (toMobile != null && !toMobile.isBlank());
    }

    /**
     * Returns the sender's display name.
     *
     * @return the sender name
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Sets the sender's display name.
     *
     * @param senderName the sender name; must not be blank
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Returns the recipient email address.
     *
     * @return the recipient email, or {@code null} if not provided
     */
    public String getToEmail() {
        return toEmail;
    }

    /**
     * Sets the recipient email address.
     *
     * @param toEmail the recipient email address
     */
    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    /**
     * Returns the recipient mobile number.
     *
     * @return the recipient mobile number, or {@code null} if not provided
     */
    public String getToMobile() {
        return toMobile;
    }

    /**
     * Sets the recipient mobile number.
     *
     * @param toMobile the recipient mobile number
     */
    public void setToMobile(String toMobile) {
        this.toMobile = toMobile;
    }

    /**
     * Returns whether a WhatsApp message should also be sent.
     *
     * @return {@code true} if a WhatsApp message should be sent
     */
    public boolean isSendWhatsApp() {
        return sendWhatsApp;
    }

    /**
     * Sets whether a WhatsApp message should also be sent.
     *
     * @param sendWhatsApp {@code true} to enable WhatsApp delivery
     */
    public void setSendWhatsApp(boolean sendWhatsApp) {
        this.sendWhatsApp = sendWhatsApp;
    }
}
