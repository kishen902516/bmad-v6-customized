package com.insurance.policy.application.usecase;

import com.insurance.policy.application.dto.ProcessPaymentInput;
import com.insurance.policy.application.dto.ProcessPaymentOutput;

/**
 * Use case interface for processing payments
 */
public interface ProcessPaymentUseCase {
    /**
     * Process a payment for an approved claim
     *
     * @param input Payment details
     * @return Payment confirmation
     */
    ProcessPaymentOutput execute(ProcessPaymentInput input);
}
