# Add Entity Workflow Instructions

<critical>This workflow adds a domain entity following Test-Driven Development (TDD) and Git Flow</critical>
<critical>All code follows the Red-Green-Refactor cycle with proper git commits and GitHub issue tracking</critical>
<critical>Communicate with {user_name} in {communication_language} throughout this workflow</critical>

<workflow>

<step n="1" goal="Identify target project and verify git setup">
<check if="target_project_path not provided">
  <ask>Path to your Spring Boot Clean Architecture project?</ask>
  <action>Store target_project_path</action>
  <action>Scan project to detect base_package</action>
</check>

<check if="target_project_path provided">
  <action>Use provided target_project_path</action>
  <action>Confirm base_package is correct</action>
</check>

<action>Validate project structure (check for Clean Architecture layers)</action>

<action>Check if project has git initialized:
- Check for .git directory
- Verify current branch (should be 'develop' for Git Flow)
- Check if GitHub remote is configured
</action>

<check if="not on develop branch">
  <ask>Current branch is {current_branch}. Switch to 'develop' branch?
  [y/n]:</ask>

  <check if="yes">
    <action>git checkout develop</action>
    <action>git pull origin develop (if remote exists)</action>
  </check>
</check>
</step>

<step n="2" goal="Define entity concept">
<ask>What is the entity name? (e.g., "Policy", "Claim", "Customer")</ask>
<action>Store entity_name</action>
<action>Convert to PascalCase for class name</action>

<ask>Brief description of what this entity represents?</ask>
<action>Store entity_description</action>

<ask>Is this entity an Aggregate Root?
An Aggregate Root is the main entity that controls access to other related entities.
Examples: Order (controls OrderItems), Policy (controls Coverages)

[yes/no]:</ask>

<action>Store is_aggregate_root</action>
</step>

<step n="3" goal="Define entity attributes">
<ask>Define entity attributes (one per line, format: "name:type:description"):
Examples:
- id:Long:Unique identifier
- policyNumber:String:Policy number
- effectiveDate:LocalDate:Policy effective date
- premium:Money:Premium amount (Value Object)

Enter attributes (type 'done' when finished):</ask>

<action>Collect attributes iteratively until 'done'</action>
<action>Parse each attribute into name, type, description</action>
<action>Identify which types are Value Objects (custom types, Money, Email, etc.)</action>
<action>Store attribute_list</action>
</step>

<step n="4" goal="Define business rules and validations">
<ask>What business rules should this entity enforce?
Examples:
- Premium cannot be negative
- Effective date must be in the future
- Policy number must be unique
- Status transitions have specific rules

Enter business rules (one per line, type 'done' when finished):</ask>

<action>Collect business_rules iteratively until 'done'</action>
<action>Parse rules to identify validations vs business methods</action>
</step>

<step n="5" goal="Define entity relationships">
<ask>Does this entity have relationships with other entities?
[yes/no]:</ask>

<check if="yes">
  <ask>Define relationships (format: "relatedEntity:type:cardinality"):
  Examples:
  - OrderItem:contains:one-to-many
  - Customer:references:many-to-one
  - Address:embeds:one-to-one

  Enter relationships (type 'done' when finished):</ask>

  <action>Collect relationships iteratively until 'done'</action>
  <action>Store relationship_list</action>
</check>

<check if="no">
  <action>Set relationship_list = empty</action>
</check>
</step>

<step n="6" goal="Generate Value Objects first">
<action>From attribute_list, identify custom Value Objects that need to be created</action>

<check if="value objects needed">
  <action>Load Value Object template from {value_object_template}</action>
  <action for-each="value object in value_objects_needed">
    Generate Value Object using template:
    - Apply template to create Java Record in {base_package}/domain/valueobject/
    - Replace {{VALUE_OBJECT_NAME}} with value object name
    - Replace {{BASE_PACKAGE}} with {base_package}
    - Add compact constructor with validation
    - Include business methods if needed
    - equals/hashCode automatic with Record
  </action>
</check>
</step>

<step n="7" goal="Generate domain entity">
<action>Load domain entity template from {entity_template}</action>
<action>Load TDD guide from {tdd_guide} for reference</action>

<action>Generate entity class using template:
- Apply template and replace placeholders:
  - {{ENTITY_NAME}} → {entity_name}
  - {{BASE_PACKAGE}} → {base_package}
  - {{ATTRIBUTES}} → generated fields from attribute_list
  - {{BUSINESS_METHODS}} → generated methods from business_rules
  - {{JAVADOC}} → entity_description
- Ensure private fields for all attributes
- Constructor with required fields (invariant enforcement)
- Private setters, public getters
- Business methods based on business_rules
- Validation in constructor
- Equals/hashCode based on ID
- NO framework annotations (pure domain)
</action>

<action>Write generated entity to {base_package}/domain/entity/{entity_name}.java</action>
</step>

<step n="8" goal="Generate repository interface">
<action>Load repository interface template from {repository_interface_template}</action>

<action>Generate repository interface using template:
- Apply template and replace placeholders:
  - {{ENTITY_NAME}} → {entity_name}
  - {{BASE_PACKAGE}} → {base_package}
  - {{CUSTOM_METHODS}} → query methods based on entity attributes
- Include standard methods: save(), findById(), findAll(), delete()
- Add custom query methods based on entity attributes
- Methods return domain entities (not JPA entities)
- NO Spring annotations (pure interface)
</action>

