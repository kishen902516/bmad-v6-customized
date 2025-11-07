package com.insurance.policy.infrastructure.persistence;

import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.valueobject.PaymentAmount;
import com.insurance.policy.domain.valueobject.PaymentMethod;
import com.insurance.policy.domain.valueobject.PaymentStatus;
import com.insurance.policy.domain.valueobject.TransactionId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for PaymentRepositoryAdapter
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({PaymentRepositoryAdapter.class, com.insurance.policy.infrastructure.persistence.mapper.PaymentMapper.class})
class PaymentRepositoryAdapterIntegrationTest {

    @Autowired
    private PaymentRepositoryAdapter paymentRepository;

    @Test
    void shouldSaveAndFindPaymentById() {
        // Given
        Payment payment = createTestPayment("TXN1111111111");

        // When
        Payment savedPayment = paymentRepository.save(payment);

        // Then
        assertThat(savedPayment.getPaymentId()).isNotNull();

        Optional<Payment> found = paymentRepository.findById(savedPayment.getPaymentId());
        assertThat(found).isPresent();
        assertThat(found.get().getTransactionId().getValue()).isEqualTo("TXN1111111111");
    }

    @Test
    void shouldFindPaymentsByClaimId() {
        // Given
        Payment payment1 = createTestPayment("TXN2222222222");
        payment1 = paymentRepository.save(payment1);

        Payment payment2 = createTestPayment("TXN3333333333");
        payment2 = paymentRepository.save(payment2);

        // When
        List<Payment> payments = paymentRepository.findByClaimId(1L);

        // Then
        assertThat(payments).hasSize(2);
        assertThat(payments).extracting(p -> p.getTransactionId().getValue())
            .contains("TXN2222222222", "TXN3333333333");
    }

    @Test
    void shouldFindPaymentsByStatus() {
        // Given
        Payment pendingPayment = createTestPayment("TXN4444444444");
        paymentRepository.save(pendingPayment);

        // When
        List<Payment> pendingPayments = paymentRepository.findByPaymentStatus(PaymentStatus.PENDING);

        // Then
        assertThat(pendingPayments).isNotEmpty();
        assertThat(pendingPayments).allMatch(p -> p.getPaymentStatus() == PaymentStatus.PENDING);
    }

    @Test
    void shouldFindPaymentByTransactionId() {
        // Given
        Payment payment = createTestPayment("TXN5555555555");
        paymentRepository.save(payment);

        // When
        Optional<Payment> found = paymentRepository.findByTransactionId("TXN5555555555");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTransactionId().getValue()).isEqualTo("TXN5555555555");
    }

    @Test
    void shouldCheckIfTransactionIdExists() {
        // Given
        Payment payment = createTestPayment("TXN6666666666");
        paymentRepository.save(payment);

        // When
        boolean exists = paymentRepository.existsByTransactionId("TXN6666666666");
        boolean notExists = paymentRepository.existsByTransactionId("TXN9999999999");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldFindCompletedPaymentsAfterDate() {
        // Given
        Payment payment = createTestPayment("TXN7777777777");
        Payment savedPayment = paymentRepository.save(payment);

        // Manually mark as completed by updating status
        savedPayment.markAsProcessing();
        savedPayment.markAsCompleted();
        paymentRepository.save(savedPayment);

        // When
        List<Payment> completedPayments = paymentRepository.findCompletedPaymentsAfter(
            LocalDate.now().minusDays(1)
        );

        // Then
        assertThat(completedPayments).isNotEmpty();
        assertThat(completedPayments).allMatch(p -> p.getPaymentStatus() == PaymentStatus.COMPLETED);
    }

    @Test
    void shouldDeletePaymentById() {
        // Given
        Payment payment = createTestPayment("TXN8888888888");
        Payment savedPayment = paymentRepository.save(payment);
        Long paymentId = savedPayment.getPaymentId();

        // When
        paymentRepository.deleteById(paymentId);

        // Then
        Optional<Payment> found = paymentRepository.findById(paymentId);
        assertThat(found).isEmpty();
    }

    private Payment createTestPayment(String transactionId) {
        return Payment.builder()
            .claimId(1L)
            .amount(new PaymentAmount(new BigDecimal("5000.00")))
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .transactionId(new TransactionId(transactionId))
            .processedBy("admin@test.com")
            .notes("Test payment")
            .build();
    }
}
