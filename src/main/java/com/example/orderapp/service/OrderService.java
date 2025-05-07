package com.example.orderapp.service;

import com.example.orderapp.model.Order;
import com.example.orderapp.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

/**
 * Order Service
 * 
 * This service class encapsulates the business logic for order-related operations.
 * It acts as an intermediary between the controllers and the repository layer,
 * providing a clean separation of concerns.
 * 
 * Responsibilities:
 * - Implementing business rules around order creation
 * - Validating and processing order data
 * - Managing order history retrieval
 * - Providing comprehensive logging for order operations
 * 
 * The service follows the principle of single responsibility and hides
 * the data access details from the controllers.
 */
@Service
public class OrderService {
    /**
     * Logger for capturing service-level events and errors
     */
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    /**
     * Repository for data access operations
     */
    private final OrderRepository repo;
    
    /**
     * Constructor for dependency injection
     * 
     * @param repo The order repository to be used for data operations
     */
    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    /**
     * Process and save a new order
     * 
     * This method:
     * 1. Sets the authenticated user ID on the order
     * 2. Sets the current timestamp as the order time
     * 3. Persists the order to the database
     * 4. Logs the operation outcome
     * 
     * Any exceptions during the process are logged and propagated up to the controller.
     * 
     * @param userId The authenticated user's ID (from Firebase auth)
     * @param order The order details to be saved
     * @return The saved order with generated ID and timestamp
     * @throws RuntimeException if database operation fails
     */
    public Order saveOrder(String userId, Order order) {
        try {
            logger.info("Attempting to save order for user: {}", userId);
            
            // Set the user ID from the authentication context
            order.setUserId(userId);
            
            // Set the current timestamp as the order time
            order.setOrderTime(Instant.now());
            
            // Persist the order to the database
            Order savedOrder = repo.save(order);
            
            // Log success message with the generated order ID
            logger.info("Order saved successfully with ID: {}", savedOrder.getId());
            
            return savedOrder;
        } catch (Exception e) {
            // Log detailed error information but avoid exposing sensitive data
            logger.error("Failed to save order for user: {}", userId, e);
            
            // Re-throw the exception to allow the controller's exception handler to manage it
            throw e;
        }
    }

    /**
     * Retrieve order history for a specific user
     * 
     * This method fetches all orders placed by a particular user,
     * which allows them to view their purchase history.
     * 
     * @param userId The user ID to retrieve orders for
     * @return List of orders placed by the user
     * @throws RuntimeException if database operation fails
     */
    public List<Order> getHistory(String userId) {
        try {
            logger.info("Fetching order history for user: {}", userId);
            
            // Fetch all orders for the specified user
            List<Order> orders = repo.findByUserId(userId);
            
            // Log the result with count for monitoring and debugging
            logger.info("Found {} orders for user: {}", orders.size(), userId);
            
            return orders;
        } catch (Exception e) {
            // Log detailed error information
            logger.error("Failed to fetch order history for user: {}", userId, e);
            
            // Re-throw the exception to allow the controller's exception handler to manage it
            throw e;
        }
    }
}