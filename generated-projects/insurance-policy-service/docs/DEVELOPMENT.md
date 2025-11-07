# Development Guide

## Overview

This guide provides comprehensive information for developers working on the Insurance Policy Service, including setup instructions, development workflows, coding standards, and common tasks.

---

## Getting Started

### Prerequisites

- **Java 21+** (with preview features enabled)
- **Maven 3.8+**
- **PostgreSQL 14+** (or Docker for TestContainers)
- **Git**
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions
- **Docker** (optional, for TestContainers and local database)

### Initial Setup

#### 1. Clone the Repository

```bash
git clone <repository-url>
cd insurance-policy-service
```

#### 2. Configure Database

**Option A: Local PostgreSQL**

Create a database:
```sql
CREATE DATABASE insurance_policy_db;
```

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/insurance_policy_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**Option B: Docker PostgreSQL**

```bash
docker run --name insurance-postgres \
  -e POSTGRES_DB=insurance_policy_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

#### 3. Build the Project

```bash
mvn clean install
```

This will:
- Compile the code
- Run all tests (unit, integration, architecture)
- Generate test coverage report
- Package the application

#### 4. Run the Application

```bash
mvn spring-boot:run
```

Application starts on: `http://localhost:8080`

#### 5. Verify Setup

Test the health endpoint:
```bash
curl http://localhost:8080/actuator/health
```

Access Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

---

## Project Structure

### Package Organization

```
com.insurance.policy/
├── domain/                          # Domain Layer (No framework dependencies)
│   ├── entity/                      # Domain entities with business logic
│   │   ├── Policy.java              # Policy aggregate root
│   │   ├── Claim.java               # Claim aggregate root
│   │   ├── Payment.java             # Payment aggregate root
│   │   └── Customer.java            # Customer aggregate root
│   ├── valueobject/                 # Immutable value objects (Records)
│   │   ├── PolicyNumber.java        # Policy identifier
│   │   ├── Money.java               # Monetary values
│   │   ├── Coverage.java            # Coverage details
│   │   └── ...
│   ├── port/                        # Repository interfaces (ports)
│   │   ├── PolicyRepository.java    # Policy persistence contract
│   │   ├── ClaimRepository.java     # Claim persistence contract
│   │   └── ...
│   └── service/                     # Domain services (if needed)
│
├── application/                     # Application Layer (Use Cases)
│   ├── usecase/                     # Use case interfaces
│   │   ├── CreatePolicyUseCase.java
│   │   ├── SubmitClaimUseCase.java
│   │   └── ProcessPaymentUseCase.java
│   ├── service/                     # Use case implementations
│   │   ├── CreatePolicyService.java
│   │   ├── SubmitClaimService.java
│   │   └── ProcessPaymentService.java
│   ├── dto/                         # Data Transfer Objects
│   │   ├── CreatePolicyInput.java
│   │   ├── CreatePolicyOutput.java
│   │   └── ...
│   └── exception/                   # Application exceptions
│       ├── PolicyNotFoundException.java
│       └── ...
│
├── infrastructure/                  # Infrastructure Layer (Technical Details)
│   ├── adapter/
│   │   └── persistence/             # Database adapters
│   │       ├── entity/              # JPA entities
│   │       │   ├── PolicyJpaEntity.java
│   │       │   └── ...
│   │       ├── mapper/              # Domain ↔ JPA mappers
│   │       │   ├── PolicyMapper.java
│   │       │   └── ...
│   │       ├── PolicySpringDataRepository.java   # Spring Data JPA
│   │       └── PolicyRepositoryAdapter.java      # Repository implementation
│   ├── config/                      # Spring configurations
│   │   ├── OpenApiConfig.java       # OpenAPI/Swagger config
│   │   └── WebConfig.java           # Web configuration
│   └── persistence/                 # Additional persistence (if different from adapter)
│
└── presentation/                    # Presentation Layer (REST API)
    └── rest/                        # REST controllers
        ├── PolicyController.java    # Policy endpoints
        ├── ClaimController.java     # Claim endpoints
        ├── PaymentController.java   # Payment endpoints
        ├── model/                   # Request/Response models
        │   ├── CreatePolicyRequest.java
        │   ├── PolicyResponse.java
        │   └── ...
        └── exception/               # Exception handlers
            └── GlobalExceptionHandler.java
```

### Layer Responsibilities

