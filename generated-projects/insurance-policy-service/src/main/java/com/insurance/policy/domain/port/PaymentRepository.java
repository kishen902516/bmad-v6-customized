package com.insurance.policy.domain.port;

import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.valueobject.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Payment repository port (interface)
 * Defines the contract for payment persistence operations
 */
public interface PaymentRepository {

    /**
     * Save a new payment or update an existing one
     */
    Payment save(Payment payment);

    /**
     * Find a payment by its ID
     */
    Optional<Payment> findById(Long id);

    /**
     * Find all payments for a specific claim
     */
    List<Payment> findByClaimId(Long claimId);

    /**
     * Find all payments with a specific status
     */
    List<Payment> findByPaymentStatus(PaymentStatus status);

    /**
     * Find a payment by its transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find all completed payments after a specific date
     */
    List<Payment> findCompletedPaymentsAfter(LocalDate date);

    /**
     * Find all payments
     */
    List<Payment> findAll();

    /**
     * Delete a payment by ID
     */
    void deleteById(Long id);

    /**
     * Check if a transaction ID already exists
     */
    boolean existsByTransactionId(String transactionId);
}
