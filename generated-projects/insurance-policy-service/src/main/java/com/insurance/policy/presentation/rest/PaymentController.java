package com.insurance.policy.presentation.rest;

import com.insurance.policy.application.dto.ProcessPaymentInput;
import com.insurance.policy.application.dto.ProcessPaymentOutput;
import com.insurance.policy.application.usecase.ProcessPaymentUseCase;
import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.port.PaymentRepository;
import com.insurance.policy.domain.valueobject.PaymentStatus;
import com.insurance.policy.presentation.rest.model.PaymentResponse;
import com.insurance.policy.presentation.rest.model.ProcessPaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for payment operations
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment processing and management APIs")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentRepository paymentRepository;

    public PaymentController(
            ProcessPaymentUseCase processPaymentUseCase,
            PaymentRepository paymentRepository) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Process a new payment for an approved claim
     */
    @PostMapping
    @Operation(
        summary = "Process a new payment",
        description = "Process a payment for an approved insurance claim",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Payment processed successfully",
                content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid payment request"),
            @ApiResponse(responseCode = "404", description = "Claim not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate transaction ID or claim not approved")
        }
    )
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody ProcessPaymentRequest request) {

        ProcessPaymentInput input = new ProcessPaymentInput(
            request.claimId(),
            request.amount(),
            request.paymentMethod(),
            request.transactionId(),
            request.processedBy(),
            request.notes()
        );

        ProcessPaymentOutput output = processPaymentUseCase.execute(input);

        // Fetch full payment details
        Payment payment = paymentRepository.findById(output.paymentId())
            .orElseThrow(() -> new RuntimeException("Payment not found after creation"));

        PaymentResponse response = mapToResponse(payment);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get payment by ID",
        description = "Retrieve payment details by payment ID",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Payment found",
                content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Payment not found")
        }
    )
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID", required = true, example = "1")
            @PathVariable Long id) {

        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));

        return ResponseEntity.ok(mapToResponse(payment));
    }

    /**
     * Get all payments for a claim
     */
    @GetMapping("/claim/{claimId}")
    @Operation(
        summary = "Get payments for a claim",
        description = "Retrieve all payments associated with a specific claim",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Payments retrieved successfully",
                content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            )
        }
    )
    public ResponseEntity<List<PaymentResponse>> getPaymentsByClaimId(
            @Parameter(description = "Claim ID", required = true, example = "1")
            @PathVariable Long claimId) {

        List<Payment> payments = paymentRepository.findByClaimId(claimId);
        List<PaymentResponse> responses = payments.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get all payments with optional status filter
     */
    @GetMapping
    @Operation(
        summary = "Get all payments",
        description = "Retrieve all payments, optionally filtered by status",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Payments retrieved successfully",
                content = @Content(schema = @Schema(implementation = PaymentResponse.class))
            )
        }
    )
    public ResponseEntity<List<PaymentResponse>> getAllPayments(
            @Parameter(description = "Payment status filter (optional)", example = "COMPLETED")
            @RequestParam(required = false) String status) {

        List<Payment> payments;

        if (status != null && !status.isBlank()) {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            payments = paymentRepository.findByPaymentStatus(paymentStatus);
        } else {
            payments = paymentRepository.findAll();
        }

        List<PaymentResponse> responses = payments.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Map domain Payment to PaymentResponse
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getPaymentId(),
            payment.getClaimId(),
            payment.getAmount().getValue(),
            payment.getPaymentMethod().name(),
            payment.getPaymentStatus().name(),
            payment.getTransactionId().getValue(),
            payment.getPaymentDate(),
            payment.getProcessedBy(),
            payment.getNotes()
        );
    }
}
