# Add Entity Workflow Instructions

<critical>This workflow adds a domain entity following Test-Driven Development (TDD) and Git Flow</critical>
<critical>All code follows the Red-Green-Refactor cycle with proper git commits and GitHub issue tracking</critical>

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
  <action for-each="value object in value_objects_needed">
    Create Value Object as Java Record in {base_package}/domain/valueobject/
    - Compact constructor with validation
    - Business methods if needed
    - Proper equals/hashCode (automatic with Record)
  </action>
</check>
</step>

<step n="7" goal="Generate domain entity">
<action>Create {entity_name}.java in {base_package}/domain/entity/</action>

<action>Generate entity class with:
- Private fields for all attributes
- Constructor with required fields (invariant enforcement)
- Private setters, public getters
- Business methods based on business_rules
- Validation in constructor
- Equals/hashCode based on ID
- NO framework annotations (pure domain)
</action>

<action>Add JavaDoc with entity_description and usage examples</action>
</step>

<step n="8" goal="Generate repository interface">
<action>Create {entity_name}Repository.java interface in {base_package}/domain/port/</action>

<action>Generate repository interface with:
- Standard methods: save(), findById(), findAll(), delete()
- Custom query methods based on entity attributes
- Methods return domain entities (not JPA entities)
- NO Spring annotations (pure interface)
</action>
</step>

<step n="9" goal="Generate JPA entity and repository implementation">
<action>Create {entity_name}JpaEntity.java in {base_package}/infrastructure/adapter/persistence/entity/</action>

<action>Generate JPA entity with:
- @Entity, @Table annotations
- @Id and @GeneratedValue for ID field
- JPA annotations for relationships
- Field mappings
</action>

<action>Create {entity_name}RepositoryImpl.java in {base_package}/infrastructure/adapter/persistence/</action>

<action>Generate repository implementation with:
- @Repository annotation
- Implements domain repository interface
- Uses Spring Data JPA repository internally
- Maps between domain entity and JPA entity
- Mapper methods for conversion
</action>

<action>Create Spring Data JPA repository interface if needed</action>
</step>

<step n="10" goal="Generate unit tests for domain entity">
<action>Invoke Test Engineer Agent to generate unit tests</action>

<action>Create {entity_name}Test.java in test/{base_package}/domain/entity/</action>

<action>Generate unit tests covering:
- Constructor validation (invalid inputs throw exceptions)
- Business method behavior
- Invariant enforcement
- Equals/hashCode contract
- Edge cases
</action>

<action>Use JUnit 5, parameterized tests for comprehensive coverage</action>
</step>

<step n="11" goal="Generate integration tests for repository">
<action>Invoke Test Engineer Agent to generate integration tests</action>

<action>Create {entity_name}RepositoryIntegrationTest.java in test/{base_package}/infrastructure/</action>

<action>Generate integration tests with:
- @SpringBootTest or @DataJpaTest
- TestContainers for database
- Test save, findById, findAll, delete operations
- Test custom query methods
- Database cleanup between tests
</action>
</step>

<step n="12" goal="Run ArchUnit validation">
<action>Invoke Architecture Validator Agent</action>

<action>Run ArchUnit tests to validate:
- Entity is in correct package (domain.entity)
- Entity has no framework annotations
- Repository interface in domain.port
- Repository implementation in infrastructure
- Proper dependency direction
</action>

<check if="violations found">
  <action>Report violations with fix suggestions</action>
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
<action>Display entity creation summary:
- Entity: {entity_name}
- Location: {base_package}/domain/entity/{entity_name}.java
- Repository: {entity_name}Repository interface created
- Repository Implementation: JPA implementation created
- Value Objects: {list of created value objects}
- Tests: Unit tests ✅ Integration tests ✅ Architecture tests ✅
</action>

<action>Suggest next steps:
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
