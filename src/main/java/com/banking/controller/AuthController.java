package com.banking.controller;

import com.banking.security.JwtTokenProvider;
import com.banking.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller - JWT token generation
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Generate JWT token for user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestParam String userId,
            @RequestParam String email) {
        log.info("POST /api/v1/auth/login for user: {}", userId);
        
        String token = tokenProvider.generateToken(userId, email);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("email", email);
        data.put("token", token);
        data.put("type", "Bearer");
        
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.ok(data, "Login successful"));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate a JWT token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestParam String token) {
        log.info("POST /api/v1/auth/validate");
        
        boolean isValid = tokenProvider.validateToken(token);
        
        Map<String, Object> data = new HashMap<>();
        data.put("valid", isValid);
        
        if (isValid) {
            data.put("userId", tokenProvider.getUserIdFromToken(token));
            data.put("email", tokenProvider.getEmailFromToken(token));
        }
        
        return ResponseEntity.ok(ApiResponse.ok(data, "Token validation complete"));
    }
}
