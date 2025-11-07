package com.insurance.policy.application.service;

import com.insurance.policy.application.dto.ProcessPaymentInput;
import com.insurance.policy.application.dto.ProcessPaymentOutput;
import com.insurance.policy.application.exception.*;
import com.insurance.policy.application.usecase.ProcessPaymentUseCase;
import com.insurance.policy.domain.entity.Claim;
import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.port.ClaimRepository;
import com.insurance.policy.domain.port.PaymentRepository;
import com.insurance.policy.domain.valueobject.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service implementation for processing payments
 * Implements business rules and orchestrates payment creation
 */
@Service
public class ProcessPaymentService implements ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final ClaimRepository claimRepository;

    public ProcessPaymentService(
            PaymentRepository paymentRepository,
            ClaimRepository claimRepository) {
        this.paymentRepository = paymentRepository;
        this.claimRepository = claimRepository;
    }

    @Override
    @Transactional
    public ProcessPaymentOutput execute(ProcessPaymentInput input) {
        // 1. Verify claim exists
        Claim claim = claimRepository.findById(input.claimId())
            .orElseThrow(() -> new InvalidPaymentException(
                "Claim not found with ID: " + input.claimId()
            ));

        // 2. Verify claim is APPROVED
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new ClaimNotApprovedException(input.claimId());
        }

        // 3. Verify transaction ID is unique
        if (paymentRepository.existsByTransactionId(input.transactionId())) {
            throw new DuplicateTransactionIdException(input.transactionId());
        }

        // 4. Verify payment amount <= claim amount
        PaymentAmount paymentAmount = new PaymentAmount(input.amount());
        ClaimAmount claimAmount = claim.getClaimedAmount();

        if (paymentAmount.getValue().compareTo(claimAmount.amount()) > 0) {
            throw new InvalidPaymentException(
                "Payment amount (" + paymentAmount.getValue() +
                ") cannot exceed claim amount (" + claimAmount.amount() + ")"
            );
        }

        // 5. Parse payment method
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(input.paymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentException(
                "Invalid payment method: " + input.paymentMethod()
            );
        }

        // 6. Create payment entity
        Payment payment = Payment.builder()
            .claimId(input.claimId())
            .amount(paymentAmount)
            .paymentMethod(paymentMethod)
            .transactionId(new TransactionId(input.transactionId()))
            .processedBy(input.processedBy())
            .notes(input.notes())
            .build(); // Sets status to PENDING and date to now

        // 7. Save payment
        Payment savedPayment = paymentRepository.save(payment);

        // 8. Return output
        return new ProcessPaymentOutput(
            savedPayment.getPaymentId(),
            savedPayment.getPaymentStatus().name(),
            savedPayment.getPaymentDate(),
            savedPayment.getTransactionId().getValue()
        );
    }
}
