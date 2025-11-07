package com.insurance.policy.application.dto;

import java.math.BigDecimal;

/**
 * Input DTO for processing a payment
 */
public record ProcessPaymentInput(
    Long claimId,
    BigDecimal amount,
    String paymentMethod,
    String transactionId,
    String processedBy,
    String notes
) {
    public ProcessPaymentInput {
        if (claimId == null) {
            throw new IllegalArgumentException("Claim ID cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Payment method cannot be null or blank");
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }
        if (processedBy == null || processedBy.isBlank()) {
            throw new IllegalArgumentException("Processed by cannot be null or blank");
        }
    }
}
