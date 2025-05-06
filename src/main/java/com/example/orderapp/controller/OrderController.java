package com.example.orderapp.controller;

import com.example.orderapp.model.Order;
import com.example.orderapp.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) {
        this.service = service;
    }

    // Called by your payment gateway webhook or client after payment succeeds
    @PostMapping
    public Order placeOrder(@RequestBody Order order,
                            Authentication auth) {
        String userId = auth.getName();  // Firebase UID
        return service.saveOrder(userId, order);
    }

    // Get order history for logged‑in user
    @GetMapping("/history")
    public List<Order> history(Authentication auth) {
        return service.getHistory(auth.getName());
    }
} 