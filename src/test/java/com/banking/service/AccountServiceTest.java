package com.banking.service;

import com.banking.dto.AccountDTO;
import com.banking.entity.Account;
import com.banking.repository.AccountRepository;
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
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
            .id(1L)
            .accountNumber("ACC001")
            .accountHolder("John Doe")
            .userId("user123")
            .balance(new BigDecimal("5000.00"))
            .status(Account.AccountStatus.ACTIVE)
            .accountType(Account.AccountType.CHECKING)
            .build();
    }

    @Test
    void testGetAccountByNumber_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber("ACC001"))
            .thenReturn(Optional.of(testAccount));

        // Act
        AccountDTO result = accountService.getAccountByNumber("ACC001");

        // Assert
        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        assertEquals("John Doe", result.getAccountHolder());
        assertEquals(new BigDecimal("5000.00"), result.getBalance());
    }

    @Test
    void testGetAccountByNumber_NotFound() {
        // Arrange
        when(accountRepository.findByAccountNumber("INVALID"))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> accountService.getAccountByNumber("INVALID"));
    }

    @Test
    void testCreateAccount_Success() {
        // Arrange
        when(accountRepository.save(any(Account.class)))
            .thenReturn(testAccount);

        // Act
        AccountDTO result = accountService.createAccount(
            "user123", "ACC001", "John Doe", Account.AccountType.CHECKING
        );

        // Assert
        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testUpdateBalance_Success() {
        // Arrange
        BigDecimal newBalance = new BigDecimal("7500.00");
        when(accountRepository.findByAccountNumber("ACC001"))
            .thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class)))
            .thenReturn(testAccount);

        // Act
        accountService.updateBalance("ACC001", newBalance);

        // Assert
        verify(accountRepository, times(1)).findByAccountNumber("ACC001");
        verify(accountRepository, times(1)).save(testAccount);
    }

    @Test
    void testDeactivateAccount_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber("ACC001"))
            .thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class)))
            .thenReturn(testAccount);

        // Act
        accountService.deactivateAccount("ACC001");

        // Assert
        verify(accountRepository, times(1)).findByAccountNumber("ACC001");
        verify(accountRepository, times(1)).save(testAccount);
    }
}
