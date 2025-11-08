package com.example.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ChallengeManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeManagementApplication.class, args);
	}

}
