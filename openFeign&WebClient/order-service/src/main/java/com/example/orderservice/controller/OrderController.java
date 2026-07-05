package com.example.orderservice.controller;

import com.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/feign/{bookId}")
    public String orderUsingFeign(@PathVariable Long bookId) {
        return orderService.placeOrderUsingFeign(bookId);
    }

    @GetMapping("/webclient/{bookId}")
    public String orderUsingWebClient(@PathVariable Long bookId) {
        return orderService.placeOrderUsingWebClient(bookId);
    }
}