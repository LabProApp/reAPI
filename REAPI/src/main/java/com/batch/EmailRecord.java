package com.batch;

public class EmailRecord {
    private Long id;
    private String email;
    private String subject;
    private String message;

    public EmailRecord() {}

    public EmailRecord(Long id, String email, String subject, String message) {
        this.id = id;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
