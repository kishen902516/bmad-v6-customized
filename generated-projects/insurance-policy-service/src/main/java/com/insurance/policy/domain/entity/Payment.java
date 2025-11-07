package com.insurance.policy.domain.entity;

import com.insurance.policy.domain.valueobject.PaymentAmount;
import com.insurance.policy.domain.valueobject.PaymentMethod;
import com.insurance.policy.domain.valueobject.PaymentStatus;
import com.insurance.policy.domain.valueobject.TransactionId;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Payment domain entity
 * Represents a payment made on an approved insurance claim
 */
public class Payment {
    private Long paymentId;
    private Long claimId;
    private PaymentAmount amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private TransactionId transactionId;
    private LocalDate paymentDate;
    private String processedBy;
    private String notes;

    // Private constructor for builder
    private Payment() {}

    public static Builder builder() {
        return new Builder();
    }

    // Business methods
    public void markAsProcessing() {
        if (!paymentStatus.canTransitionTo(PaymentStatus.PROCESSING)) {
            throw new IllegalStateException(
                "Cannot transition from " + paymentStatus + " to PROCESSING"
            );
        }
        this.paymentStatus = PaymentStatus.PROCESSING;
    }

    public void markAsCompleted() {
        if (!paymentStatus.canTransitionTo(PaymentStatus.COMPLETED)) {
            throw new IllegalStateException(
                "Cannot transition from " + paymentStatus + " to COMPLETED"
            );
        }
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void markAsFailed() {
        if (!paymentStatus.canTransitionTo(PaymentStatus.FAILED)) {
            throw new IllegalStateException(
                "Cannot transition from " + paymentStatus + " to FAILED"
            );
        }
        this.paymentStatus = PaymentStatus.FAILED;
    }

    public void markAsRefunded() {
        if (!paymentStatus.canTransitionTo(PaymentStatus.REFUNDED)) {
            throw new IllegalStateException(
                "Cannot transition from " + paymentStatus + " to REFUNDED"
            );
        }
        this.paymentStatus = PaymentStatus.REFUNDED;
    }

    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING;
    }

    public boolean isCompleted() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }

    public boolean isTerminal() {
        return paymentStatus.isTerminal();
    }

    // Getters
    public Long getPaymentId() { return paymentId; }
    public Long getClaimId() { return claimId; }
    public PaymentAmount getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public TransactionId getTransactionId() { return transactionId; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getProcessedBy() { return processedBy; }
    public String getNotes() { return notes; }

    // Setters (package-private for repository use)
    void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", claimId=" + claimId +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                ", transactionId=" + transactionId +
                ", paymentDate=" + paymentDate +
                ", processedBy='" + processedBy + '\'' +
                '}';
    }

    public static class Builder {
        private final Payment payment = new Payment();

        public Builder paymentId(Long paymentId) {
            payment.paymentId = paymentId;
            return this;
        }

        public Builder claimId(Long claimId) {
            payment.claimId = claimId;
            return this;
        }

        public Builder amount(PaymentAmount amount) {
            payment.amount = amount;
            return this;
        }

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            payment.paymentMethod = paymentMethod;
            return this;
        }

        public Builder paymentStatus(PaymentStatus paymentStatus) {
            payment.paymentStatus = paymentStatus;
            return this;
        }

        public Builder transactionId(TransactionId transactionId) {
            payment.transactionId = transactionId;
            return this;
        }

        public Builder paymentDate(LocalDate paymentDate) {
            payment.paymentDate = paymentDate;
            return this;
        }

        public Builder processedBy(String processedBy) {
            payment.processedBy = processedBy;
            return this;
        }

        public Builder notes(String notes) {
            payment.notes = notes;
            return this;
        }

        public Payment build() {
            // Validate required fields
            Objects.requireNonNull(payment.claimId, "Claim ID is required");
            Objects.requireNonNull(payment.amount, "Amount is required");
            Objects.requireNonNull(payment.paymentMethod, "Payment method is required");
            Objects.requireNonNull(payment.transactionId, "Transaction ID is required");
            Objects.requireNonNull(payment.processedBy, "Processed by is required");

            // Set defaults
            if (payment.paymentStatus == null) {
                payment.paymentStatus = PaymentStatus.PENDING;
            }
            if (payment.paymentDate == null) {
                payment.paymentDate = LocalDate.now();
            }

            return payment;
        }
    }
}
