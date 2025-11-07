# Clean Architecture Documentation

## Overview

This document explains the Clean Architecture implementation in the Insurance Policy Service.

## What is Clean Architecture?

Clean Architecture (also known as Hexagonal Architecture or Ports & Adapters) is an architectural pattern that emphasizes:

1. **Independence**: Business logic is independent of frameworks, databases, and UIs
2. **Testability**: Business rules can be tested without external dependencies
3. **Separation of Concerns**: Each layer has a specific responsibility
4. **Dependency Rule**: Dependencies point inward (toward the domain)

## The Dependency Rule

> Source code dependencies must point only inward, toward higher-level policies.

```
Infrastructure → Application → Domain
Presentation   → Application → Domain
```

- **Domain** depends on nothing (pure Java)
- **Application** depends only on Domain
- **Infrastructure** depends on Domain and Application
- **Presentation** depends on Application (not directly on Infrastructure)

## Layers in Detail

### 1. Domain Layer (Core Business Logic)

**Package**: `com.insurance.policy.domain`

**Purpose**: Contains pure business logic with no framework dependencies.

**Components**:
- **Entities** (`domain.entity`): Business objects with identity and lifecycle
  - Example: `Policy.java`
  - Contains business rules and invariants
  - No JPA annotations
  - No framework dependencies

- **Value Objects** (`domain.valueobject`): Immutable objects defined by their attributes
  - Example: `PolicyNumber`, `Money`, `Coverage`
  - Implemented as Java Records
  - Self-validating
  - No identity, only values matter

- **Repository Interfaces** (`domain.port`): Contracts for data persistence
  - Example: `PolicyRepository.java`
  - Define what operations are needed
  - Implementation is in Infrastructure layer

- **Domain Services** (`domain.service`): Complex business logic spanning multiple entities
  - Business operations that don't naturally fit in a single entity

- **Domain Events** (`domain.event`): Events representing business occurrences
  - Example: `PolicyCreatedEvent`

**Key Principles**:
- No Spring annotations
- No JPA annotations
- No framework imports (except logging)
- Pure Java with business rules

### 2. Application Layer (Use Cases)

**Package**: `com.insurance.policy.application`

**Purpose**: Orchestrates domain objects to fulfill use cases.

**Components**:
- **Use Case Interfaces** (`application.usecase`): Define what the application can do
  - Example: `CreatePolicyUseCase`
  - One interface per use case
  - Input/Output pattern

- **Service Implementations** (`application.service`): Implement use cases
  - Example: `CreatePolicyService`
  - Orchestrate domain objects
  - Manage transactions with `@Transactional`
  - Convert between DTOs and domain objects

- **DTOs** (`application.dto`): Data transfer objects
  - Example: `CreatePolicyInput`, `CreatePolicyOutput`
  - Decouple external data structures from domain
  - Include validation annotations

**Key Principles**:
- Minimal framework dependencies (only `@Service`, `@Transactional`)
- No direct dependency on Infrastructure
- Depend only on Domain interfaces (ports)

### 3. Infrastructure Layer (Technical Implementation)

**Package**: `com.insurance.policy.infrastructure`

**Purpose**: Implements technical concerns and external dependencies.

**Components**:
- **Repository Adapters** (`infrastructure.adapter.persistence`): Implement domain repository interfaces
  - Example: `PolicyRepositoryAdapter`
  - Uses Spring Data JPA
  - Maps between domain entities and JPA entities

- **JPA Entities** (`infrastructure.adapter.persistence.entity`): Database mapping
  - Example: `PolicyJpaEntity`
  - Contains JPA annotations
  - Separate from domain entities

- **External Service Clients** (`infrastructure.adapter.external`): Integrate with external systems
  - REST clients
  - Message queues
  - Third-party APIs

- **Configuration** (`infrastructure.config`): Spring configuration
  - Example: `WebConfig`, `OpenApiConfig`
  - Database configuration
  - Bean definitions

**Key Principles**:
- Contains all framework-specific code
- Implements domain ports (interfaces)
- Never expose infrastructure details to other layers

### 4. Presentation Layer (External Interface)

**Package**: `com.insurance.policy.presentation`

**Purpose**: Handles external communication (REST APIs, GraphQL, etc.)

**Components**:
- **Controllers** (`presentation.rest`): REST API endpoints
  - Example: `PolicyController`
  - Annotated with `@RestController`, `@RequestMapping`
  - Delegate to use cases
  - Convert between API models and DTOs

- **API Models** (`presentation.rest.model`): Request/Response objects
  - Example: `CreatePolicyRequest`, `PolicyResponse`
  - Separate from DTOs and domain objects
  - Include OpenAPI annotations

- **Exception Handlers** (`presentation.rest.exception`): Global error handling
  - Example: `GlobalExceptionHandler`
  - Standardized error responses
  - Logging

