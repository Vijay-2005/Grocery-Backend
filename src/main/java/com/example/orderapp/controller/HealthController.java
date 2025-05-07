package com.example.orderapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Controller
 * 
 * This controller provides endpoints for monitoring the health and status
 * of the application. These endpoints are typically used by:
 * - Load balancers to determine if the service is available
 * - Monitoring tools to track uptime and performance
 * - DevOps for automated health checking
 * - Client applications to verify API availability
 * 
 * The health endpoints are publicly accessible (not requiring authentication)
 * as configured in the FirebaseSecurityConfig.
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    
    /**
     * Basic health check endpoint
     * 
     * This endpoint provides a simple status check that confirms:
     * - The application is running
     * - The web server is properly handling requests
     * - The API layer is functioning correctly
     * 
     * No database checks are performed here to keep the response fast
     * and to avoid unnecessary database load.
     * 
     * @return A ResponseEntity containing health status information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        // Create a response map with health information
        Map<String, Object> response = new HashMap<>();
        
        // Indicate that the service is available
        response.put("status", "UP");
        
        // Include current timestamp for monitoring tools
        response.put("timestamp", LocalDateTime.now().toString());
        
        // Identify this service by name
        response.put("service", "Fresh Cart API");
        
        // Add a human-readable status message
        response.put("message", "Service is running properly");
        
        // Return HTTP 200 OK with the health information
        return ResponseEntity.ok(response);
    }
}