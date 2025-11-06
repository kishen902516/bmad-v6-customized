# Test-Driven Development (TDD) Workflow Guide

## Overview

This guide describes how the Spring Boot Clean Architecture Generator enforces Test-Driven Development using the Red-Green-Refactor cycle. All code generation follows TDD principles to ensure high-quality, well-tested code.

## TDD Principles

### The Red-Green-Refactor Cycle

```
┌──────────────────────────────────────────────────────┐
│                                                      │
│  🔴 RED → 🟢 GREEN → 🔵 REFACTOR → 🔁 Repeat       │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### Phase 1: 🔴 RED - Write Failing Tests

**Goal**: Write tests that fail because the functionality doesn't exist yet.

**Process**:
1. Generate test files first (entity tests, use case tests, controller tests)
2. Tests describe the expected behavior
3. Run tests to verify they fail
4. Commit with TDD-Phase: RED

**Example**:
```java
// PolicyTest.java - RED Phase
@Test
@DisplayName("Should create policy with valid data")
void shouldCreatePolicyWithValidData() {
    // Arrange
    PolicyNumber number = PolicyNumber.of("POL-2025-000001");
    LocalDate effectiveDate = LocalDate.now().plusDays(30);

    // Act
    Policy policy = new Policy(number, "CUST-001", effectiveDate);

    // Assert
    assertThat(policy.getPolicyNumber()).isEqualTo(number);
    assertThat(policy.getStatus()).isEqualTo(PolicyStatus.DRAFT);
}
```

**Test Status**: ❌ **FAILING** (Policy class doesn't exist yet)

**Benefits**:
- Forces you to think about requirements first
- Tests describe the API you want
- Ensures tests actually test something (not false positives)

### Phase 2: 🟢 GREEN - Make Tests Pass

**Goal**: Write minimal code to make all tests pass.

**Process**:
1. Generate implementation code (entities, use cases, controllers)
2. Write only enough code to pass the tests
3. Don't worry about perfection - just make it work
4. Run tests to verify they pass
5. Commit with TDD-Phase: GREEN

**Example**:
```java
// Policy.java - GREEN Phase
public class Policy {
    private final PolicyNumber policyNumber;
    private final String customerId;
    private final LocalDate effectiveDate;
    private PolicyStatus status;

    public Policy(PolicyNumber policyNumber, String customerId, LocalDate effectiveDate) {
        this.policyNumber = policyNumber;
        this.customerId = customerId;
        this.effectiveDate = effectiveDate;
        this.status = PolicyStatus.DRAFT; // Minimal implementation
    }

    // Getters...
}
```

**Test Status**: ✅ **PASSING**

**Benefits**:
- Proves your implementation satisfies requirements
- Provides immediate feedback
- Builds confidence in the code

### Phase 3: 🔵 REFACTOR - Improve Design

**Goal**: Improve code quality while keeping tests green.

**Process**:
1. Review generated code for improvements
2. Extract methods, classes, or value objects
3. Improve naming and structure
4. Eliminate duplication
5. Run tests after each change to ensure they still pass
6. Commit with TDD-Phase: REFACTOR

**Example**:
```java
// Before Refactor
public class Policy {
    private BigDecimal premiumAmount;
    private String premiumCurrency;

    public void calculatePremium() {
        // Complex calculation...
    }
}

// After Refactor - Extract Value Object
public class Policy {
    private Money premium; // Extracted to Money value object

    public void calculatePremium() {
        this.premium = Money.of(calculateAmount(), "USD");
    }
}

// Money.java - New Value Object
public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }
}
```

**Test Status**: ✅ **STILL PASSING**

**Benefits**:
- Improves code quality without breaking functionality
- Tests act as safety net
- Encourages clean, maintainable code

## TDD in BMAD Workflows

### Workflow: *add-entity

**Step 1: RED Phase**
```
Agent: "I'll generate the entity tests first (RED phase)..."

Generated files:
✅ PolicyTest.java (domain entity test)
✅ PolicyMapperTest.java (mapper test)

Running tests...
❌ 10 tests failing (expected - RED phase)

Commit: "test: add failing tests for Policy entity (RED) #42"
GitHub: Update issue label: tdd:red
```

**Step 2: GREEN Phase**
```
Agent: "Now I'll implement the entity to make tests pass (GREEN phase)..."

Generated files:
✅ Policy.java (domain entity)
✅ PolicyJpaEntity.java (JPA entity)
✅ PolicyMapper.java (mapper)

Running tests...
✅ 10 tests passing (GREEN phase)

Commit: "feat: implement Policy entity (GREEN) #42"
GitHub: Update issue label: tdd:green
```

**Step 3: REFACTOR Phase**
```
Agent: "Let me review the code for improvements (REFACTOR phase)..."

