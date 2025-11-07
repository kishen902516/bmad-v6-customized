package com.insurance.policy.application.exception;

/**
 * Exception thrown when a transaction ID already exists
 */
public class DuplicateTransactionIdException extends RuntimeException {
    public DuplicateTransactionIdException(String transactionId) {
        super("Transaction ID already exists: " + transactionId);
    }
}
