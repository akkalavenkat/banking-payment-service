package com.banking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;

    @JsonProperty("reference_number")
    private String referenceNumber;

    @JsonProperty("from_account_number")
    private String fromAccountNumber;

    @JsonProperty("to_account_number")
    private String toAccountNumber;

    private BigDecimal amount;

    private String status;

    @JsonProperty("payment_type")
    private String paymentType;

    private String description;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
