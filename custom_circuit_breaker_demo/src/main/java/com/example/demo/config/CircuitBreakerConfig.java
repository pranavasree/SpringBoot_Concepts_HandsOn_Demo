package com.example.demo.config;

import com.example.demo.circuitbreaker.SimpleCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class CircuitBreakerConfig {

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService circuitBreakerScheduler() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean
    public SimpleCircuitBreaker paymentCircuitBreaker(ScheduledExecutorService scheduler) {
        return new SimpleCircuitBreaker(
                scheduler,
                3,
                1,
                Duration.ofSeconds(10)
        );
    }
}