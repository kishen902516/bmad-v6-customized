package com.insurance.policy.application.service;

import com.insurance.policy.application.dto.ProcessPaymentInput;
import com.insurance.policy.application.dto.ProcessPaymentOutput;
import com.insurance.policy.application.exception.*;
import com.insurance.policy.domain.entity.Claim;
import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.port.ClaimRepository;
import com.insurance.policy.domain.port.PaymentRepository;
import com.insurance.policy.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProcessPaymentService
 */
@ExtendWith(MockitoExtension.class)
class ProcessPaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private ProcessPaymentService service;

    private Claim approvedClaim;
    private ProcessPaymentInput validInput;

    @BeforeEach
    void setUp() {
        // Create an approved claim
        approvedClaim = new Claim(
            new ClaimNumber("CLM-2025-000001"),
            new ClaimAmount(new BigDecimal("10000.00"), "USD"),
            LocalDate.now().minusDays(5),
            "POL-123"
        );

        // Move claim to APPROVED status
        approvedClaim.moveToUnderReview();
        approvedClaim.approve();

        // Set ID for the claim
        try {
            java.lang.reflect.Field idField = Claim.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(approvedClaim, 1L);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set claim ID", e);
        }

        // Create valid input
        validInput = new ProcessPaymentInput(
            1L,
            new BigDecimal("5000.00"),
            "BANK_TRANSFER",
            "TXN1234567890",
            "admin@test.com",
            "Test payment"
        );
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        // Given
        when(claimRepository.findById(1L)).thenReturn(Optional.of(approvedClaim));
        when(paymentRepository.existsByTransactionId("TXN1234567890")).thenReturn(false);

        Payment savedPayment = Payment.builder()
            .paymentId(1L)
            .claimId(1L)
            .amount(new PaymentAmount(new BigDecimal("5000.00")))
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .transactionId(new TransactionId("TXN1234567890"))
            .processedBy("admin@test.com")
            .notes("Test payment")
            .build();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // When
        ProcessPaymentOutput output = service.execute(validInput);

        // Then
        assertThat(output.paymentId()).isEqualTo(1L);
        assertThat(output.paymentStatus()).isEqualTo("PENDING");
        assertThat(output.transactionId()).isEqualTo("TXN1234567890");
        assertThat(output.paymentDate()).isEqualTo(LocalDate.now());

        verify(claimRepository).findById(1L);
        verify(paymentRepository).existsByTransactionId("TXN1234567890");
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void shouldThrowExceptionWhenClaimNotFound() {
        // Given
        when(claimRepository.findById(1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> service.execute(validInput))
            .isInstanceOf(InvalidPaymentException.class)
            .hasMessageContaining("Claim not found");

        verify(claimRepository).findById(1L);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void shouldThrowExceptionWhenClaimNotApproved() {
        // Given - Claim in SUBMITTED status (not APPROVED)
        Claim submittedClaim = new Claim(
            new ClaimNumber("CLM-2025-000001"),
            new ClaimAmount(new BigDecimal("10000.00"), "USD"),
            LocalDate.now().minusDays(1),
            "POL-123"
        );
        // Set ID
        try {
            java.lang.reflect.Field idField = Claim.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(submittedClaim, 1L);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set claim ID", e);
        }

        when(claimRepository.findById(1L)).thenReturn(Optional.of(submittedClaim));

        // When/Then
        assertThatThrownBy(() -> service.execute(validInput))
            .isInstanceOf(ClaimNotApprovedException.class)
            .hasMessageContaining("not in APPROVED status");

        verify(claimRepository).findById(1L);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void shouldThrowExceptionWhenTransactionIdExists() {
        // Given
        when(claimRepository.findById(1L)).thenReturn(Optional.of(approvedClaim));
        when(paymentRepository.existsByTransactionId("TXN1234567890")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> service.execute(validInput))
            .isInstanceOf(DuplicateTransactionIdException.class)
            .hasMessageContaining("Transaction ID already exists");

        verify(claimRepository).findById(1L);
        verify(paymentRepository).existsByTransactionId("TXN1234567890");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPaymentAmountExceedsClaimAmount() {
        // Given
        ProcessPaymentInput excessiveInput = new ProcessPaymentInput(
            1L,
            new BigDecimal("15000.00"), // Exceeds claim amount of 10000
            "BANK_TRANSFER",
            "TXN1234567890",
            "admin@test.com",
            "Test payment"
        );

        when(claimRepository.findById(1L)).thenReturn(Optional.of(approvedClaim));
        when(paymentRepository.existsByTransactionId("TXN1234567890")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> service.execute(excessiveInput))
            .isInstanceOf(InvalidPaymentException.class)
            .hasMessageContaining("cannot exceed claim amount");

        verify(claimRepository).findById(1L);
        verify(paymentRepository).existsByTransactionId("TXN1234567890");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionForInvalidPaymentMethod() {
        // Given
        ProcessPaymentInput invalidMethodInput = new ProcessPaymentInput(
            1L,
            new BigDecimal("5000.00"),
            "INVALID_METHOD",
            "TXN1234567890",
            "admin@test.com",
            "Test payment"
        );

        when(claimRepository.findById(1L)).thenReturn(Optional.of(approvedClaim));
        when(paymentRepository.existsByTransactionId("TXN1234567890")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> service.execute(invalidMethodInput))
            .isInstanceOf(InvalidPaymentException.class)
            .hasMessageContaining("Invalid payment method");

        verify(paymentRepository, never()).save(any());
    }
}
