package com.api.notifications;

/**
 * Immutable value object representing an outbound email message.
 *
 * <p>Instances are constructed via the fluent {@link Builder} API:
 * <pre>{@code
 * EmailMessage msg = EmailMessage.to("user@example.com")
 *         .subject("Hello")
 *         .body("Hi there!")
 *         .cc("cc@example.com")
 *         .bcc("bcc@example.com")
 *         .html(false)
 *         .build();
 * }</pre>
 *
 * <p>CC and BCC arrays default to empty arrays; {@code html} defaults to {@code false}.
 */
public class EmailMessage {

    private final String to;
    private final String[] cc;
    private final String[] bcc;
    private final String subject;
    private final String body;
    private final boolean html;

    /**
     * Private constructor — use {@link #to(String)} to obtain a {@link Builder}.
     *
     * @param b the fully configured builder
     */
    private EmailMessage(Builder b) {
        this.to = b.to;
        this.cc = b.cc;
        this.bcc = b.bcc;
        this.subject = b.subject;
        this.body = b.body;
        this.html = b.html;
    }

    /**
     * Creates a new {@link Builder} for an email addressed to the given recipient.
     *
     * @param to the primary recipient email address
     * @return a new {@link Builder} instance
     */
    public static Builder to(String to) { return new Builder(to); }

    /** @return the primary recipient email address */
    public String getTo() { return to; }

    /** @return the CC recipient email addresses (never {@code null}; may be empty) */
    public String[] getCc() { return cc; }

    /** @return the BCC recipient email addresses (never {@code null}; may be empty) */
    public String[] getBcc() { return bcc; }

    /** @return the email subject line */
    public String getSubject() { return subject; }

    /** @return the email body content */
    public String getBody() { return body; }

    /** @return {@code true} if the body should be treated as HTML */
    public boolean isHtml() { return html; }

    /**
     * Fluent builder for {@link EmailMessage}.
     */
    public static class Builder {
        private final String to;
        private String[] cc = new String[0];
        private String[] bcc = new String[0];
        private String subject = "";
        private String body = "";
        private boolean html = false;

        /**
         * Initialises the builder with the mandatory primary recipient.
         *
         * @param to the primary recipient email address
         */
        private Builder(String to) { this.to = to; }

        /**
         * Sets the CC recipients.
         *
         * @param cc one or more CC email addresses
         * @return this builder
         */
        public Builder cc(String... cc) { if (cc != null) this.cc = cc; return this; }

        /**
         * Sets the BCC recipients.
         *
         * @param bcc one or more BCC email addresses
         * @return this builder
         */
        public Builder bcc(String... bcc) { if (bcc != null) this.bcc = bcc; return this; }

        /**
         * Sets the email subject line.
         *
         * @param subject the subject text
         * @return this builder
         */
        public Builder subject(String subject) { this.subject = subject; return this; }

        /**
         * Sets the email body content.
         *
         * @param body the body text (plain text or HTML)
         * @return this builder
         */
        public Builder body(String body) { this.body = body; return this; }

        /**
         * Specifies whether the body should be rendered as HTML.
         *
         * @param html {@code true} for an HTML body; {@code false} for plain text
         * @return this builder
         */
        public Builder html(boolean html) { this.html = html; return this; }

        /**
         * Builds and returns the immutable {@link EmailMessage}.
         *
         * @return a new {@link EmailMessage} with the configured fields
         */
        public EmailMessage build() { return new EmailMessage(this); }
    }
}
