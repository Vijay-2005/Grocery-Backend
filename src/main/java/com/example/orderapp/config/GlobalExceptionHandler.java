package com.example.orderapp.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * 
 * This class provides centralized exception handling across all controllers
 * in the application. It converts exceptions into appropriate HTTP responses
 * with structured error messages to provide clear feedback to API clients.
 * 
 * Benefits:
 * - Consistent error response format
 * - Proper HTTP status codes based on exception type
 * - Hides implementation details from clients
 * - Centralizes error handling logic
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all RuntimeExceptions with special handling for Firebase initialization errors.
     * 
     * This method:
     * 1. Detects if the exception is related to Firebase initialization
     * 2. Returns a service unavailable (503) response for Firebase issues
     * 3. Returns a generic internal server error (500) for other runtime issues
     * 
     * Each error response contains:
     * - HTTP status code
     * - Error type description
     * - User-friendly error message
     * 
     * @param ex The RuntimeException that was thrown
     * @param request The current web request
     * @return A ResponseEntity with appropriate status code and error details
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleFirebaseInitException(RuntimeException ex, WebRequest request) {
        // Check if this is a Firebase initialization error by message content
        if (ex.getMessage() != null && ex.getMessage().contains("Firebase initialization failed")) {
            // Create structured error response for Firebase service issues
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            body.put("error", "Firebase Authentication Service Unavailable");
            body.put("message", "The authentication service is currently unavailable. Please try again later or contact support.");
            
            // Return 503 Service Unavailable status
            return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
        }
        
        // For other runtime exceptions, return a generic error
        // This prevents exposing sensitive implementation details to clients
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        
        // Return 500 Internal Server Error status
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Additional exception handlers can be added below to handle specific 
     * exception types with custom error responses and status codes.
     * 
     * For example:
     * - ResourceNotFoundException -> 404 Not Found
     * - ValidationException -> 400 Bad Request
     * - AuthenticationException -> 401 Unauthorized
     * - ForbiddenException -> 403 Forbidden
     */
}