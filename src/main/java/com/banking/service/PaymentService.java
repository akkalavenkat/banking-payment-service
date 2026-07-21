package com.banking.service;

import com.banking.dto.PaymentRequest;
import com.banking.dto.PaymentResponse;
import com.banking.entity.Account;
import com.banking.entity.Payment;
import com.banking.entity.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.PaymentRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Payment Service - Business logic for payment processing
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Process a payment between two accounts
     */
    @Transactional
    @CacheEvict(value = {"accounts", "userAccounts"}, allEntries = true)
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment from {} to {} for amount {}", 
            request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount());

        // Validate and fetch accounts
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
            .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Validate sufficient funds
        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Create payment record
        String referenceNumber = generateReferenceNumber();
        Payment payment = Payment.builder()
            .referenceNumber(referenceNumber)
            .fromAccount(fromAccount)
            .toAccount(toAccount)
            .amount(request.getAmount())
            .status(Payment.PaymentStatus.PROCESSING)
            .paymentType(Payment.PaymentType.valueOf(request.getPaymentType() != null ? 
                request.getPaymentType() : "TRANSFER"))
            .description(request.getDescription())
            .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
        
        accountRepository.saveAll(List.of(fromAccount, toAccount));

        // Create transaction records
        createTransaction(fromAccount, request.getAmount().negate(), 
            Transaction.TransactionType.TRANSFER_OUT, "Transfer to " + request.getToAccountNumber());
        createTransaction(toAccount, request.getAmount(), 
            Transaction.TransactionType.TRANSFER_IN, "Transfer from " + request.getFromAccountNumber());

        // Update payment status to completed
        savedPayment.setStatus(Payment.PaymentStatus.COMPLETED);
        paymentRepository.save(savedPayment);

        log.info("Payment processed successfully. Reference: {}", referenceNumber);
        return mapToResponse(savedPayment);
    }

    /**
     * Get payment by reference number
     */
    @Cacheable(value = "payments", key = "#referenceNumber")
    public PaymentResponse getPaymentByReference(String referenceNumber) {
        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + referenceNumber));
        return mapToResponse(payment);
    }

    /**
     * Get payment history for an account
     */
    public List<PaymentResponse> getAccountPayments(Long accountId) {
        List<Payment> payments = paymentRepository.findPaymentsByAccount(accountId);
        return payments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Get pending payments
     */
    @Cacheable(value = "pendingPayments")
    public List<PaymentResponse> getPendingPayments() {
        List<Payment> payments = paymentRepository.findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus.PENDING);
        return payments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private void createTransaction(Account account, BigDecimal amount, 
                                   Transaction.TransactionType type, String description) {
        Transaction transaction = Transaction.builder()
            .account(account)
            .amount(amount)
            .type(type)
            .description(description)
            .balanceAfter(account.getBalance())
            .build();
        transactionRepository.save(transaction);
    }

    private String generateReferenceNumber() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
            .id(payment.getId())
            .referenceNumber(payment.getReferenceNumber())
            .fromAccountNumber(payment.getFromAccount().getAccountNumber())
            .toAccountNumber(payment.getToAccount().getAccountNumber())
            .amount(payment.getAmount())
            .status(payment.getStatus().name())
            .paymentType(payment.getPaymentType().name())
            .description(payment.getDescription())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .build();
    }
}
