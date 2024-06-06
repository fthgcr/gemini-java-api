package com.isch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Isch {

	public static void main(String[] args) {
		SpringApplication.run(Isch.class, args);
	}

}
