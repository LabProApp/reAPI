package com.api.notifications;

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

/**
 * Low-level communication service that provides direct send operations for
 * email (via Spring Mail / JavaMailSender), SMS (via Twilio), and OTP delivery.
 *
 * <p>All send methods are executed asynchronously using {@code @Async} so that
 * callers are not blocked on network I/O. Errors are caught internally and
 * logged; they do not propagate to the caller.
 */
@Service
public class CommService {

	private static final Logger log = LoggerFactory.getLogger(CommService.class);

	@Value("${twilio.accountSid}")
	private String ACCOUNT_SID;

	@Value("${twilio.authToken}")
	private String AUTH_TOKEN;

	@Value("${twilio.source.number}")
	private String SMS_FROM;

	@Value("${spring.mail.username:noreply@keybricks.in}")
	private String MAIL_FROM;

	@Autowired
	private JavaMailSender mailSender;

	// ─── OTP ─────────────────────────────────────────────────────────────────

	/**
	 * Generates a random six-digit OTP string.
	 *
	 * @return a six-digit numeric OTP as a {@code String}
	 */
	public String generateOtp() {
		String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
		log.debug("generateOtp - Generated OTP");
		return otp;
	}

	/**
	 * Asynchronously sends an OTP to the given mobile number via SMS using Twilio.
	 * The message includes a 10-minute validity notice.
	 *
	 * @param mobile the destination mobile number (without country code; {@code +91} is prepended)
	 * @param otp    the one-time password string to send
	 */
	@Async
	public void sendOtpOnSms(String mobile, String otp) {
		log.info("sendOtpOnSms - mobile={}", mobile);
		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message msg = Message.creator(new PhoneNumber("+91" + mobile),
					new PhoneNumber(SMS_FROM),
					"Your OTP is: " + otp + " (Valid for 10 minutes)").create();
			log.info("sendOtpOnSms - sent mobile={}, sid={}", mobile, msg.getSid());
		} catch (Exception e) {
			log.error("sendOtpOnSms - failed mobile={}: {}", mobile, e.getMessage(), e);
		}
	}

	// ─── SMS ─────────────────────────────────────────────────────────────────

	/**
	 * Asynchronously sends a plain-text SMS to the given mobile number via Twilio.
	 *
	 * @param mobile     the destination mobile number (without country code; {@code +91} is prepended)
	 * @param txtMessage the text content of the SMS
	 */
	@Async
	public void sendSMSMessage(String mobile, String txtMessage) {
		log.info("sendSMSMessage - mobile={}", mobile);
		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message msg = Message.creator(new PhoneNumber("+91" + mobile),
					new PhoneNumber(SMS_FROM),
					txtMessage).create();
			log.info("sendSMSMessage - sent mobile={}, sid={}", mobile, msg.getSid());
		} catch (Exception e) {
			log.error("sendSMSMessage - failed mobile={}: {}", mobile, e.getMessage(), e);
		}
	}

	// ─── Email ────────────────────────────────────────────────────────────────

	/** Full email with CC, BCC and optional HTML body. */
	@Async
	public void sendEmail(EmailMessage msg) {
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
		} catch (Exception e) {
			log.error("sendEmail - failed to={}: {}", msg.getTo(), e.getMessage(), e);
		}
	}

	/** Convenience overload — plain text, no CC/BCC. */
	@Async
	public void sendEmail(String to, String body, String subject) {
		sendEmail(EmailMessage.to(to).subject(subject).body(body).build());
	}
}
