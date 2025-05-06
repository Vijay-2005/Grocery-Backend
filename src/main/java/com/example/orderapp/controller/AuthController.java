package com.example.orderapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/status")
    public Map<String, Object> getAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Server is running");
        response.put("authRequired", true);
        response.put("message", "Authentication required for all endpoints except this one");
        return response;
    }
} 