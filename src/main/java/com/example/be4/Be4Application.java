package com.example.be4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Be4Application {

	public static void main(String[] args) {
		SpringApplication.run(Be4Application.class, args);
	}

}
