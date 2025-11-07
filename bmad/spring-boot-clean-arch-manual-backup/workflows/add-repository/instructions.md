# Add Repository Workflow Instructions

<critical>Adds repository with interface in domain layer and JPA implementation in infrastructure</critical>
<critical>Communicate with {user_name} in {communication_language} throughout this workflow</critical>

<workflow>

<step n="1" goal="Identify entity for repository">
<action>Scan for existing entities in domain.entity package</action>
<ask>For which entity do you want to create a repository?
{list available entities}</ask>
<action>Store entity_name</action>
</step>

<step n="2" goal="Define custom query methods">
<ask>What custom query methods do you need besides standard CRUD?
Examples:
- findByPolicyNumber(String policyNumber)
- findByCustomerIdAndStatus(Long customerId, PolicyStatus status)
- findAllByEffectiveDateBetween(LocalDate start, LocalDate end)

Enter methods (one per line, type 'done' when finished):</ask>
<action>Collect custom_query_methods until 'done'</action>
</step>

<step n="3" goal="Generate repository interface">
<action>Load repository interface template from {repository_interface_template}</action>
<action>Generate interface using template with replacements:
  - {{ENTITY_NAME}} → {entity_name}
  - {{BASE_PACKAGE}} → {base_package}
  - {{CUSTOM_METHODS}} → custom_query_methods
</action>
<action>Write to {base_package}/domain/port/{entity_name}Repository.java</action>
<action>Ensure no Spring annotations (pure domain interface)</action>
</step>

<step n="4" goal="Generate JPA repository and implementation">
<action>Load JPA repository template from {jpa_repository_template}</action>
<action>Generate Spring Data JPA repository interface extending JpaRepository</action>
<action>Write to {base_package}/infrastructure/adapter/persistence/{entity_name}JpaRepository.java</action>

<action>Load repository implementation template from {repository_impl_template}</action>
<action>Generate implementation with @Repository annotation</action>
<action>Include domain repository interface implementation</action>
<action>Add JPA repository internally with mapper methods</action>
<action>Write to {base_package}/infrastructure/adapter/persistence/{entity_name}RepositoryImpl.java</action>
</step>

<step n="5" goal="Generate integration tests">
<action>Load Test Engineer agent context from {test_engineer_agent}</action>
<action>Load repository test template from {repository_test_template}</action>
<action>Reference TDD guide from {tdd_guide}</action>

<action>Generate integration tests using template:
  - {{ENTITY_NAME}} → {entity_name}
  - {{BASE_PACKAGE}} → {base_package}
  - {{CUSTOM_METHODS}} → test cases for custom_query_methods
  - Include TestContainers setup
  - Test all CRUD operations
  - Test all custom query methods
</action>
<action>Write to test/{base_package}/infrastructure/{entity_name}RepositoryIntegrationTest.java</action>
</step>

<step n="6" goal="Validate">
<action>Load Architecture Validator agent context from {arch_validator_agent}</action>
<action>Run ArchUnit validation for proper layering</action>
<action>Run integration tests: mvn test -Dtest={entity_name}RepositoryIntegrationTest</action>
<action>Report results to {user_name}</action>
</step>

</workflow>
