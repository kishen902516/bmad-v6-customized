package com.insurance.policy.presentation.rest.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * REST response model for payment details
 */
@Schema(description = "Payment details response")
public record PaymentResponse(

    @Schema(description = "Payment ID", example = "1")
    Long paymentId,

    @Schema(description = "Claim ID", example = "1")
    Long claimId,

    @Schema(description = "Payment amount", example = "5000.00")
    BigDecimal amount,

    @Schema(description = "Payment method", example = "BANK_TRANSFER")
    String paymentMethod,

    @Schema(description = "Payment status", example = "PENDING")
    String paymentStatus,

    @Schema(description = "Transaction ID", example = "TXN1234567890")
    String transactionId,

    @Schema(description = "Payment date", example = "2025-11-07")
    LocalDate paymentDate,

    @Schema(description = "User or system that processed the payment", example = "admin@insurance.com")
    String processedBy,

    @Schema(description = "Optional notes", example = "Emergency payment processing")
    String notes
) {}
