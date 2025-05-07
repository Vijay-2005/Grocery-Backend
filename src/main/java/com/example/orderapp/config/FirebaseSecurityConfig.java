package com.example.orderapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Firebase Security Configuration
 * 
 * This class configures authentication using Firebase as the identity provider.
 * It handles JWT token verification, CORS configuration, and provides different
 * security behaviors for development vs. production environments.
 * 
 * The authentication flow works as follows:
 * 1. Client obtains a Firebase auth token and sends it in Authorization header
 * 2. This filter intercepts requests and verifies the token with Firebase
 * 3. If token is valid, request is allowed to proceed
 * 4. If token is invalid or missing, 401 Unauthorized response is returned
 */
@Configuration
@EnableWebSecurity
public class FirebaseSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseSecurityConfig.class);

    // Path to the Firebase service account credentials file from application.properties
    @Value("${firebase.service.account.file}")
    private String firebaseConfigPath;
    
    // Flag to determine if we're running in development mode (no auth required)
    @Value("${app.auth.development-mode:false}")
    private boolean developmentMode;
    
    // Internal state to track if Firebase was successfully initialized
    private boolean firebaseInitialized = false;
    
    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
     * This allows the frontend application(s) to make API calls to this backend.
     * 
     * @return A configured CORS source with allowed origins, methods and headers
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Define allowed origins (frontend URLs)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // Development environment origin
            "https://www.fresh-cart.live" // Production environment origin
        ));
        // Define allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Define allowed request headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Origin", "Accept", "X-Requested-With"));
        // Allow credentials (cookies/auth headers)
        configuration.setAllowCredentials(true);
        // Cache CORS configuration in browser for 1 hour (3600 seconds)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply config to all paths
        return source;
    }
    
    /**
     * Main Spring Security filter chain configuration.
     * This configures all security aspects of the application, including:
     * - Firebase authentication integration
     * - CORS support
     * - CSRF protection
     * - Public vs. protected endpoints
     * - Development mode bypassing of security checks
     * 
     * @param http The HttpSecurity configuration object
     * @return A configured SecurityFilterChain
     * @throws Exception if security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Initialize Firebase if not in development mode and not already initialized
        if (FirebaseApp.getApps().isEmpty() && !developmentMode) {
            try {
                // Load the Firebase service account from classpath resources
                InputStream serviceAccount = new ClassPathResource(firebaseConfigPath.replace("classpath:", "")).getInputStream();
                // Configure Firebase with the service account credentials
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                // Initialize the Firebase application
                FirebaseApp.initializeApp(options);
                firebaseInitialized = true;
                logger.info("Firebase initialized successfully");
            } catch (Exception e) {
                // Log warning but don't fail the application - will fall back to dev mode
                logger.warn("Failed to initialize Firebase - proceeding in development mode", e);
                firebaseInitialized = false;
            }
        } else if (developmentMode) {
            logger.info("Running in development mode - authentication disabled");
            firebaseInitialized = false;
        }

        // Basic security configuration
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(csrf -> csrf.disable()) // Disable CSRF since we're using token-based auth
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // No sessions
        
        // Configure authentication filter only if in production mode with Firebase initialized
        if (!developmentMode && firebaseInitialized) {
            http.addFilterBefore(new OncePerRequestFilter() {
                /**
                 * This filter intercepts all incoming requests and authenticates them
                 * using the Firebase JWT token provided in the Authorization header.
                 * 
                 * Authentication flow:
                 * 1. Extract JWT token from Authorization header
                 * 2. Verify token with Firebase Auth
                 * 3. If valid, set up Spring Security context with user info
                 * 4. If invalid, return 401 Unauthorized
                 * 
                 * Exceptions:
                 * - OPTIONS requests (CORS preflight) are allowed without authentication
                 * - Health check endpoint (/api/health) is publicly accessible
                 */
                @Override
                protected void doFilterInternal(HttpServletRequest req,
                                               HttpServletResponse res,
                                               FilterChain chain)
                        throws ServletException, IOException {
                    // Skip authentication for OPTIONS requests (CORS preflight)
                    if (req.getMethod().equals("OPTIONS")) {
                        chain.doFilter(req, res);
                        return;
                    }
                    
                    // Skip authentication for health endpoint (for monitoring systems)
                    String requestPath = req.getRequestURI();
                    if (requestPath.equals("/api/health")) {
                        chain.doFilter(req, res);
                        return;
                    }
                    
                    // Extract and validate the JWT token from Authorization header
                    String token = req.getHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        try {
                            // Verify the token with Firebase Auth service
                            var decoded = com.google.firebase.auth.FirebaseAuth
                                    .getInstance()
                                    .verifyIdToken(token.substring(7)); // Remove "Bearer " prefix
                            
                            // Create Spring Security authentication object with Firebase user ID
                            var auth = new UsernamePasswordAuthenticationToken(
                                    decoded.getUid(), null, null);
                            
                            // Set the authentication in Spring Security context
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            
                            // Continue to the next filter in the chain
                            chain.doFilter(req, res);
                        } catch (Exception e) {
                            // Log authentication failure
                            logger.error("Failed to verify Firebase token", e);
                            
                            // Return 401 Unauthorized response
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write("Invalid Authentication token");
                        }
                    } else {
                        // No token or invalid token format
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.getWriter().write("Missing or invalid Authorization header");
                    }
                }
            }, UsernamePasswordAuthenticationFilter.class); // Place this filter before username/password filter
            
            // Configure request authorization rules
            http.authorizeHttpRequests(authorize -> 
                authorize.requestMatchers("/api/auth/status", "/actuator/**", "/api/health").permitAll() // Public endpoints
                        .anyRequest().authenticated()); // All other endpoints require authentication
        } else {
            // In development mode, allow all requests without authentication
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            logger.warn("DEVELOPMENT MODE: All endpoints are accessible without authentication");
        }
                
        return http.build();
    }
}
