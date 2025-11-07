package com.insurance.policy.application.dto;

import java.time.LocalDate;

/**
 * Output DTO for processed payment
 */
public record ProcessPaymentOutput(
    Long paymentId,
    String paymentStatus,
    LocalDate paymentDate,
    String transactionId
) {}
