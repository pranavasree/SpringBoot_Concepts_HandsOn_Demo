package com.example.resilience4jcb.controller;

import com.example.resilience4jcb.service.OrderService;
import com.example.resilience4jcb.service.PaymentClient;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final PaymentClient paymentClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public OrderController(
            OrderService orderService,
            PaymentClient paymentClient,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.orderService = orderService;
        this.paymentClient = paymentClient;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @GetMapping("/orders/{orderId}")
    public String placeOrder(@PathVariable String orderId) {
        return orderService.placeOrder(orderId);
    }

    @PostMapping("/payment/healthy/{value}")
    public String updatePaymentHealth(@PathVariable boolean value) {
        paymentClient.setHealthy(value);
        return "Payment service healthy = " + paymentClient.isHealthy();
    }

    @GetMapping("/payment/healthy")
    public String checkPaymentHealth() {
        return "Payment service healthy = " + paymentClient.isHealthy();
    }

    @GetMapping("/circuit/state")
    public String getCircuitState() {
        return circuitBreakerRegistry
                .circuitBreaker("paymentService")
                .getState()
                .name();
    }
}