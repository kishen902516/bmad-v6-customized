# Spring Boot Clean Architecture Generator

An intelligent, interactive system for generating Spring Boot 3.x applications with Java 21 that strictly follow Clean Architecture principles and Domain-Driven Design patterns.

## Overview

Unlike Spring Initializr which generates basic boilerplate, this module creates fully-structured, well-architected applications with:

- **Automated ArchUnit Enforcement** - Architecture violations fail the build, not just warnings
- **Complete Test Generation** - Unit, Integration, Contract, E2E, and Architecture tests generated automatically
- **Intelligent Conversational Guidance** - Claude Code Skills enable interactive generation that teaches as it builds
- **Progressive Complexity** - From simple CRUD to complex domain-rich applications with CQRS and Event Sourcing
- **Java 21 Optimization** - Records for Value Objects, Virtual Threads for scalability, Sealed Classes for domain modeling

## Installation

```bash
bmad install spring-boot-clean-arch
```

During installation, you'll configure:
- **Projects Output Path** - Where generated Spring Boot projects will be saved
- **Default Scenario** - Simple CRUD, Enterprise Microservice, or Complex Domain-Rich
- **Example Domains** - Include insurance industry examples (Policy Management, Claims Processing, Underwriting)
- **ArchUnit Strictness** - Relaxed, Standard, or Strict enforcement

## Components

### Agents (4)

**1. Spring Architecture Agent** (Primary Agent)
- Master orchestrator and architectural consultant
- Guides project initialization and architectural decisions
- Explains Clean Architecture and DDD principles
- Commands: `*bootstrap-project`, `*add-entity`, `*add-use-case`, `*validate-architecture`

**2. Code Generator Agent**
- Generates domain entities, repositories, controllers
- Leverages Java 21 features (Records, Sealed Classes, Virtual Threads)
- Creates OpenAPI-documented REST APIs
- Commands: `*generate-entity`, `*generate-repository`, `*generate-controller`

**3. Test Engineer Agent**
- Generates complete test suites across all layers
- Unit tests (JUnit 5 + Mockito), Integration tests (TestContainers)
- Contract tests (REST Assured), ArchUnit tests
- Commands: `*generate-tests`, `*configure-archunit`, `*add-test-scenario`

**4. Architecture Validator Agent**
- Enforces Clean Architecture rules with ArchUnit
- Detects violations and suggests fixes
- Validates layer dependencies and naming conventions
- Commands: `*validate`, `*analyze-dependencies`, `*fix-violations`

### Workflows (9)

**Core Workflows:**
1. **bootstrap-project** - Initialize new Clean Architecture project with scenario selection
2. **add-entity** - Add domain entity with complete layer support and synchronized tests
3. **add-use-case** - Add application service use case with orchestration logic

**Feature Workflows:**
4. **add-rest-endpoint** - Add REST API endpoint with OpenAPI and contract tests
5. **add-repository** - Add repository with interface in domain and JPA implementation
6. **scaffold-feature** - Add complete feature across all layers with conversational guidance
7. **apply-pattern** - Apply design patterns (CQRS, Event Sourcing, Saga, etc.)

**Utility Workflows:**
8. **validate-architecture** - Run complete ArchUnit validation suite
9. **generate-documentation** - Auto-generate README, architecture diagrams, and API docs

## Quick Start

### 1. Load the Spring Architecture Agent

```
agent spring-architect
```

### 2. Bootstrap a New Project

```
*bootstrap-project
```

The agent will guide you through:
- Project name and base package
- Scenario selection (Simple CRUD, Enterprise, Complex)
- Database choice (PostgreSQL, MySQL, H2)
- ArchUnit strictness level
- Optional insurance domain examples

### 3. Explore the Generated Project

The agent generates a complete Spring Boot 3.x project with:
- Clean Architecture layer structure
- ArchUnit tests configured
- Maven build configuration
- Database configuration
- OpenAPI documentation
- Example implementations (if selected)

### 4. Add Features Interactively

**Add an Entity:**
```
*add-entity
```

**Add a Use Case:**
```
*add-use-case
```

**Add a REST Endpoint:**
```
*add-rest-endpoint
```

**Scaffold a Complete Feature:**
```
*scaffold-feature
```

### 5. Validate Architecture

```
*validate-architecture
```

Runs ArchUnit tests to ensure:
- Layer dependencies are correct
- Naming conventions are followed
- No circular dependencies
- Annotation usage is compliant

## Module Structure

