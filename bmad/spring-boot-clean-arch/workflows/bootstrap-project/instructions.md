# Bootstrap Project Workflow Instructions

<critical>This workflow creates a new Spring Boot Clean Architecture project from scratch</critical>

<workflow>

<step n="1" goal="Gather project requirements">
<ask>What is your project name? (e.g., "Policy Management System")</ask>
<action>Store project_name</action>

<ask>What is your base package? (e.g., "com.insurance.policy")</ask>
<action>Store base_package</action>
<action>Validate package naming convention (lowercase, dot-separated)</action>

<ask>Brief description of your project domain?</ask>
<action>Store project_description</action>
</step>

<step n="2" goal="Select project scenario and complexity">
<action>Explain scenario options based on module brief</action>

<ask>Which scenario fits your project?
1. Simple CRUD - Minimal layers for straightforward data operations
2. Enterprise Microservice - Full DDD with all layers (Recommended)
3. Complex Domain-Rich - Advanced patterns (CQRS, Event Sourcing)

Choose [1/2/3] or use default: {default_scenario}</ask>

<action>Store selected_scenario</action>
<action>Explain what will be generated for the selected scenario</action>
</step>

<step n="3" goal="Configure database and dependencies">
<ask>Which database will you use?
1. PostgreSQL (Recommended)
2. MySQL
3. H2 (In-memory, for testing/prototypes)

Choose [1/2/3]:</ask>

<action>Store database_choice</action>

<ask>Additional Spring Boot starters needed? (comma-separated or 'none')
Examples: spring-boot-starter-security, spring-boot-starter-validation, spring-boot-starter-actuator</ask>

<action>Store additional_starters</action>
</step>

<step n="4" goal="Configure ArchUnit enforcement">
<action>Explain ArchUnit strictness levels from config: {archunit_strictness}</action>

<ask>Use configured strictness level '{archunit_strictness}' or customize?
1. Use configured: {archunit_strictness}
2. Customize for this project

Choose [1/2]:</ask>

<check if="customize">
  <ask>Select strictness:
  1. Relaxed - Warnings only, doesn't fail build
  2. Standard - Recommended rules, fails on violations
  3. Strict - All rules enforced, zero tolerance

  Choose [1/2/3]:</ask>

  <action>Store project_archunit_strictness</action>
</check>

<check if="use configured">
  <action>Set project_archunit_strictness = {archunit_strictness}</action>
</check>
</step>

<step n="5" goal="Include example domain code">
<check if="{include_examples} == 'yes'">
  <ask>Include insurance domain examples?
  1. Policy Management example (Policy, Coverage, Premium calculation)
  2. Claims Processing example (Claim, Adjudication, Settlement)
  3. Underwriting example (Application, Risk Assessment, Approval)
  4. All examples
  5. None - clean project only

  Choose [1/2/3/4/5]:</ask>

  <action>Store example_choice</action>
</check>

<check if="{include_examples} == 'no'">
  <action>Set example_choice = 'none'</action>
</check>
</step>

<step n="6" goal="Generate Maven project structure">
<action>Create project directory at: {default_output_path}</action>

<action>Generate pom.xml with:
- Spring Boot 3.2+ parent
- Java 21 configuration
- Selected database dependency
- ArchUnit dependency
- JUnit 5, Mockito, TestContainers, REST Assured
- Additional selected starters
</action>

<action>Create Clean Architecture package structure:
- {base_package}/domain/entity
- {base_package}/domain/valueobject
- {base_package}/domain/port (repository interfaces)
- {base_package}/domain/service
- {base_package}/domain/event
- {base_package}/application/usecase
- {base_package}/application/service
- {base_package}/application/dto
- {base_package}/infrastructure/adapter/persistence
- {base_package}/infrastructure/adapter/persistence/entity (JPA entities)
- {base_package}/infrastructure/adapter/external
- {base_package}/infrastructure/config
- {base_package}/presentation/rest
- {base_package}/presentation/rest/model
- {base_package}/presentation/rest/exception
</action>

<action>Create test directory structure mirroring main structure</action>

<action>Generate application.properties with database configuration</action>
<action>Generate application-test.properties for testing</action>
</step>

<step n="7" goal="Generate ArchUnit test configuration">
<action>Create ArchitectureTest.java in test directory</action>

<action>Configure ArchUnit rules based on {project_archunit_strictness}:
- Layer dependency rules
- Naming convention rules
- Annotation rules
- Package structure rules
</action>

<action>Add layer architecture definition for Clean Architecture layers</action>
</step>

<step n="8" goal="Generate Spring Boot main class and configuration">
<action>Create Application.java main class with @SpringBootApplication</action>
<action>Create configuration classes in infrastructure/config:
- DatabaseConfig.java (if needed)
- WebConfig.java for CORS and web configuration
</action>

<action>Create OpenAPI configuration (Springdoc OpenAPI)</action>
</step>

<step n="9" goal="Include example domain code" if="example_choice != 'none'">
<action>Based on {example_choice}, generate appropriate example:
- Policy Management: Policy entity, PolicyRepository, CreatePolicyUseCase, PolicyController
- Claims Processing: Claim entity, ClaimRepository, SubmitClaimUseCase, ClaimController
- Underwriting: Application entity, RiskAssessmentService, UnderwritingUseCase
</action>

<action>For each example, generate:
- Domain entities with business logic
- Value objects as Records
- Repository interface in domain
- Repository implementation in infrastructure
- Use case in application layer
- REST controller in presentation
- Complete test suite (unit, integration, contract, architecture)
</action>
</step>

<step n="10" goal="Generate project documentation">
<action>Create README.md with:
- Project description
- Architecture overview
- How to run
- How to test
- How to add new features
- ArchUnit validation explanation
</action>

<action>Create ARCHITECTURE.md explaining Clean Architecture layers</action>

<action>Create .gitignore for Java/Maven/Spring Boot</action>
</step>

<step n="11" goal="Validate generated project">
<action>Run Maven compilation check (mvn compile)</action>
<action>Run all tests including ArchUnit (mvn test)</action>

<check if="tests fail">
  <action>Report failures and fix issues</action>
  <goto step="11">Re-validate after fixes</goto>
</check>

<check if="tests pass">
  <action>Confirm project successfully generated and validated</action>
</check>
</step>

<step n="12" goal="Present summary and next steps">
<action>Display project summary:
- Project name: {project_name}
- Location: {default_output_path}
- Scenario: {selected_scenario}
- Database: {database_choice}
- ArchUnit strictness: {project_archunit_strictness}
- Examples included: {example_choice}
- Build status: ✅ All tests passing
</action>

<action>Suggest next steps:
1. cd {default_output_path}
2. mvn spring-boot:run (to start the application)
3. Open http://localhost:8080/swagger-ui.html (to see API docs)
4. Use *add-entity to add new domain entities
5. Use *add-use-case to add new use cases
6. Use *add-rest-endpoint to add new API endpoints
</action>

<ask>Would you like to:
1. Add another entity now
2. Add a use case now
3. Exit and explore the generated project

Choose [1/2/3]:</ask>

<check if="1">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-entity/workflow.yaml</invoke-workflow>
</check>

<check if="2">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml</invoke-workflow>
</check>

<check if="3">
  <action>Workflow complete! Project ready for development.</action>
</check>
</step>

</workflow>
