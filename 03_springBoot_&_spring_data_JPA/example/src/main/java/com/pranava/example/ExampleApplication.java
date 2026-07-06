package com.pranava.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleApplication {

	public static void main(String[] args) {

		var ctx = SpringApplication.run(ExampleApplication.class, args);

		MyFirstService myFirstService = ctx.getBean("myFirstService",MyFirstService.class);

		System.out.println(myFirstService.tellAStory());

		System.out.println(myFirstService.getMyCustomPropFromAnotherFile());

	}



}
