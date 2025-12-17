package com.batch;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailItemProcessor implements ItemProcessor<EmailRecord, SimpleMailMessage> {

    @Override
    public SimpleMailMessage process(EmailRecord record) throws Exception {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(record.getEmail());
        msg.setSubject(record.getSubject());
        msg.setText(record.getMessage());
        return msg;
    }
}
