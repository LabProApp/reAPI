package com.api.notifications.events;

public class OtpGeneratedEvent {

    private final String name;
    private final String email;
    private final String mobile;
    private final String otp;
    private final String reason; // SIGNUP or RESEND

    public OtpGeneratedEvent(String name, String email, String mobile, String otp, String reason) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.otp = otp;
        this.reason = reason;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getOtp() { return otp; }
    public String getReason() { return reason; }
}
