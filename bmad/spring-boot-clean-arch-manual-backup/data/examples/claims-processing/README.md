# Claims Processing Example

This example demonstrates a complete Clean Architecture implementation for an insurance claims processing system with state transitions and adjudication workflows.

## Domain Model

**Claim** - An insurance claim submitted by a policyholder for incident coverage

### Entities
- `Claim` - Main aggregate root representing an insurance claim with adjudication workflow

### Value Objects
- `ClaimNumber` - Unique claim identifier (format: CLM-YYYY-NNNNNN)
- `ClaimStatus` - Enumeration of claim lifecycle states
- `ClaimAmount` - Monetary amount being claimed
- `IncidentDetails` - Details about the incident (date, location, description)
- `AdjudicationResult` - Result of claim review (approved amount, reason, notes)

### Use Cases
- `SubmitClaimUseCase` - Submit a new claim for processing
- `ProcessClaimUseCase` - Adjudicate claim (approve or reject with reasoning)
- `PayClaimUseCase` - Mark approved claim as paid
- `GetClaimByNumberUseCase` - Retrieve claim details by claim number

### REST Endpoints
- `POST /api/v1/claims` - Submit new claim
- `PUT /api/v1/claims/{claimNumber}/process` - Process/adjudicate claim
- `PUT /api/v1/claims/{claimNumber}/pay` - Mark claim as paid
- `GET /api/v1/claims/{claimNumber}` - Get claim details
- `GET /api/v1/claims` - List all claims with filters

## Architecture Layers

```
domain/
├── entity/
│   └── Claim.java
├── valueobject/
│   ├── ClaimNumber.java
│   ├── ClaimStatus.java
│   ├── ClaimAmount.java
│   ├── IncidentDetails.java
│   └── AdjudicationResult.java
├── port/
│   └── ClaimRepository.java
└── exception/
    ├── InvalidClaimStateException.java
    └── ClaimProcessingException.java

application/
├── dto/
│   ├── SubmitClaimInput.java
│   ├── SubmitClaimOutput.java
│   ├── ProcessClaimInput.java
│   ├── ProcessClaimOutput.java
│   └── ClaimDto.java
├── usecase/
│   ├── SubmitClaimUseCase.java
│   ├── ProcessClaimUseCase.java
│   └── PayClaimUseCase.java
└── service/
    ├── SubmitClaimService.java
    ├── ProcessClaimService.java
    └── PayClaimService.java

infrastructure/
├── adapter/
│   └── persistence/
│       ├── entity/
│       │   ├── ClaimJpaEntity.java
│       │   └── IncidentDetailsJpaEmbeddable.java
│       ├── jpa/
│       │   └── ClaimJpaRepository.java
│       ├── mapper/
│       │   └── ClaimMapper.java
│       └── ClaimRepositoryImpl.java
└── config/
    └── DatabaseConfig.java

presentation/
├── rest/
│   ├── ClaimController.java
│   ├── model/
│   │   ├── SubmitClaimRequest.java
│   │   ├── ProcessClaimRequest.java
│   │   ├── ClaimResponse.java
│   │   └── AdjudicationResultResponse.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── ClaimNotFoundException.java
```

## Business Rules

### Claim Submission Rules
1. **Claim Amount**: Must be greater than zero
2. **Incident Date**: Cannot be in the future
3. **Policy Reference**: Must reference a valid, active policy
4. **Initial Status**: New claims start in DRAFT status
5. **Description**: Incident description is required (minimum 20 characters)

### Claim Processing Rules
6. **Status Transitions**:
   - DRAFT → SUBMITTED (when policyholder submits)
   - SUBMITTED → UNDER_REVIEW (when adjuster picks up)
   - UNDER_REVIEW → APPROVED or REJECTED (after adjudication)
   - APPROVED → PAID (after payment processed)
7. **Invalid Transitions**: Cannot skip states or go backwards
8. **Adjudication Required**: Must provide approved amount and reason when processing
9. **Approved Amount**: Cannot exceed claimed amount
10. **Processing Authorization**: Only adjusters can process claims

