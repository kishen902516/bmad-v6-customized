package com.insurance.policy.presentation.rest;

import com.insurance.policy.application.dto.ProcessPaymentInput;
import com.insurance.policy.application.dto.ProcessPaymentOutput;
import com.insurance.policy.application.exception.ClaimNotApprovedException;
import com.insurance.policy.application.exception.DuplicateTransactionIdException;
import com.insurance.policy.application.exception.InvalidPaymentException;
import com.insurance.policy.application.usecase.ProcessPaymentUseCase;
import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.port.PaymentRepository;
import com.insurance.policy.domain.valueobject.PaymentAmount;
import com.insurance.policy.domain.valueobject.PaymentMethod;
import com.insurance.policy.domain.valueobject.PaymentStatus;
import com.insurance.policy.domain.valueobject.TransactionId;
import com.insurance.policy.presentation.rest.model.ProcessPaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for PaymentController
 */
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessPaymentUseCase processPaymentUseCase;

    @MockBean
    private PaymentRepository paymentRepository;

    @Test
    void shouldProcessPaymentSuccessfully() throws Exception {
        // Given
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            1L,
            new BigDecimal("5000.00"),
            "BANK_TRANSFER",
            "TXN1234567890",
            "admin@test.com",
            "Test payment"
        );

        ProcessPaymentOutput output = new ProcessPaymentOutput(
            1L,
            "PENDING",
            LocalDate.now(),
            "TXN1234567890"
        );

        Payment payment = createTestPayment(1L);

        when(processPaymentUseCase.execute(any(ProcessPaymentInput.class))).thenReturn(output);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When/Then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.paymentId").value(1))
            .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
            .andExpect(jsonPath("$.transactionId").value("TXN1234567890"));

        verify(processPaymentUseCase).execute(any(ProcessPaymentInput.class));
        verify(paymentRepository).findById(1L);
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() throws Exception {
        // Given
        ProcessPaymentRequest invalidRequest = new ProcessPaymentRequest(
            null, // Invalid - claimId is required
            new BigDecimal("5000.00"),
            "BANK_TRANSFER",
            "TXN1234567890",
            "admin@test.com",
            null
        );

        // When/Then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(processPaymentUseCase);
    }

    @Test
    void shouldReturnConflictWhenClaimNotApproved() throws Exception {
        // Given
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            1L,
            new BigDecimal("5000.00"),
            "BANK_TRANSFER",
            "TXN1234567890",
            "admin@test.com",
            "Test payment"
        );

        when(processPaymentUseCase.execute(any(ProcessPaymentInput.class)))
            .thenThrow(new ClaimNotApprovedException(1L));

        // When/Then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnConflictWhenDuplicateTransactionId() throws Exception {
        // Given
        ProcessPaymentRequest request = new ProcessPaymentRequest(
            1L,
            new BigDecimal("5000.00"),
            "BANK_TRANSFER",
            "TXN1234567890",
            "admin@test.com",
            "Test payment"
        );

        when(processPaymentUseCase.execute(any(ProcessPaymentInput.class)))
            .thenThrow(new DuplicateTransactionIdException("TXN1234567890"));

        // When/Then
        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldGetPaymentById() throws Exception {
        // Given
        Payment payment = createTestPayment(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // When/Then
        mockMvc.perform(get("/api/v1/payments/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentId").value(1))
            .andExpect(jsonPath("$.claimId").value(1))
            .andExpect(jsonPath("$.amount").value(5000.00))
            .andExpect(jsonPath("$.paymentMethod").value("BANK_TRANSFER"))
            .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
            .andExpect(jsonPath("$.transactionId").value("TXN1234567890"));

        verify(paymentRepository).findById(1L);
    }

    @Test
    void shouldGetPaymentsByClaimId() throws Exception {
        // Given
        List<Payment> payments = Arrays.asList(
            createTestPayment(1L),
            createTestPayment(2L)
        );
        when(paymentRepository.findByClaimId(1L)).thenReturn(payments);

        // When/Then
        mockMvc.perform(get("/api/v1/payments/claim/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

        verify(paymentRepository).findByClaimId(1L);
    }

    @Test
    void shouldGetAllPayments() throws Exception {
        // Given
        List<Payment> payments = Arrays.asList(
            createTestPayment(1L),
            createTestPayment(2L)
        );
        when(paymentRepository.findAll()).thenReturn(payments);

        // When/Then
        mockMvc.perform(get("/api/v1/payments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

        verify(paymentRepository).findAll();
    }

    @Test
    void shouldGetPaymentsByStatus() throws Exception {
        // Given
        List<Payment> payments = Arrays.asList(createTestPayment(1L));
        when(paymentRepository.findByPaymentStatus(PaymentStatus.PENDING)).thenReturn(payments);

        // When/Then
        mockMvc.perform(get("/api/v1/payments?status=PENDING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));

        verify(paymentRepository).findByPaymentStatus(PaymentStatus.PENDING);
    }

    private Payment createTestPayment(Long paymentId) {
        Payment payment = Payment.builder()
            .claimId(1L)
            .amount(new PaymentAmount(new BigDecimal("5000.00")))
            .paymentMethod(PaymentMethod.BANK_TRANSFER)
            .transactionId(new TransactionId("TXN1234567890"))
            .processedBy("admin@test.com")
            .notes("Test payment")
            .build();

        // Use reflection or setter to set the ID (since it's package-private)
        try {
            java.lang.reflect.Field field = Payment.class.getDeclaredField("paymentId");
            field.setAccessible(true);
            field.set(payment, paymentId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set payment ID", e);
        }

        return payment;
    }
}
