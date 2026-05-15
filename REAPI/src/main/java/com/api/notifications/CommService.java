package com.api.notifications;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.mail.internet.MimeMessage;

@Service
public class CommService {

	private static final Logger log = LoggerFactory.getLogger(CommService.class);

	@Value("${twilio.accountSid:}")
	private String ACCOUNT_SID;

	@Value("${twilio.authToken:}")
	private String AUTH_TOKEN;

	@Value("${twilio.source.number:}")
	private String SMS_FROM;

	@Value("${spring.mail.username:}")
	private String MAIL_USERNAME;

	@Value("${mail.from:noreply@keybricks.in}")
	private String MAIL_FROM;

	@Autowired
	private JavaMailSender mailSender;

	private boolean mailConfigured() {
		return MAIL_USERNAME != null && !MAIL_USERNAME.isBlank();
	}

	private boolean smsConfigured() {
		return ACCOUNT_SID != null && !ACCOUNT_SID.isBlank()
				&& AUTH_TOKEN != null && !AUTH_TOKEN.isBlank();
	}

	// ─── OTP ─────────────────────────────────────────────────────────────────

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private static String mask(String mobile) {
		if (mobile == null || mobile.length() < 4) return "***";
		return mobile.substring(0, 2) + "****" + mobile.substring(mobile.length() - 2);
	}

	public String generateOtp() {
		int otp = SECURE_RANDOM.nextInt(900000) + 100000;
		log.debug("generateOtp - Generated OTP");
		return String.valueOf(otp);
	}

	
	@Async
	public void sendOtpOnSms(String mobile, String otp) {
		if (!smsConfigured()) {
			log.warn("sendOtpOnSms - SMS credentials not configured; skipping send to {}", mask(mobile));
			return;
		}
		log.info("sendOtpOnSms - mobile={}", mask(mobile));
		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message msg = Message.creator(new PhoneNumber("+91" + mobile),
					new PhoneNumber(SMS_FROM),
					"Your OTP is: " + otp + " (Valid for 10 minutes)").create();
			log.info("sendOtpOnSms - sent mobile={}, sid={}", mask(mobile), msg.getSid());
		} catch (Exception e) {
			log.error("sendOtpOnSms - failed mobile={}: {}", mask(mobile), e.getMessage(), e);
		}
	}

	// ─── SMS ─────────────────────────────────────────────────────────────────

	@Async
	public void sendSMSMessage(String mobile, String txtMessage) {
		if (!smsConfigured()) {
			log.warn("sendSMSMessage - SMS credentials not configured; skipping send to {}", mask(mobile));
			return;
		}
		log.info("sendSMSMessage - mobile={}", mask(mobile));
		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message msg = Message.creator(new PhoneNumber("+91" + mobile),
					new PhoneNumber(SMS_FROM),
					txtMessage).create();
			log.info("sendSMSMessage - sent mobile={}, sid={}", mask(mobile), msg.getSid());
		} catch (Exception e) {
			log.error("sendSMSMessage - failed mobile={}: {}", mask(mobile), e.getMessage(), e);
		}
	}

	// ─── Email ────────────────────────────────────────────────────────────────

	
	@Async
	public void sendEmail(EmailMessage msg) {
		if (!mailConfigured()) {
			log.warn("sendEmail - SMTP credentials not configured; skipping send to {}", msg.getTo());
			return;
		}
		log.info("sendEmail - to={}, cc={}, bcc={}, subject={}",
				msg.getTo(),
				msg.getCc() != null ? msg.getCc().length : 0,
				msg.getBcc() != null ? msg.getBcc().length : 0,
				msg.getSubject());
		try {
			MimeMessage mimeMsg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, false, "UTF-8");
			helper.setFrom(MAIL_FROM);
			helper.setTo(msg.getTo());
			if (msg.getCc() != null && msg.getCc().length > 0) helper.setCc(msg.getCc());
			if (msg.getBcc() != null && msg.getBcc().length > 0) helper.setBcc(msg.getBcc());
			helper.setSubject(msg.getSubject());
			helper.setText(msg.getBody(), msg.isHtml());
			mailSender.send(mimeMsg);
			log.info("sendEmail - sent to={}", msg.getTo());
		} catch (jakarta.mail.AuthenticationFailedException e) {
			log.warn("sendEmail - SMTP authentication failed (check MAIL_USERNAME/MAIL_PASSWORD); skipping send to {}: {}",
					msg.getTo(), e.getMessage());
		} catch (Exception e) {
			log.error("sendEmail - failed to={}: {}", msg.getTo(), e.getMessage(), e);
		}
	}

	
	@Async
	public void sendEmail(String to, String body, String subject) {
		sendEmail(EmailMessage.to(to).subject(subject).body(body).build());
	}
}