<action>Write generated interface to {base_package}/domain/port/{entity_name}Repository.java</action>
</step>

<step n="9" goal="Generate JPA entity and repository implementation">
<action>Load JPA entity template from {jpa_entity_template}</action>

<action>Generate JPA entity using template:
- Apply template and replace placeholders:
  - {{ENTITY_NAME}} → {entity_name}
  - {{BASE_PACKAGE}} → {base_package}
  - {{FIELDS}} → JPA field mappings from attribute_list
  - {{RELATIONSHIPS}} → JPA relationship annotations
- Include @Entity, @Table annotations
- Add @Id and @GeneratedValue for ID field
- Apply JPA annotations for relationships
</action>

<action>Write JPA entity to {base_package}/infrastructure/adapter/persistence/entity/{entity_name}JpaEntity.java</action>

<action>Load repository implementation template from {repository_impl_template}</action>

<action>Generate repository implementation using template:
- Apply template with replacements
- Add @Repository annotation
- Implement domain repository interface
- Include Spring Data JPA repository internally
- Add mapper methods for domain ↔ JPA entity conversion
</action>

<action>Write to {base_package}/infrastructure/adapter/persistence/{entity_name}RepositoryImpl.java</action>

<action>Load Spring Data JPA repository template from {jpa_repository_template}</action>
<action>Generate Spring Data JPA repository interface</action>
<action>Write to {base_package}/infrastructure/adapter/persistence/{entity_name}JpaRepository.java</action>
</step>

<step n="10" goal="Generate unit tests for domain entity">
<action>Load Test Engineer agent context from {test_engineer_agent}</action>
<action>Load entity test template from {entity_test_template}</action>
<action>Reference TDD guide from {tdd_guide} for test patterns</action>

<action>Generate unit tests using template and following Test Engineer guidelines:
- Apply template and replace placeholders:
  - {{ENTITY_NAME}} → {entity_name}
  - {{BASE_PACKAGE}} → {base_package}
  - {{TEST_CASES}} → generated from business_rules
- Cover constructor validation (invalid inputs throw exceptions)
- Test business method behavior
- Verify invariant enforcement
- Test equals/hashCode contract
- Include edge cases
- Use JUnit 5, parameterized tests for comprehensive coverage
</action>

<action>Write generated tests to test/{base_package}/domain/entity/{entity_name}Test.java</action>
</step>

<step n="11" goal="Generate integration tests for repository">
<action>Load Test Engineer agent context from {test_engineer_agent}</action>
<action>Load repository test template from {repository_test_template}</action>
<action>Reference TDD guide from {tdd_guide} for integration test patterns</action>

<action>Generate integration tests using template and Test Engineer guidelines:
- Apply template and replace placeholders:
  - {{ENTITY_NAME}} → {entity_name}
  - {{BASE_PACKAGE}} → {base_package}
  - {{REPOSITORY_NAME}} → {entity_name}Repository
- Include @SpringBootTest or @DataJpaTest
- Add TestContainers for database
- Test save, findById, findAll, delete operations
- Test custom query methods
- Add database cleanup between tests
</action>

<action>Write tests to test/{base_package}/infrastructure/{entity_name}RepositoryIntegrationTest.java</action>
</step>

<step n="12" goal="Run ArchUnit validation">
<action>Load Architecture Validator agent context from {arch_validator_agent}</action>
<action>Reference patterns folder {patterns_folder} for architecture rules</action>

<action>Following Architecture Validator agent guidelines, run ArchUnit tests to validate:
- Entity is in correct package (domain.entity)
- Entity has no framework annotations
- Repository interface in domain.port
- Repository implementation in infrastructure
- Proper dependency direction
- Clean Architecture layer boundaries respected
</action>

<check if="violations found">
  <action>Report violations with fix suggestions from agent knowledge</action>
  <action>Fix violations</action>
  <goto step="12">Re-validate after fixes</goto>
</check>
</step>

<step n="13" goal="Run all tests">
<action>Execute: mvn test -Dtest={entity_name}*</action>

<check if="tests fail">
  <action>Report failures</action>
  <action>Fix issues</action>
  <goto step="13">Re-test after fixes</goto>
</check>

<check if="tests pass">
  <action>Confirm all tests passing</action>
</check>
</step>

<step n="14" goal="Present summary and next steps">
<action>Display entity creation summary to {user_name}:
- Entity: {entity_name}
- Location: {base_package}/domain/entity/{entity_name}.java
- Repository: {entity_name}Repository interface created
- Repository Implementation: JPA implementation created
- Value Objects: {list of created value objects}
- Tests: Unit tests ✅ Integration tests ✅ Architecture tests ✅
</action>

<action>Suggest next steps to {user_name}:
1. Add use case to interact with {entity_name} (*add-use-case)
2. Add REST endpoint to expose {entity_name} (*add-rest-endpoint)
3. Add another related entity (*add-entity)
4. Validate full architecture (*validate-architecture)
</action>

<ask>Would you like to:
1. Add a use case for {entity_name}
2. Add REST endpoint for {entity_name}
3. Add another entity
4. Exit

Choose [1/2/3/4]:</ask>

<check if="1">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml</invoke-workflow>
  <action>Pass entity_name as context</action>
</check>

<check if="2">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml</invoke-workflow>
  <action>Pass entity_name as context</action>
</check>

<check if="3">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-entity/workflow.yaml</invoke-workflow>
</check>

<check if="4">
  <action>Entity creation complete!</action>
</check>
</step>

</workflow>
