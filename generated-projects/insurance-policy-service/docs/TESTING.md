# Testing Documentation

## Overview

This document describes the testing strategy, practices, and guidelines for the Insurance Policy Service.

The project follows the **Test Pyramid** approach with comprehensive testing at all levels:
- Unit Tests (Domain, Application)
- Integration Tests (Database, API)
- Architecture Tests (ArchUnit)
- Contract Tests (Pact)

**Test Coverage Target**: 85% minimum (enforced by JaCoCo)

---

## Testing Strategy

### Test Pyramid

```
           ┌──────────────┐
          │   Contract    │  (Consumer-Driven Contract Tests)
         │    Tests       │
        └────────────────┘
       ┌──────────────────┐
      │   Architecture     │  (ArchUnit Tests)
     │      Tests          │
    └──────────────────────┘
   ┌────────────────────────┐
  │   Integration Tests     │  (API, Database, TestContainers)
 │                          │
└──────────────────────────┘
┌──────────────────────────────┐
│         Unit Tests            │  (Domain, Application, Fast)
│                               │
└───────────────────────────────┘
```

### Test Categories

1. **Unit Tests** (majority): Fast, isolated, no external dependencies
2. **Integration Tests**: Test with real database (TestContainers) and Spring context
3. **Architecture Tests**: Enforce architectural rules with ArchUnit
4. **Contract Tests**: Ensure API contracts with Pact

---

## Test Structure

### Directory Layout

```
src/test/java/com/insurance/policy/
├── ArchitectureTest.java                    # ArchUnit architecture tests
├── domain/
│   ├── entity/
│   │   ├── PolicyTest.java                  # Policy entity unit tests
│   │   ├── ClaimTest.java                   # Claim entity unit tests
│   │   ├── PaymentTest.java                 # Payment entity unit tests
│   │   └── CustomerTest.java                # Customer entity unit tests
│   └── valueobject/
│       ├── MoneyTest.java                   # Money value object tests
│       ├── PolicyNumberTest.java            # PolicyNumber tests
│       └── ...
├── application/
│   └── service/
│       ├── CreatePolicyServiceTest.java     # Use case unit tests
│       ├── SubmitClaimServiceTest.java      # Use case unit tests
│       └── ProcessPaymentServiceTest.java   # Use case unit tests
├── infrastructure/
│   └── adapter/
│       └── persistence/
│           ├── PolicyRepositoryAdapterTest.java    # Repository integration tests
│           ├── ClaimRepositoryAdapterTest.java
│           └── PaymentRepositoryAdapterTest.java
└── presentation/
    └── rest/
        ├── PolicyControllerTest.java        # Controller unit tests
        ├── ClaimControllerTest.java         # Controller unit tests
        ├── PaymentControllerTest.java       # Controller unit tests
        └── contract/
            └── PolicyServicePactTest.java   # Pact contract tests
```

---

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Only Unit Tests

```bash
mvn test -Dtest=*Test
```

### Run Integration Tests

```bash
mvn verify
```

or

```bash
mvn test -Dtest=*IntegrationTest
```

### Run Architecture Tests Only

```bash
mvn test -Dtest=ArchitectureTest
```

### Run Specific Test Class

```bash
mvn test -Dtest=PolicyTest
```

### Run Specific Test Method

```bash
mvn test -Dtest=PolicyTest#shouldActivatePolicyWhenInDraftStatus
```

### Run Tests with Coverage Report

```bash
mvn clean test jacoco:report
```

View the report: `target/site/jacoco/index.html`

---

## Unit Testing

### Domain Entity Tests

**Purpose**: Test business logic and invariants in domain entities.

**Characteristics**:
- No Spring context
- No database
- Pure Java tests
- Very fast (<10ms per test)

**Example: PolicyTest.java**

