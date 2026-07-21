package com.banking;

import com.banking.entity.Account;
import com.banking.repository.AccountRepository;
import com.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Data Initialization - Populate sample data on startup
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        if (accountRepository.count() == 0) {
            log.info("Initializing sample data...");

            // Create sample accounts
            Account account1 = Account.builder()
                .accountNumber("ACC001")
                .accountHolder("John Doe")
                .userId("user001")
                .balance(new BigDecimal("5000.00"))
                .status(Account.AccountStatus.ACTIVE)
                .accountType(Account.AccountType.CHECKING)
                .build();

            Account account2 = Account.builder()
                .accountNumber("ACC002")
                .accountHolder("Jane Smith")
                .userId("user002")
                .balance(new BigDecimal("10000.00"))
                .status(Account.AccountStatus.ACTIVE)
                .accountType(Account.AccountType.SAVINGS)
                .build();

            accountRepository.save(account1);
            accountRepository.save(account2);

            log.info("Sample data initialized successfully");
        }
    }
}
