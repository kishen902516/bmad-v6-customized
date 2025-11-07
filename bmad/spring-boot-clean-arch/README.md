# Spring Boot Clean Architecture Generator

An intelligent, interactive system for generating Spring Boot 3.x applications with Java 21 that strictly follow Clean Architecture principles and Domain-Driven Design patterns.

**🎯 Now with Git Flow + Test-Driven Development (TDD) Integration!**

## Overview

Unlike Spring Initializr which generates basic boilerplate, this module creates fully-structured, well-architected applications with:

- **🔴🟢🔵 Test-Driven Development (TDD) Enforced** - All features follow Red-Green-Refactor cycle automatically
- **🌿 Git Flow Workflow Integrated** - Automatic feature branches, GitHub issues, and PR management
- **📋 GitHub Projects Tracking** - Every feature tracked with custom TDD phase field
- **Automated ArchUnit Enforcement** - Architecture violations fail the build, not just warnings
- **Complete Test Generation** - Unit, Integration, Contract (Pact), E2E, and Architecture tests generated automatically
- **Consumer-Driven Contract Testing** - Pact integration for robust microservices contract verification
- **Enterprise Resilience Patterns** - Circuit Breaker, Retry, Bulkhead, Rate Limiter, and Time Limiter with Resilience4j
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

**Prerequisites:**
- Git installed and configured
- GitHub CLI (`gh`) installed and authenticated (for full Git Flow + GitHub Projects integration)
- GitHub account (optional, for issue tracking and project boards)

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
- Contract tests (Pact consumer-driven contracts), ArchUnit tests
- Commands: `*generate-tests`, `*configure-archunit`, `*add-test-scenario`

**4. Architecture Validator Agent**
- Enforces Clean Architecture rules with ArchUnit
- Detects violations and suggests fixes
- Validates layer dependencies and naming conventions
- Commands: `*validate`, `*analyze-dependencies`, `*fix-violations`

### Workflows (9)

**All workflows now follow TDD (Red-Green-Refactor) and Git Flow automatically!**

**Core Workflows:**
1. **bootstrap-project** - Initialize new Clean Architecture project with Git Flow and GitHub Projects setup
2. **add-entity** - Add domain entity following TDD with automatic GitHub issue and feature branch
3. **add-use-case** - Add application use case following TDD with mocked dependencies

**Feature Workflows:**
4. **add-rest-endpoint** - Add REST API endpoint following TDD with Pact contract testing
5. **add-repository** - Add repository with interface in domain and JPA implementation
6. **scaffold-feature** - Add complete feature across all layers with conversational guidance
7. **apply-pattern** - Apply design patterns (CQRS, Event Sourcing, Saga, etc.)

**Utility Workflows:**
8. **validate-architecture** - Run complete ArchUnit validation suite
9. **generate-documentation** - Auto-generate README, architecture diagrams, and API docs

## 🔴🟢🔵 TDD + Git Flow Workflow

Every feature automatically follows this professional development workflow:

```
1. 📋 GitHub Issue Created
   - Structured template with acceptance criteria
   - Labels: type:entity, layer:domain, tdd
   - Added to GitHub Project board
   - Custom fields: TDD Phase, Layer, Component Type

2. 🌿 Feature Branch Created
   - Branch: feature/{issue-number}-{description}
   - Checked out from develop branch
   - Ready for TDD cycle

3. 🔴 RED Phase: Write Failing Tests
   - Generate comprehensive test suite (~20-40 tests)
   - Unit tests with JUnit 5 + Mockito
   - Integration tests with TestContainers
   - Contract tests with Pact
   - Run tests → All FAIL (expected!)
   - Commit: "test: add failing tests (RED) #{issue}"
   - Update issue label: tdd:red

4. 🟢 GREEN Phase: Make Tests Pass
   - Generate implementation code
   - Domain entities (pure, no framework)
   - Application use cases
   - Infrastructure adapters
   - Presentation controllers
   - Run tests → All PASS!
   - Coverage: 90%+ guaranteed
   - Commit: "feat: implement feature (GREEN) #{issue}"
   - Update issue label: tdd:green

5. 🔵 REFACTOR Phase: Improve Code
   - Analyze for improvements
   - Extract methods/value objects
   - Improve naming and structure
   - Run tests → Still PASSING!
   - Commit: "refactor: improve design (REFACTOR) #{issue}"
   - Update issue label: tdd:refactor

6. ✅ Validation & Pull Request
   - Run ArchUnit validation
   - Run complete test suite
   - Create PR to develop branch
   - Update issue: tdd:complete, status:in-review
   - Move issue in Project to "In Review"

7. 🎯 Merge & Cleanup
   - CI checks pass
   - PR merged (squash merge)
   - Issue closed automatically
   - Feature branch deleted
   - Switch back to develop

Result: High-quality, well-tested code with full traceability!
```

