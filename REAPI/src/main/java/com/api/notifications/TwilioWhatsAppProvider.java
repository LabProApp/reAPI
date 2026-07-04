package com.api.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Primary
@Component
@ConditionalOnProperty(name = "whatsapp.provider", havingValue = "twilio", matchIfMissing = true)
public class TwilioWhatsAppProvider implements WhatsAppProvider {

    private static final Logger log = LoggerFactory.getLogger(TwilioWhatsAppProvider.class);

    private final TwilioConfig config;
    private boolean initialized = false;

    public TwilioWhatsAppProvider(TwilioConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        String sid = config.getAccountSid();
        if (sid == null || sid.isBlank()) {
            log.warn("TwilioWhatsAppProvider - Twilio credentials not configured; WhatsApp sends will be no-ops");
            return;
        }
        try {
            Twilio.init(sid, config.getAuthToken());
            initialized = true;
            log.info("TwilioWhatsAppProvider - Twilio initialized");
        } catch (Exception e) {
            log.warn("TwilioWhatsAppProvider - Twilio init failed: {}", e.getMessage());
        }
    }

    @Override
    public boolean isConfigured() {
        return initialized
            && config.getAccountSid() != null && !config.getAccountSid().isBlank()
            && config.getWhatsappNumber() != null && !config.getWhatsappNumber().isBlank();
    }

    @Override
    public String send(String toNumber, String body) {
        if (!isConfigured()) {
            log.warn("TwilioWhatsAppProvider - not configured; skipping WhatsApp send to {}", toNumber);
            return null;
        }
        log.info("TwilioWhatsAppProvider - sending WhatsApp to={}", toNumber);
        try {
            Message msg = Message.creator(
                    new PhoneNumber("whatsapp:" + toNumber),
                    new PhoneNumber(config.getWhatsappNumber()),
                    body).create();
            log.info("TwilioWhatsAppProvider - sent sid={} to={}", msg.getSid(), toNumber);
            return msg.getSid();
        } catch (Exception e) {
            log.error("TwilioWhatsAppProvider - send failed to={}: {}", toNumber, e.getMessage(), e);
            return null;
        }
    }
}
