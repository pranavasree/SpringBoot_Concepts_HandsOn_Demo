package com.example.demo.controller;

import com.example.demo.service.OrderService;
import com.example.demo.service.PaymentClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final PaymentClient paymentClient;

    public OrderController(OrderService orderService, PaymentClient paymentClient) {
        this.orderService = orderService;
        this.paymentClient = paymentClient;
    }

    @GetMapping("/orders/{orderId}")
    public String placeOrder(@PathVariable String orderId) {
        return orderService.placeOrder(orderId);
    }

    @GetMapping("/circuit/state")
    public String getCircuitState() {
        return orderService.getCircuitState();
    }

    @PostMapping("/payment/healthy/{value}")
    public String changePaymentHealth(@PathVariable boolean value) {
        paymentClient.setHealthy(value);
        return "Payment service healthy = " + paymentClient.isHealthy();
    }
}