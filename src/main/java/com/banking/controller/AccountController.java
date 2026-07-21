package com.banking.controller;

import com.banking.dto.AccountDTO;
import com.banking.entity.Account;
import com.banking.service.AccountService;
import com.banking.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Account Controller - REST endpoints for account operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by account number", description = "Retrieve account details by account number")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccount(@PathVariable String accountNumber) {
        log.info("GET /api/v1/accounts/{}", accountNumber);
        AccountDTO account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(ApiResponse.ok(account, "Account retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get user accounts", description = "Retrieve all accounts for the authenticated user")
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getUserAccounts() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("GET /api/v1/accounts for user: {}", userId);
        List<AccountDTO> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(ApiResponse.ok(accounts, "Accounts retrieved successfully"));
    }

    @PostMapping
    @Operation(summary = "Create new account", description = "Create a new bank account")
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(
            @RequestParam String accountNumber,
            @RequestParam String accountHolder,
            @RequestParam(defaultValue = "CHECKING") String accountType) {
        
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("POST /api/v1/accounts - Creating account for user: {}", userId);
        
        AccountDTO account = accountService.createAccount(
            userId, 
            accountNumber, 
            accountHolder, 
            Account.AccountType.valueOf(accountType)
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(account, "Account created successfully"));
    }

    @PutMapping("/{accountNumber}/deactivate")
    @Operation(summary = "Deactivate account", description = "Deactivate a bank account")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(@PathVariable String accountNumber) {
        log.info("PUT /api/v1/accounts/{}/deactivate", accountNumber);
        accountService.deactivateAccount(accountNumber);
        return ResponseEntity.ok(ApiResponse.ok(null, "Account deactivated successfully"));
    }
}
