# Add Use Case Workflow Instructions (TDD + Git Flow)

<critical>This workflow adds an application layer use case following Test-Driven Development (TDD) and Git Flow</critical>
<critical>All code follows the Red-Green-Refactor cycle: 🔴 RED → 🟢 GREEN → 🔵 REFACTOR</critical>
<critical>Each TDD phase is committed separately with proper labels and GitHub issue tracking</critical>

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

<action>Validate project structure</action>
<action>Scan for existing entities and repositories</action>

<action>Check git setup:
- Verify .git directory exists
- Check current branch (should be 'develop')
- Verify GitHub remote configured
- Check gh CLI authentication
</action>

<check if="not on develop branch">
  <ask>Current branch is {current_branch}. Switch to 'develop'? [y/n]:</ask>
  <check if="yes">
    <action>cd {target_project_path}</action>
    <action>git checkout develop</action>
    <action>git pull origin develop (if remote exists)</action>
  </check>
</check>
</step>

<step n="2" goal="Define use case concept">
<ask>What is the use case name? (e.g., "CreatePolicy", "SubmitClaim", "AssessRisk")</ask>
<action>Store use_case_name</action>
<action>Ensure name follows command pattern (verb + noun)</action>
<action>Convert to kebab-case: {use_case_name_kebab}</action>

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

<step n="3" goal="Define input and output DTOs">
<ask>Does this use case require input data? [yes/no]:</ask>

<check if="yes">
  <ask>Define input DTO fields (format: "fieldName:type:description"):
  Examples:
  - customerId:Long:Customer ID reference
  - effectiveDate:LocalDate:Policy start date
  - coverageIds:List&lt;Long&gt;:Coverage selections

  Enter fields (type 'done' when finished):</ask>

  <action>Collect input_fields iteratively until 'done'</action>
  <action>Store input_dto_fields</action>
  <action>Determine input DTO name: {use_case_name}Input</action>
</check>

<check if="no">
  <action>Set input_dto_fields = empty</action>
</check>

<check if="use_case_type == Command (void)">
  <action>Set output_dto = void</action>
</check>

<check if="use_case_type == Query or Command+Query">
  <ask>Define output DTO fields (format: "fieldName:type:description"):
  Examples:
  - policyId:Long:Created policy ID
  - policyNumber:String:Policy number
  - status:PolicyStatus:Policy status

  Enter fields (type 'done' when finished):</ask>

  <action>Collect output_fields iteratively until 'done'</action>
  <action>Store output_dto_fields</action>
  <action>Determine output DTO name: {use_case_name}Output</action>
</check>
</step>

<step n="4" goal="Define dependencies and business logic">
<ask>Which repositories does this use case need?
Available: {list scanned repositories}

Select repositories (comma-separated or 'none'):</ask>

<action>Store repository_dependencies</action>

<ask>Does this use case call other use cases? [yes/no]:</ask>

<check if="yes">
  <ask>Which use cases? (comma-separated):</ask>
  <action>Store use_case_dependencies</action>
</check>

<ask>What business logic steps should this use case perform?
Examples:
- Validate customer exists
- Check credit limit
- Calculate premium
- Create policy entity
- Save to repository
- Publish domain event

Enter steps (one per line, type 'done' when finished):</ask>

<action>Collect business_logic_steps until 'done'</action>
</step>

<step n="5" goal="Create GitHub issue and feature branch">
<check if="GitHub remote exists">
  <action>Generate issue body from use-case template:
  - Use case name: {use_case_name}
  - Type: {use_case_type}
  - Purpose: {use_case_description}
  - Input: {input_dto_fields}
  - Output: {output_dto_fields}
  - Dependencies: {repository_dependencies}, {use_case_dependencies}
  - Business logic: {business_logic_steps}
  - Estimated effort: {calculated_hours} hours
  </action>

  <action>Create GitHub issue:
  gh issue create \
    --title "Add {use_case_name} use case" \
    --body-file issue_body.md \
    --label "type:use-case,layer:application,tdd,status:todo" \
    --assignee "@me"
  </action>

  <action>Capture issue_number</action>

  <action>Display: ✅ GitHub Issue #{issue_number} created</action>

  <check if="GitHub Project linked">
    <action>Add to project and set fields:
    - TDD Phase: "Not Started"
    - Layer: "Application"
    - Component Type: "Use Case"
    - Estimated Hours: {calculated_hours}
    </action>
  </check>
</check>

<check if="no GitHub remote">
  <action>Set issue_number = "local"</action>
</check>

<action>Create feature branch:
git checkout -b feature/{issue_number}-add-{use_case_name_kebab}-use-case
</action>

