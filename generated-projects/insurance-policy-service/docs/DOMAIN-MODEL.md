# Domain Model Documentation

## Overview

This document describes the domain model of the Insurance Policy Service, including entities, value objects, aggregates, and business rules.

The domain model follows Domain-Driven Design (DDD) principles and is implemented using Clean Architecture patterns.

## Domain Entities

Entities are objects with a unique identity that persists over time. They contain business logic and enforce invariants.

### 1. Policy

**Purpose**: Represents an insurance policy.

**Aggregate Root**: Yes

**Location**: `com.insurance.policy.domain.entity.Policy`

**Attributes**:
- `id` (Long): Internal database ID
- `policyNumber` (PolicyNumber): Unique policy identifier (POL-YYYY-NNNNNN)
- `customerId` (String): Reference to customer
- `effectiveDate` (LocalDate): When policy becomes effective
- `expirationDate` (LocalDate): When policy expires
- `coverages` (List<Coverage>): List of coverage items
- `totalPremium` (Money): Total premium amount
- `status` (PolicyStatus): Current status (DRAFT, ACTIVE, CANCELLED, EXPIRED)

**Business Rules**:
- Customer ID is required
- Effective date must be in the future when created
- Must have at least one coverage
- Total premium is calculated from all coverages
- Can only be activated if in DRAFT status and effective date has passed
- Can only add coverages when in DRAFT status

**Business Methods**:
- `activate()`: Transition from DRAFT to ACTIVE
- `cancel()`: Cancel the policy
- `addCoverage(Coverage)`: Add a new coverage
- `isActive()`: Check if policy is currently active

**Invariants Enforced**:
- Policy number is auto-generated and unique
- Total premium is always calculated from coverages
- Status transitions follow valid state machine
- Cannot modify coverages after activation

### 2. Claim

**Purpose**: Represents an insurance claim for a policy.

**Aggregate Root**: Yes

**Location**: `com.insurance.policy.domain.entity.Claim`

**Attributes**:
- `id` (Long): Internal database ID
- `claimNumber` (ClaimNumber): Unique claim identifier (CLM-YYYY-NNNNNN)
- `claimedAmount` (ClaimAmount): Amount being claimed
- `incidentDate` (LocalDate): When the incident occurred
- `status` (ClaimStatus): Current status (SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID)
- `submittedDate` (LocalDate): When claim was submitted
- `policyId` (String): Reference to associated policy

**Business Rules**:
- Claim number is auto-generated and unique
- Claimed amount must be greater than zero
- Incident date cannot be in the future
- Policy ID is required
- Status starts as SUBMITTED
- Can only move to UNDER_REVIEW from SUBMITTED
- Can only be APPROVED or REJECTED from UNDER_REVIEW
- Can only be marked PAID from APPROVED

**Business Methods**:
- `moveToUnderReview()`: Transition to under review status
- `approve()`: Approve the claim
- `reject()`: Reject the claim
- `markAsPaid()`: Mark as paid
- `canBeProcessedForPayment()`: Check if ready for payment
- `isInFinalState()`: Check if in final state (PAID or REJECTED)

**State Diagram**:
```
SUBMITTED → UNDER_REVIEW → APPROVED → PAID
                ↓
            REJECTED
```

### 3. Payment

**Purpose**: Represents a payment for an approved claim.

**Aggregate Root**: Yes

**Location**: `com.insurance.policy.domain.entity.Payment`

**Attributes**:
- `paymentId` (Long): Internal database ID
- `claimId` (Long): Reference to associated claim
- `amount` (PaymentAmount): Payment amount
- `paymentMethod` (PaymentMethod): How payment is made
- `paymentStatus` (PaymentStatus): Current status (PENDING, COMPLETED, FAILED)
- `transactionId` (TransactionId): Unique transaction identifier
- `paymentDate` (LocalDate): When payment was processed
- `processedBy` (String): User who processed the payment
- `notes` (String): Additional notes

**Business Rules**:
- Claim ID is required and must be approved
- Amount must be greater than zero
- Transaction ID must be unique (no duplicates)
- Payment method is required
- Status starts as PENDING
- Cannot process payment if claim is not approved

**Business Methods**:
- `complete()`: Mark payment as completed
- `fail()`: Mark payment as failed
- `isCompleted()`: Check if payment is completed

