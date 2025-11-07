# Maven Templates - Spring Boot Clean Architecture

This directory contains Maven project templates for generating Spring Boot microservices following Clean Architecture principles.

## Overview

The Maven templates support three architectural scenarios with progressively increasing complexity:

1. **Simple CRUD** - Basic microservices with standard CRUD operations
2. **Enterprise** - Production-ready services with resilience patterns, contract testing, and monitoring
3. **Complex** - Domain-rich services with CQRS, Event Sourcing, Kafka, Redis, and distributed tracing

## Template Files

### POM Templates

| Template | Scenario | Description |
|----------|----------|-------------|
| `pom-base.xml` | Base | Minimal dependencies for Clean Architecture |
| `pom-simple-crud.xml` | Simple CRUD | Basic CRUD with OpenAPI, ArchUnit, TestContainers |
| `pom-enterprise.xml` | Enterprise | Adds Resilience4j, Pact, Actuator, REST Assured |
| `pom-complex.xml` | Complex | Adds Kafka, Redis, Axon (CQRS), Distributed Tracing |

### Application Properties Templates

| Template | Environment | Description |
|----------|-------------|-------------|
| `application.properties` | Default | Base configuration with placeholders |
| `application-test.properties` | Test | H2 in-memory, TestContainers overrides |
| `application-dev.properties` | Development | Debug logging, SQL logging, DevTools |
| `application-prod.properties` | Production | Environment variables, security hardened |

## POM Template Comparison

### Common Features (All Templates)

- **Spring Boot 3.2.0** with Java 21
- **Clean Architecture** structure enforced
- **Spring Web** for REST APIs
- **Spring Data JPA** for persistence
- **Bean Validation** for input validation
- **OpenAPI (SpringDoc)** for API documentation
- **ArchUnit** for architecture testing
- **TestContainers** for integration testing
- **JUnit 5** with Spring Boot Test

### Simple CRUD Scenario

**Use Case:** Basic microservices with CRUD operations

**Additional Features:**
- REST Assured for API testing
- JaCoCo with 80% coverage threshold
- Minimal dependencies for fast startup

**Ideal For:**
- Small internal services
- Proof of concepts
- Simple data management services

**Maven Command:**
```bash
mvn clean install
mvn spring-boot:run
```

### Enterprise Scenario

**Use Case:** Production-ready microservices with fault tolerance

**Additional Features:**
- **Resilience4j** (Circuit Breaker, Retry, Bulkhead, Rate Limiter)
- **Pact** for consumer-driven contract testing
- **Spring Actuator** for health checks and metrics
- **Lombok** for reducing boilerplate (optional)
- JaCoCo with 85% coverage threshold
- REST Assured for comprehensive API testing

**Ideal For:**
- Production microservices
- Services requiring fault tolerance
- Microservice architectures with service-to-service communication
- Teams practicing contract-driven development

**Maven Commands:**
```bash
mvn clean install
mvn spring-boot:run -Dspring.profiles.active=dev
```

**Pact Testing:**
```bash
# Generate Pact contracts (consumer tests)
mvn test -Dtest=*PactConsumerTest

# Verify contracts (provider tests)
mvn test -Dtest=*PactProviderTest
```

### Complex Scenario

**Use Case:** Domain-rich microservices with event-driven architecture

**Additional Features:**
- **Spring Kafka** for event streaming
- **Spring Data Redis** for caching
- **Axon Framework** for CQRS & Event Sourcing (optional)
- **Micrometer Prometheus** for metrics
- **Zipkin** for distributed tracing
- **Pitest** for mutation testing
- **Awaitility** for async testing
- JaCoCo with 90% coverage threshold
- All Enterprise features included

**Ideal For:**
- Complex domain models (DDD)
- Event-driven microservices
- CQRS architectures
- High-scale distributed systems
- Services requiring advanced observability

**Maven Commands:**
```bash
mvn clean install
mvn spring-boot:run -Dspring.profiles.active=dev

# Run with mutation testing
mvn org.pitest:pitest-maven:mutationCoverage
```

**Docker Compose for Dependencies:**
```yaml
# docker-compose.yml (Complex scenario)
version: '3.8'
services:
  postgres:
    image: postgres:16
    ports: ["5432:5432"]
    environment:
      POSTGRES_PASSWORD: postgres

  redis:
    image: redis:7-alpine
    ports: ["6379:6379"]

  kafka:
    image: confluentinc/cp-kafka:latest
    ports: ["9092:9092"]

  zipkin:
    image: openzipkin/zipkin
    ports: ["9411:9411"]
```

