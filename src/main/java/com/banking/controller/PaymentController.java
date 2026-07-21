package com.banking.controller;

import com.banking.dto.PaymentRequest;
import com.banking.dto.PaymentResponse;
import com.banking.service.PaymentService;
import com.banking.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Payment Controller - REST endpoints for payment operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process payment", description = "Process a payment between two accounts")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest paymentRequest) {
        log.info("POST /api/v1/payments - Processing payment from {} to {}",
            paymentRequest.getFromAccountNumber(), paymentRequest.getToAccountNumber());
        
        try {
            PaymentResponse payment = paymentService.processPayment(paymentRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(payment, "Payment processed successfully"));
        } catch (RuntimeException e) {
            log.error("Payment processing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Payment processing failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{referenceNumber}")
    @Operation(summary = "Get payment details", description = "Retrieve payment details by reference number")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String referenceNumber) {
        log.info("GET /api/v1/payments/{}", referenceNumber);
        PaymentResponse payment = paymentService.getPaymentByReference(referenceNumber);
        return ResponseEntity.ok(ApiResponse.ok(payment, "Payment retrieved successfully"));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get account payments", description = "Retrieve payment history for an account")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAccountPayments(@PathVariable Long accountId) {
        log.info("GET /api/v1/payments/account/{}", accountId);
        List<PaymentResponse> payments = paymentService.getAccountPayments(accountId);
        return ResponseEntity.ok(ApiResponse.ok(payments, "Payments retrieved successfully"));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending payments", description = "Retrieve all pending payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPendingPayments() {
        log.info("GET /api/v1/payments/pending");
        List<PaymentResponse> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(ApiResponse.ok(payments, "Pending payments retrieved successfully"));
    }
}