| Layer | Responsibilities | Dependencies |
|-------|-----------------|--------------|
| **Domain** | Business logic, entities, value objects, repository interfaces | None (pure Java) |
| **Application** | Use case orchestration, DTOs, transaction management | Domain only |
| **Infrastructure** | Database, external APIs, technical implementations | Domain, Application |
| **Presentation** | REST API, request/response handling, validation | Application |

---

## Development Workflow

### Test-Driven Development (TDD)

This project follows **Red-Green-Refactor** TDD cycle:

1. **Red**: Write a failing test
2. **Green**: Write minimal code to pass the test
3. **Refactor**: Improve code while keeping tests green

### Workflow for Adding Features

#### 1. Adding a New Entity

Use the BMAD workflow (if available):
```bash
bmad add-entity
```

Or manually:

1. Create domain entity in `domain.entity`
2. Create value objects in `domain.valueobject`
3. Create repository interface in `domain.port`
4. Write unit tests for entity
5. Create JPA entity in `infrastructure.adapter.persistence.entity`
6. Create mapper in `infrastructure.adapter.persistence.mapper`
7. Create repository adapter in `infrastructure.adapter.persistence`
8. Write integration tests

#### 2. Adding a New Use Case

1. Create use case interface in `application.usecase`
2. Create input/output DTOs in `application.dto`
3. Create service implementation in `application.service`
4. Write unit tests with mocked dependencies
5. Add `@Service` and `@Transactional` annotations

Example:
```java
// 1. Use Case Interface
public interface CreatePolicyUseCase {
    CreatePolicyOutput execute(CreatePolicyInput input);
}

// 2. Input DTO
public record CreatePolicyInput(
    String customerId,
    LocalDate effectiveDate,
    List<CoverageDto> coverages
) {}

// 3. Service Implementation
@Service
@Transactional
public class CreatePolicyService implements CreatePolicyUseCase {
    private final PolicyRepository policyRepository;

    public CreatePolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public CreatePolicyOutput execute(CreatePolicyInput input) {
        // Business logic here
    }
}
```

#### 3. Adding a New REST Endpoint

1. Create request/response models in `presentation.rest.model`
2. Add controller method in appropriate controller
3. Add OpenAPI annotations for documentation
4. Write controller tests
5. Test with Swagger UI

Example:
```java
@RestController
@RequestMapping("/api/v1/policies")
@Tag(name = "Policy Management")
public class PolicyController {

    private final CreatePolicyUseCase createPolicyUseCase;

    @PostMapping
    @Operation(summary = "Create a new policy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Policy created"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<PolicyResponse> createPolicy(
            @Valid @RequestBody CreatePolicyRequest request) {

        CreatePolicyInput input = // convert request to input
        CreatePolicyOutput output = createPolicyUseCase.execute(input);
        PolicyResponse response = // convert output to response

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

---

## Coding Standards

### General Principles

1. **SOLID Principles**
   - Single Responsibility
   - Open/Closed
   - Liskov Substitution
   - Interface Segregation
   - Dependency Inversion

2. **Clean Code**
   - Meaningful names
   - Small functions
   - Single level of abstraction
   - No comments (self-documenting code)

3. **DRY (Don't Repeat Yourself)**
   - Extract common logic
   - Use helper methods
   - Create reusable components

### Domain Layer Standards

- **No framework dependencies** (except SLF4J for logging)
- **No JPA annotations** on domain entities
- **Immutable value objects** (use Records)
- **Business rules in entity methods**
- **Self-validating objects** (validate in constructor)
- **Package-private setters** for ORM (if needed)

Example:
```java
public class Policy {
    private Long id;
    private PolicyNumber policyNumber;
    private PolicyStatus status;

    // Constructor enforces invariants
    public Policy(String customerId, LocalDate effectiveDate, List<Coverage> coverages) {
        validateCustomerId(customerId);
        validateEffectiveDate(effectiveDate);
        validateCoverages(coverages);
        // Initialize fields
    }

    // Business method
    public void activate() {
        if (status != PolicyStatus.DRAFT) {
            throw new IllegalStateException("Can only activate DRAFT policies");
        }
        this.status = PolicyStatus.ACTIVE;
    }

    // Only getters are public
    public PolicyStatus getStatus() {
        return status;
    }

