package com.example.resilience4jcb.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public String placeOrder(String orderId) {
        String paymentResponse = paymentClient.chargePayment(orderId);

        return "Order placed successfully. " + paymentResponse;
    }

    public String paymentFallback(String orderId, Exception exception) {
        return "Fallback response: Payment service is temporarily unavailable. "
                + "Order is saved as PENDING for orderId: " + orderId
                + ". Reason: " + exception.getMessage();
    }
}