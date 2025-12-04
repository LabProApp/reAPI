package com.api.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class CommService {

	@Value("${twilio.accountSid}")
	private String ACCOUNT_SID;

	@Value("${twilio.authToken}")
	private String AUTH_TOKEN;

	@Value("${twilio.source.number}")
	private String SMS_FROM; // e.g., whatsapp:+14155238886

	@Autowired
	private JavaMailSender mailSender;

	// Generate 6-digit OTP
	public String generateOtp() {
		return String.valueOf((int) (Math.random() * 900000) + 100000);
	}

	// Send OTP on SMS
	@Async
	public void sendOtpOnSms(String mobile, String otp) {
		try {
			// Initialize Twilio with your credentials
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

			// Send SMS instead of WhatsApp
			Message message = Message.creator(new PhoneNumber("+91" + mobile), // Recipient number with country code
					new PhoneNumber(SMS_FROM), // Your Twilio SMS-enabled number
					"Your OTP is: " + otp + " (Valid for 10 minutes)").create();

			System.out.println("SMS sent successfully: " + message.getSid());
		} catch (Exception e) {
			System.err.println("Error sending SMS OTP: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Send OTP on SMS
	@Async
	public void sendSMSMessage(String mobile, String txtMessage) {
		try {
			// Initialize Twilio with your credentials
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

			// Send SMS instead of WhatsApp
			Message message = Message.creator(new PhoneNumber("+91" + mobile), // Recipient number with country code
					new PhoneNumber(SMS_FROM), // Your Twilio SMS-enabled number
					txtMessage).create();

			System.out.println("SMS sent successfully: " + message.getSid());
		} catch (Exception e) {
			System.err.println("Error sending SMS Message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Send OTP via Email
	@Async
	public void sendEmail(String email, String email_body, String subject) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject(subject);
		msg.setText(email_body);
		mailSender.send(msg);
	}
}