<action>Display:
🌿 Feature branch created: feature/{issue_number}-add-{use_case_name_kebab}-use-case
📋 GitHub Issue: #{issue_number}
🔴 Starting TDD RED Phase...
</action>
</step>

<step n="6" goal="🔴 TDD RED PHASE - Generate failing tests">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔴 TDD RED PHASE: Write Failing Tests First
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Generating comprehensive use case tests BEFORE implementation.
Tests will use Mockito to mock dependencies.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Generate Input DTO tests (if input exists):
Create {use_case_name}InputTest.java in test/{base_package}/application/dto/

Test coverage:
- Record creation with valid data
- Factory methods work correctly
- Jakarta Bean Validation annotations (if any)
- Null/invalid field handling

Use JUnit 5 + AssertJ
</action>

<action>Generate Output DTO tests (if output exists):
Create {use_case_name}OutputTest.java in test/{base_package}/application/dto/

Test coverage:
- Record creation
- Factory methods from domain entities
- All fields mapped correctly
</action>

<action>Generate Use Case unit tests:
Create {use_case_name}UseCaseTest.java in test/{base_package}/application/usecase/

Test coverage:
- Happy path: Valid input produces expected output
- Each business logic step tested individually
- Repository interactions verified with Mockito
- Use case dependencies verified
- Validation failures throw correct exceptions
- Transaction behavior (if applicable)
- Edge cases and boundary conditions

Test structure:
- Given: Setup mocks and test data
- When: Execute use case
- Then: Verify results and mock interactions

Use:
- JUnit 5 (@Test, @DisplayName, @BeforeEach)
- Mockito (@Mock, @InjectMocks, when(), verify())
- AssertJ for fluent assertions
- @ExtendWith(MockitoExtension.class)
</action>

<action>Display generated test files:
✅ {use_case_name}InputTest.java (5 tests)
✅ {use_case_name}OutputTest.java (3 tests)
✅ {use_case_name}UseCaseTest.java (12 tests)

Total: ~20 tests generated
</action>
</step>

