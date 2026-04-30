package com.api.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

/**
 * Spring service that wraps the Twilio API to send WhatsApp messages.
 *
 * <p>On application startup, the {@link #init()} method initialises the Twilio SDK
 * using credentials and the WhatsApp-enabled sender number from {@link TwilioConfig}.
 * Messages are sent synchronously; callers are responsible for exception handling if
 * fire-and-forget behaviour is required.
 */
@Service
public class WhatsAppService {

	private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

	private final TwilioConfig config;

	/**
	 * Constructs the {@code WhatsAppService} with the required Twilio configuration.
	 *
	 * @param config the Twilio configuration properties (accountSid, authToken, whatsappNumber)
	 */
	public WhatsAppService(TwilioConfig config) {
		this.config = config;
	}

	/**
	 * Initialises the Twilio SDK with credentials from {@link TwilioConfig}.
	 * Invoked automatically by Spring after dependency injection is complete.
	 */
	@PostConstruct
	public void init() {
		log.info("WhatsAppService - Initializing Twilio with accountSid={}", config.getAccountSid());
		Twilio.init(config.getAccountSid(), config.getAuthToken());
		log.info("WhatsAppService - Twilio initialized successfully");
	}

	/**
	 * Sends a WhatsApp message to the specified phone number via the Twilio API.
	 *
	 * <p>The destination number must include the country code (e.g. {@code +919876543210}).
	 * The Twilio "whatsapp:" URI scheme is prepended automatically.
	 *
	 * @param toNumber    the destination phone number in E.164 format (e.g. {@code +919876543210})
	 * @param messageBody the text content of the WhatsApp message
	 * @return the Twilio message SID of the sent message
	 * @throws RuntimeException if the Twilio API call fails
	 */
	public String sendMessage(String toNumber, String messageBody) {
		log.info("sendMessage - Sending WhatsApp message to={}", toNumber);
		try {
			Message message = Message.creator(new PhoneNumber("whatsapp:" + toNumber),
					new PhoneNumber(config.getWhatsappNumber()),
					messageBody).create();
			log.info("sendMessage - WhatsApp message sent successfully to={}, sid={}", toNumber, message.getSid());
			return message.getSid();
		} catch (Exception e) {
			log.error("sendMessage - Failed to send WhatsApp message to={}: {}", toNumber, e.getMessage(), e);
			throw e;
		}
	}
}
