# Add Use Case Workflow Instructions

<critical>This workflow adds an application service use case with complete orchestration logic and synchronized tests</critical>

<workflow>

<step n="1" goal="Identify target project">
<check if="target_project_path not provided">
  <ask>Path to your Spring Boot Clean Architecture project?</ask>
  <action>Store target_project_path</action>
  <action>Scan project to detect base_package</action>
</check>

<check if="target_project_path provided">
  <action>Use provided target_project_path</action>
  <action>Confirm base_package is correct</action>
</check>

<action>Validate project structure</action>
<action>Scan for existing entities and repositories</action>
</step>

<step n="2" goal="Define use case concept">
<ask>What is the use case name? (e.g., "CreatePolicy", "SubmitClaim", "AssessRisk")</ask>
<action>Store use_case_name</action>
<action>Ensure name follows command pattern (verb + noun)</action>

<ask>Brief description of what this use case does?
Example: "Creates a new insurance policy with coverages and calculates premium"</ask>
<action>Store use_case_description</action>

<ask>What type of use case is this?
1. Command (creates/updates/deletes data, returns void or ID)
2. Query (reads data, returns data)
3. Command + Query (creates and returns full data)

Choose [1/2/3]:</ask>

<action>Store use_case_type</action>
</step>

<step n="3" goal="Define input DTO">
<ask>Does this use case require input data? [yes/no]:</ask>

<check if="yes">
  <ask>Define input DTO fields (format: "fieldName:type:description"):
  Examples:
  - policyNumber:String:Policy identification number
  - customerId:Long:Customer ID reference
  - effectiveDate:LocalDate:Policy start date
  - coverageIds:List&lt;Long&gt;:Coverage selections

  Enter fields (type 'done' when finished):</ask>

  <action>Collect input_fields iteratively until 'done'</action>
  <action>Store input_dto_fields</action>
  <action>Determine input DTO name: {use_case_name}Input or {use_case_name}Command</action>
</check>

<check if="no">
  <action>Set input_dto_fields = empty (use case takes no parameters)</action>
</check>
</step>

<step n="4" goal="Define output DTO">
<check if="use_case_type == Command (void)">
  <action>Set output_dto = void (no return value)</action>
</check>

<check if="use_case_type == Query or Command+Query">
  <ask>Define output DTO fields (format: "fieldName:type:description"):
  Examples:
  - policyId:Long:Created policy ID
  - policyNumber:String:Policy number
  - status:PolicyStatus:Policy status
  - premium:BigDecimal:Calculated premium

  Enter fields (type 'done' when finished):</ask>

  <action>Collect output_fields iteratively until 'done'</action>
  <action>Store output_dto_fields</action>
  <action>Determine output DTO name: {use_case_name}Output or {use_case_name}Result</action>
</check>
</step>

<step n="5" goal="Define dependencies and orchestration">
<ask>Which repositories does this use case need?
Available repositories: {scan existing repositories}

Select repositories (comma-separated or 'none'):</ask>

<action>Store repository_dependencies</action>

<ask>Does this use case call other use cases? [yes/no]:</ask>

<check if="yes">
  <ask>Which use cases does it call? (comma-separated):</ask>
  <action>Store use_case_dependencies</action>
</check>

<ask>Does this use case need domain services? [yes/no]:</ask>

<check if="yes">
  <ask>Which domain services? (comma-separated):</ask>
  <action>Store domain_service_dependencies</action>
</check>
</step>

<step n="6" goal="Define business logic flow">
<ask>Describe the business logic flow step by step:
Example:
1. Validate customer exists
2. Validate coverage selections
3. Create policy entity
4. Calculate premium using PremiumCalculator
5. Save policy
6. Return policy ID

Enter steps (one per line, type 'done' when finished):</ask>

<action>Collect business_logic_steps iteratively until 'done'</action>
<action>Analyze steps to identify validations, calculations, persistence operations</action>
</step>

<step n="7" goal="Define validation rules">
<ask>What validations should this use case perform?
Examples:
- Customer ID must not be null
- Effective date must be in the future
- At least one coverage must be selected
- Premium must be calculated before saving

Enter validation rules (one per line, type 'done' when finished):</ask>

<action>Collect validation_rules iteratively until 'done'</action>
</step>

