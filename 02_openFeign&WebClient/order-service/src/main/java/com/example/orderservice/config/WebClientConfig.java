package com.example.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient bookWebClient(
            @Value("${book-service.url}") String bookServiceUrl
    ) {
        return WebClient.builder()
                .baseUrl(bookServiceUrl)
                .build();
    }
}