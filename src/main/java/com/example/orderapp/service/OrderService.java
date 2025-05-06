package com.example.orderapp.service;

import com.example.orderapp.model.Order;
import com.example.orderapp.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository repo;
    
    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    public Order saveOrder(String userId, Order order) {
        try {
            logger.info("Attempting to save order for user: {}", userId);
            order.setUserId(userId);
            order.setOrderTime(Instant.now());
            Order savedOrder = repo.save(order);
            logger.info("Order saved successfully with ID: {}", savedOrder.getId());
            return savedOrder;
        } catch (Exception e) {
            logger.error("Failed to save order for user: {}", userId, e);
            throw e; // Re-throw to allow controller to handle it
        }
    }

    public List<Order> getHistory(String userId) {
        try {
            logger.info("Fetching order history for user: {}", userId);
            List<Order> orders = repo.findByUserId(userId);
            logger.info("Found {} orders for user: {}", orders.size(), userId);
            return orders;
        } catch (Exception e) {
            logger.error("Failed to fetch order history for user: {}", userId, e);
            throw e; // Re-throw to allow controller to handle it
        }
    }
}