```
spring-boot-clean-arch/
├── agents/                           # Agent definitions
│   ├── spring-architect.agent.yaml
│   ├── code-generator.agent.yaml
│   ├── test-engineer.agent.yaml
│   └── arch-validator.agent.yaml
├── workflows/                        # Workflow definitions
│   ├── bootstrap-project/
│   ├── add-entity/
│   ├── add-use-case/
│   ├── add-rest-endpoint/
│   ├── add-repository/
│   ├── scaffold-feature/
│   ├── apply-pattern/
│   ├── validate-architecture/
│   └── generate-documentation/
├── templates/                        # Code generation templates
│   ├── entity/
│   ├── usecase/
│   ├── controller/
│   └── tests/
├── data/                             # Module data
│   ├── maven-templates/
│   ├── archunit-templates/
│   ├── examples/                     # Insurance domain examples
│   │   ├── policy-management/
│   │   ├── claims-processing/
│   │   └── underwriting/
│   └── patterns/                     # Pattern templates
├── _module-installer/
│   └── install-config.yaml
└── README.md
```

## Configuration

Module configuration is stored in `bmad/spring-boot-clean-arch/config.yaml`

Key settings:
- **projects_output_path** - Where generated projects are saved
- **default_scenario** - Default complexity (simple-crud, enterprise, complex)
- **include_examples** - Whether to include insurance examples
- **archunit_strictness** - Validation strictness (relaxed, standard, strict)
- **module_data_path** - Internal path for templates and data
- **module_version** - Current module version

## Examples

### Example 1: Generate a Policy Management Microservice

```
agent spring-architect
*bootstrap-project
```

When prompted:
- Project name: "Policy Management System"
- Base package: "com.insurance.policy"
- Scenario: Enterprise Microservice
- Database: PostgreSQL
- Include examples: Yes → Policy Management

The agent generates a complete microservice with:
- Policy entity (domain layer)
- PolicyRepository (interface in domain, JPA impl in infrastructure)
- CreatePolicyUseCase (application layer)
- PolicyController (REST API with OpenAPI docs)
- Complete test suite (90%+ coverage)
- ArchUnit validation (passes build)

### Example 2: Add Claims Processing Feature

```
*scaffold-feature
```

Describe: "I want users to be able to submit insurance claims and track their status"

The agent:
1. Analyzes requirements
2. Proposes implementation plan (Claim entity, SubmitClaimUseCase, ClaimController)
3. Generates all components across layers
4. Creates synchronized tests
5. Validates architecture
6. Documents the feature

## Development Roadmap

### Phase 1: MVP (Current - Weeks 1-3)
- ✅ Spring Architecture Agent
- ✅ Code Generator Agent
- ✅ Test Engineer Agent
- ✅ Architecture Validator Agent
- ✅ All 9 workflows defined
- ⏳ Template implementations (maven, entity, usecase, controller, tests)
- ⏳ Insurance domain examples
- ⏳ ArchUnit rule templates

### Phase 2: Enhancement (Weeks 4-7)
- Advanced DDD patterns (Aggregates, Domain Events)
- Enhanced OpenAPI documentation generation
- Contract testing with REST Assured
- Additional design patterns (Factory, Strategy, CQRS basics)
- Multiple database support

### Phase 3: Polish (Weeks 8-13)
- Claude Code Skills deep integration
- Complex Domain-Rich scenario with Event Sourcing
- Living documentation generation
- Performance optimizations
- Video demonstrations and tutorials

## Contributing

To extend this module:

1. **Add new agent:** Use `create-agent` workflow
2. **Add new workflow:** Use `create-workflow` workflow
3. **Add templates:** Place in `templates/` directory
4. **Add examples:** Place in `data/examples/` directory
5. **Test thoroughly:** Ensure ArchUnit compliance
6. **Document:** Update README and workflow docs

## Technical Stack

- **Spring Boot:** 3.2+
- **Java:** 21
- **Build Tool:** Maven 3.9+
- **Architecture Testing:** ArchUnit 1.2+
- **Testing:** JUnit 5, Mockito, TestContainers, REST Assured
- **Database:** PostgreSQL, MySQL, H2 (configurable)
- **API Documentation:** Springdoc OpenAPI
- **BMAD Core:** v6.0.0+

## Resources

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) by Uncle Bob
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/) by Eric Evans
- [ArchUnit Documentation](https://www.archunit.org/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Java 21 Features](https://openjdk.org/projects/jdk/21/)

## Author

Created by Kishen Sivalingam on 2025-11-05

## License

Part of the BMAD Method ecosystem

---

**Module Version:** 1.0.0
**BMAD Version:** 6.0.0+
**Status:** Ready for Development (Phase 1 - Template Implementation)
