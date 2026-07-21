package com.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;

    @JsonProperty("account_number")
    private String accountNumber;

    private BigDecimal amount;

    private String type;

    private String description;

    @JsonProperty("balance_after")
    private BigDecimal balanceAfter;

    @JsonProperty("transaction_date")
    private LocalDateTime transactionDate;
}