### 4. Customer

**Purpose**: Represents a customer (simplified entity for this service).

**Aggregate Root**: Yes

**Location**: `com.insurance.policy.domain.entity.Customer`

**Attributes**:
- `id` (Long): Internal database ID
- `customerId` (String): Unique customer identifier
- `name` (String): Customer name
- `email` (String): Customer email
- `status` (CustomerStatus): Current status (ACTIVE, INACTIVE, SUSPENDED)

**Note**: This is a simplified representation. In a microservices architecture, customer data would likely be managed by a separate Customer Service.

---

## Value Objects

Value objects are immutable objects defined by their attributes rather than identity. They are implemented as Java Records.

### 1. PolicyNumber

**Purpose**: Unique policy identifier

**Format**: POL-YYYY-NNNNNN (e.g., POL-2025-000001)

**Location**: `com.insurance.policy.domain.valueobject.PolicyNumber`

**Generation Logic**:
- Year is extracted from effective date
- Sequential number is auto-incremented

### 2. ClaimNumber

**Purpose**: Unique claim identifier

**Format**: CLM-YYYY-NNNNNN (e.g., CLM-2025-000001)

**Location**: `com.insurance.policy.domain.valueobject.ClaimNumber`

**Generation Logic**:
- Year is current year
- Sequential number is auto-incremented

### 3. Money

**Purpose**: Represents monetary amounts with currency

**Location**: `com.insurance.policy.domain.valueobject.Money`

**Attributes**:
- `amount` (BigDecimal): Numeric value
- `currency` (String): Currency code (e.g., USD)

**Business Rules**:
- Amount must be non-negative
- Currency must be valid ISO 4217 code
- Supports arithmetic operations (add, subtract, multiply)

**Static Factory**:
- `Money.ZERO_USD`: Zero dollars constant

### 4. Coverage

**Purpose**: Represents a coverage item in a policy

**Location**: `com.insurance.policy.domain.valueobject.Coverage`

**Attributes**:
- `coverageType` (String): Type of coverage
- `premiumAmount` (Money): Premium for this coverage

**Validation**:
- Coverage type is required
- Premium amount must be positive

### 5. ClaimAmount

**Purpose**: Represents the amount being claimed

**Location**: `com.insurance.policy.domain.valueobject.ClaimAmount`

**Attributes**:
- `value` (BigDecimal): Claimed amount

**Validation**:
- Must be greater than zero

**Business Methods**:
- `isZero()`: Check if amount is zero

### 6. PaymentAmount

**Purpose**: Represents a payment amount

**Location**: `com.insurance.policy.domain.valueobject.PaymentAmount`

**Attributes**:
- `value` (BigDecimal): Payment amount

**Validation**:
- Must be greater than zero

### 7. TransactionId

**Purpose**: Unique transaction identifier for payments

**Location**: `com.insurance.policy.domain.valueobject.TransactionId`

**Attributes**:
- `value` (String): Transaction ID

**Validation**:
- Must be non-empty
- Must be unique across all payments

---

## Enumerations

### 1. PolicyStatus

**Values**:
- `DRAFT`: Policy is being created
- `ACTIVE`: Policy is active and in effect
- `CANCELLED`: Policy was cancelled
- `EXPIRED`: Policy has expired

### 2. ClaimStatus

**Values**:
- `SUBMITTED`: Claim was submitted
- `UNDER_REVIEW`: Claim is being reviewed
- `APPROVED`: Claim was approved for payment
- `REJECTED`: Claim was rejected
- `PAID`: Claim payment was completed

### 3. PaymentStatus

**Values**:
- `PENDING`: Payment is pending
- `COMPLETED`: Payment was successfully completed
- `FAILED`: Payment failed

### 4. PaymentMethod

**Values**:
- `BANK_TRANSFER`: Direct bank transfer
- `CHECK`: Paper check
- `CARD`: Credit/debit card
- `CASH`: Cash payment

### 5. CustomerStatus

**Values**:
- `ACTIVE`: Customer is active
- `INACTIVE`: Customer is inactive
- `SUSPENDED`: Customer account is suspended

---

## Repository Interfaces (Ports)

Repository interfaces define data persistence contracts. They are part of the domain layer but implemented in the infrastructure layer.

