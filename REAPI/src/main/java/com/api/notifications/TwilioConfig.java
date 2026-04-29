package com.api.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Spring {@code @Configuration} class that holds Twilio SDK credentials and sender numbers.
 *
 * <p>Properties are bound from the {@code twilio.*} namespace in {@code application.properties}
 * (or {@code application.yml}):
 * <ul>
 *   <li>{@code twilio.accountSid} — Twilio Account SID</li>
 *   <li>{@code twilio.authToken} — Twilio Auth Token</li>
 *   <li>{@code twilio.whatsappNumber} — Twilio WhatsApp-enabled sender number
 *       (e.g. {@code whatsapp:+14155238886})</li>
 * </ul>
 *
 * <p>This bean is consumed by {@link WhatsAppService} to initialise the Twilio SDK and
 * by {@link CommService} for direct SMS/OTP delivery.
 */
@Configuration
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {

	@Value("${twilio.accountSid}")
	private String accountSid;
	@Value("${twilio.authToken}")
	private String authToken;

	private String whatsappNumber;

	// Getters and setters

	/** @return the Twilio Account SID */
	public String getAccountSid() {
		return accountSid;
	}

	/** @param accountSid the Twilio Account SID to set */
	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	/** @return the Twilio Auth Token */
	public String getAuthToken() {
		return authToken;
	}

	/** @param authToken the Twilio Auth Token to set */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/** @return the Twilio WhatsApp-enabled sender number */
	public String getWhatsappNumber() {
		return whatsappNumber;
	}

	/** @param whatsappNumber the Twilio WhatsApp-enabled sender number to set */
	public void setWhatsappNumber(String whatsappNumber) {
		this.whatsappNumber = whatsappNumber;
	}
}
