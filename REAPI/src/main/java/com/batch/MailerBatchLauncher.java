package com.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class MailerBatchLauncher {

    private static final Logger log =
            LoggerFactory.getLogger(MailerBatchLauncher.class);

    private final JobLauncher jobLauncher;
    private final Job dailyEmailJob;

    public MailerBatchLauncher(JobLauncher jobLauncher, Job dailyEmailJob) {
        this.jobLauncher = jobLauncher;
        this.dailyEmailJob = dailyEmailJob;
    }

    @PostConstruct
    public void init() {
        log.info("MailerBatchLauncher initialized");
    }

    @Scheduled(cron = "*/30 * 1 * * *")
    public synchronized void run() throws Exception {

        log.info("Triggering dailyEmailJob via scheduler");

        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters();

        log.debug("JobParameters created: run.id={}", params.getLong("run.id"));

        jobLauncher.run(dailyEmailJob, params);

        log.info("dailyEmailJob launch request submitted");
    }
}
