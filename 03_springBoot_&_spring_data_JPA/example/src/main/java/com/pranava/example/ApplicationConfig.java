package com.pranava.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfig {

    @Bean("bean1")
    public MyFirstClass myFirstClass(){
        return new MyFirstClass("First Bean");
    }

    @Bean("bean2")
    public MyFirstClass mySecondClass(){
        return new MyFirstClass("First Bean");
    }

    @Bean("bean3")
    public MyFirstClass myThirdClass(){
        return new MyFirstClass("First Bean");
    }

//    @Bean
//    public MyFirstService myFirstService(){
//        return  new MyFirstService();
//    }
}