**Key Principles**:
- No business logic
- Thin controllers that delegate to use cases
- Never depend on Infrastructure directly

## Data Flow Example

### Creating a Policy

```
1. Client → POST /api/v1/policies
   ↓
2. PolicyController (Presentation)
   - Receives CreatePolicyRequest
   - Converts to CreatePolicyInput (DTO)
   ↓
3. CreatePolicyService (Application)
   - Validates input
   - Converts DTOs to domain objects (Coverage)
   - Creates Policy entity (Domain)
   - Calls PolicyRepository.save()
   ↓
4. PolicyRepositoryAdapter (Infrastructure)
   - Converts Policy to PolicyJpaEntity
   - Saves to database via Spring Data JPA
   - Converts back to Policy
   ↓
5. CreatePolicyService (Application)
   - Receives saved Policy
   - Converts to CreatePolicyOutput
   ↓
6. PolicyController (Presentation)
   - Converts CreatePolicyOutput to PolicyResponse
   - Returns HTTP 201 with PolicyResponse
```

## Benefits of This Architecture

### 1. Testability
- Domain logic can be tested without any framework
- Use cases can be tested with mocked repositories
- Each layer can be tested independently

### 2. Maintainability
- Changes to infrastructure don't affect business logic
- Clear boundaries between concerns
- Easy to understand where code belongs

### 3. Flexibility
- Easy to swap databases (PostgreSQL → MongoDB)
- Easy to add new interfaces (REST → GraphQL)
- Easy to change frameworks (Spring → Micronaut)

### 4. Team Collaboration
- Teams can work on different layers independently
- Clear contracts (interfaces) between layers
- Reduced merge conflicts

## Architecture Enforcement

The project uses **ArchUnit** to enforce architecture rules automatically:

- Domain layer cannot depend on other layers
- Domain layer has no Spring/JPA annotations
- Repository interfaces must be in `domain.port`
- Controllers must be in `presentation` package
- No circular dependencies

Run architecture tests:
```bash
mvn test -Dtest=ArchitectureTest
```

## Naming Conventions

### Domain Layer
- Entities: `Policy`, `Claim` (noun, no suffix)
- Value Objects: `PolicyNumber`, `Money` (noun, descriptive)
- Repository Interfaces: `PolicyRepository` (noun + Repository)

### Application Layer
- Use Case Interfaces: `CreatePolicyUseCase` (Verb + Noun + UseCase)
- Service Implementations: `CreatePolicyService` (Verb + Noun + Service)
- Input DTOs: `CreatePolicyInput` (Verb + Noun + Input)
- Output DTOs: `CreatePolicyOutput` (Verb + Noun + Output)

### Infrastructure Layer
- Repository Adapters: `PolicyRepositoryAdapter` (Noun + RepositoryAdapter)
- JPA Entities: `PolicyJpaEntity` (Noun + JpaEntity)

### Presentation Layer
- Controllers: `PolicyController` (Noun + Controller)
- Requests: `CreatePolicyRequest` (Verb + Noun + Request)
- Responses: `PolicyResponse` (Noun + Response)

## Common Patterns

### 1. Repository Pattern (Port/Adapter)
```java
// Domain (Port)
public interface PolicyRepository {
    Policy save(Policy policy);
    Optional<Policy> findById(Long id);
}

// Infrastructure (Adapter)
@Component
public class PolicyRepositoryAdapter implements PolicyRepository {
    private final PolicyJpaRepository jpaRepository;
    // Implementation...
}
```

### 2. Use Case Pattern
```java
// Application (Interface)
public interface CreatePolicyUseCase {
    CreatePolicyOutput execute(CreatePolicyInput input);
}

// Application (Implementation)
@Service
@Transactional
public class CreatePolicyService implements CreatePolicyUseCase {
    // Implementation...
}
```

### 3. Value Objects as Records
```java
public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
}
```

## Anti-Patterns to Avoid

1. **Anemic Domain Model**: Don't put all logic in services. Entities should contain business rules.

2. **Layer Violation**: Don't skip layers (e.g., Controller → Repository directly)

3. **Framework in Domain**: Don't use Spring/JPA annotations in domain entities

4. **DTO Everywhere**: Don't pass DTOs to domain layer. Convert to domain objects.

5. **God Services**: Keep services focused on one use case

## Further Reading

- **Clean Architecture** by Robert C. Martin
- **Domain-Driven Design** by Eric Evans
- **Implementing Domain-Driven Design** by Vaughn Vernon
- **Hexagonal Architecture** by Alistair Cockburn

## Questions?

Refer to the example code in this project:
- `Policy.java` - Domain entity example
- `CreatePolicyService.java` - Use case example
- `PolicyRepositoryAdapter.java` - Repository adapter example
- `PolicyController.java` - Controller example
