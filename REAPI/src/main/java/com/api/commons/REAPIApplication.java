package com.api.commons;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class REAPIApplication {
	public static void main(String[] args) {
		SpringApplication.run(REAPIApplication.class, args);
	}
}