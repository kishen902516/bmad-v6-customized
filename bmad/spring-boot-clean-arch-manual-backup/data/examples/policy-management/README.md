# Policy Management Example

This example demonstrates a complete Clean Architecture implementation for an insurance policy management system.

## Domain Model

**Policy** - An insurance policy with coverages and premium calculation

### Entities
- `Policy` - Main aggregate root representing an insurance policy

### Value Objects
- `PolicyNumber` - Unique policy identifier
- `Money` - Premium amount with currency
- `Coverage` - Insurance coverage details
- `PolicyStatus` - Enumeration of policy states

### Use Cases
- `CreatePolicyUseCase` - Create a new insurance policy
- `GetPolicyByNumberUseCase` - Retrieve policy by policy number
- `CalculatePremiumUseCase` - Calculate premium for coverages

### REST Endpoints
- `POST /api/v1/policies` - Create new policy
- `GET /api/v1/policies/{policyNumber}` - Get policy by number
- `GET /api/v1/policies` - List all policies

## Architecture Layers

```
domain/
├── entity/
│   └── Policy.java
├── valueobject/
│   ├── PolicyNumber.java
│   ├── Money.java
│   ├── Coverage.java
│   └── PolicyStatus.java
├── port/
│   └── PolicyRepository.java
└── service/
    └── PremiumCalculator.java (domain service)

application/
├── dto/
│   ├── CreatePolicyInput.java
│   ├── CreatePolicyOutput.java
│   ├── PolicyDto.java
│   └── CoverageDto.java
├── usecase/
│   ├── CreatePolicyUseCase.java
│   └── GetPolicyByNumberUseCase.java
└── service/
    ├── CreatePolicyService.java
    └── GetPolicyByNumberService.java

infrastructure/
├── adapter/
│   └── persistence/
│       ├── entity/
│       │   ├── PolicyJpaEntity.java
│       │   └── CoverageJpaEntity.java
│       ├── jpa/
│       │   └── PolicyJpaRepository.java
│       ├── mapper/
│       │   └── PolicyMapper.java
│       └── PolicyRepositoryImpl.java
└── config/
    └── DatabaseConfig.java

presentation/
├── rest/
│   ├── PolicyController.java
│   ├── model/
│   │   ├── CreatePolicyRequest.java
│   │   ├── PolicyResponse.java
│   │   └── CoverageRequest.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       └── PolicyNotFoundException.java
```

## Business Rules

1. **Policy Number**: Must be unique and follow format "POL-YYYY-NNNNNN"
2. **Premium**: Cannot be negative, calculated from coverages
3. **Coverages**: Policy must have at least one coverage
4. **Status**: Valid transitions: DRAFT → ACTIVE → EXPIRED or CANCELLED
5. **Effective Date**: Must be in the future when creating a new policy

## Test Coverage

- ✅ Unit tests for domain entities and value objects
- ✅ Unit tests for use cases with mocked repositories
- ✅ Integration tests for repository with TestContainers
- ✅ Contract tests for REST controllers
- ✅ ArchUnit tests for architectural compliance

## Usage Example

```java
// Create a policy
CreatePolicyInput input = new CreatePolicyInput(
    "CUST-001",
    LocalDate.now().plusDays(1),
    List.of(
        new CoverageDto("Liability", new Money(500.00, "USD")),
        new CoverageDto("Collision", new Money(300.00, "USD"))
    )
);

CreatePolicyOutput output = createPolicyService.execute(input);
// Returns: PolicyNumber, Total Premium, Status
```

## API Documentation

When running the application, OpenAPI documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs
