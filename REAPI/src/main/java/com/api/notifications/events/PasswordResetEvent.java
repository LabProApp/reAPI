package com.api.notifications.events;

public class PasswordResetEvent {

    private final String name;
    private final String email;
    private final String mobile;

    public PasswordResetEvent(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
}
