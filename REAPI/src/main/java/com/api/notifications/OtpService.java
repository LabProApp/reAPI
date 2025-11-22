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
public class OtpService {

	@Value("${twilio.accountSid}")
	private String ACCOUNT_SID;

	@Value("${twilio.authToken}")
	private String AUTH_TOKEN;

	@Value("${twilio.whatsapp.number}")
	private String WHATSAPP_FROM; // e.g., whatsapp:+14155238886

	@Autowired
	private JavaMailSender mailSender;

	// Generate 6-digit OTP
	public String generateOtp() {
		return String.valueOf((int) (Math.random() * 900000) + 100000);
	}

	// Send OTP on WhatsApp
    @Async
	public void sendOtpOnWhatsapp(String mobile, String otp) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		try {
			Message.creator(new PhoneNumber("whatsapp:+91" + mobile), new PhoneNumber(WHATSAPP_FROM),
					"Your OTP is: " + otp + " (Valid for 10 minutes)").create();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Send OTP via Email
    @Async
	public void sendOtpOnEmail(String email, String otp) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject("Your OTP Verification Code");
		msg.setText("Your OTP is: " + otp + "\nValid for 10 minutes.");
		mailSender.send(msg);
	}
}