### 1. PolicyRepository

**Location**: `com.insurance.policy.domain.port.PolicyRepository`

**Methods**:
- `save(Policy): Policy`: Save or update a policy
- `findById(Long): Optional<Policy>`: Find policy by ID
- `findByPolicyNumber(PolicyNumber): Optional<Policy>`: Find by policy number
- `findAll(): List<Policy>`: Get all policies

### 2. ClaimRepository

**Location**: `com.insurance.policy.domain.port.ClaimRepository`

**Methods**:
- `save(Claim): Claim`: Save or update a claim
- `findById(Long): Optional<Claim>`: Find claim by ID
- `findByClaimNumber(ClaimNumber): Optional<Claim>`: Find by claim number
- `findByPolicyId(String): List<Claim>`: Find all claims for a policy
- `findAll(): List<Claim>`: Get all claims

### 3. PaymentRepository

**Location**: `com.insurance.policy.domain.port.PaymentRepository`

**Methods**:
- `save(Payment): Payment`: Save or update a payment
- `findById(Long): Optional<Payment>`: Find payment by ID
- `findByClaimId(Long): List<Payment>`: Find all payments for a claim
- `findByPaymentStatus(PaymentStatus): List<Payment>`: Find by status
- `findByTransactionId(TransactionId): Optional<Payment>`: Find by transaction ID
- `findAll(): List<Payment>`: Get all payments

### 4. CustomerRepository

**Location**: `com.insurance.policy.domain.port.CustomerRepository`

**Methods**:
- `save(Customer): Customer`: Save or update a customer
- `findById(Long): Optional<Customer>`: Find customer by ID
- `findByCustomerId(String): Optional<Customer>`: Find by customer ID
- `findAll(): List<Customer>`: Get all customers

---

## Aggregates and Boundaries

### Policy Aggregate

**Root**: Policy

**Boundary**:
- Policy entity
- Coverage value objects (part of Policy)

**Invariants**:
- Total premium equals sum of all coverage premiums
- Policy must have at least one coverage
- Cannot modify coverages after activation

**Access Rules**:
- All coverage modifications go through Policy methods
- Coverages cannot be accessed or modified directly

### Claim Aggregate

**Root**: Claim

**Boundary**:
- Claim entity
- ClaimAmount value object
- ClaimNumber value object

**Invariants**:
- Claim must reference a valid policy
- Claimed amount must be positive
- Status transitions must follow state machine

### Payment Aggregate

**Root**: Payment

**Boundary**:
- Payment entity
- PaymentAmount value object
- TransactionId value object

**Invariants**:
- Payment must reference an approved claim
- Transaction ID must be unique
- Payment amount must be positive

### Customer Aggregate

**Root**: Customer

**Boundary**:
- Customer entity

**Invariants**:
- Customer ID must be unique
- Email must be valid

---

## Domain Events

(Note: Domain events are not currently implemented but can be added)

**Potential Events**:
- `PolicyCreatedEvent`: Fired when a new policy is created
- `PolicyActivatedEvent`: Fired when a policy is activated
- `ClaimSubmittedEvent`: Fired when a claim is submitted
- `ClaimApprovedEvent`: Fired when a claim is approved
- `PaymentProcessedEvent`: Fired when a payment is completed

**Event Usage**:
- Trigger notifications (email, SMS)
- Update analytics and reporting
- Integrate with external systems
- Maintain audit trail

---

## Business Rules Summary

### Policy Creation Rules
1. Customer ID is required
2. Effective date must be in the future
3. At least one coverage is required
4. All coverage amounts must be positive
5. Policy number is auto-generated

### Claim Submission Rules
1. Policy must exist and be active
2. Claimed amount must be greater than zero
3. Incident date cannot be in the future
4. Incident date must be after policy effective date
5. Claim number is auto-generated

### Payment Processing Rules
1. Claim must exist and be approved
2. Payment amount must be greater than zero
3. Transaction ID must be unique
4. Payment method is required
5. Payment marks claim as PAID when completed

### State Transition Rules

**Policy States**:
- DRAFT → ACTIVE (when effective date passes)
- ACTIVE → CANCELLED (manual cancellation)
- ACTIVE → EXPIRED (when expiration date passes)

