package com.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailItemWriter implements ItemWriter<SimpleMailMessage> {

    private final JavaMailSender mailSender;

    public EmailItemWriter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

	/*
	 * @Override public void write(List<? extends SimpleMailMessage> messages) {
	 * messages.forEach(mailSender::send); }
	 */
	@Override
	public void write(Chunk<? extends SimpleMailMessage> chunk) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
