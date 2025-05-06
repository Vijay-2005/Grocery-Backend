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

@Configuration
@EnableWebSecurity
public class FirebaseSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseSecurityConfig.class);

    @Value("${firebase.service.account.file}")
    private String firebaseConfigPath;
    
    @Value("${app.auth.development-mode:false}")
    private boolean developmentMode;
    
    private boolean firebaseInitialized = false;
    
    // Add CORS configuration bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000", 
            "https://www.fresh-cart.live"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Origin", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Initialize Firebase
        if (FirebaseApp.getApps().isEmpty() && !developmentMode) {
            try {
                InputStream serviceAccount = new ClassPathResource(firebaseConfigPath.replace("classpath:", "")).getInputStream();
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                firebaseInitialized = true;
                logger.info("Firebase initialized successfully");
            } catch (Exception e) {
                logger.warn("Failed to initialize Firebase - proceeding in development mode", e);
                firebaseInitialized = false;
                // Don't throw exception - continue without Firebase authentication
            }
        } else if (developmentMode) {
            logger.info("Running in development mode - authentication disabled");
            firebaseInitialized = false;
        }

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        // Only add authentication filter if not in development mode
        if (!developmentMode && firebaseInitialized) {
            http.addFilterBefore(new OncePerRequestFilter() {
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
                    
                    // Skip authentication for health endpoint
                    String requestPath = req.getRequestURI();
                    if (requestPath.equals("/api/health")) {
                        chain.doFilter(req, res);
                        return;
                    }
                    
                    String token = req.getHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        try {
                            var decoded = com.google.firebase.auth.FirebaseAuth
                                    .getInstance()
                                    .verifyIdToken(token.substring(7));
                            var auth = new UsernamePasswordAuthenticationToken(
                                    decoded.getUid(), null, null);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            chain.doFilter(req, res);
                        } catch (Exception e) {
                            logger.error("Failed to verify Firebase token", e);
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write("Invalid Authentication token");
                        }
                    } else {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.getWriter().write("Missing or invalid Authorization header");
                    }
                }
            }, UsernamePasswordAuthenticationFilter.class);
            
            http.authorizeHttpRequests(authorize -> 
                authorize.requestMatchers("/api/auth/status", "/actuator/**", "/api/health").permitAll()
                        .anyRequest().authenticated());
        } else {
            // In development mode, allow all requests
            http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
            logger.warn("DEVELOPMENT MODE: All endpoints are accessible without authentication");
        }
                
        return http.build();
    }
}
