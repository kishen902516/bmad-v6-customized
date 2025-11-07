# Test Generation Guide

This guide explains how the bootstrap-project workflow generates comprehensive tests for Clean Architecture projects.

## Test Templates Available

The following test templates are available in `templates/tests/`:

### 1. Controller Tests (`controller-web-mvc-test.java.template`)
**Technology**: @WebMvcTest, MockMvc
**Purpose**: Test REST API endpoints without full application context
**What it tests**:
- Request mapping and validation
- Response serialization
- HTTP status codes
- Error handling

### 2. Repository Integration Tests (`repository-integration-test.java.template`)
**Technology**: @DataJpaTest, TestContainers, PostgreSQL
**Purpose**: Test repository with real database
**What it tests**:
- CRUD operations
- Custom queries
- Data persistence
- Transaction handling

### 3. Resilience Integration Tests (`resilience-integration-test.java.template`)
**Technology**: Resilience4j
**Purpose**: Test resilience patterns
**What it tests**:
- Circuit Breaker (CLOSED/OPEN/HALF_OPEN states)
- Retry mechanism
- Rate Limiter
- Bulkhead
- Combined patterns

### 4. Pact Consumer Tests (`pact-consumer-test.java.template`)
**Technology**: Pact
**Purpose**: Define API contracts from consumer perspective
**What it tests**:
- Contract definitions
- Expected request/response formats
- Error scenarios

### 5. Pact Provider Tests (`pact-provider-test.java.template`)
**Technology**: Pact
**Purpose**: Verify provider satisfies consumer contracts
**What it tests**:
- Contract verification
- Provider state management
- API compatibility

### 6. Unit Tests
- Entity tests (`entity-unit-test.java.template`)
- Use case tests (`use-case-unit-test.java.template`)

## Example Test Files

Complete test examples are available in `data/examples/{domain}/tests/`:

### Weather Example Tests
Located in `data/examples/weather/tests/`:

1. `WeatherDataControllerTest.java.example`
   - Complete @WebMvcTest example
   - Tests all REST endpoints (POST, GET, DELETE)
   - Validation tests
   - Error handling tests

2. `WeatherDataRepositoryIntegrationTest.java.example`
   - TestContainers PostgreSQL setup
   - CRUD operation tests
   - Custom query tests (findByCity, findRecent)

3. `WeatherServiceResilienceTest.java.example`
   - Circuit Breaker tests (all state transitions)
   - Retry mechanism tests
   - Combined pattern tests
   - Fallback tests

4. `WeatherDataConsumerPactTest.java.example`
   - Consumer contract definitions
   - POST /api/weather contract
   - GET /api/weather/{id} contract
   - 404 error handling contract

5. `WeatherDataProviderPactTest.java.example`
   - Provider verification setup
   - State handlers
   - MockMvc integration

## How to Generate Tests

### Step 9 of Bootstrap Workflow

When generating examples (step 9), the workflow should:

1. **Generate source code** for the chosen example domain
2. **Generate comprehensive test suite** using templates:
   - Copy domain entity unit tests
   - Apply controller test template
   - Apply repository integration test template
   - Apply Pact consumer/provider test templates
   - Apply resilience test template (if applicable)
3. **Copy example test files** from `data/examples/{domain}/tests/`

### Manual Generation

To manually add tests to an existing project:

#### Add Controller Tests
```bash
# Use controller-web-mvc-test.java.template
# Fill in placeholders:
# - {{entity_name}}
# - {{base_package}}
# - {{endpoint_path}}
# - {{create_use_case_name}}
# - {{get_use_case_name}}
# - etc.
```

#### Add Repository Integration Tests
```bash
# Use repository-integration-test.java.template
# Requires TestContainers configured in pom.xml
# Fill in placeholders for entity-specific tests
```

#### Add Resilience Tests
```bash
# Use resilience-integration-test.java.template
# Requires Resilience4j configured
# Fill in service-specific placeholders
```

#### Add Pact Tests
```bash
# Use pact-consumer-test.java.template
# Use pact-provider-test.java.template
# Fill in API contract details
```

## Test Pyramid Coverage

After generating all tests, projects will have:

```
        /\
       /  \          E2E Tests (Pact Contracts)
      /____\
     /      \        Integration Tests (Repository + Resilience)
    /________\
   /          \      Unit Tests (Domain + Value Objects)
  /____________\     Controller Tests (@WebMvcTest)
```

### Test Count by Type

Example projects include approximately:

- **Unit Tests**: 20+ tests
  - Domain entity tests: 12 tests
  - Value object tests: 8 tests

- **Controller Tests**: 10+ tests
  - Endpoint tests with various scenarios
  - Validation tests
  - Error handling tests

- **Repository Integration Tests**: 8+ tests
  - CRUD operations
  - Custom queries

- **Resilience Tests**: 15+ tests
  - Circuit Breaker: 6 tests
  - Retry: 4 tests
  - Rate Limiter: 2 tests
  - Bulkhead: 2 tests
  - Combined: 1+ tests

- **Pact Tests**: 8+ tests
  - Consumer: 4 tests
  - Provider: 4 state handlers

- **Architecture Tests**: 15 tests
  - Already generated in step 7

**Total**: 70+ tests covering all layers

## Running Tests

### All Tests
```bash
mvn test
```

### By Category
```bash
# Controller tests only
mvn test -Dtest="*ControllerTest"

# Repository integration tests
mvn test -Dtest="*RepositoryIntegrationTest"

# Resilience tests
mvn test -Dtest="*ResilienceTest"

# Pact consumer tests
mvn test -Dtest="*ConsumerPactTest"

# Pact provider tests
mvn test -Dtest="*ProviderPactTest"

# Architecture tests
mvn test -Dtest="ArchitectureTest"
```

### With Coverage
```bash
mvn clean test jacoco:report
# View report: target/site/jacoco/index.html
```

## Dependencies Required

All test dependencies are included in generated `pom.xml`:

- spring-boot-starter-test (JUnit 5, Mockito, AssertJ)
- TestContainers (junit-jupiter, postgresql)
- Pact (consumer junit5, provider junit5spring)
- Resilience4j (spring-boot3, reactor)
- ArchUnit (junit5)

## Best Practices

1. **Test Isolation**: Each test is independent
2. **Real Dependencies**: Integration tests use real databases
3. **Clear Naming**: Test names describe what is being tested
4. **AAA Pattern**: Arrange-Act-Assert structure
5. **Comprehensive Coverage**: Happy path + edge cases + errors
6. **Contract Testing**: API compatibility verification
7. **Resilience Verification**: Failure handling tests

## Troubleshooting

### TestContainers Issues
```bash
# Ensure Docker is running
docker ps

# Increase Docker memory if needed
# Docker Desktop → Settings → Resources → Memory: 4GB+
```

### Pact File Generation
```bash
# Pact files generated in target/pacts/
# Run consumer tests to generate:
mvn test -Dtest="*ConsumerPactTest"

# Copy pact files to provider project for verification
cp target/pacts/*.json ../provider-project/target/pacts/
```

### Resilience4j Configuration
```bash
# Ensure application-test.properties has resilience config
# Or use @TestPropertySource in test classes
```

## Next Steps

After generating tests:

1. Run all tests: `mvn test`
2. Check coverage: `mvn jacoco:report`
3. Fix any failing tests
4. Add project-specific test cases
5. Integrate into CI/CD pipeline

## Resources

- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [TestContainers Documentation](https://www.testcontainers.org/)
- [Pact Documentation](https://docs.pact.io/)
- [Resilience4j Guide](https://resilience4j.readme.io/)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)