**Claim States**:
- SUBMITTED → UNDER_REVIEW (manual review)
- UNDER_REVIEW → APPROVED (approved by adjuster)
- UNDER_REVIEW → REJECTED (rejected by adjuster)
- APPROVED → PAID (when payment is completed)

**Payment States**:
- PENDING → COMPLETED (successful payment)
- PENDING → FAILED (payment failure)

---

## Domain Model Diagram

```
┌─────────────────────────────────────────────────────────┐
│                        Policy                           │
│  - id: Long                                             │
│  - policyNumber: PolicyNumber                           │
│  - customerId: String                                   │
│  - effectiveDate: LocalDate                             │
│  - expirationDate: LocalDate                            │
│  - coverages: List<Coverage>                            │
│  - totalPremium: Money                                  │
│  - status: PolicyStatus                                 │
│                                                         │
│  + activate(): void                                     │
│  + cancel(): void                                       │
│  + addCoverage(Coverage): void                          │
│  + isActive(): boolean                                  │
└─────────────────────────────────────────────────────────┘
                         │
                         │ 1:N
                         ▼
┌─────────────────────────────────────────────────────────┐
│                        Claim                            │
│  - id: Long                                             │
│  - claimNumber: ClaimNumber                             │
│  - claimedAmount: ClaimAmount                           │
│  - incidentDate: LocalDate                              │
│  - status: ClaimStatus                                  │
│  - submittedDate: LocalDate                             │
│  - policyId: String                                     │
│                                                         │
│  + moveToUnderReview(): void                            │
│  + approve(): void                                      │
│  + reject(): void                                       │
│  + markAsPaid(): void                                   │
└─────────────────────────────────────────────────────────┘
                         │
                         │ 1:N
                         ▼
┌─────────────────────────────────────────────────────────┐
│                       Payment                           │
│  - paymentId: Long                                      │
│  - claimId: Long                                        │
│  - amount: PaymentAmount                                │
│  - paymentMethod: PaymentMethod                         │
│  - paymentStatus: PaymentStatus                         │
│  - transactionId: TransactionId                         │
│  - paymentDate: LocalDate                               │
│  - processedBy: String                                  │
│  - notes: String                                        │
│                                                         │
│  + complete(): void                                     │
│  + fail(): void                                         │
│  + isCompleted(): boolean                               │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                      Customer                           │
│  - id: Long                                             │
│  - customerId: String                                   │
│  - name: String                                         │
│  - email: String                                        │
│  - status: CustomerStatus                               │
└─────────────────────────────────────────────────────────┘
                         │
                         │ 1:N
                         ▼
                      Policy
```

---

## Design Patterns Used

### 1. Aggregate Pattern
- Policy, Claim, Payment, Customer are aggregate roots
- Each aggregate maintains its own consistency boundary

### 2. Value Object Pattern
- Money, PolicyNumber, ClaimNumber, Coverage, etc.
- Immutable, self-validating, no identity

### 3. Repository Pattern
- PolicyRepository, ClaimRepository, PaymentRepository
- Abstract data access behind interfaces

### 4. Factory Methods
- PolicyNumber.generate()
- ClaimNumber.generate()

### 5. State Pattern
- PolicyStatus, ClaimStatus, PaymentStatus
- Enforced state transitions

---

## Testing the Domain Model

The domain model is designed to be testable without framework dependencies:

```java
// Example: Testing Policy business logic
@Test
void shouldActivatePolicyWhenEffectiveDatePasses() {
    // Given
    Policy policy = new Policy(
        "CUST-001",
        LocalDate.now(),
        List.of(new Coverage("Liability", Money.of(500, "USD")))
    );

    // When
    policy.activate();

    // Then
    assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
}
```

All business rules can be tested with plain JUnit tests without Spring or databases.

---

## Evolution Guidelines

When evolving the domain model:

1. **Add new entities**: Create in `domain.entity` package
2. **Add new value objects**: Create as Records in `domain.valueobject`
3. **Add new business rules**: Implement in entity methods
4. **Add new repository operations**: Add to repository interfaces in `domain.port`
5. **Maintain invariants**: Validate in entity constructors and methods
6. **Keep domain pure**: Avoid framework dependencies

---

Generated by BMAD Spring Boot Clean Architecture Generator
