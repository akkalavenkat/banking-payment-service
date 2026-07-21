package com.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Payment Request DTO for creating payments
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotBlank(message = "Source account number is required")
    @JsonProperty("from_account_number")
    private String fromAccountNumber;

    @NotBlank(message = "Destination account number is required")
    @JsonProperty("to_account_number")
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Amount exceeds maximum limit")
    private BigDecimal amount;

    @JsonProperty("payment_type")
    private String paymentType;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
