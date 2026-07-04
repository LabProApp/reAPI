package com.api.notifications;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WhatsApp provider backed by the Meta Cloud API (WhatsApp Business Platform).
 *
 * <p>Activated when {@code whatsapp.provider=meta} is set in application properties.
 * Requires a verified Meta WhatsApp Business phone number and a permanent system-user
 * access token from the Meta Business Suite.</p>
 *
 * <p>Required properties (set via environment variables):
 * <ul>
 *   <li>{@code META_WA_PHONE_NUMBER_ID} — numeric Phone Number ID from Meta Business Suite</li>
 *   <li>{@code META_WA_ACCESS_TOKEN}    — permanent system-user access token</li>
 *   <li>{@code META_WA_API_VERSION}     — Graph API version, e.g. {@code v19.0} (optional)</li>
 * </ul>
 * </p>
 */
@Primary
@Component
@ConditionalOnProperty(name = "whatsapp.provider", havingValue = "meta")
public class MetaWhatsAppProvider implements WhatsAppProvider {

    private static final Logger log = LoggerFactory.getLogger(MetaWhatsAppProvider.class);
    private static final String GRAPH_BASE = "https://graph.facebook.com";

    @Value("${meta.whatsapp.phone-number-id:}")
    private String phoneNumberId;

    @Value("${meta.whatsapp.access-token:}")
    private String accessToken;

    @Value("${meta.whatsapp.api-version:v19.0}")
    private String apiVersion;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isConfigured() {
        return phoneNumberId != null && !phoneNumberId.isBlank()
                && accessToken != null && !accessToken.isBlank();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String send(String toNumber, String body) {
        if (!isConfigured()) {
            log.warn("MetaWhatsAppProvider - not configured; skipping WhatsApp send to {}", toNumber);
            return null;
        }

        // Meta API expects digits only — strip leading '+' if present
        String to = toNumber.startsWith("+") ? toNumber.substring(1) : toNumber;

        String url = GRAPH_BASE + "/" + apiVersion + "/" + phoneNumberId + "/messages";

        try {
            Map<String, Object> payload = Map.of(
                    "messaging_product", "whatsapp",
                    "to",               to,
                    "type",             "text",
                    "text",             Map.of("body", body)
            );
            String json = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
                List<Map<String, Object>> messages = (List<Map<String, Object>>) responseBody.get("messages");
                if (messages != null && !messages.isEmpty()) {
                    String msgId = (String) messages.get(0).get("id");
                    log.info("MetaWhatsAppProvider - sent msgId={} to={}", msgId, toNumber);
                    return msgId;
                }
                log.warn("MetaWhatsAppProvider - send succeeded but no message ID in response to={}", toNumber);
                return "ok";
            }

            // Parse Meta error response
            String errorDetail = extractMetaError(response.body());
            log.error("MetaWhatsAppProvider - HTTP {} to={}: {}", response.statusCode(), toNumber, errorDetail);
            throw new RuntimeException("Meta WhatsApp API error " + response.statusCode() + ": " + errorDetail);

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            log.error("MetaWhatsAppProvider - send failed to={}: {}", toNumber, e.getMessage(), e);
            throw new RuntimeException("Meta WhatsApp send failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractMetaError(String responseBody) {
        try {
            Map<String, Object> body = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> error = (Map<String, Object>) body.get("error");
            if (error != null) {
                return error.getOrDefault("message", "unknown error").toString()
                        + " (code=" + error.getOrDefault("code", "?") + ")";
            }
        } catch (Exception ignored) { }
        return responseBody;
    }
}
