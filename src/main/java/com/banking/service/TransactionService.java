package com.banking.service;

import com.banking.dto.TransactionDTO;
import com.banking.entity.Transaction;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transaction Service - Business logic for transaction history
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;

    /**
     * Get transaction history for an account
     */
    public List<TransactionDTO> getAccountTransactions(Long accountId) {
        log.info("Fetching transactions for account ID: {}", accountId);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Get transaction history for a date range
     */
    public List<TransactionDTO> getAccountTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching transactions for account ID: {} from {} to {}", accountId, startDate, endDate);
        List<Transaction> transactions = transactionRepository.findByAccountAndDateRange(accountId, startDate, endDate);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Get all transactions of a specific type
     */
    public List<TransactionDTO> getTransactionsByType(Transaction.TransactionType type) {
        List<Transaction> transactions = transactionRepository.findByTypeOrderByTransactionDateDesc(type);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
            .id(transaction.getId())
            .accountNumber(transaction.getAccount().getAccountNumber())
            .amount(transaction.getAmount())
            .type(transaction.getType().name())
            .description(transaction.getDescription())
            .balanceAfter(transaction.getBalanceAfter())
            .transactionDate(transaction.getTransactionDate())
            .build();
    }
}