## Template Variables

All templates use the following placeholders:

| Variable | Description | Example |
|----------|-------------|---------|
| `{{group_id}}` | Maven group ID | `com.insurance` |
| `{{artifact_id}}` | Maven artifact ID | `policy-service` |
| `{{project_name}}` | Project name | `Policy Management Service` |
| `{{project_description}}` | Project description | `Insurance policy management microservice` |
| `{{database_type}}` | Database type | `postgresql`, `mysql`, `h2` |
| `{{database_dependency}}` | Database driver dependency XML | PostgreSQL/MySQL/H2 dependency |
| `{{testcontainers_database_dependency}}` | TestContainers database dependency | PostgreSQL/MySQL TestContainers |
| `{{database_configuration}}` | Database connection properties | JDBC URL, username, password |
| `{{hibernate_dialect}}` | Hibernate dialect | `org.hibernate.dialect.PostgreSQLDialect` |
| `{{base_package}}` | Base Java package | `com.insurance.policy` |

## Application Properties Profiles

### Default (`application.properties`)

Base configuration used across all environments. Sets defaults that can be overridden by profile-specific files.

**Key Settings:**
- Server port: 8080
- JPA DDL: validate (safe for production)
- OpenAPI: enabled
- Logging: INFO root, DEBUG for application
- Virtual Threads: enabled

### Test (`application-test.properties`)

Optimized for unit and integration tests.

**Key Settings:**
- H2 in-memory database (overridden by TestContainers)
- JPA DDL: create-drop (clean state per test)
- SQL logging: enabled
- OpenAPI: disabled
- Logging: WARN root, DEBUG for application

**Usage:**
```java
@SpringBootTest
@ActiveProfiles("test")
class PolicyServiceTest {
    // Tests automatically use application-test.properties
}
```

### Development (`application-dev.properties`)

Developer-friendly configuration for local development.

**Key Settings:**
- JPA DDL: update (auto-schema updates)
- SQL logging: enabled with formatting
- OpenAPI Swagger UI: enabled with try-it-out
- Actuator: all endpoints exposed
- DevTools: enabled (hot reload)
- Caching: disabled (always fresh data)
- Detailed logging with colors

**Usage:**
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Production (`application-prod.properties`)

Hardened configuration for production deployment.

**Key Settings:**
- Environment variable based configuration
- JPA DDL: validate only (no schema changes)
- SQL logging: disabled
- OpenAPI: disabled (security)
- Actuator: limited endpoints (health, metrics, prometheus)
- Connection pooling optimized
- File-based logging with rotation
- Resilience patterns tuned for production
- Redis caching enabled
- Distributed tracing with sampling

**Required Environment Variables:**
```bash
DATABASE_URL=jdbc:postgresql://prod-db:5432/policydb
DATABASE_USERNAME=policy_user
DATABASE_PASSWORD=<secure-password>
REDIS_HOST=redis.production.local
REDIS_PORT=6379
REDIS_PASSWORD=<secure-password>
SECURITY_USERNAME=admin
SECURITY_PASSWORD=<secure-password>
ZIPKIN_URL=http://zipkin.production.local:9411/api/v2/spans
```

**Usage:**
```bash
# With environment variables
export DATABASE_URL=jdbc:postgresql://...
export DATABASE_PASSWORD=...
java -jar app.jar --spring.profiles.active=prod

# Or via command line
java -jar app.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://... \
  --spring.datasource.password=...
```

## Dependency Management

### Core Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.2.0 | Framework foundation |
| Java | 21 | Language version (LTS) |
| ArchUnit | 1.2.1 | Architecture testing |
| TestContainers | 1.19.3 | Integration testing |
| SpringDoc OpenAPI | 2.3.0 | API documentation |

### Enterprise Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Resilience4j | 2.2.0 | Fault tolerance patterns |
| Pact | 4.6.4 | Contract testing |
| REST Assured | Managed | API testing |

### Complex Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Axon Framework | 4.9.1 | CQRS & Event Sourcing |
| Spring Kafka | Managed | Event streaming |
| Spring Data Redis | Managed | Caching |
| Micrometer | Managed | Metrics |
| Zipkin | Managed | Distributed tracing |
| Pitest | 1.15.3 | Mutation testing |

## Java 21 Features

All templates leverage Java 21 features:

1. **Virtual Threads** - Enabled by default for improved scalability
2. **Records** - Used for DTOs and Value Objects
3. **Pattern Matching** - Enhanced switch expressions
4. **Sealed Classes** - Domain type hierarchies
5. **Preview Features** - Enabled via compiler args

