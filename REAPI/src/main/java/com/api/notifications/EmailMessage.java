package com.api.notifications;

public class EmailMessage {

    private final String to;
    private final String[] cc;
    private final String[] bcc;
    private final String subject;
    private final String body;
    private final boolean html;

    private EmailMessage(Builder b) {
        this.to = b.to;
        this.cc = b.cc;
        this.bcc = b.bcc;
        this.subject = b.subject;
        this.body = b.body;
        this.html = b.html;
    }

    public static Builder to(String to) { return new Builder(to); }

    public String getTo() { return to; }
    public String[] getCc() { return cc; }
    public String[] getBcc() { return bcc; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public boolean isHtml() { return html; }

    public static class Builder {
        private final String to;
        private String[] cc = new String[0];
        private String[] bcc = new String[0];
        private String subject = "";
        private String body = "";
        private boolean html = false;

        private Builder(String to) { this.to = to; }

        public Builder cc(String... cc) { if (cc != null) this.cc = cc; return this; }
        public Builder bcc(String... bcc) { if (bcc != null) this.bcc = bcc; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder body(String body) { this.body = body; return this; }
        public Builder html(boolean html) { this.html = html; return this; }
        public EmailMessage build() { return new EmailMessage(this); }
    }
}
