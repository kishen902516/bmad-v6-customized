# Architecture Documentation

## Clean Architecture Overview

This project implements Clean Architecture (also known as Hexagonal Architecture or Ports and Adapters). The key principle is the **Dependency Rule**: dependencies point inward, with the domain at the center.

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│                   (REST Controllers, DTOs)                   │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              Infrastructure Layer                       │ │
│  │     (Database, External APIs, Messaging)               │ │
│  │                                                         │ │
│  │  ┌──────────────────────────────────────────────────┐ │ │
│  │  │           Application Layer                       │ │ │
│  │  │      (Use Cases, Application Services)           │ │ │
│  │  │                                                   │ │ │
│  │  │  ┌────────────────────────────────────────────┐ │ │ │
│  │  │  │        Domain Layer                         │ │ │ │
│  │  │  │  (Entities, Value Objects, Ports)          │ │ │ │
│  │  │  │                                             │ │ │ │
│  │  │  │  Pure Business Logic                        │ │ │ │
│  │  │  │  No Framework Dependencies                  │ │ │ │
│  │  │  └────────────────────────────────────────────┘ │ │ │
│  │  └──────────────────────────────────────────────────┘ │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Layers

### 1. Domain Layer (`com.kishen.weather.domain`)

**Purpose**: Contains the core business logic and rules.

**Characteristics**:
- Framework-agnostic (no Spring, JPA, or other framework dependencies)
- Contains entities, value objects, domain services, and domain events
- Defines repository interfaces (ports) but not implementations
- No dependencies on other layers

**Components**:

#### Entities (`domain/entity/`)
```java
public class WeatherData {
    private UUID id;
    private Location location;
    private Temperature temperature;
    private Integer humidity;

    // Business logic methods
    public boolean isStale() { ... }
    public WeatherSeverity getSeverity() { ... }
}
```

**Key Points**:
- Contains identity (UUID)
- Mutable state with business logic
- Validates business rules
- Independent of persistence framework

#### Value Objects (`domain/valueobject/`)
```java
public record Temperature(BigDecimal value, TemperatureUnit unit) {
    // Validation in constructor
    public Temperature {
        if (value.compareTo(absoluteZero) < 0) {
            throw new IllegalArgumentException(...);
        }
    }

    // Business logic
    public Temperature convertTo(TemperatureUnit targetUnit) { ... }
}
```

**Key Points**:
- Immutable (using Java records)
- Equality by value, not identity
- Self-validating
- Contains related business logic

#### Ports (`domain/port/`)
```java
public interface WeatherDataRepository {
    WeatherData save(WeatherData weatherData);
    Optional<WeatherData> findById(UUID id);
    List<WeatherData> findByCity(String city);
}
```

**Key Points**:
- Defines what the domain needs
- Implementation in infrastructure layer
- Follows Dependency Inversion Principle

### 2. Application Layer (`com.kishen.weather.application`)

**Purpose**: Orchestrates domain objects to perform use cases.