    // Setters are package-private for ORM
    void setId(Long id) {
        this.id = id;
    }
}
```

### Application Layer Standards

- **Use case per interface**
- **Input/Output DTOs as Records**
- **@Transactional on service methods**
- **Depend only on domain interfaces (ports)**
- **Convert DTOs ↔ Domain objects**

### Infrastructure Layer Standards

- **Implement repository interfaces from domain**
- **JPA entities separate from domain entities**
- **Mappers for domain ↔ JPA conversion**
- **Handle technical exceptions**

### Presentation Layer Standards

- **Thin controllers** (no business logic)
- **Validation with Bean Validation annotations**
- **OpenAPI annotations for documentation**
- **Proper HTTP status codes**
- **Exception handling with @ControllerAdvice**

### Naming Conventions

| Type | Convention | Example |
|------|-----------|---------|
| Classes | PascalCase | `PolicyController`, `CreatePolicyService` |
| Methods | camelCase | `createPolicy()`, `findById()` |
| Variables | camelCase | `policyNumber`, `customerId` |
| Constants | UPPER_SNAKE_CASE | `MAX_COVERAGE_AMOUNT` |
| Packages | lowercase | `com.insurance.policy.domain.entity` |

### Code Formatting

- **Indentation**: 4 spaces
- **Line length**: 120 characters max
- **Braces**: K&R style (opening brace on same line)
- **Imports**: Organize and remove unused
- **Blank lines**: One between methods

---

## Testing Guidelines

### Test Coverage Requirements

- **Minimum**: 85% (enforced by JaCoCo)
- **Domain Layer**: 95%+
- **Application Layer**: 90%+
- **Infrastructure Layer**: 80%+
- **Presentation Layer**: 85%+

### Testing Best Practices

1. **Follow AAA Pattern**: Arrange, Act, Assert
2. **One assertion per test** (when possible)
3. **Descriptive test names**: `shouldXxxWhenYyy()`
4. **Use test builders** for complex objects
5. **Mock external dependencies only**
6. **TestContainers** for integration tests

Example:
```java
@Test
void shouldCreatePolicyWhenValidDataProvided() {
    // Arrange
    String customerId = "CUST-001";
    LocalDate effectiveDate = LocalDate.now().plusDays(1);
    List<Coverage> coverages = List.of(
        new Coverage("Liability", Money.of(500, "USD"))
    );

    // Act
    Policy policy = new Policy(customerId, effectiveDate, coverages);

    // Assert
    assertNotNull(policy);
    assertEquals(PolicyStatus.DRAFT, policy.getStatus());
    assertEquals(Money.of(500, "USD"), policy.getTotalPremium());
}
```

### Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=PolicyTest

# Specific test method
mvn test -Dtest=PolicyTest#shouldActivatePolicy

# With coverage
mvn clean test jacoco:report

# Skip tests (use sparingly)
mvn clean install -DskipTests
```

---

## Common Tasks

### Task 1: Add a New Field to an Entity

1. Add field to domain entity
2. Update constructor if needed
3. Add getter (and package-private setter for ORM)
4. Update JPA entity
5. Update mapper
6. Update tests
7. Run tests and verify

### Task 2: Add Validation to an Endpoint

1. Add validation annotations to request DTO
```java
public record CreatePolicyRequest(
    @NotBlank String customerId,
    @Future LocalDate effectiveDate,
    @NotEmpty List<CoverageDto> coverages
) {}
```

2. Ensure `@Valid` on controller parameter
3. Add test for validation failure
4. Update OpenAPI documentation

### Task 3: Add a New Query Method to Repository

1. Add method to repository interface (domain.port)
```java
public interface PolicyRepository {
    Optional<Policy> findByCustomerId(String customerId);
}
```

2. Implement in repository adapter
```java
@Override
public Optional<Policy> findByCustomerId(String customerId) {
    return springDataRepository.findByCustomerId(customerId)
        .map(policyMapper::toDomain);
}
```

3. Add query method to Spring Data repository
```java
public interface PolicySpringDataRepository extends JpaRepository<PolicyJpaEntity, Long> {
    Optional<PolicyJpaEntity> findByCustomerId(String customerId);
}
```

4. Write integration test

### Task 4: Handle a New Exception Type

1. Create custom exception in `application.exception`
```java
public class InvalidPolicyException extends RuntimeException {
    public InvalidPolicyException(String message) {
        super(message);
    }
}
```

