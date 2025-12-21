package com.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class EmailItemProcessor
        implements ItemProcessor<EmailRecord, SimpleMailMessage> {

    private static final Logger log =
            LoggerFactory.getLogger(EmailItemProcessor.class);

    @Override
    public SimpleMailMessage process(EmailRecord record) {

        if (record == null) {
            log.warn("Received null EmailRecord in processor");
            return null;
        }

        log.debug("Processing EmailRecord id={} email={}",
                record.getId(), record.getEmail());

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(record.getEmail());
        msg.setSubject(record.getSubject());
        msg.setText(record.getMessage());

        log.trace("EmailMessage created for id={} (subject length={})",
                record.getId(),
                record.getSubject() != null ? record.getSubject().length() : 0);

        return msg;
    }
}
