package com.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class MailerBatchLauncher {

	private final JobLauncher jobLauncher;
	private final Job dailyEmailJob;
	
	@PostConstruct
	public void init() {
	    System.out.println(">>> MailerBatchLauncher initialized!");
	}


	public MailerBatchLauncher(JobLauncher jobLauncher, Job dailyEmailJob) {
		this.jobLauncher = jobLauncher;
		this.dailyEmailJob = dailyEmailJob;
	}

	@Scheduled(cron = "*/30 * * * * *") 
	public synchronized  void run() throws Exception {
		JobParameters params = new JobParametersBuilder().addLong("run.id", System.currentTimeMillis())
				.toJobParameters();

		jobLauncher.run(dailyEmailJob, params);
	}
}
