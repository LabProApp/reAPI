package com.api.notifications.events;

public class UserRegisteredEvent {

    private final Long userId;
    private final String name;
    private final String email;
    private final String mobile;

    public UserRegisteredEvent(Long userId, String name, String email, String mobile) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
}