### Payment Rules
11. **Payment Eligibility**: Only APPROVED claims can be paid
12. **Payment Once**: Cannot pay a claim twice
13. **Payment Amount**: Must match approved amount

## Claim Status State Machine

```
    DRAFT
      ↓ submit()
   SUBMITTED
      ↓ startReview()
  UNDER_REVIEW
      ↓ approve() or reject()
   APPROVED / REJECTED
      ↓ pay() (only if APPROVED)
     PAID
```

## Test Coverage

- ✅ Unit tests for Claim entity with state transitions
- ✅ Unit tests for value objects (ClaimNumber, ClaimAmount, etc.)
- ✅ Unit tests for use cases with mocked repositories
- ✅ Unit tests for invalid state transitions (expect exceptions)
- ✅ Integration tests for repository with TestContainers
- ✅ Contract tests for REST controllers
- ✅ ArchUnit tests for architectural compliance

## Usage Examples

### Example 1: Submit a Claim

```java
// Submit a new claim
SubmitClaimInput input = new SubmitClaimInput(
    "POL-2024-123456",  // Policy number
    new ClaimAmount(5000.00, "USD"),
    new IncidentDetails(
        LocalDate.of(2024, 11, 1),
        "123 Main St, Springfield",
        "Vehicle collision at intersection. Front bumper damaged, airbag deployed."
    )
);

SubmitClaimOutput output = submitClaimService.execute(input);
// Returns: ClaimNumber = CLM-2024-000123, Status = SUBMITTED
```

### Example 2: Process (Approve) a Claim

```java
// Adjudicate and approve claim
ProcessClaimInput input = new ProcessClaimInput(
    new ClaimNumber("CLM-2024-000123"),
    true,  // approved
    new ClaimAmount(4500.00, "USD"),  // Approved for $4,500
    "Approved based on policy coverage. $500 deductible applied."
);

ProcessClaimOutput output = processClaimService.execute(input);
// Returns: ClaimNumber, Status = APPROVED, ApprovedAmount = $4,500
```

### Example 3: Process (Reject) a Claim

```java
// Adjudicate and reject claim
ProcessClaimInput input = new ProcessClaimInput(
    new ClaimNumber("CLM-2024-000124"),
    false,  // rejected
    null,  // No approved amount
    "Incident occurred before policy effective date. Claim denied per policy terms."
);

ProcessClaimOutput output = processClaimService.execute(input);
// Returns: ClaimNumber, Status = REJECTED, ApprovedAmount = null
```

### Example 4: Pay a Claim

```java
// Mark approved claim as paid
PayClaimInput input = new PayClaimInput(
    new ClaimNumber("CLM-2024-000123"),
    "Check #78945 issued on 2024-11-15"
);

PayClaimOutput output = payClaimService.execute(input);
// Returns: ClaimNumber, Status = PAID, PaymentDate
```

## Domain Events (Future Enhancement)

This example can be extended with domain events:
- `ClaimSubmittedEvent` - When claim moves to SUBMITTED
- `ClaimApprovedEvent` - When claim is approved (trigger payment workflow)
- `ClaimRejectedEvent` - When claim is rejected (notify policyholder)
- `ClaimPaidEvent` - When claim payment is completed

## API Documentation

When running the application, OpenAPI documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Key Learning Points

This example demonstrates:

1. **Aggregate Root Pattern** - Claim enforces business rules and state transitions
2. **State Machine** - Clear, validated transitions between claim statuses
3. **Domain Exceptions** - InvalidClaimStateException for illegal state transitions
4. **Rich Value Objects** - ClaimNumber, ClaimAmount, IncidentDetails encapsulate behavior
5. **Use Case Pattern** - Each business operation is a separate use case
6. **Invariant Enforcement** - Domain entity validates all business rules
7. **Immutability** - Value objects are immutable (Java Records)
8. **Clean Architecture** - Domain layer has zero framework dependencies
