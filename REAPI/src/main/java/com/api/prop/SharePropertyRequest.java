package com.api.prop;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SharePropertyRequest {

    
    @NotBlank(message = "Sender name is required")
    private String senderName;

    
    @Email(message = "Invalid email format")
    private String toEmail;

    
    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Invalid phone number format")
    private String toMobile;

    
    private boolean sendWhatsApp;

    
    @AssertTrue(message = "Either toEmail or toMobile must be provided")
    public boolean isAtLeastOneRecipient() {
        return (toEmail != null && !toEmail.isBlank()) || (toMobile != null && !toMobile.isBlank());
    }

    
    public String getSenderName() {
        return senderName;
    }

    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    
    public String getToEmail() {
        return toEmail;
    }

    
    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    
    public String getToMobile() {
        return toMobile;
    }

    
    public void setToMobile(String toMobile) {
        this.toMobile = toMobile;
    }

    
    public boolean isSendWhatsApp() {
        return sendWhatsApp;
    }

    
    public void setSendWhatsApp(boolean sendWhatsApp) {
        this.sendWhatsApp = sendWhatsApp;
    }
}
