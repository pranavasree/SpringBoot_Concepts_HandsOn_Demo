package com.example.resilience4jcb.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PaymentClient {

    private final AtomicBoolean healthy = new AtomicBoolean(false);

    public String chargePayment(String orderId) {
        System.out.println("Calling payment service for orderId: " + orderId);

        if (!healthy.get()) {
            throw new RuntimeException("Payment service is DOWN");
        }

        return "Payment successful for orderId: " + orderId;
    }

    public void setHealthy(boolean value) {
        healthy.set(value);
    }

    public boolean isHealthy() {
        return healthy.get();
    }
}