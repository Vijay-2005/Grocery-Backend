package com.example.orderapp.service;

import com.example.orderapp.model.Order;
import com.example.orderapp.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository repo;
    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    public Order saveOrder(String userId, Order order) {
        order.setUserId(userId);
        order.setOrderTime(Instant.now());
        return repo.save(order);
    }

    public List<Order> getHistory(String userId) {
        return repo.findByUserId(userId);
    }
} 