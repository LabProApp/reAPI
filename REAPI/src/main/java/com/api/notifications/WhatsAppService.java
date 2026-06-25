package com.api.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Legacy WhatsApp send facade — delegates to {@link WhatsAppProvider}.
 * Kept for backward compat with callers that inject WhatsAppService directly.
 */
@Service
public class WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

    private final WhatsAppProvider provider;

    public WhatsAppService(WhatsAppProvider provider) {
        this.provider = provider;
    }

    public String sendMessage(String toNumber, String messageBody) {
        log.info("sendMessage - dispatching WhatsApp to={}", toNumber);
        return provider.send(toNumber, messageBody);
    }
}
