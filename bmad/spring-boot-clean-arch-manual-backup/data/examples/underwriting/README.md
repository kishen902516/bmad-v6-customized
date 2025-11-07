# Underwriting Example

This example demonstrates a complete Clean Architecture implementation for an insurance underwriting system with **Domain Services** for risk assessment and business rule evaluation.

## Domain Model

**Application** - An insurance application submitted by an applicant for coverage approval

### Entities
- `Application` - Main aggregate root representing an insurance application

### Value Objects
- `ApplicationNumber` - Unique application identifier (format: APP-YYYY-NNNNNN)
- `ApplicationStatus` - Enumeration of application lifecycle states
- `ApplicantDetails` - Information about the applicant (name, age, occupation, health status)
- `CoverageRequest` - Requested coverage type and amount
- `RiskScore` - Numerical risk score (0-100, higher = riskier)
- `RiskAssessment` - Complete risk assessment result with score, factors, and recommendation

### Domain Services
- `UnderwritingRulesEngine` - Evaluates risk based on applicant profile and coverage request
- `PremiumCalculationService` - Calculates premium based on risk assessment

### Use Cases
- `SubmitApplicationUseCase` - Submit new insurance application
- `AssessRiskUseCase` - Perform automated risk assessment using domain service
- `ApproveApplicationUseCase` - Approve application with premium quote
- `RejectApplicationUseCase` - Reject application with reason
- `GetApplicationByNumberUseCase` - Retrieve application details

### REST Endpoints
- `POST /api/v1/applications` - Submit new application
- `POST /api/v1/applications/{applicationNumber}/assess-risk` - Perform risk assessment
- `PUT /api/v1/applications/{applicationNumber}/approve` - Approve application
- `PUT /api/v1/applications/{applicationNumber}/reject` - Reject application
- `GET /api/v1/applications/{applicationNumber}` - Get application details
- `GET /api/v1/applications` - List all applications

## Architecture Layers

```
domain/
├── entity/
│   └── Application.java
├── valueobject/
│   ├── ApplicationNumber.java
│   ├── ApplicationStatus.java
│   ├── ApplicantDetails.java
│   ├── CoverageRequest.java
│   ├── RiskScore.java
│   └── RiskAssessment.java
├── port/
│   └── ApplicationRepository.java
├── service/
│   ├── UnderwritingRulesEngine.java (Domain Service)
│   └── PremiumCalculationService.java (Domain Service)
└── exception/
    ├── InvalidApplicationStateException.java
    └── RiskAssessmentException.java

application/
├── dto/
│   ├── SubmitApplicationInput.java
│   ├── SubmitApplicationOutput.java
│   ├── AssessRiskOutput.java
│   ├── ApproveApplicationInput.java
│   └── ApproveApplicationOutput.java
├── usecase/
│   ├── SubmitApplicationUseCase.java
│   ├── AssessRiskUseCase.java
│   └── ApproveApplicationUseCase.java
└── service/
    ├── SubmitApplicationService.java
    ├── AssessRiskService.java
    └── ApproveApplicationService.java

infrastructure/
├── adapter/
│   └── persistence/
│       ├── entity/
│       │   ├── ApplicationJpaEntity.java
│       │   └── ApplicantDetailsJpaEmbeddable.java
│       ├── jpa/
│       │   └── ApplicationJpaRepository.java
│       ├── mapper/
│       │   └── ApplicationMapper.java
│       └── ApplicationRepositoryImpl.java
└── config/
    └── DatabaseConfig.java

presentation/
├── rest/
│   ├── ApplicationController.java
│   ├── model/
│   │   ├── SubmitApplicationRequest.java
│   │   ├── ApplicationResponse.java
│   │   └── RiskAssessmentResponse.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── ApplicationNotFoundException.java
```

## Business Rules

