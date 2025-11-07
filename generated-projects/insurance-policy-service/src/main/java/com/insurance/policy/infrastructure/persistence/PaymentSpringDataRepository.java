package com.insurance.policy.infrastructure.persistence;

import com.insurance.policy.infrastructure.persistence.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Payment
 */
@Repository
public interface PaymentSpringDataRepository extends JpaRepository<PaymentJpaEntity, Long> {

    /**
     * Find all payments for a specific claim
     */
    List<PaymentJpaEntity> findByClaimId(Long claimId);

    /**
     * Find all payments with a specific status
     */
    List<PaymentJpaEntity> findByPaymentStatus(String paymentStatus);

    /**
     * Find a payment by transaction ID
     */
    Optional<PaymentJpaEntity> findByTransactionId(String transactionId);

    /**
     * Find all completed payments after a specific date
     */
    @Query("SELECT p FROM PaymentJpaEntity p WHERE p.paymentStatus = 'COMPLETED' AND p.paymentDate > :date")
    List<PaymentJpaEntity> findCompletedPaymentsAfter(@Param("date") LocalDate date);

    /**
     * Check if a transaction ID exists
     */
    boolean existsByTransactionId(String transactionId);
}