2. Add handler in `GlobalExceptionHandler`
```java
@ExceptionHandler(InvalidPolicyException.class)
public ResponseEntity<ErrorResponse> handleInvalidPolicy(InvalidPolicyException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse(ex.getMessage()));
}
```

3. Update OpenAPI documentation
4. Write test

### Task 5: Add Logging

Use SLF4J:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePolicyService {
    private static final Logger log = LoggerFactory.getLogger(CreatePolicyService.class);

    public CreatePolicyOutput execute(CreatePolicyInput input) {
        log.info("Creating policy for customer: {}", input.customerId());
        // Business logic
        log.debug("Policy created with number: {}", output.policyNumber());
        return output;
    }
}
```

**Logging Levels**:
- ERROR: Errors that need immediate attention
- WARN: Warning conditions
- INFO: Important business events
- DEBUG: Detailed diagnostic information
- TRACE: Very detailed diagnostic information

---

## Database Management

### Schema Management

The project uses JPA with `spring.jpa.hibernate.ddl-auto=update` in development.

**For Production**: Use Flyway or Liquibase for migrations.

### Viewing Database Schema

```bash
# Connect to database
psql -U postgres -d insurance_policy_db

# List tables
\dt

# Describe table
\d policy
```

### Resetting Database

```bash
# Drop and recreate database
dropdb insurance_policy_db
createdb insurance_policy_db

# Restart application (schema will be recreated)
mvn spring-boot:run
```

---

## Debugging

### Running in Debug Mode

**IntelliJ IDEA**:
1. Right-click on `InsurancePolicyApplication`
2. Select "Debug 'InsurancePolicyApplication'"
3. Set breakpoints as needed

**Command Line**:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

Then attach debugger to port 5005.

### Common Issues

**Issue**: Tests fail with database connection error

**Solution**: Ensure Docker is running (for TestContainers)

**Issue**: Application won't start - port 8080 already in use

**Solution**:
```bash
# Find and kill process
lsof -i :8080
kill -9 <PID>

# Or change port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

**Issue**: ArchUnit tests fail

**Solution**: Review the violation message and fix architectural dependency

---

## Performance Optimization

### Database Query Optimization

1. **Use proper indexes**
2. **Avoid N+1 queries** (use `@EntityGraph` or JOIN FETCH)
3. **Use pagination** for large result sets
4. **Monitor slow queries** with Hibernate logging

### Enable SQL Logging

```properties
# application.properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## Documentation

### Updating OpenAPI Documentation

Add annotations to controller methods:

```java
@Operation(
    summary = "Create a new policy",
    description = "Creates a new insurance policy with coverages"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Policy created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request data")
})
public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
    // Implementation
}
```

Access Swagger UI: `http://localhost:8080/swagger-ui.html`

### Generating JavaDoc

```bash
mvn javadoc:javadoc
```

View at: `target/site/apidocs/index.html`

---

## Version Control

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `test`: Adding or updating tests
- `refactor`: Code refactoring
- `style`: Code style changes
- `chore`: Maintenance tasks

**Example**:
```
feat(policy): add policy cancellation feature

Implement policy cancellation with business rules:
- Can only cancel ACTIVE policies
- Cannot cancel EXPIRED policies

Closes #123
```

### Branching Strategy

- `main`: Production-ready code
- `develop`: Integration branch
- `feature/xxx`: Feature branches
- `bugfix/xxx`: Bug fix branches
- `hotfix/xxx`: Production hotfixes

---

## IDE Configuration

### IntelliJ IDEA

1. **Import Project**: File → Open → Select `pom.xml`
2. **Enable Annotation Processing**: Settings → Build → Compiler → Annotation Processors
3. **Code Style**: Import from `code-style.xml` (if provided)
4. **Enable Java 21 Preview Features**: Project Structure → Project → Language Level → 21 (Preview)

### VS Code

Install extensions:
- Java Extension Pack
- Spring Boot Extension Pack
- Lombok Annotations Support

---

## Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [ArchUnit Documentation](https://www.archunit.org/)

---

## Getting Help

1. Check existing documentation (README, ARCHITECTURE, DOMAIN-MODEL, API)
2. Review Swagger UI for API details
3. Run architecture tests to verify layering
4. Consult the development team
5. Review commit history for similar changes

---

Generated by BMAD Spring Boot Clean Architecture Generator
