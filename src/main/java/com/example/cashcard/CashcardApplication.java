package com.example.cashcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class CashcardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CashcardApplication.class, args);

		System.out.println("hola");
	}

}
