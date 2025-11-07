package com.insurance.policy.domain.entity;

import com.insurance.policy.domain.valueobject.PaymentAmount;
import com.insurance.policy.domain.valueobject.PaymentMethod;
import com.insurance.policy.domain.valueobject.PaymentStatus;
import com.insurance.policy.domain.valueobject.TransactionId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Payment domain entity
 */
class PaymentTest {

    @Test
    void shouldCreatePaymentWithBuilder() {
        // Given
        PaymentAmount amount = new PaymentAmount(new BigDecimal("5000.00"));
        TransactionId txnId = new TransactionId("TXN1234567890");

        // When
        Payment payment = Payment.builder()
            .claimId(1L)
            .amount(amount)
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .transactionId(txnId)
            .processedBy("admin@test.com")
            .notes("Test payment")
            .build();

        // Then
        assertThat(payment.getClaimId()).isEqualTo(1L);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.BANK_TRANSFER);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getTransactionId()).isEqualTo(txnId);
        assertThat(payment.getPaymentDate()).isEqualTo(LocalDate.now());
        assertThat(payment.getProcessedBy()).isEqualTo("admin@test.com");
        assertThat(payment.getNotes()).isEqualTo("Test payment");
    }

    @Test
    void shouldThrowExceptionWhenRequiredFieldsAreMissing() {
        assertThatThrownBy(() -> Payment.builder().build())
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldTransitionFromPendingToProcessing() {
        // Given
        Payment payment = createTestPayment();

        // When
        payment.markAsProcessing();

        // Then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PROCESSING);
    }

    @Test
    void shouldTransitionFromProcessingToCompleted() {
        // Given
        Payment payment = createTestPayment();
        payment.markAsProcessing();

        // When
        payment.markAsCompleted();

        // Then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.isCompleted()).isTrue();
        assertThat(payment.isTerminal()).isTrue();
    }

    @Test
    void shouldTransitionFromProcessingToFailed() {
        // Given
        Payment payment = createTestPayment();
        payment.markAsProcessing();

        // When
        payment.markAsFailed();

        // Then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.isTerminal()).isTrue();
    }

    @Test
    void shouldTransitionFromCompletedToRefunded() {
        // Given
        Payment payment = createTestPayment();
        payment.markAsProcessing();
        payment.markAsCompleted();

        // When
        payment.markAsRefunded();

        // Then
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(payment.isTerminal()).isTrue();
    }

    @Test
    void shouldNotAllowInvalidStateTransition() {
        // Given
        Payment payment = createTestPayment();
        payment.markAsProcessing();
        payment.markAsCompleted();

        // When/Then - cannot go from COMPLETED to FAILED
        assertThatThrownBy(() -> payment.markAsFailed())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition from COMPLETED to FAILED");
    }

    @Test
    void shouldCheckIfPaymentIsPending() {
        // Given
        Payment payment = createTestPayment();

        // Then
        assertThat(payment.isPending()).isTrue();
        assertThat(payment.isCompleted()).isFalse();
    }

    private Payment createTestPayment() {
        return Payment.builder()
            .claimId(1L)
            .amount(new PaymentAmount(new BigDecimal("5000.00")))
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .transactionId(new TransactionId("TXN1234567890"))
            .processedBy("admin@test.com")
            .build();
    }
}