### Application Submission Rules
1. **Applicant Age**: Must be between 18 and 80 years old
2. **Coverage Amount**: Must be greater than zero and within limits ($10K - $5M)
3. **Required Information**: Name, age, occupation, health status, coverage details
4. **Initial Status**: New applications start in DRAFT status
5. **Health Declaration**: Required for coverage above $500K

### Risk Assessment Rules
6. **Age Factor**:
   - Under 25: +15 risk points
   - 25-40: +5 risk points
   - 41-60: +10 risk points
   - Over 60: +20 risk points

7. **Occupation Factor**:
   - Low risk (office worker): +0 points
   - Medium risk (teacher, nurse): +10 points
   - High risk (construction, pilot): +25 points

8. **Health Status Factor**:
   - Excellent: +0 points
   - Good: +5 points
   - Fair: +15 points
   - Poor: +30 points

9. **Coverage Amount Factor**:
   - Under $100K: +5 points
   - $100K-$500K: +10 points
   - Over $500K: +20 points

10. **Risk Score Ranges**:
    - 0-25: Low Risk → Auto-approve
    - 26-50: Medium Risk → Manual review
    - 51-75: High Risk → Require additional underwriting
    - 76+: Very High Risk → Auto-reject

### Approval Rules
11. **Auto-Approval Threshold**: Risk score ≤ 25
12. **Manual Review Required**: Risk score 26-75
13. **Auto-Rejection Threshold**: Risk score > 75
14. **Premium Calculation**: Base premium × (1 + risk_score/100)
15. **Underwriter Authority**: Manual approvals require underwriter license verification

### Status Transitions
```
DRAFT → SUBMITTED → UNDER_ASSESSMENT → APPROVED/REJECTED → ACTIVE (after payment)
```

## Test Coverage

- ✅ Unit tests for Application entity with business rules
- ✅ Unit tests for UnderwritingRulesEngine (domain service)
- ✅ Unit tests for all value objects
- ✅ Unit tests for use cases with mocked repositories and domain services
- ✅ Integration tests for repository with TestContainers
- ✅ Contract tests for REST controllers
- ✅ ArchUnit tests for architectural compliance

## Usage Examples

### Example 1: Submit an Application

```java
// Submit a new insurance application
SubmitApplicationInput input = new SubmitApplicationInput(
    new ApplicantDetails(
        "John Smith",
        35,
        "Software Engineer",
        "Excellent"
    ),
    new CoverageRequest(
        "Term Life Insurance",
        new Money(500000.00, "USD"),
        20  // 20-year term
    )
);

SubmitApplicationOutput output = submitApplicationService.execute(input);
// Returns: ApplicationNumber = APP-2024-000456, Status = SUBMITTED
```

### Example 2: Assess Risk (using Domain Service)

```java
// Perform automated risk assessment
AssessRiskInput input = new AssessRiskInput(
    new ApplicationNumber("APP-2024-000456")
);

AssessRiskOutput output = assessRiskService.execute(input);
// Domain Service calculates:
// Age factor (35): +5
// Occupation (Software Engineer): +0
// Health (Excellent): +0
// Coverage ($500K): +10
// Total Risk Score: 15 (Low Risk)
// Recommendation: AUTO_APPROVE

// Returns:
// - RiskScore: 15
// - Risk Level: LOW
// - Recommendation: AUTO_APPROVE
// - Contributing Factors: [Age: +5, Coverage: +10]
```

### Example 3: Approve Application (Low Risk)

```java
// Approve low-risk application
ApproveApplicationInput input = new ApproveApplicationInput(
    new ApplicationNumber("APP-2024-000456"),
    new Money(850.00, "USD"),  // Annual premium
    "Auto-approved based on low risk score",
    "SYSTEM"  // Auto-approval
);

ApproveApplicationOutput output = approveApplicationService.execute(input);
// Returns: ApplicationNumber, Status = APPROVED, Premium = $850/year
```

### Example 4: Manual Review (Medium Risk)

