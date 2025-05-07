package com.example.orderapp.model;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Order Entity
 * 
 * This class represents the core Order domain object in the system, which stores
 * information about customer orders. It is mapped to the "orders" table in the database
 * using JPA (Java Persistence API) annotations.
 * 
 * Each order contains:
 * - A unique identifier (auto-generated)
 * - Reference to the user who placed the order (Firebase UID)
 * - Product information
 * - Quantity and pricing details
 * - Timestamp information
 * 
 * This entity is used throughout the application for CRUD operations on order data.
 */
@Entity
@Table(name = "orders")
public class Order {
    /**
     * Unique identifier for the order
     * Auto-generated using an identity column in the database
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * The Firebase User ID of the customer who placed the order
     * This links the order to a specific authenticated user
     */
    private String userId;
    
    /**
     * The product identifier that was ordered
     * This could be enhanced to be a many-to-many relationship for multi-product orders
     */
    private String productId;
    
    /**
     * The number of units of the product ordered
     * Must be at least 1
     */
    private int quantity;
    
    /**
     * The total monetary value of the order
     * Typically calculated as price per unit × quantity
     */
    private double amount;
    
    /**
     * The timestamp when the order was placed
     * Stored as an Instant for precise time tracking with timezone information
     */
    private Instant orderTime;

    // Getters & Setters
    
    /**
     * Get the unique identifier of this order
     * @return The order ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the unique identifier of this order
     * @param id The order ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the user ID of the customer who placed this order
     * @return The Firebase user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user ID of the customer who placed this order
     * @param userId The Firebase user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the product ID that was ordered
     * @return The product identifier
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Set the product ID that was ordered
     * @param productId The product identifier
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Get the quantity of items ordered
     * @return The number of units ordered
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set the quantity of items ordered
     * @param quantity The number of units ordered
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Get the total monetary amount of the order
     * @return The order amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the total monetary amount of the order
     * @param amount The order amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get the timestamp when the order was placed
     * @return The order creation time
     */
    public Instant getOrderTime() {
        return orderTime;
    }

    /**
     * Set the timestamp when the order was placed
     * @param orderTime The order creation time
     */
    public void setOrderTime(Instant orderTime) {
        this.orderTime = orderTime;
    }
}