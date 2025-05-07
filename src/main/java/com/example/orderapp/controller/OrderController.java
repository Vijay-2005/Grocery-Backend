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

/**
 * Order Controller
 * 
 * This controller handles all HTTP requests related to order management in the system.
 * It exposes RESTful API endpoints that allow clients to:
 * - Place new orders
 * - View order history
 * - Perform system health checks
 * 
 * The controller uses Firebase authentication to identify users and ensure they
 * can only access their own orders.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    /**
     * Service that contains the business logic for order operations
     */
    private final OrderService service;
    
    /**
     * Repository for direct data access operations
     * Used primarily for diagnostic endpoints
     */
    private final OrderRepository repository;
    
    /**
     * Constructor for dependency injection of required services
     * 
     * @param service The order service for business logic operations
     * @param repository The order repository for data access
     */
    @Autowired
    public OrderController(OrderService service, OrderRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    /**
     * Endpoint to place a new order
     * 
     * This endpoint should be called after a successful payment has been processed.
     * The user ID is automatically extracted from the authentication context,
     * so the client doesn't need to provide it.
     * 
     * @param order The order details from the request body
     * @param auth The authentication object containing the user's identity
     * @return The saved order with generated ID and timestamp
     */
    @PostMapping
    public Order placeOrder(@RequestBody Order order,
                            Authentication auth) {
        String userId = auth.getName();  // Firebase UID
        return service.saveOrder(userId, order);
    }

    /**
     * Endpoint to retrieve order history for the authenticated user
     * 
     * This endpoint returns all orders placed by the current user, sorted by date.
     * The user ID is automatically extracted from the authentication context.
     * 
     * @param auth The authentication object containing the user's identity
     * @return List of orders placed by the user
     */
    @GetMapping("/history")
    public List<Order> history(Authentication auth) {
        return service.getHistory(auth.getName());
    }
    
    /**
     * System health check endpoint
     * 
     * This diagnostic endpoint verifies:
     * - API availability
     * - Database connectivity
     * - Order entity mapping
     * 
     * It's useful for monitoring systems and troubleshooting.
     * 
     * @return A map containing system health information
     */
    @GetMapping("/system-check")
    public ResponseEntity<Map<String, Object>> systemCheck() {
        Map<String, Object> response = new HashMap<>();
        // Current system status
        response.put("status", "UP");
        // Current timestamp for freshness verification
        response.put("timestamp", Instant.now().toString());
        
        // Get total count of orders in the database to verify DB connectivity
        long orderCount = repository.count();
        response.put("totalOrders", orderCount);
        
        // Create a test order object (will not be saved)
        // This validates that the Order entity is properly configured
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
    
    /**
     * Development/testing endpoint to create a test order
     * 
     * WARNING: This endpoint should be disabled in production as it creates
     * test data without authentication.
     * 
     * It's useful for:
     * - Validating database connectivity
     * - Testing order creation logic
     * - Populating test data
     * 
     * @return The newly created test order
     */
    @GetMapping("/create-test-order")
    public ResponseEntity<Order> createTestOrder() {
        // Create a test order with timestamp in user ID to ensure uniqueness
        Order testOrder = new Order();
        testOrder.setUserId("test-user-" + Instant.now().getEpochSecond());
        testOrder.setProductId("test-product");
        testOrder.setQuantity(1);
        testOrder.setAmount(99.99);
        testOrder.setOrderTime(Instant.now());
        
        // Save directly to the repository, bypassing service layer
        Order savedOrder = repository.save(testOrder);
        return ResponseEntity.ok(savedOrder);
    }
}