```java
// Application with medium risk requires manual review
AssessRiskInput input = new AssessRiskInput(
    new ApplicationNumber("APP-2024-000457")
);

AssessRiskOutput output = assessRiskService.execute(input);
// Risk Score: 45 (Medium Risk)
// Recommendation: MANUAL_REVIEW_REQUIRED

// Underwriter reviews and decides to approve with higher premium
ApproveApplicationInput approveInput = new ApproveApplicationInput(
    new ApplicationNumber("APP-2024-000457"),
    new Money(1200.00, "USD"),  // Higher premium due to risk
    "Approved after manual review. Increased premium due to occupation risk.",
    "underwriter-jane-doe"
);

ApproveApplicationOutput result = approveApplicationService.execute(approveInput);
```

### Example 5: Auto-Rejection (High Risk)

```java
// High-risk application automatically rejected
AssessRiskInput input = new AssessRiskInput(
    new ApplicationNumber("APP-2024-000458")
);

AssessRiskOutput output = assessRiskService.execute(input);
// Risk Score: 80 (Very High Risk)
// Recommendation: AUTO_REJECT

RejectApplicationInput rejectInput = new RejectApplicationInput(
    new ApplicationNumber("APP-2024-000458"),
    "Application exceeds acceptable risk threshold (score: 80). " +
    "Contributing factors: Age (65), High-risk occupation (Construction), " +
    "Health status (Fair), High coverage amount ($2M)."
);

RejectApplicationOutput result = rejectApplicationService.execute(rejectInput);
```

## Domain Service Pattern

This example showcases the **Domain Service** pattern:

### What is a Domain Service?

A **Domain Service** encapsulates domain logic that:
- Doesn't naturally fit into an entity
- Involves multiple entities or value objects
- Represents a business process or calculation

### UnderwritingRulesEngine (Domain Service)

```java
public class UnderwritingRulesEngine {

    public RiskAssessment assessRisk(
        ApplicantDetails applicant,
        CoverageRequest coverage) {

        int riskScore = 0;
        List<String> factors = new ArrayList<>();

        // Apply age factor
        riskScore += calculateAgeFactor(applicant.age());

        // Apply occupation factor
        riskScore += calculateOccupationFactor(applicant.occupation());

        // Apply health factor
        riskScore += calculateHealthFactor(applicant.healthStatus());

        // Apply coverage factor
        riskScore += calculateCoverageFactor(coverage.amount());

        // Determine recommendation
        Recommendation recommendation = determineRecommendation(riskScore);

        return new RiskAssessment(
            new RiskScore(riskScore),
            factors,
            recommendation
        );
    }
}
```

**Key Points:**
- Lives in domain layer (pure business logic)
- No infrastructure dependencies
- Stateless (no instance variables)
- Uses only domain objects (entities, value objects)
- Encapsulates complex business rules

## Key Learning Points

This example demonstrates:

1. **Domain Services** - Business logic that doesn't belong to a single entity
2. **Complex Business Rules** - Multi-factor risk assessment algorithm
3. **Aggregate Root Pattern** - Application enforces invariants
4. **Rich Value Objects** - RiskScore, RiskAssessment encapsulate behavior
5. **State Machine** - Clear status transitions with validation
6. **Automated Decision Making** - Rule engine for auto-approve/reject
7. **Manual Intervention Points** - Medium-risk applications require review
8. **Separation of Concerns** - Domain service for calculation, entity for state management
9. **Testability** - Domain service can be unit tested in isolation
10. **Clean Architecture** - Domain layer remains pure, no framework dependencies

## API Documentation

When running the application, OpenAPI documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Domain Events (Future Enhancement)

This example can be extended with domain events:
- `ApplicationSubmittedEvent` - When application is submitted
- `RiskAssessedEvent` - When risk assessment is completed
- `ApplicationApprovedEvent` - When application is approved (trigger policy creation)
- `ApplicationRejectedEvent` - When application is rejected (notify applicant)
- `ManualReviewRequiredEvent` - When medium-risk application needs review
