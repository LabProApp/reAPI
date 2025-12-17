package com.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
public class MailerBatchConfig {

    @Bean
    public Job dailyEmailJob(JobRepository jobRepository, Step sendEmailsStep) {
        return new JobBuilder("dailyEmailJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(sendEmailsStep)
                .build();
    }

    @Bean
    public Step sendEmailsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            EmailStoredProcedureReader reader,
            EmailItemProcessor processor,
            EmailItemWriter writer) {

        return new StepBuilder("sendEmailsStep", jobRepository)
                .<EmailRecord, SimpleMailMessage>chunk(20, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
