package com.insurance.policy.domain.valueobject;

/**
 * Payment method enumeration
 * Represents the different payment methods available for processing payments
 */
public enum PaymentMethod {
    BANK_TRANSFER("Bank Transfer"),
    CHECK("Check"),
    CREDIT_CARD("Credit Card"),
    CASH("Cash");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
