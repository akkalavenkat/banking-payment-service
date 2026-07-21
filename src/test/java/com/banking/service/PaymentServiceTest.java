package com.banking.service;

import com.banking.dto.PaymentRequest;
import com.banking.dto.PaymentResponse;
import com.banking.entity.Account;
import com.banking.entity.Payment;
import com.banking.repository.AccountRepository;
import com.banking.repository.PaymentRepository;
import com.banking.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Account fromAccount;
    private Account toAccount;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        fromAccount = Account.builder()
            .id(1L)
            .accountNumber("ACC001")
            .accountHolder("John Doe")
            .userId("user123")
            .balance(new BigDecimal("5000.00"))
            .status(Account.AccountStatus.ACTIVE)
            .accountType(Account.AccountType.CHECKING)
            .build();

        toAccount = Account.builder()
            .id(2L)
            .accountNumber("ACC002")
            .accountHolder("Jane Smith")
            .userId("user456")
            .balance(new BigDecimal("10000.00"))
            .status(Account.AccountStatus.ACTIVE)
            .accountType(Account.AccountType.SAVINGS)
            .build();

        paymentRequest = PaymentRequest.builder()
            .fromAccountNumber("ACC001")
            .toAccountNumber("ACC002")
            .amount(new BigDecimal("500.00"))
            .paymentType("TRANSFER")
            .description("Test payment")
            .build();
    }

    @Test
    void testProcessPayment_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber("ACC001"))
            .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002"))
            .thenReturn(Optional.of(toAccount));
        when(paymentRepository.save(any(Payment.class)))
            .thenReturn(Payment.builder()
                .referenceNumber("PAY-12345")
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(new BigDecimal("500.00"))
                .status(Payment.PaymentStatus.COMPLETED)
                .build()
            );

        // Act
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Assert
        assertNotNull(result);
        assertEquals("PAY-12345", result.getReferenceNumber());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void testProcessPayment_InsufficientFunds() {
        // Arrange
        fromAccount.setBalance(new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("ACC001"))
            .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC002"))
            .thenReturn(Optional.of(toAccount));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentService.processPayment(paymentRequest));
    }

    @Test
    void testGetPaymentByReference_Success() {
        // Arrange
        Payment payment = Payment.builder()
            .referenceNumber("PAY-12345")
            .fromAccount(fromAccount)
            .toAccount(toAccount)
            .amount(new BigDecimal("500.00"))
            .status(Payment.PaymentStatus.COMPLETED)
            .build();

        when(paymentRepository.findByReferenceNumber("PAY-12345"))
            .thenReturn(Optional.of(payment));

        // Act
        PaymentResponse result = paymentService.getPaymentByReference("PAY-12345");

        // Assert
        assertNotNull(result);
        assertEquals("PAY-12345", result.getReferenceNumber());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
    }

    @Test
    void testGetPaymentByReference_NotFound() {
        // Arrange
        when(paymentRepository.findByReferenceNumber("INVALID"))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentService.getPaymentByReference("INVALID"));
    }
}