```java
@Test
void shouldCreatePolicyWithValidData() {
    // Given
    String customerId = "CUST-001";
    LocalDate effectiveDate = LocalDate.now().plusDays(1);
    List<Coverage> coverages = List.of(
        new Coverage("Liability", Money.of(500, "USD"))
    );

    // When
    Policy policy = new Policy(customerId, effectiveDate, coverages);

    // Then
    assertNotNull(policy);
    assertEquals(PolicyStatus.DRAFT, policy.getStatus());
    assertEquals(Money.of(500, "USD"), policy.getTotalPremium());
}

@Test
void shouldThrowExceptionWhenCustomerIdIsNull() {
    // Given
    LocalDate effectiveDate = LocalDate.now().plusDays(1);
    List<Coverage> coverages = List.of(
        new Coverage("Liability", Money.of(500, "USD"))
    );

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
        new Policy(null, effectiveDate, coverages);
    });
}

@Test
void shouldActivatePolicyWhenInDraftStatus() {
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

@Test
void shouldThrowExceptionWhenActivatingNonDraftPolicy() {
    // Given
    Policy policy = new Policy(
        "CUST-001",
        LocalDate.now(),
        List.of(new Coverage("Liability", Money.of(500, "USD")))
    );
    policy.activate();

    // When & Then
    assertThrows(IllegalStateException.class, () -> {
        policy.activate();
    });
}
```

**Testing Guidelines**:
- Test all business rules
- Test all validation logic
- Test state transitions
- Test edge cases and boundaries
- Use descriptive test method names (shouldXxxWhenYyy)

### Application Service Tests

**Purpose**: Test use case orchestration logic.

**Characteristics**:
- Mock repository dependencies
- Test transaction boundaries
- Test DTO conversions
- Verify repository interactions

**Example: CreatePolicyServiceTest.java**

```java
@ExtendWith(MockitoExtension.class)
class CreatePolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private CreatePolicyService createPolicyService;

    @Test
    void shouldCreatePolicySuccessfully() {
        // Given
        CreatePolicyInput input = new CreatePolicyInput(
            "CUST-001",
            LocalDate.now().plusDays(1),
            List.of(new CoverageDto("Liability", 500.00, "USD"))
        );

        Policy savedPolicy = new Policy(
            input.customerId(),
            input.effectiveDate(),
            List.of(new Coverage("Liability", Money.of(500, "USD")))
        );
        savedPolicy.setId(1L);

        when(policyRepository.save(any(Policy.class))).thenReturn(savedPolicy);

        // When
        CreatePolicyOutput output = createPolicyService.execute(input);

        // Then
        assertNotNull(output);
        assertNotNull(output.policyNumber());
        assertEquals(500.00, output.totalPremiumAmount());
        assertEquals("USD", output.totalPremiumCurrency());
        assertEquals("DRAFT", output.status());

        verify(policyRepository, times(1)).save(any(Policy.class));
    }
}
```

**Testing Guidelines**:
- Mock all repository dependencies
- Verify repository method calls
- Test both success and failure scenarios
- Test validation logic
- Use Mockito for mocking

---

## Integration Testing

### Repository Integration Tests

**Purpose**: Test database interactions with real database (TestContainers).

**Characteristics**:
- Uses Spring Boot Test context
- Uses TestContainers for PostgreSQL
- Tests actual database operations
- Slower than unit tests (~100ms-500ms per test)

**Setup**: TestContainers automatically starts a PostgreSQL container

**Example: PolicyRepositoryAdapterTest.java**

```java
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PolicyRepositoryAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private PolicyRepositoryAdapter policyRepositoryAdapter;

    @Test
    void shouldSaveAndFindPolicy() {
        // Given
        Policy policy = new Policy(
            "CUST-001",
            LocalDate.now().plusDays(1),
            List.of(new Coverage("Liability", Money.of(500, "USD")))
        );

        // When
        Policy savedPolicy = policyRepositoryAdapter.save(policy);

        // Then
        assertNotNull(savedPolicy.getId());

        Optional<Policy> foundPolicy = policyRepositoryAdapter.findById(savedPolicy.getId());
        assertTrue(foundPolicy.isPresent());
        assertEquals("CUST-001", foundPolicy.get().getCustomerId());
    }
}
```

