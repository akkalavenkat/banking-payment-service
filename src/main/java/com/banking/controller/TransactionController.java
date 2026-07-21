package com.banking.controller;

import com.banking.dto.TransactionDTO;
import com.banking.service.TransactionService;
import com.banking.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction Controller - REST endpoints for transaction history
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction history endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get account transactions", description = "Retrieve transaction history for an account")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getAccountTransactions(@PathVariable Long accountId) {
        log.info("GET /api/v1/transactions/account/{}", accountId);
        List<TransactionDTO> transactions = transactionService.getAccountTransactions(accountId);
        return ResponseEntity.ok(ApiResponse.ok(transactions, "Transactions retrieved successfully"));
    }

    @GetMapping("/account/{accountId}/range")
    @Operation(summary = "Get account transactions by date range", 
               description = "Retrieve transaction history for an account within a date range")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        log.info("GET /api/v1/transactions/account/{}/range from {} to {}", accountId, startDate, endDate);
        List<TransactionDTO> transactions = transactionService.getAccountTransactionsByDateRange(accountId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.ok(transactions, "Transactions retrieved successfully"));
    }
}
