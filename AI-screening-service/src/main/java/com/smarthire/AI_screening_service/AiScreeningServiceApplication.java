package com.smarthire.AI_screening_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class AiScreeningServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiScreeningServiceApplication.class, args);
	}

}
