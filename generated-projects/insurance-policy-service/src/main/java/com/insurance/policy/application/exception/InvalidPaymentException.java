package com.insurance.policy.application.exception;

/**
 * Exception thrown when a payment is invalid
 */
public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String message) {
        super(message);
    }

    public InvalidPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
