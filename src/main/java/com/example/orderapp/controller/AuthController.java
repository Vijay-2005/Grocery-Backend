package com.example.orderapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * 
 * This controller handles authentication-related endpoints and information.
 * It provides a public endpoint for clients to check authentication requirements
 * and server status without requiring authentication.
 * 
 * Key responsibilities:
 * - Reporting authentication status and requirements
 * - Providing a public accessible endpoint for health checking
 * - Informing clients about authentication configuration
 * 
 * This controller works in conjunction with the FirebaseSecurityConfig
 * which manages the actual authentication process.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Authentication Status Endpoint
     * 
     * This endpoint is publicly accessible (does not require authentication)
     * and provides information about:
     * - Server availability
     * - Authentication requirements
     * - System message
     * 
     * Client applications can use this endpoint to:
     * - Verify if the backend is online before attempting login
     * - Check if authentication is required
     * - Display appropriate messages to users
     * 
     * @return A map containing authentication status information
     */
    @GetMapping("/status")
    public Map<String, Object> getAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        
        // Indicate that the server is operational
        response.put("status", "Server is running");
        
        // Inform client that authentication is required for protected endpoints
        response.put("authRequired", true);
        
        // Provide a human-readable message about authentication requirements
        response.put("message", "Authentication required for all endpoints except this one");
        
        return response;
    }
}