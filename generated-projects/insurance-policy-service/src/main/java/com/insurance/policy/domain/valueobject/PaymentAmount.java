package com.insurance.policy.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object representing a payment amount
 * Ensures that payment amounts are always positive and properly validated
 */
public class PaymentAmount {
    private final BigDecimal value;

    public PaymentAmount(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Payment amount cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive, got: " + value);
        }
        // Round to 2 decimal places for currency
        this.value = value.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getValue() {
        return value;
    }

    public boolean isGreaterThan(PaymentAmount other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isLessThanOrEqual(PaymentAmount other) {
        return this.value.compareTo(other.value) <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentAmount that = (PaymentAmount) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