### TDD Guarantees

✅ **Tests written BEFORE code** - Can't skip TDD, it's enforced
✅ **High test coverage** - 90%+ automatically
✅ **Clean Architecture** - ArchUnit validates every feature
✅ **Full traceability** - Issue → Branch → Commits → PR → Done
✅ **GitHub Projects tracking** - Visual progress through TDD phases
✅ **Pact contracts** - Microservices contract testing built-in

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
- **Git Flow initialization** (optional)
  - Initialize git repository
  - Create main + develop branches
  - Setup commit message templates
- **GitHub integration** (optional)
  - Create GitHub repository
  - Setup GitHub Project board with custom fields
  - Link repository to project
- Optional insurance domain examples

### 3. Explore the Generated Project

The agent generates a complete Spring Boot 3.x project with:
- Clean Architecture layer structure
- **Git Flow configured** (main + develop branches)
- **GitHub Project** with TDD tracking
- ArchUnit tests configured
- Maven build configuration with Resilience4j
- **CONTRIBUTING.md** with Git Flow + TDD guidelines
- **docs/TDD-WORKFLOW-GUIDE.md** for developers
- Database configuration
- OpenAPI documentation
- Resilience patterns (Circuit Breaker, Retry, Bulkhead)
- Actuator monitoring endpoints
- Example implementations (if selected)

### 4. Add Features with TDD

All feature additions now follow TDD automatically!

**Add an Entity (with TDD):**
```
*add-entity

→ Creates GitHub issue #42
→ Creates branch: feature/42-add-policy-entity
→ 🔴 Generates failing tests (~40 tests)
→ 🟢 Generates implementation (entity, repository, mapper)
→ 🔵 Suggests refactorings
→ Creates PR to develop
→ Merges and closes issue
```

**Add a Use Case (with TDD):**
```
*add-use-case

→ Creates GitHub issue #43
→ Creates branch: feature/43-add-create-policy-use-case
→ 🔴 Generates use case tests with mocks (~20 tests)
→ 🟢 Generates use case implementation
→ 🔵 Improves orchestration logic
→ Creates PR and merges
```

**Add a REST Endpoint (with TDD + Pact):**
```
*add-rest-endpoint

→ Creates GitHub issue #44
→ Creates branch: feature/44-add-post-policies-endpoint
→ 🔴 Generates MockMvc + Pact tests (~20 tests)
→ 🟢 Generates controller with OpenAPI docs
→ 🔵 Improves error handling
→ Generates Pact contract file
→ Creates PR and merges
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
│   ├── resilience/                   # Resilience pattern templates
│   └── tests/
├── data/                             # Module data
│   ├── maven-templates/
│   ├── archunit-templates/
│   ├── resilience-templates/         # Resilience4j configuration
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
- **Testing:** JUnit 5, Mockito, TestContainers, Pact (Consumer-Driven Contracts)
- **Resilience:** Resilience4j 2.2.0 (Circuit Breaker, Retry, Bulkhead, Rate Limiter, Time Limiter)
- **Database:** PostgreSQL, MySQL, H2 (configurable)
- **API Documentation:** Springdoc OpenAPI
- **BMAD Core:** v6.0.0+

## Resources

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) by Uncle Bob
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/) by Eric Evans
- [ArchUnit Documentation](https://www.archunit.org/)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Pact Testing Guide](https://docs.pact.io/)
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
