package com.api.notifications;

import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

@Service
public class WhatsAppService {

	private final TwilioConfig config;

	public WhatsAppService(TwilioConfig config) {
		this.config = config;
	}

	@PostConstruct
	public void init() {
		Twilio.init(config.getAccountSid(), config.getAuthToken());
	}

	public String sendMessage(String toNumber, String messageBody) {
		Message message = Message.creator(new PhoneNumber("whatsapp:" + toNumber), // recipient
				new PhoneNumber(config.getWhatsappNumber()), // Twilio number
				messageBody).create();

		return message.getSid();
	}
}
