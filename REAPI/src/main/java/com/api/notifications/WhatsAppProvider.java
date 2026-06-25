package com.api.notifications;

public interface WhatsAppProvider {

    /**
     * Sends a WhatsApp message.
     *
     * @param toNumber E.164 number including country code, e.g. +919876543210
     * @param body     Message text
     * @return provider message SID/ID, or null if unconfigured
     */
    String send(String toNumber, String body);

    /** Returns true if the provider is fully configured and ready to send. */
    boolean isConfigured();
}
