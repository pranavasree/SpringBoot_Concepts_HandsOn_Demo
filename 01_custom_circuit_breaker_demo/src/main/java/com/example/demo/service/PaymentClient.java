package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PaymentClient {

    private final AtomicBoolean healthy = new AtomicBoolean(false);

    public String charge(String orderId) {
        if (!healthy.get()) {
            throw new RuntimeException("Payment service is DOWN");
        }

        return "Payment successful for order " + orderId;
    }

    public void setHealthy(boolean value) {
        healthy.set(value);
    }

    public boolean isHealthy() {
        return healthy.get();
    }
}