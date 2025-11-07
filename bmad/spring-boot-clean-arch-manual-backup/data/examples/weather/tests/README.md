# Weather Data Management - Test Examples

This directory contains comprehensive test examples for the Weather Data Management domain, demonstrating the complete test pyramid for Clean Architecture applications.

## Test Types Included

### 1. Controller Tests (`WeatherDataControllerTest.java.example`)

**Technology**: @WebMvcTest, MockMvc
**Layer**: Presentation
**Purpose**: Test REST API endpoints without starting the full application context

**Coverage**:
- Request mapping and validation
- Response serialization
- HTTP status codes
- Error handling
- Input validation (missing fields, invalid values)

**Example Test**:
```java
@Test
@DisplayName("Should record weather data and return 201 Created")
void shouldRecordWeatherData() throws Exception {
    // Tests POST /api/weather endpoint with valid request
}
```

### 2. Repository Integration Tests (`WeatherDataRepositoryIntegrationTest.java.example`)

**Technology**: @DataJpaTest, TestContainers, PostgreSQL
**Layer**: Infrastructure
**Purpose**: Test repository implementation with real database

**Coverage**:
- CRUD operations with actual database
- Custom query methods
- Data persistence and retrieval
- Concurrent operations
- Transaction handling

**Key Features**:
- Uses TestContainers for isolated PostgreSQL instance
- Tests against real database (not in-memory H2)
- Verifies database schema and mappings
- Tests domain-to-JPA entity mapping

**Example Test**:
```java
@Test
@DisplayName("Should save and retrieve weather data")
void shouldSaveAndRetrieve() {
    // Tests actual database save and retrieval
}
```

### 3. Resilience4j Integration Tests (`WeatherServiceResilienceTest.java.example`)

**Technology**: Resilience4j (Circuit Breaker, Retry, Rate Limiter, Bulkhead)
**Layer**: Infrastructure (External Service Adapter)
**Purpose**: Test resilience patterns with real Resilience4j configuration

**Patterns Tested**:
1. **Circuit Breaker**: Prevents cascading failures
   - CLOSED → OPEN → HALF_OPEN → CLOSED state transitions
   - Failure rate threshold
   - Fast-fail behavior

2. **Retry**: Handles transient failures
   - Retry on specific exceptions
   - Maximum retry attempts
   - Wait duration between retries

3. **Rate Limiter**: Controls request rate
   - Request per time period limits
   - Rejection of excess requests

4. **Bulkhead**: Isolates thread pools
   - Concurrent call limits
   - Queue saturation handling

5. **Combined Patterns**: Retry + Circuit Breaker + Fallback

**Example Test**:
```java
@Test
@DisplayName("Should open circuit after failure rate threshold exceeded")
void shouldOpenCircuitAfterFailureThreshold() {
    // Tests circuit breaker opening after repeated failures
}
```

### 4. Pact Consumer Tests (`WeatherDataConsumerPactTest.java.example`)

**Technology**: Pact Consumer-Driven Contract Testing
**Layer**: Presentation (API Consumer Perspective)
**Purpose**: Define API contracts from consumer's perspective

**Coverage**:
- Contract definition for API interactions
- Expected request/response formats
- Error scenarios (404, 400, etc.)
- Generates Pact files for provider verification

**Contract Interactions**:
1. Create weather data (POST)
2. Get weather data by ID (GET)
3. Get all weather data (GET)
4. Handle not found (404)

**Example Test**:
```java
@Pact(consumer = "WeatherDataConsumer")
public V4Pact createWeatherDataPact(PactDslWithProvider builder) {
    // Defines contract for creating weather data
}
```

### 5. Pact Provider Tests (`WeatherDataProviderPactTest.java.example`)

**Technology**: Pact Provider Verification
**Layer**: Presentation (API Provider Perspective)
**Purpose**: Verify that provider satisfies consumer contracts

**Coverage**:
- Verifies all Pact contracts from consumers
- State management for different scenarios
- Ensures API compatibility

**Provider States**:
1. "WeatherData can be created"
2. "WeatherData with ID X exists"
3. "WeatherData with ID X does not exist"
4. "Multiple weather data entries exist"

**Example State Handler**:
```java
@State("WeatherData with ID X exists")
void toWeatherDataExists() {
    // Sets up mock data for this state
}
```

## Test Pyramid

```
        /\
       /  \          E2E Tests (Contract/Pact)
      /____\
     /      \        Integration Tests (Repository, Resilience)
    /________\
   /          \      Unit Tests (Domain, Value Objects)
  /____________\     Controller Tests (@WebMvcTest)
```

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories

**Unit Tests Only** (Domain + Value Objects):
```bash
mvn test -Dtest="*Test" -DexcludedGroups="integration,contract"
```

**Controller Tests**:
```bash
mvn test -Dtest="*ControllerTest"
```

**Repository Integration Tests** (requires TestContainers):
```bash
mvn test -Dtest="*RepositoryIntegrationTest"
```

**Resilience Tests**:
```bash
mvn test -Dtest="*ResilienceTest"
```

**Pact Consumer Tests** (generates Pact files):
```bash
mvn test -Dtest="*ConsumerPactTest"
```

**Pact Provider Tests** (verifies Pact files):
```bash
mvn test -Dtest="*ProviderPactTest"
```

## Test Coverage by Layer

| Layer | Test Type | Technology | Example |
|-------|-----------|------------|---------|
| Domain | Unit Tests | JUnit 5 | WeatherDataTest |
| Domain | Unit Tests | JUnit 5 | TemperatureTest |
| Application | Unit Tests | JUnit 5, Mockito | (Service tests) |
| Infrastructure | Integration Tests | @DataJpaTest, TestContainers | WeatherDataRepositoryIntegrationTest |
| Infrastructure | Resilience Tests | Resilience4j | WeatherServiceResilienceTest |
| Presentation | Controller Tests | @WebMvcTest | WeatherDataControllerTest |
| Presentation | Contract Tests | Pact | WeatherDataConsumerPactTest |
| Presentation | Contract Tests | Pact | WeatherDataProviderPactTest |

## Dependencies Required

All test dependencies are included in the generated `pom.xml`:

```xml
<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- TestContainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>

<!-- Pact -->
<dependency>
    <groupId>au.com.dius.pact.consumer</groupId>
    <artifactId>junit5</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>au.com.dius.pact.provider</groupId>
    <artifactId>junit5spring</artifactId>
    <scope>test</scope>
</dependency>

<!-- ArchUnit -->
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <scope>test</scope>
</dependency>
```

## Best Practices Demonstrated

1. **Test Isolation**: Each test is independent and can run in any order
2. **Real Dependencies**: Integration tests use real database (PostgreSQL via TestContainers)
3. **Clear Naming**: Test names clearly describe what is being tested
4. **Arrange-Act-Assert**: All tests follow AAA pattern
5. **Comprehensive Coverage**: Tests cover happy path, edge cases, and error scenarios
6. **Contract Testing**: Pact ensures API compatibility between consumers and providers
7. **Resilience Verification**: Tests verify system behavior under failure conditions

## Next Steps

After adding a new feature:
1. Add unit tests for domain logic
2. Add controller tests for new endpoints
3. Add repository integration tests for new queries
4. Update Pact contracts if API changes
5. Add resilience tests if calling external services
6. Run `mvn test` to verify all tests pass
7. Check ArchUnit tests to ensure architecture compliance

## Resources

- [TestContainers Documentation](https://www.testcontainers.org/)
- [Pact Documentation](https://docs.pact.io/)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
