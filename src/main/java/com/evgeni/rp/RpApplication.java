package com.evgeni.rp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RpApplication {

	public static void main(String[] args) {
		SpringApplication.run(RpApplication.class, args);
	}
}