**Compiler Configuration:**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>21</source>
        <target>21</target>
        <compilerArgs>
            <arg>--enable-preview</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

## Database Support

### PostgreSQL (Recommended)

**Dependency:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/policydb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**TestContainers:**
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
```

### MySQL

**Dependency:**
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Configuration:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/policydb
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### H2 (Testing Only)

**Dependency:**
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Configuration:**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

## Testing Strategy

### Test Pyramid

Each scenario enforces a comprehensive test pyramid:

1. **Unit Tests** - Domain logic, use cases (fast, many)
2. **Integration Tests** - Repository layer with TestContainers (medium)
3. **Contract Tests** - API contracts with Pact (Enterprise+)
4. **Architecture Tests** - ArchUnit validation (all scenarios)
5. **E2E Tests** - Complete user journeys (few, critical paths)

### Code Coverage Thresholds

- **Simple CRUD:** 80% line coverage
- **Enterprise:** 85% line coverage
- **Complex:** 90% line coverage + mutation testing

### Running Tests

```bash
# All tests
mvn clean test

# Unit tests only
mvn test -Dtest=*Test

# Integration tests only
mvn test -Dtest=*IntegrationTest

# Architecture tests
mvn test -Dtest=ArchitectureTest

# Coverage report
mvn clean test jacoco:report
# Report: target/site/jacoco/index.html

# Mutation testing (Complex scenario)
mvn org.pitest:pitest-maven:mutationCoverage
# Report: target/pit-reports/index.html
```

## Maven Lifecycle

### Clean Architecture Build Phases

```bash
# 1. Clean previous builds
mvn clean

# 2. Compile domain, application, infrastructure layers
mvn compile

# 3. Run all tests (unit, integration, architecture)
mvn test

# 4. Package as executable JAR
mvn package

# 5. Install to local repository
mvn install

# 6. Run application
mvn spring-boot:run
```

### Recommended Build Command

```bash
# Full build with tests
mvn clean install

# Fast build (skip tests)
mvn clean install -DskipTests

# Build with specific profile
mvn clean install -Dspring.profiles.active=dev
```

## Migration Guide

### Upgrading Between Scenarios

**Simple → Enterprise:**
1. Replace `pom-simple-crud.xml` with `pom-enterprise.xml`
2. Add Resilience4j configuration to `application-dev.properties`
3. Add Pact test templates
4. Update coverage threshold to 85%

**Enterprise → Complex:**
1. Replace `pom-enterprise.xml` with `pom-complex.xml`
2. Add Kafka and Redis configuration
3. Add event-driven patterns
4. Configure distributed tracing
5. Update coverage threshold to 90%

## Troubleshooting

### Common Issues

**Issue: Tests fail with "Port already in use"**
```bash
# Solution: TestContainers uses random ports automatically
# Check for zombie containers:
docker ps -a
docker rm -f <container-id>
```

**Issue: ArchUnit tests fail after adding new layer**
```bash
# Solution: Update ArchUnit rules to recognize new layer
# See: src/test/java/.../ArchitectureTest.java
```

**Issue: JaCoCo coverage threshold not met**
```bash
# Solution: Add more tests or adjust threshold in pom.xml
<minimum>0.80</minimum> <!-- Lower if needed -->
```

**Issue: Pact contract verification fails**
```bash
# Solution: Update provider to match consumer contract
# Check: target/pacts/*.json for expected contract
```

## Best Practices

1. **Start Simple** - Use Simple CRUD for MVP, upgrade as needed
2. **Profile-Based Config** - Use profiles (dev, test, prod) consistently
3. **Environment Variables** - Never commit secrets, use env vars in production
4. **Test Pyramid** - Maintain balance: many unit, some integration, few E2E
5. **Architecture Testing** - Run ArchUnit on every build to enforce boundaries
6. **Code Coverage** - Meet thresholds but focus on meaningful tests
7. **Contract Testing** - Use Pact for microservice communication
8. **Observability** - Enable metrics and tracing in Enterprise/Complex scenarios

## See Also

- [Spring Boot Clean Architecture Module README](../../README.md)
- [Database Configuration Templates](../../data/maven-templates/database-configs.yaml)
- [Template Variables Reference](../README.md#template-variables)
- [Example Projects](../../data/examples/)

---

**Generated by:** BMad Builder
**Version:** 6.0.0
**Last Updated:** 2025-11-06
