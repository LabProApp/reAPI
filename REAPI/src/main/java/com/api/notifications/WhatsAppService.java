package com.api.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class WhatsAppService {

	private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

	private final TwilioConfig config;

	
	public WhatsAppService(TwilioConfig config) {
		this.config = config;
	}

	
	@PostConstruct
	public void init() {
		String sid = config.getAccountSid();
		if (sid == null || sid.isBlank()) {
			log.warn("WhatsAppService - Twilio credentials not configured; init skipped");
			return;
		}
		try {
			Twilio.init(sid, config.getAuthToken());
			log.info("WhatsAppService - Twilio initialized");
		} catch (Exception e) {
			log.warn("WhatsAppService - Twilio init failed; WhatsApp sends will be no-ops: {}", e.getMessage());
		}
	}

	public String sendMessage(String toNumber, String messageBody) {
		String sid = config.getAccountSid();
		if (sid == null || sid.isBlank()) {
			log.warn("sendMessage - Twilio not configured; skipping WhatsApp send to {}", toNumber);
			return null;
		}
		log.info("sendMessage - Sending WhatsApp message to={}", toNumber);
		try {
			Message message = Message.creator(new PhoneNumber("whatsapp:" + toNumber),
					new PhoneNumber(config.getWhatsappNumber()),
					messageBody).create();
			log.info("sendMessage - WhatsApp message sent successfully to={}, sid={}", toNumber, message.getSid());
			return message.getSid();
		} catch (Exception e) {
			log.error("sendMessage - Failed to send WhatsApp message to={}: {}", toNumber, e.getMessage(), e);
			return null;
		}
	}
}