Identified improvements:
- Extract Money value object
- Extract Coverage value object
- Improve validation logic

Apply refactoring? [y/n]
User: y

Refactoring applied...
Running tests...
✅ 10 tests still passing (REFACTOR phase)

Commit: "refactor: extract value objects for Policy #42"
GitHub: Update issue label: tdd:refactor
```

### Workflow: *add-use-case

**Step 1: RED Phase**
```
Agent: "I'll generate the use case tests first with mocks (RED phase)..."

Generated files:
✅ CreatePolicyUseCaseTest.java (use case test with Mockito)

Running tests...
❌ 8 tests failing (expected - RED phase)

Commit: "test: add failing tests for CreatePolicyUseCase (RED) #43"
GitHub: Update issue label: tdd:red
```

**Step 2: GREEN Phase**
```
Agent: "Now I'll implement the use case (GREEN phase)..."

Generated files:
✅ CreatePolicyInput.java (input DTO)
✅ CreatePolicyOutput.java (output DTO)
✅ CreatePolicyUseCase.java (interface)
✅ CreatePolicyUseCaseImpl.java (implementation)

Running tests...
✅ 8 tests passing (GREEN phase)

Commit: "feat: implement CreatePolicyUseCase (GREEN) #43"
GitHub: Update issue label: tdd:green
```

**Step 3: REFACTOR Phase**
```
Agent: "Let me review for improvements (REFACTOR phase)..."

Identified improvements:
- Extract validation to separate method
- Improve error handling
- Add more detailed logging

Apply refactoring? [y/n]
User: y

Refactoring applied...
Running tests...
✅ 8 tests still passing (REFACTOR phase)

Commit: "refactor: improve CreatePolicyUseCase error handling #43"
GitHub: Update issue label: tdd:refactor
```

### Workflow: *add-rest-endpoint

**Step 1: RED Phase**
```
Agent: "I'll generate controller and Pact tests first (RED phase)..."

Generated files:
✅ PolicyControllerTest.java (MockMvc test)
✅ PolicyConsumerPactTest.java (Pact consumer test)

Running tests...
❌ 12 tests failing (expected - RED phase)

Commit: "test: add failing tests for PolicyController (RED) #44"
GitHub: Update issue label: tdd:red
```

**Step 2: GREEN Phase**
```
Agent: "Now I'll implement the controller (GREEN phase)..."

Generated files:
✅ PolicyController.java (REST controller)
✅ CreatePolicyRequest.java (request DTO)
✅ PolicyResponse.java (response DTO)

Running tests...
✅ 12 tests passing (GREEN phase)
✅ Pact contract generated: target/pacts/PolicyConsumer-PolicyProvider.json

Commit: "feat: implement PolicyController (GREEN) #44"
GitHub: Update issue label: tdd:green
```

**Step 3: REFACTOR Phase**
```
Agent: "Let me review for improvements (REFACTOR phase)..."

Identified improvements:
- Improve OpenAPI documentation
- Add more detailed error responses
- Extract request validation

Apply refactoring? [y/n]
User: y

Refactoring applied...
Running tests...
✅ 12 tests still passing (REFACTOR phase)

Commit: "refactor: improve PolicyController documentation #44"
GitHub: Update issue label: tdd:refactor
```

## TDD Best Practices

### 1. Write the Simplest Test First

Start with the simplest, most basic test case:

```java
// Start simple
@Test
void shouldCreateEntity() {
    Entity entity = new Entity("id");
    assertThat(entity).isNotNull();
}

// Then add complexity
@Test
void shouldValidateRequiredFields() {
    assertThatThrownBy(() -> new Entity(null))
        .isInstanceOf(IllegalArgumentException.class);
}
```

### 2. One Test, One Concept

Each test should verify a single behavior:

```java
// Good - Single concept
@Test
void shouldCalculatePremiumForLiabilityCoverage() {
    // Test one thing
}

@Test
void shouldCalculatePremiumForCollisionCoverage() {
    // Test another thing
}

// Bad - Multiple concepts
@Test
void shouldCalculateAllPremiums() {
    // Tests multiple things - hard to debug
}
```

### 3. Use Descriptive Test Names

Test names should describe what they test:

```java
// Good
@Test
@DisplayName("Should throw exception when effective date is in the past")
void shouldThrowExceptionWhenEffectiveDateIsInPast() { }

