package com.api.notifications;

import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WhatsAppService {

	private final TwilioConfig config;

	public WhatsAppService(TwilioConfig config) {
		this.config = config;
	}

	@PostConstruct
	public void init() {
		log.info("WhatsAppService - Initializing Twilio with accountSid={}", config.getAccountSid());
		Twilio.init(config.getAccountSid(), config.getAuthToken());
		log.info("WhatsAppService - Twilio initialized successfully");
	}

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