<step n="8" goal="Generate input DTO">
<check if="input_dto_fields not empty">
  <action>Create {use_case_name}Input.java as Record in {base_package}/application/dto/</action>

  <action>Generate input DTO Record with:
  - All input fields
  - Compact constructor with validation based on validation_rules
  - JavaDoc with field descriptions
  - Validation annotations if appropriate (Jakarta Bean Validation)
  </action>
</check>
</step>

<step n="9" goal="Generate output DTO">
<check if="output_dto != void">
  <action>Create {use_case_name}Output.java as Record in {base_package}/application/dto/</action>

  <action>Generate output DTO Record with:
  - All output fields
  - JavaDoc with field descriptions
  - Factory methods if useful (e.g., from domain entity)
  </action>
</check>
</step>

<step n="10" goal="Generate use case interface">
<action>Create {use_case_name}UseCase.java interface in {base_package}/application/usecase/</action>

<action>Generate use case interface with:
- Single method: execute(Input) → Output
- Or handle(Command) → Result
- Or process(Query) → Result
- JavaDoc describing the use case and preconditions
</action>
</step>

<step n="11" goal="Generate use case implementation">
<action>Create {use_case_name}Service.java in {base_package}/application/service/</action>

<action>Generate use case implementation with:
- @Service annotation
- Implements use case interface
- Constructor injection for all dependencies
- @Transactional annotation (if writes data)
- execute() method implementing business logic flow
- Private helper methods for each major step
- Validation logic
- Error handling
- Logging
</action>

<action>Implement business logic based on business_logic_steps</action>
</step>

<step n="12" goal="Generate unit tests">
<action>Invoke Test Engineer Agent</action>

<action>Create {use_case_name}ServiceTest.java in test/{base_package}/application/service/</action>

<action>Generate unit tests with:
- @ExtendWith(MockitoExtension.class)
- Mock all dependencies (repositories, domain services)
- Test successful execution path
- Test validation failures
- Test business rule violations
- Test exception scenarios
- Verify interactions with mocks
- Use meaningful test names (shouldX_whenY_givenZ)
</action>
</step>

<step n="13" goal="Generate integration test">
<action>Invoke Test Engineer Agent</action>

<action>Create {use_case_name}IntegrationTest.java in test/{base_package}/application/</action>

<action>Generate integration test with:
- @SpringBootTest
- TestContainers for database
- Real repository implementations
- Test complete use case flow end-to-end
- Verify data persisted correctly
- Test transaction rollback on errors
</action>
</step>

<step n="14" goal="Run ArchUnit validation">
<action>Invoke Architecture Validator Agent</action>

<action>Run ArchUnit tests to validate:
- Use case interface in application.usecase
- Service implementation in application.service
- DTOs in application.dto
- Service has @Service annotation
- Service depends only on domain interfaces
- No direct infrastructure dependencies
</action>

<check if="violations found">
  <action>Report violations and fix</action>
  <goto step="14">Re-validate</goto>
</check>
</step>

<step n="15" goal="Run all tests">
<action>Execute: mvn test -Dtest=*{use_case_name}*</action>

<check if="tests fail">
  <action>Report failures and fix</action>
  <goto step="15">Re-test</goto>
</check>

<check if="tests pass">
  <action>Confirm all tests passing ✅</action>
</check>
</step>

<step n="16" goal="Present summary and next steps">
<action>Display use case creation summary:
- Use Case: {use_case_name}
- Type: {use_case_type}
- Input: {use_case_name}Input (if applicable)
- Output: {use_case_name}Output (if applicable)
- Dependencies: {repository_dependencies}, {domain_service_dependencies}
- Tests: Unit tests ✅ Integration tests ✅ Architecture tests ✅
</action>

<action>Suggest next steps:
1. Add REST endpoint to expose this use case (*add-rest-endpoint)
2. Add another related use case (*add-use-case)
3. Validate full architecture (*validate-architecture)
4. Generate documentation (*generate-documentation)
</action>

<ask>Would you like to:
1. Add REST endpoint for {use_case_name}
2. Add another use case
3. Validate architecture
4. Exit

Choose [1/2/3/4]:</ask>

<check if="1">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml</invoke-workflow>
  <action>Pass use_case_name as context</action>
</check>

<check if="2">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml</invoke-workflow>
</check>

<check if="3">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/validate-architecture/workflow.yaml</invoke-workflow>
</check>

<check if="4">
  <action>Use case creation complete!</action>
</check>
</step>

</workflow>
