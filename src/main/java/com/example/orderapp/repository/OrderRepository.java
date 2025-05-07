package com.example.orderapp.repository;

import com.example.orderapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Order Repository Interface
 * 
 * This interface provides data access operations for the Order entity.
 * It extends Spring Data JPA's JpaRepository which automatically implements
 * basic CRUD operations and pagination support.
 * 
 * The repository follows the Repository pattern, providing a clean separation
 * between the domain model and data access logic.
 * 
 * Features provided by JpaRepository:
 * - save(): Create or update entity
 * - findById(): Retrieve entity by ID
 * - findAll(): Retrieve all entities
 * - count(): Count total entities
 * - delete(): Remove an entity
 * - exists(): Check if entity exists
 * 
 * @param <Order> The entity type this repository manages
 * @param <Long> The type of the entity's primary key
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find all orders belonging to a specific user
     * 
     * This method uses Spring Data JPA's method naming convention to
     * automatically generate the appropriate query. The method name
     * "findByUserId" is parsed to create a query that filters orders
     * where the userId field matches the provided parameter.
     * 
     * @param userId The Firebase user ID to search orders for
     * @return A list of orders placed by the specified user
     */
    List<Order> findByUserId(String userId);
}