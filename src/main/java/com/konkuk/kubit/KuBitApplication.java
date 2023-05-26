package com.konkuk.kubit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class KuBitApplication {
	public static void main(String[] args) {
		SpringApplication.run(KuBitApplication.class, args);
	}
}
