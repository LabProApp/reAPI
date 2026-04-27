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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommService {

	@Value("${twilio.accountSid}")
	private String ACCOUNT_SID;

	@Value("${twilio.authToken}")
	private String AUTH_TOKEN;

	@Value("${twilio.source.number}")
	private String SMS_FROM;

	@Autowired
	private JavaMailSender mailSender;

	public String generateOtp() {
		String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
		log.debug("generateOtp - Generated OTP");
		return otp;
	}

	@Async
	public void sendOtpOnSms(String mobile, String otp) {
		log.info("sendOtpOnSms - Sending OTP SMS to mobile={}", mobile);
		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message message = Message.creator(new PhoneNumber("+91" + mobile),
					new PhoneNumber(SMS_FROM),
					"Your OTP is: " + otp + " (Valid for 10 minutes)").create();
			log.info("sendOtpOnSms - SMS sent successfully to mobile={}, sid={}", mobile, message.getSid());
		} catch (Exception e) {
			log.error("sendOtpOnSms - Failed to send OTP SMS to mobile={}: {}", mobile, e.getMessage(), e);
		}
	}

	@Async
	public void sendSMSMessage(String mobile, String txtMessage) {
		log.info("sendSMSMessage - Sending SMS to mobile={}", mobile);
		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message message = Message.creator(new PhoneNumber("+91" + mobile),
					new PhoneNumber(SMS_FROM),
					txtMessage).create();
			log.info("sendSMSMessage - SMS sent successfully to mobile={}, sid={}", mobile, message.getSid());
		} catch (Exception e) {
			log.error("sendSMSMessage - Failed to send SMS to mobile={}: {}", mobile, e.getMessage(), e);
		}
	}

	@Async
	public void sendEmail(String email, String email_body, String subject) {
		log.info("sendEmail - Sending email to={}, subject={}", email, subject);
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(email);
			msg.setSubject(subject);
			msg.setText(email_body);
			mailSender.send(msg);
			log.info("sendEmail - Email sent successfully to={}", email);
		} catch (Exception e) {
			log.error("sendEmail - Failed to send email to={}: {}", email, e.getMessage(), e);
		}
	}
}
