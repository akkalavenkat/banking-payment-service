package com.banking.service;

import com.banking.dto.AccountDTO;
import com.banking.entity.Account;
import com.banking.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Account Service - Business logic for account operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Get account by account number with caching
     */
    @Cacheable(value = "accounts", key = "#accountNumber")
    public AccountDTO getAccountByNumber(String accountNumber) {
        log.info("Fetching account: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return mapToDTO(account);
    }

    /**
     * Get account by ID
     */
    @Cacheable(value = "accounts", key = "#id")
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found: " + id));
        return mapToDTO(account);
    }

    /**
     * Get all active accounts for a user
     */
    @Cacheable(value = "userAccounts", key = "#userId")
    public List<AccountDTO> getUserAccounts(String userId) {
        log.info("Fetching accounts for user: {}", userId);
        List<Account> accounts = accountRepository.findActiveAccountsByUserId(userId, Account.AccountStatus.ACTIVE);
        return accounts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Create a new account
     */
    @Transactional
    @CacheEvict(value = "userAccounts", key = "#userId")
    public AccountDTO createAccount(String userId, String accountNumber, String accountHolder,
                                    Account.AccountType accountType) {
        log.info("Creating account for user: {}", userId);

        Account account = Account.builder()
            .userId(userId)
            .accountNumber(accountNumber)
            .accountHolder(accountHolder)
            .accountType(accountType)
            .status(Account.AccountStatus.ACTIVE)
            .balance(BigDecimal.ZERO)
            .build();

        Account saved = accountRepository.save(account);
        return mapToDTO(saved);
    }

    /**
     * Update account balance
     */
    @Transactional
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public void updateBalance(String accountNumber, BigDecimal newBalance) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        account.setBalance(newBalance);
        accountRepository.save(account);
        log.info("Updated balance for account: {} to {}", accountNumber, newBalance);
    }

    /**
     * Deactivate account
     */
    @Transactional
    @CacheEvict(value = "accounts", key = "#accountNumber")
    public void deactivateAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        account.setStatus(Account.AccountStatus.INACTIVE);
        accountRepository.save(account);
        log.info("Deactivated account: {}", accountNumber);
    }

    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
            .id(account.getId())
            .accountNumber(account.getAccountNumber())
            .accountHolder(account.getAccountHolder())
            .balance(account.getBalance())
            .status(account.getStatus().name())
            .accountType(account.getAccountType().name())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }
}
