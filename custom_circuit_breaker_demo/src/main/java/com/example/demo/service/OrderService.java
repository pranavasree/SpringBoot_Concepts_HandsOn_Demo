package com.example.demo.service;

import com.example.demo.circuitbreaker.CircuitOpenException;
import com.example.demo.circuitbreaker.SimpleCircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final PaymentClient paymentClient;
    private final SimpleCircuitBreaker paymentCircuitBreaker;

    public OrderService(
            PaymentClient paymentClient,
            SimpleCircuitBreaker paymentCircuitBreaker
    ) {
        this.paymentClient = paymentClient;
        this.paymentCircuitBreaker = paymentCircuitBreaker;
    }

    public String placeOrder(String orderId) {
        try {
            String paymentResult = paymentCircuitBreaker.execute(
                    () -> paymentClient.charge(orderId)
            );

            return "Order placed. " + paymentResult;

        } catch (CircuitOpenException ex) {
            return "Fallback: Payment system is temporarily unavailable. Please try again later.";

        } catch (RuntimeException ex) {
            return "Payment failed: " + ex.getMessage();
        }
    }

    public String getCircuitState() {
        return paymentCircuitBreaker.getState().name();
    }
}