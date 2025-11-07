package com.insurance.policy.infrastructure.persistence.mapper;

import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.valueobject.PaymentAmount;
import com.insurance.policy.domain.valueobject.PaymentMethod;
import com.insurance.policy.domain.valueobject.PaymentStatus;
import com.insurance.policy.domain.valueobject.TransactionId;
import com.insurance.policy.infrastructure.persistence.entity.PaymentJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between Payment domain entity and PaymentJpaEntity
 */
@Component
public class PaymentMapper {

    /**
     * Convert domain entity to JPA entity
     */
    public PaymentJpaEntity toJpaEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentJpaEntity jpaEntity = new PaymentJpaEntity();
        jpaEntity.setPaymentId(payment.getPaymentId());
        jpaEntity.setClaimId(payment.getClaimId());
        jpaEntity.setAmount(payment.getAmount().getValue());
        jpaEntity.setPaymentMethod(payment.getPaymentMethod().name());
        jpaEntity.setPaymentStatus(payment.getPaymentStatus().name());
        jpaEntity.setTransactionId(payment.getTransactionId().getValue());
        jpaEntity.setPaymentDate(payment.getPaymentDate());
        jpaEntity.setProcessedBy(payment.getProcessedBy());
        jpaEntity.setNotes(payment.getNotes());

        return jpaEntity;
    }

    /**
     * Convert JPA entity to domain entity
     */
    public Payment toDomainEntity(PaymentJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Payment payment = Payment.builder()
            .paymentId(jpaEntity.getPaymentId())
            .claimId(jpaEntity.getClaimId())
            .amount(new PaymentAmount(jpaEntity.getAmount()))
            .paymentMethod(PaymentMethod.valueOf(jpaEntity.getPaymentMethod()))
            .paymentStatus(PaymentStatus.valueOf(jpaEntity.getPaymentStatus()))
            .transactionId(new TransactionId(jpaEntity.getTransactionId()))
            .paymentDate(jpaEntity.getPaymentDate())
            .processedBy(jpaEntity.getProcessedBy())
            .notes(jpaEntity.getNotes())
            .build();

        return payment;
    }
}
