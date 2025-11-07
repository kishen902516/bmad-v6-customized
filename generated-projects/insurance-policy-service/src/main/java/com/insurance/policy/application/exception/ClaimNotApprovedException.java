package com.insurance.policy.application.exception;

/**
 * Exception thrown when attempting to process payment on a claim that is not approved
 */
public class ClaimNotApprovedException extends RuntimeException {
    public ClaimNotApprovedException(String message) {
        super(message);
    }

    public ClaimNotApprovedException(Long claimId) {
        super("Claim with ID " + claimId + " is not in APPROVED status");
    }
}
