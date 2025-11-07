package com.insurance.policy.domain.valueobject;

/**
 * Customer status enumeration.
 *
 * Represents the possible statuses for a customer in the system.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
public enum CustomerStatus {
    /**
     * Customer account is active and in good standing.
     */
    ACTIVE,

    /**
     * Customer account is inactive (no recent activity).
     */
    INACTIVE,

    /**
     * Customer account is suspended (due to policy violation, payment issues, etc.).
     */
    SUSPENDED
}
