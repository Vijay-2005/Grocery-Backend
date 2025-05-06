package com.example.orderapp.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleFirebaseInitException(RuntimeException ex, WebRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains("Firebase initialization failed")) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
            body.put("error", "Firebase Authentication Service Unavailable");
            body.put("message", "The authentication service is currently unavailable. Please try again later or contact support.");
            
            return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
        }
        
        // For other runtime exceptions, return a generic error
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred");
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 