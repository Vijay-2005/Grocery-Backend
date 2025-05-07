package com.example.orderapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for the Order Management System
 * 
 * This is the entry point for the Spring Boot application that serves as a backend
 * for the grocery ordering system. It initializes the Spring application context,
 * configures all beans, and starts the embedded web server.
 * 
 * The @SpringBootApplication annotation combines:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Tells Spring Boot to auto-configure the application
 * - @ComponentScan: Tells Spring to scan for components in the current package and subpackages
 * 
 * Key application features:
 * - RESTful API for order management
 * - Firebase authentication integration
 * - Spring Security configuration
 * - Exception handling
 */
@SpringBootApplication
public class OrderAppApplication {
    /**
     * Main method that serves as the entry point for the application
     * 
     * This method starts the Spring Boot application by calling SpringApplication.run()
     * which performs the following:
     * 1. Creates an appropriate ApplicationContext instance
     * 2. Registers a CommandLinePropertySource to expose command line arguments
     * 3. Refreshes the application context, loading all singleton beans
     * 4. Triggers any CommandLineRunner beans
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderAppApplication.class, args);
    }
}