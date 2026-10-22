package com.priv.birdie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BirdieApplication {

	public static void main(String[] args) {
		System.setProperty("log.name","birdie-service");
        SpringApplication.run(BirdieApplication.class, args);
	}

}