// Bad
@Test
void test1() { }
```

### 4. Arrange-Act-Assert Pattern

Structure tests clearly:

```java
@Test
void shouldAddCoverageToPolicy() {
    // Arrange
    Policy policy = createTestPolicy();
    Coverage coverage = new Coverage("Liability", Money.of(500, "USD"));

    // Act
    policy.addCoverage(coverage);

    // Assert
    assertThat(policy.getCoverages()).contains(coverage);
    assertThat(policy.getTotalPremium().amount())
        .isEqualTo(Money.of(500, "USD").amount());
}
```

### 5. Don't Test Framework Code

Focus on your business logic:

```java
// Good - Tests business logic
@Test
void shouldEnforcePolicyMustHaveAtLeastOneCoverage() {
    Policy policy = new Policy(/* ... */);

    assertThatThrownBy(() -> policy.activate())
        .isInstanceOf(PolicyValidationException.class)
        .hasMessage("Policy must have at least one coverage");
}

// Bad - Tests framework (JPA)
@Test
void shouldSavePolicyToDatabase() {
    // Don't test JPA - that's framework responsibility
}
```

### 6. Use Test Data Builders

Create readable test data:

```java
public class PolicyTestBuilder {
    private PolicyNumber number = PolicyNumber.of("POL-2025-000001");
    private String customerId = "CUST-001";
    private LocalDate effectiveDate = LocalDate.now().plusDays(30);

    public PolicyTestBuilder withCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public Policy build() {
        return new Policy(number, customerId, effectiveDate);
    }
}

// Usage
@Test
void test() {
    Policy policy = new PolicyTestBuilder()
        .withCustomerId("CUST-999")
        .build();
}
```

## Commit Message Convention

All commits follow this pattern to track TDD phase:

```
<type>: <subject> #<issue>

TDD-Phase: RED | GREEN | REFACTOR
```

**Examples**:

```
test: add failing tests for Policy entity (RED) #42

TDD-Phase: RED
```

```
feat: implement Policy entity (GREEN) #42

TDD-Phase: GREEN
```

```
refactor: extract Money value object #42

TDD-Phase: REFACTOR
```

## GitHub Labels

Issues are labeled to track TDD progress:

- `tdd:red` - Tests written, implementation pending
- `tdd:green` - Implementation complete, tests passing
- `tdd:refactor` - Code refactored while maintaining green tests
- `tdd:complete` - All TDD phases complete

## Test Coverage Goals

| Layer | Target Coverage | Tool |
|-------|----------------|------|
| Domain Entities | 95%+ | JUnit 5 |
| Application Use Cases | 90%+ | JUnit 5 + Mockito |
| Infrastructure | 80%+ | TestContainers |
| Presentation Controllers | 85%+ | MockMvc + Pact |
| Overall Project | 90%+ | JaCoCo |

## Common TDD Patterns

### Pattern 1: Entity Creation

```java
// RED
@Test
void shouldCreateEntityWithValidData() {
    Entity entity = new Entity(validData);
    assertThat(entity.getData()).isEqualTo(validData);
}

// GREEN
public class Entity {
    private final String data;
    public Entity(String data) { this.data = data; }
    public String getData() { return data; }
}

// REFACTOR
public class Entity {
    private final String data;

    public Entity(String data) {
        validateData(data);
        this.data = data;
    }

    private void validateData(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Data is required");
        }
    }

    public String getData() { return data; }
}
```

### Pattern 2: Use Case Implementation

```java
// RED
@Test
void shouldExecuteUseCaseSuccessfully() {
    // Mock dependencies
    when(repository.save(any())).thenReturn(savedEntity);

    // Execute
    Output output = useCase.execute(input);

    // Verify
    assertThat(output).isNotNull();
    verify(repository).save(any());
}

// GREEN
public class UseCaseImpl implements UseCase {
    private final Repository repository;

    public Output execute(Input input) {
        Entity entity = input.toEntity();
        Entity saved = repository.save(entity);
        return Output.from(saved);
    }
}

// REFACTOR
public class UseCaseImpl implements UseCase {
    private final Repository repository;
    private final DomainService service;

    @Transactional
    public Output execute(Input input) {
        validateInput(input);
        Entity entity = service.createEntity(input);
        Entity saved = repository.save(entity);
        log.info("Entity created: {}", saved.getId());
        return Output.from(saved);
    }

    private void validateInput(Input input) {
        // Extracted validation logic
    }
}
```

## Benefits of TDD in This Module

1. **High Confidence**: Every feature has tests before code
2. **Better Design**: Tests drive cleaner interfaces
3. **Documentation**: Tests document expected behavior
4. **Regression Prevention**: Tests catch breaking changes
5. **Refactoring Safety**: Can improve code without fear
6. **Architecture Validation**: ArchUnit tests enforce Clean Architecture
7. **Contract Testing**: Pact ensures API compatibility

## Resources

- [Test-Driven Development by Kent Beck](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)
- [Growing Object-Oriented Software, Guided by Tests](http://www.growing-object-oriented-software.com/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

**Generated by BMAD Spring Boot Clean Architecture Generator**
**Module Version**: 1.0.0
