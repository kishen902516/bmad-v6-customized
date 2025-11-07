# Weather API Service

A Spring Boot application managing weather data using Clean Architecture principles.

## Overview

This service provides a RESTful API for recording and retrieving weather data for different locations. It demonstrates Clean Architecture with clear separation of concerns across domain, application, infrastructure, and presentation layers.

## Architecture

The project follows Clean Architecture with the following layers:

- **Domain Layer**: Pure business logic, entities, value objects, and repository interfaces
- **Application Layer**: Use cases and application services that orchestrate domain logic
- **Infrastructure Layer**: Implementation details (database, external APIs, messaging)
- **Presentation Layer**: REST API controllers and DTOs

### Key Features

- Spring Boot 3.2+ with Java 21
- Clean Architecture enforcement with ArchUnit
- PostgreSQL database with JPA/Hibernate
- Complete test pyramid (Unit, Integration, Architecture)
- OpenAPI documentation with Swagger UI
- Resilience4j patterns (Circuit Breaker, Retry, Bulkhead)
- Spring Boot Actuator for monitoring
- Validation with Jakarta Bean Validation

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- PostgreSQL 14+ (or use Docker)

## Getting Started

### 1. Setup Database

#### Using Docker:
```bash
docker run --name weather-postgres \
  -e POSTGRES_DB=weatherdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:14
```

#### Manual Setup:
```sql
CREATE DATABASE weatherdb;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE weatherdb TO postgres;
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access API Documentation

Open your browser and navigate to:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## API Endpoints

### Record Weather Data
```bash
POST /api/v1/weather
Content-Type: application/json

{
  "city": "London",
  "country": "GB",
  "latitude": 51.5074,
  "longitude": -0.1278,
  "temperatureValue": 20.5,
  "temperatureUnit": "CELSIUS",
  "humidity": 75,
  "description": "Partly cloudy",
  "windSpeed": 15.5
}
```

### Get Weather Data by ID
```bash
GET /api/v1/weather/{id}
```

### Get Latest Weather Data for a City
```bash
GET /api/v1/weather/city/{city}
```

### Health Check
```bash
GET /api/v1/weather/health
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories

#### Unit Tests
```bash
mvn test -Dtest="*Test"
```

#### Architecture Tests
```bash
mvn test -Dtest="ArchitectureTest"
```

## Architecture Tests

The project includes ArchUnit tests that enforce architectural rules:

- Layer dependency rules (Clean Architecture)
- Naming convention rules
- Annotation rules
- Circular dependency detection

These tests fail the build if architectural violations are detected.

## Project Structure

```
weather-api-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/kishen/weather/
│   │   │       ├── domain/                    # Domain Layer
│   │   │       │   ├── entity/               # Domain entities
│   │   │       │   ├── valueobject/          # Value objects
│   │   │       │   ├── port/                 # Repository interfaces
│   │   │       │   ├── service/              # Domain services
│   │   │       │   └── event/                # Domain events
│   │   │       ├── application/               # Application Layer
│   │   │       │   ├── usecase/              # Use case interfaces
│   │   │       │   ├── service/              # Use case implementations
│   │   │       │   └── dto/                  # Application DTOs
│   │   │       ├── infrastructure/            # Infrastructure Layer
│   │   │       │   ├── adapter/
│   │   │       │   │   ├── persistence/      # Database adapters
│   │   │       │   │   │   └── entity/       # JPA entities
│   │   │       │   │   └── external/         # External service adapters
│   │   │       │   └── config/               # Configuration classes
│   │   │       └── presentation/              # Presentation Layer
│   │   │           └── rest/                 # REST controllers
│   │   │               ├── model/            # Request/Response DTOs
│   │   │               └── exception/        # Exception handlers
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       │   └── com/kishen/weather/
│       │       ├── ArchitectureTest.java     # Architecture tests
│       │       ├── domain/                   # Domain unit tests
│       │       ├── application/              # Application tests
│       │       └── presentation/             # Controller tests
│       └── resources/
│           └── application-test.properties
├── docs/                                      # Additional documentation
├── pom.xml
└── README.md
```

## Adding New Features

### 1. Add a New Entity

1. Create the domain entity in `domain/entity/`
2. Create value objects in `domain/valueobject/`
3. Create repository interface in `domain/port/`
4. Implement repository in `infrastructure/adapter/persistence/`
5. Create JPA entity in `infrastructure/adapter/persistence/entity/`

### 2. Add a New Use Case

1. Create use case interface in `application/usecase/`
2. Implement use case in `application/service/`
3. Add tests in `test/java/.../application/`

### 3. Add a New REST Endpoint

1. Create request/response DTOs in `presentation/rest/model/`
2. Create controller in `presentation/rest/`
3. Add integration tests

## Configuration

### Application Properties

Main configuration: `src/main/resources/application.properties`
Test configuration: `src/test/resources/application-test.properties`

### Database Configuration

Update the following properties in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/weatherdb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Actuator Endpoints

Actuator is enabled at `/actuator`. Available endpoints:
- `/actuator/health` - Health check
- `/actuator/metrics` - Metrics
- `/actuator/info` - Application info

## Monitoring

The application exposes Prometheus metrics at `/actuator/prometheus` for monitoring with Prometheus and Grafana.

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Java 21** - Programming language with modern features
- **PostgreSQL** - Primary database
- **Spring Data JPA** - Data access layer
- **Hibernate** - ORM
- **ArchUnit** - Architecture testing
- **TestContainers** - Integration testing with containers
- **Springdoc OpenAPI** - API documentation
- **Resilience4j** - Resilience patterns
- **Spring Boot Actuator** - Production-ready features
- **Jakarta Bean Validation** - Input validation

## Contributing

1. Follow Clean Architecture principles
2. Write tests for all new features
3. Ensure all ArchUnit tests pass
4. Update documentation as needed

## License

Apache 2.0

## Author

Kishen Sivalingam