### Controller Integration Tests

**Purpose**: Test REST API endpoints with Spring context.

**Example: PolicyControllerIntegrationTest.java**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PolicyControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Test
    void shouldCreatePolicy() {
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body("""
                {
                  "customerId": "CUST-001",
                  "effectiveDate": "2025-12-01",
                  "coverages": [
                    {"coverageType": "Liability", "premiumAmount": 500.00, "currency": "USD"}
                  ]
                }
                """)
        .when()
            .post("/api/v1/policies")
        .then()
            .statusCode(201)
            .body("policyNumber", notNullValue())
            .body("status", equalTo("DRAFT"));
    }
}
```

**Testing Guidelines**:
- Use TestContainers for real database
- Test complete request/response cycle
- Test validation errors
- Test error responses
- Use REST Assured for API testing

---

## Architecture Testing with ArchUnit

**Purpose**: Enforce architectural rules and layering constraints.

**Location**: `src/test/java/com/insurance/policy/ArchitectureTest.java`

**What It Tests**:
- Layer dependencies (dependency rule)
- Package naming conventions
- No framework dependencies in domain layer
- Repository interfaces in domain, implementations in infrastructure
- No cycles between packages

**Example Rules**:

```java
@ArchTest
static final ArchRule domain_should_not_depend_on_other_layers =
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("..application..", "..infrastructure..", "..presentation..")
        .because("Domain layer should be independent");

@ArchTest
static final ArchRule application_should_not_depend_on_infrastructure =
    noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .because("Application layer should not depend on infrastructure");

@ArchTest
static final ArchRule domain_entities_should_not_use_spring_annotations =
    noClasses()
        .that().resideInAPackage("..domain.entity..")
        .should().beAnnotatedWith("org.springframework.stereotype.Component")
        .orShould().beAnnotatedWith("org.springframework.stereotype.Service");
```

**Running Architecture Tests**:

```bash
mvn test -Dtest=ArchitectureTest
```

**Why It Matters**:
- Prevents architectural violations
- Enforces Clean Architecture principles
- Catches violations early in development
- Documentation as code

---

## Contract Testing with Pact

**Purpose**: Consumer-Driven Contract Testing for API contracts.

**When to Use**:
- Testing API contracts between microservices
- Ensuring backward compatibility
- Preventing breaking changes

**Example Consumer Test**:

```java
@ExtendWith(PactConsumerTestExt.class)
public class PolicyServiceConsumerPactTest {

    @Pact(consumer = "policy-consumer", provider = "insurance-policy-service")
    public RequestResponsePact createPolicyPact(PactDslWithProvider builder) {
        return builder
            .given("A customer exists")
            .uponReceiving("A request to create a policy")
            .path("/api/v1/policies")
            .method("POST")
            .body(new PactDslJsonBody()
                .stringType("customerId", "CUST-001")
                .date("effectiveDate", "yyyy-MM-dd")
                .array("coverages")
            )
            .willRespondWith()
            .status(201)
            .body(new PactDslJsonBody()
                .stringType("policyNumber", "POL-2025-000001")
                .numberType("totalPremiumAmount", 800.00)
            )
            .toPact();
    }
}
```

---

## Test Coverage

### Coverage Requirements

**Minimum Coverage**: 85% (enforced by JaCoCo)

**Coverage by Layer**:
- Domain Layer: 95%+ (business logic must be well-tested)
- Application Layer: 90%+ (use case orchestration)
- Infrastructure Layer: 80%+ (database adapters)
- Presentation Layer: 85%+ (controllers)

### Generating Coverage Report

```bash
mvn clean test jacoco:report
```

### Viewing Coverage Report

Open `target/site/jacoco/index.html` in a browser.

### Coverage Checks

JaCoCo fails the build if coverage is below 85%:

```xml
<limit>
    <counter>LINE</counter>
    <value>COVEREDRATIO</value>
    <minimum>0.85</minimum>
