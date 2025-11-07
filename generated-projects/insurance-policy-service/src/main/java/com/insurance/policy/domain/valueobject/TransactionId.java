package com.insurance.policy.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a transaction ID
 * Validates the format of external transaction references
 */
public class TransactionId {
    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9]{8,32}$");
    private final String value;

    public TransactionId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or blank");
        }
        String normalized = value.trim().toUpperCase();
        if (!VALID_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                "Transaction ID must be 8-32 alphanumeric characters, got: " + value
            );
        }
        this.value = normalized;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionId that = (TransactionId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
