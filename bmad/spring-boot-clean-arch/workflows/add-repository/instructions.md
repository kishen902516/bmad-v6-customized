# Add Repository Workflow Instructions

<critical>Adds repository with interface in domain layer and JPA implementation in infrastructure</critical>

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
<action>Create {entity_name}Repository.java in {base_package}/domain/port/</action>
<action>Generate interface with standard and custom methods</action>
<action>No Spring annotations (pure domain interface)</action>
</step>

<step n="4" goal="Generate JPA repository and implementation">
<action>Create {entity_name}JpaRepository extending Spring Data JPA in infrastructure</action>
<action>Create {entity_name}RepositoryImpl in {base_package}/infrastructure/adapter/persistence/</action>
<action>Implement domain repository interface with @Repository annotation</action>
<action>Use JPA repository internally and map between domain and JPA entities</action>
</step>

<step n="5" goal="Generate integration tests">
<action>Create {entity_name}RepositoryIntegrationTest with TestContainers</action>
<action>Test all CRUD and custom query methods</action>
</step>

<step n="6" goal="Validate">
<action>Run ArchUnit validation for proper layering</action>
<action>Run integration tests</action>
</step>

</workflow>
