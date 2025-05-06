package com.example.orderapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class FirebaseSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseSecurityConfig.class);

    @Value("${firebase.service.account.file}")
    private String firebaseConfigPath;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Initialize Firebase
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                InputStream serviceAccount = new ClassPathResource(firebaseConfigPath.replace("classpath:", "")).getInputStream();
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                logger.info("Firebase initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize Firebase - authentication will not work", e);
                throw new RuntimeException("Firebase initialization failed - check your firebase-service-account.json file", e);
            }
        }

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new OncePerRequestFilter() {
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
                            }
                        } else {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write("Missing or invalid Authorization header");
                        }
                    }
                }, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> 
                    authorize.requestMatchers("/api/auth/status").permitAll()
                            .anyRequest().authenticated());
                
        return http.build();
    }
}
