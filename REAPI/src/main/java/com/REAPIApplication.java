package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Real Estate API (REAPI) Spring Boot application.
 *
 * <p>Bootstraps the application context, enables asynchronous method execution
 * via {@code @EnableAsync}, and activates Spring's scheduled-task infrastructure
 * via {@code @EnableScheduling}.</p>
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class REAPIApplication {

	/**
	 * Main method that launches the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application at startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(REAPIApplication.class, args);
	}
}
