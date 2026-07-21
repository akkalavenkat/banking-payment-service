package com.banking.controller;

import com.banking.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    @GetMapping
    @Operation(summary = "Health check", description = "Check application health status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        log.info("GET /api/v1/health");
        
        HealthComponent health = healthEndpoint.health();
        
        Map<String, Object> data = new HashMap<>();
        data.put("status", health.getStatus().toString());
        data.put("components", health.getComponents());
        
        return ResponseEntity.ok(ApiResponse.ok(data, "Application is healthy"));
    }
}
