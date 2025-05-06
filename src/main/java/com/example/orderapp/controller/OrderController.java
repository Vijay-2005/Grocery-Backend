package com.example.orderapp.controller;

import com.example.orderapp.model.Order;
import com.example.orderapp.service.OrderService;
import com.example.orderapp.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    private final OrderRepository repository;
    
    @Autowired
    public OrderController(OrderService service, OrderRepository repository) {
        this.service = service;
        this.repository = repository;
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
    
    // Simple endpoint to check database connectivity and order count
    @GetMapping("/system-check")
    public ResponseEntity<Map<String, Object>> systemCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        
        // Get total count of orders in the database
        long orderCount = repository.count();
        response.put("totalOrders", orderCount);
        
        // Create a test order (will not be saved)
        Order testOrder = new Order();
        testOrder.setUserId("test-user");
        testOrder.setProductId("test-product");
        testOrder.setQuantity(1);
        testOrder.setAmount(99.99);
        testOrder.setOrderTime(Instant.now());
        
        response.put("testOrder", testOrder);
        response.put("databaseConnectionActive", true);
        
        return ResponseEntity.ok(response);
    }
    
    // Endpoint to manually save a test order - use for troubleshooting only
    @GetMapping("/create-test-order")
    public ResponseEntity<Order> createTestOrder() {
        Order testOrder = new Order();
        testOrder.setUserId("test-user-" + Instant.now().getEpochSecond());
        testOrder.setProductId("test-product");
        testOrder.setQuantity(1);
        testOrder.setAmount(99.99);
        testOrder.setOrderTime(Instant.now());
        
        Order savedOrder = repository.save(testOrder);
        return ResponseEntity.ok(savedOrder);
    }
}