**Characteristics**:
- Implements business use cases
- Coordinates domain objects
- Manages transactions
- Does NOT contain business logic (that's in domain)
- Depends only on domain layer

**Components**:

#### Use Case Interfaces (`application/usecase/`)
```java
public interface RecordWeatherDataUseCase {
    WeatherData execute(RecordWeatherDataInput input);
}
```

#### Use Case Implementations (`application/service/`)
```java
@Service
public class RecordWeatherDataService implements RecordWeatherDataUseCase {
    private final WeatherDataRepository repository;

    @Transactional
    public WeatherData execute(RecordWeatherDataInput input) {
        // Create domain objects
        Location location = new Location(...);
        Temperature temperature = new Temperature(...);
        WeatherData weatherData = new WeatherData(...);

        // Persist through port
        return repository.save(weatherData);
    }
}
```

**Key Points**:
- Thin orchestration layer
- Transaction boundaries
- No business logic (delegates to domain)

### 3. Infrastructure Layer (`com.kishen.weather.infrastructure`)

**Purpose**: Provides implementations for technical concerns.

**Characteristics**:
- Implements domain ports (adapters)
- Contains framework-specific code
- Handles persistence, external services, messaging
- Depends on domain and application layers

**Components**:

#### JPA Entities (`infrastructure/adapter/persistence/entity/`)
```java
@Entity
@Table(name = "weather_data")
public class WeatherDataJpaEntity {
    @Id
    private UUID id;

    @Column(name = "temperature_value")
    private BigDecimal temperatureValue;

    // JPA-specific annotations and mappings
}
```

**Key Points**:
- Separate from domain entities
- Contains JPA annotations
- Maps to database schema

#### Repository Implementation (`infrastructure/adapter/persistence/`)
```java
@Repository
public class WeatherDataRepositoryImpl implements WeatherDataRepository {
    private final WeatherDataJpaRepository jpaRepository;

    public WeatherData save(WeatherData weatherData) {
        WeatherDataJpaEntity jpaEntity = toJpaEntity(weatherData);
        WeatherDataJpaEntity saved = jpaRepository.save(jpaEntity);
        return toDomainEntity(saved);
    }

    // Mapping methods between domain and JPA entities
}
```

**Key Points**:
- Implements domain port
- Translates between domain and JPA entities
- Adapter pattern

#### Configuration (`infrastructure/config/`)
- Spring configuration classes
- Database configuration
- Web configuration
- OpenAPI configuration

### 4. Presentation Layer (`com.kishen.weather.presentation`)

**Purpose**: Handles external communication (REST API).

**Characteristics**:
- REST controllers
- Request/Response DTOs
- Input validation
- Exception handling
- Depends on application layer

**Components**:

#### Controllers (`presentation/rest/`)
```java
@RestController
@RequestMapping("/api/v1/weather")
public class WeatherDataController {
    private final RecordWeatherDataUseCase recordWeatherDataUseCase;

    @PostMapping
    public ResponseEntity<WeatherDataResponse> recordWeatherData(
            @Valid @RequestBody WeatherDataRequest request) {

        // Map request to use case input
        RecordWeatherDataInput input = new RecordWeatherDataInput(...);

        // Execute use case
        WeatherData result = recordWeatherDataUseCase.execute(input);

        // Map domain to response
        WeatherDataResponse response = WeatherDataResponse.fromDomain(result);

        return ResponseEntity.status(CREATED).body(response);
    }
}
```

**Key Points**:
- Thin presentation layer
- Validates input
- Maps between DTOs and domain objects
- Delegates to use cases

#### DTOs (`presentation/rest/model/`)
```java
public record WeatherDataRequest(
    @NotBlank String city,
    @NotNull Double latitude,
    @NotNull BigDecimal temperatureValue,
    // ...
) {}

public record WeatherDataResponse(
    UUID id,
    String city,
    BigDecimal temperatureValue,
    // ...
) {
    public static WeatherDataResponse fromDomain(WeatherData weatherData) {
        // Mapping logic
    }
}
```

## Dependency Flow

```
Presentation → Application → Domain
       ↑           ↑
Infrastructure ────┘
```

**Key Principle**: Dependencies point inward. The domain layer has NO dependencies on outer layers.

## Design Patterns

### 1. Repository Pattern
- **Domain Port**: Interface defined in domain layer
- **Infrastructure Adapter**: Implementation in infrastructure layer
- **Benefit**: Domain is independent of persistence technology

### 2. Use Case Pattern
- Each use case represents a single business operation
- Clear entry points for business logic
- Easy to test in isolation

### 3. Adapter Pattern
- Infrastructure adapters implement domain ports
- Translate between domain and external concerns
- Example: WeatherDataRepositoryImpl adapts Spring Data JPA to domain port

### 4. Value Object Pattern
- Immutable objects with equality by value
- Self-validating
- Contains related business logic
- Example: Temperature, Location

### 5. Entity Pattern
- Objects with identity
- Mutable state with business rules
- Contains business logic
- Example: WeatherData

## Testing Strategy

### 1. Unit Tests
- Test domain entities and value objects
- No framework dependencies
- Fast execution
- Example: `WeatherDataTest`, `TemperatureTest`

### 2. Use Case Tests
- Test application services
- Mock repository dependencies
- Verify orchestration logic

### 3. Integration Tests
- Test with real database (TestContainers)
- Verify persistence layer
- Test repository implementations

### 4. Architecture Tests (ArchUnit)
- Enforce layer dependencies
- Verify naming conventions
- Detect circular dependencies
- Example: `ArchitectureTest`

### 5. API Tests
- Test REST controllers
- Verify request/response mapping
- Test validation

## Benefits of This Architecture

### 1. Independence
- Business logic independent of frameworks
- Easy to change persistence technology
- Easy to change UI/API technology

### 2. Testability
- Domain logic easily tested without frameworks
- Use cases tested with mocks
- Clear boundaries for integration tests

### 3. Flexibility
- Easy to add new adapters (GraphQL, gRPC, messaging)
- Easy to swap implementations (different databases)
- Business logic remains stable

### 4. Maintainability
- Clear separation of concerns
- Easy to locate and modify code
- ArchUnit tests prevent architectural erosion

### 5. Domain-Driven Design
- Domain model reflects business reality
- Business logic in one place (domain layer)
- Rich domain model, not anemic

## Common Mistakes to Avoid

### 1. Domain Layer Violations
❌ **Wrong**: Domain entities with JPA annotations
```java
@Entity  // NO! Domain entities should be framework-agnostic
public class WeatherData { ... }
```

✅ **Correct**: Separate JPA entities in infrastructure
```java
// Domain entity (domain layer)
public class WeatherData { ... }

// JPA entity (infrastructure layer)
@Entity
public class WeatherDataJpaEntity { ... }
```

### 2. Business Logic in Services
❌ **Wrong**: Business logic in application service
```java
@Service
public class RecordWeatherDataService {
    public WeatherData execute(Input input) {
        // NO! Business logic belongs in domain
        if (input.windSpeed() > 50) {
            throw new SevereWeatherException();
        }
    }
}
```

✅ **Correct**: Business logic in domain entity
```java
public class WeatherData {
    public WeatherSeverity getSeverity() {
        if (windSpeed > 50) return SEVERE;
        if (windSpeed > 30) return MODERATE;
        return NORMAL;
    }
}
```

### 3. Layer Violations
❌ **Wrong**: Domain depending on infrastructure
```java
// In domain layer - NO!
import org.springframework.stereotype.Service;

public class DomainService { ... }
```

✅ **Correct**: Domain has no framework dependencies
```java
// In domain layer - Pure Java
public class DomainService { ... }
```

## Further Reading

- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design by Eric Evans](https://www.domainlanguage.com/ddd/)