<step n="7" goal="🔴 Run tests to verify FAIL (RED confirmation)">
<action>Display:
Running tests to verify RED phase...
Expected: All tests FAIL (implementation doesn't exist)
</action>

<action>Run tests:
cd {target_project_path}
mvn test -Dtest={use_case_name}*Test
</action>

<action>Display test results:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔴 RED PHASE CONFIRMED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
❌ {failed_count} tests failing (expected!)
📝 Tests describe the use case behavior

Implementation pending...
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<check if="tests unexpectedly pass">
  <action>Report issue and regenerate tests</action>
  <goto step="6">Regenerate tests</goto>
</check>
</step>

<step n="8" goal="🔴 Commit RED phase">
<action>Stage test files:
git add test/
</action>

<action>Commit RED phase:
git commit -m "test: add failing tests for {use_case_name} use case (RED) #{issue_number}

Generated comprehensive test suite for {use_case_name}:
- Input DTO tests: Validation and factory methods
- Output DTO tests: Mapping from domain entities
- Use case tests: Business logic with mocked dependencies

Business logic tested:
{list business_logic_steps}

Dependencies mocked:
{list repository_dependencies and use_case_dependencies}

TDD-Phase: RED
Layer: Application
Component: Use Case

All tests failing as expected - implementation pending."
</action>

<check if="GitHub remote exists">
  <action>Push RED phase:
  git push -u origin feature/{issue_number}-add-{use_case_name_kebab}-use-case
  </action>

  <action>Update GitHub:
  gh issue edit {issue_number} --add-label "tdd:red"
  gh issue comment {issue_number} --body "🔴 RED Phase complete: Tests written and failing"
  </action>

  <check if="GitHub Project linked">
    <action>Set TDD Phase field to "RED"</action>
  </check>
</check>

<action>Display:
✅ RED phase committed
🔴 Label: tdd:red added
</action>
</step>

<step n="9" goal="🟢 TDD GREEN PHASE - Implement use case">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🟢 TDD GREEN PHASE: Make Tests Pass
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Implementing use case to satisfy all tests.
Focus: Make it work (we'll improve it in REFACTOR).
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Generate Input DTO (if exists):
Create {use_case_name}Input.java in {base_package}/application/dto/

Implementation:
- Java Record for immutability
- All input fields
- Jakarta Bean Validation annotations (@NotNull, @NotBlank, @Min, @Max, etc.)
- Factory methods for creation
- Conversion to domain entity method (if needed)
- Comprehensive JavaDoc
</action>

<action>Generate Output DTO (if exists):
Create {use_case_name}Output.java in {base_package}/application/dto/

Implementation:
- Java Record for immutability
- All output fields
- Factory method from domain entity
- Mapping from domain to DTO
- Comprehensive JavaDoc
</action>

<action>Generate Use Case interface:
Create {use_case_name}UseCase.java in {base_package}/application/usecase/

Interface definition:
- Single method: execute({use_case_name}Input) returns {use_case_name}Output
- Clear contract
- JavaDoc explaining use case purpose and behavior
- NO implementation (interface only)
</action>

<action>Generate Use Case implementation:
Create {use_case_name}UseCaseImpl.java in {base_package}/application/service/

Implementation:
- @Service annotation
- Implements {use_case_name}UseCase interface
- Constructor injection for dependencies (repositories, other use cases)
- @Transactional annotation (if data changes)
- execute() method with business logic steps
- Input validation
- Repository calls
- Domain entity creation/manipulation
- Error handling with custom exceptions
- Logging (@Slf4j)
- Return output DTO

Business logic implementation based on {business_logic_steps}
</action>

<action>Display generated files:
✅ {use_case_name}Input.java (input DTO)
✅ {use_case_name}Output.java (output DTO)
✅ {use_case_name}UseCase.java (interface)
✅ {use_case_name}UseCaseImpl.java (service implementation)
</action>
</step>

<step n="10" goal="🟢 Run tests to verify PASS (GREEN confirmation)">
<action>Display:
Running tests to verify GREEN phase...
Expected: All tests PASS
</action>

<action>Run tests:
mvn test -Dtest={use_case_name}*Test
</action>

<action>Display detailed results:
- Tests run
- Tests passed
- Tests failed (if any)
- Code coverage percentage
- Mock verification results
</action>

<check if="tests still failing">
  <action>Analyze failures and fix implementation</action>
  <goto step="10">Re-run tests</goto>
</check>

<check if="all tests pass">
  <action>Display:
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  🟢 GREEN PHASE CONFIRMED
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ✅ {passed_count} tests passing
  📊 Coverage: {coverage}%
  ✅ All mocks verified

  Use case implementation complete!
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  </action>
</check>
</step>

<step n="11" goal="🟢 Commit GREEN phase">
<action>Stage implementation:
git add src/main/
</action>

<action>Commit GREEN phase:
git commit -m "feat: implement {use_case_name} use case (GREEN) #{issue_number}

Implemented application layer use case:

DTOs:
- {use_case_name}Input: Validated input with {count} fields
- {use_case_name}Output: Result with {count} fields

Use Case:
- {use_case_name}UseCase: Interface contract
- {use_case_name}UseCaseImpl: Service implementation

Business Logic:
{list business_logic_steps}

Dependencies:
{list repository_dependencies and use_case_dependencies}

TDD-Phase: GREEN
Layer: Application
Test Coverage: {coverage}%

All {passed_count} tests passing."
</action>

<check if="GitHub remote exists">
  <action>Push GREEN phase:
  git push origin feature/{issue_number}-add-{use_case_name_kebab}-use-case
  </action>

  <action>Update GitHub:
  gh issue edit {issue_number} --remove-label "tdd:red" --add-label "tdd:green"
  gh issue comment {issue_number} --body "🟢 GREEN Phase complete: Implementation passes all tests

Test Results:
- ✅ {passed_count} tests passing
- 📊 Coverage: {coverage}%
- ✅ All mocks verified

Ready for REFACTOR phase."
  </action>

  <check if="GitHub Project linked">
    <action>Set TDD Phase field to "GREEN"</action>
  </check>
</check>

<action>Display:
✅ GREEN phase committed
🟢 Label: tdd:green added
</action>
</step>

<step n="12" goal="🔵 TDD REFACTOR PHASE - Improve code quality">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔵 TDD REFACTOR PHASE: Improve Code Quality
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Tests are green! Let's improve the implementation.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Analyze for refactoring opportunities:
1. Extract complex validation to private methods
2. Extract business logic to domain service (if complex)
3. Look for duplicated code
4. Check method length (max 20 lines recommended)
5. Review naming for clarity
6. Check for proper error handling
7. Verify logging is appropriate
8. Look for magic numbers/strings
9. Check if orchestration could be simplified
</action>

<action>Display refactoring suggestions with rationale</action>

<ask>Apply refactorings?
1. Apply all
2. Apply selectively
3. Skip refactor phase

Choose [1/2/3]:</ask>

<check if="apply">
  <action for-each="refactoring in approved_refactorings">
    Apply refactoring
    Run tests: mvn test -Dtest={use_case_name}*Test
    Verify tests still pass
  </action>
</check>
</step>

<step n="13" goal="🔵 Commit REFACTOR phase (if changes made)">
<check if="refactorings applied">
  <action>Stage changes:
  git add src/
  </action>

  <action>Commit REFACTOR:
  git commit -m "refactor: improve {use_case_name} use case design #{issue_number}

Refactorings applied:
{list refactorings}

TDD-Phase: REFACTOR
Tests: Still passing ({passed_count} tests)
Coverage: {coverage}%

Code quality improved."
  </action>

  <check if="GitHub remote exists">
    <action>Push and update:
    git push origin feature/{issue_number}-add-{use_case_name_kebab}-use-case
    gh issue edit {issue_number} --remove-label "tdd:green" --add-label "tdd:refactor"
    gh issue comment {issue_number} --body "🔵 REFACTOR Phase complete"
    </action>

    <check if="GitHub Project linked">
      <action>Set TDD Phase to "REFACTOR"</action>
    </check>
  </check>
</check>
</step>

<step n="14" goal="Run ArchUnit validation">
<action>Display: 🏛️ Running ArchUnit validation...</action>

<action>Run: mvn test -Dtest=ArchitectureTest</action>

<check if="violations">
  <action>Display violations and fix</action>
  <goto step="14">Re-validate</goto>
</check>

<check if="no violations">
  <action>Display: ✅ ArchUnit validation PASSED</action>
</check>
</step>

<step n="15" goal="Run complete test suite">
<action>Display: 🧪 Running complete test suite...</action>

<action>Run: mvn clean test</action>

<check if="failures">
  <action>Fix issues</action>
  <goto step="15">Re-test</goto>
</check>

<check if="all pass">
  <action>Display:
  ✅ Complete test suite passing
  📊 {total_tests} tests, {overall_coverage}% coverage
  </action>
</check>
</step>

<step n="16" goal="Create Pull Request">
<check if="GitHub remote exists">
  <action>Generate PR body from template</action>

  <action>Create PR:
  gh pr create \
    --title "Add {use_case_name} use case" \
    --body-file pr_body.md \
    --base develop \
    --head feature/{issue_number}-add-{use_case_name_kebab}-use-case \
    --label "type:use-case,layer:application,tdd:complete"
  </action>

  <action>Update issue:
  gh issue comment {issue_number} --body "🎯 PR created: #{pr_number}"
  gh issue edit {issue_number} --add-label "tdd:complete,status:in-review"
  </action>

  <check if="GitHub Project linked">
    <action>Move to "In Review", set TDD Phase to "Complete"</action>
  </check>

  <ask>Merge PR now? [y/n]:</ask>

  <check if="yes">
    <action>Wait for CI...</action>
    <check if="CI passed">
      <action>Merge and cleanup:
      gh pr merge {pr_number} --squash --delete-branch
      gh issue close {issue_number} --reason "completed"
      git checkout develop
      git pull origin develop
      </action>
    </check>
  </check>
</check>

<check if="no GitHub remote">
  <action>Merge locally and cleanup</action>
</check>
</step>

<step n="17" goal="Present summary and next steps">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ {use_case_name} USE CASE COMPLETE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 Issue: #{issue_number} (Closed)
🔗 PR: #{pr_number} (Merged)

📦 Files Created:
Application Layer:
  ✅ {use_case_name}Input.java (DTO)
  ✅ {use_case_name}Output.java (DTO)
  ✅ {use_case_name}UseCase.java (interface)
  ✅ {use_case_name}UseCaseImpl.java (service)

Tests:
  ✅ {use_case_name}InputTest.java
  ✅ {use_case_name}OutputTest.java
  ✅ {use_case_name}UseCaseTest.java

📊 Test Results:
  ✅ {total_tests} tests passing
  📈 Coverage: {coverage}%

🎯 TDD Compliance:
  🔴 RED ✅ → 🟢 GREEN ✅ → 🔵 REFACTOR ✅
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Suggest next steps:
💡 Next Steps:

1. Add REST endpoint for {use_case_name} (*add-rest-endpoint)
   Expose use case through REST API with OpenAPI docs

2. Add another use case (*add-use-case)
   Build related application functionality

3. Add integration tests (*add-integration-test)
   Test use case with real database

All workflows follow TDD automatically!
</action>

<ask>Would you like to:
1. Add REST endpoint for {use_case_name} (with TDD)
2. Add another use case (with TDD)
3. Add integration test
4. Exit

Choose [1/2/3/4]:</ask>

<check if="1">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml</invoke-workflow>
  <action>Pass use_case_name context</action>
</check>

<check if="2">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml</invoke-workflow>
</check>

<check if="3">
  <action>Guide integration test creation</action>
</check>

<check if="4">
  <action>Display: 🎉 Use case complete! Happy coding! 🚀</action>
</check>
</step>

</workflow>
