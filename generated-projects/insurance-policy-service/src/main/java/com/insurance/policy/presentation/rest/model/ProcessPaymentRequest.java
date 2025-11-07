package com.insurance.policy.presentation.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * REST request model for processing a payment
 */
@Schema(description = "Request to process a payment for an approved claim")
public record ProcessPaymentRequest(

    @NotNull(message = "Claim ID is required")
    @Schema(description = "ID of the claim being paid", example = "1", required = true)
    Long claimId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Digits(integer = 17, fraction = 2, message = "Amount must have at most 2 decimal places")
    @Schema(description = "Payment amount", example = "5000.00", required = true)
    BigDecimal amount,

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "BANK_TRANSFER|CHECK|CREDIT_CARD|CASH",
             message = "Payment method must be one of: BANK_TRANSFER, CHECK, CREDIT_CARD, CASH")
    @Schema(description = "Method of payment", example = "BANK_TRANSFER",
            allowableValues = {"BANK_TRANSFER", "CHECK", "CREDIT_CARD", "CASH"}, required = true)
    String paymentMethod,

    @NotBlank(message = "Transaction ID is required")
    @Size(min = 8, max = 32, message = "Transaction ID must be 8-32 characters")
    @Pattern(regexp = "^[A-Z0-9]{8,32}$",
             message = "Transaction ID must contain only uppercase letters and numbers")
    @Schema(description = "External transaction reference ID", example = "TXN1234567890", required = true)
    String transactionId,

    @NotBlank(message = "Processed by is required")
    @Size(max = 100, message = "Processed by must not exceed 100 characters")
    @Schema(description = "User or system processing the payment", example = "admin@insurance.com", required = true)
    String processedBy,

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Schema(description = "Optional notes about the payment", example = "Emergency payment processing", required = false)
    String notes
) {}