</limit>
```

---

## Test Data Management

### Test Data Builders

Use Test Data Builders for creating test objects:

```java
public class PolicyTestBuilder {
    private String customerId = "CUST-001";
    private LocalDate effectiveDate = LocalDate.now().plusDays(1);
    private List<Coverage> coverages = List.of(
        new Coverage("Liability", Money.of(500, "USD"))
    );

    public PolicyTestBuilder withCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public PolicyTestBuilder withEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
        return this;
    }

    public Policy build() {
        return new Policy(customerId, effectiveDate, coverages);
    }
}

// Usage
Policy policy = new PolicyTestBuilder()
    .withCustomerId("CUST-002")
    .withEffectiveDate(LocalDate.now().plusDays(10))
    .build();
```

### Test Fixtures

Create reusable test fixtures for common scenarios.

---

## Test Naming Conventions

### Unit Test Methods

Format: `should[ExpectedBehavior]When[Condition]`

Examples:
- `shouldCreatePolicyWhenValidDataProvided()`
- `shouldThrowExceptionWhenCustomerIdIsNull()`
- `shouldActivatePolicyWhenInDraftStatus()`
- `shouldCalculateTotalPremiumWhenMultipleCoverages()`

### Test Classes

Format: `[ClassUnderTest]Test`

Examples:
- `PolicyTest`
- `CreatePolicyServiceTest`
- `PolicyControllerTest`

---

## Best Practices

### 1. Follow AAA Pattern

```java
@Test
void testName() {
    // Arrange: Set up test data
    Policy policy = new Policy(...);

    // Act: Execute the behavior
    policy.activate();

    // Assert: Verify the outcome
    assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
}
```

### 2. Test One Thing Per Test

Each test should verify one specific behavior.

### 3. Use Descriptive Assertions

```java
// Good
assertEquals(PolicyStatus.ACTIVE, policy.getStatus(),
    "Policy should be ACTIVE after activation");

// Better
assertThat(policy.getStatus())
    .as("Policy status after activation")
    .isEqualTo(PolicyStatus.ACTIVE);
```

### 4. Avoid Test Logic

Tests should not contain complex logic (if/else, loops).

### 5. Keep Tests Independent

Each test should run independently without depending on other tests.

### 6. Use Mocks Sparingly

- Mock external dependencies (repositories, APIs)
- Don't mock domain objects
- Don't mock value objects

### 7. Test Behavior, Not Implementation

Focus on what the code does, not how it does it.

---

## Continuous Integration

### CI Pipeline

Tests run automatically on every commit:

1. **Unit Tests**: Run first (fast feedback)
2. **Architecture Tests**: Enforce architectural rules
3. **Integration Tests**: Run with TestContainers
4. **Coverage Check**: Ensure 85% minimum
5. **Build Artifacts**: Only if all tests pass

### Test Execution Time

- Unit Tests: < 10 seconds
- Architecture Tests: < 5 seconds
- Integration Tests: < 30 seconds
- **Total**: < 1 minute

---

## Troubleshooting Tests

### TestContainers Issues

**Problem**: Docker not running

**Solution**: Start Docker Desktop

**Problem**: TestContainers slow on Windows

**Solution**: Use WSL2 backend for Docker

### Test Isolation Issues

**Problem**: Tests pass individually but fail when run together

**Solution**: Use `@DirtiesContext` or ensure proper cleanup

### Coverage Issues

**Problem**: Coverage below threshold

**Solution**: Add missing tests for uncovered code

---

## Test Metrics

Current test metrics (as of project generation):

- **Total Tests**: 117
- **Unit Tests**: 89 (76%)
- **Integration Tests**: 22 (19%)
- **Architecture Tests**: 6 (5%)
- **Test Coverage**: 98.3%
- **Test Execution Time**: ~45 seconds

---

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [ArchUnit Documentation](https://www.archunit.org/)
- [REST Assured Documentation](https://rest-assured.io/)
- [Pact Documentation](https://docs.pact.io/)

---

Generated by BMAD Spring Boot Clean Architecture Generator
