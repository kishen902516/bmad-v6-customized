# Add Entity Workflow Instructions (TDD + Git Flow)

<critical>This workflow adds a domain entity following Test-Driven Development (TDD) and Git Flow</critical>
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

<action>Validate project structure (check for Clean Architecture layers)</action>

<action>Check if project has git initialized:
- Check for .git directory
- Verify current branch (should be 'develop' for Git Flow)
- Check if GitHub remote is configured
- Verify gh CLI is authenticated
</action>

<check if="not on develop branch">
  <ask>Current branch is {current_branch}. Switch to 'develop' branch?
  [y/n]:</ask>

  <check if="yes">
    <action>cd {target_project_path}</action>
    <action>git checkout develop</action>
    <action>git pull origin develop (if remote exists)</action>
  </check>
</check>
</step>

<step n="2" goal="Define entity concept">
<ask>What is the entity name? (e.g., "Policy", "Claim", "Customer")</ask>
<action>Store entity_name</action>
<action>Convert to PascalCase for class name</action>
<action>Convert to kebab-case for issue/branch: {entity_name_kebab}</action>

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

<step n="6" goal="Create GitHub issue and feature branch">
<action>Check if GitHub remote is configured</action>

<check if="github remote exists">
  <action>Generate issue body from entity-template.md:
  - Entity name: {entity_name}
  - Purpose: {entity_description}
  - Aggregate root: {is_aggregate_root}
  - Attributes: {attribute_list}
  - Business rules: {business_rules}
  - Relationships: {relationship_list}
  - Estimated effort: {calculated_hours} hours
  </action>

  <action>Create GitHub issue using gh CLI:
  gh issue create \
    --title "Add {entity_name} entity" \
    --body-file issue_body.md \
    --label "type:entity,layer:domain,tdd,status:todo" \
    --assignee "@me"
  </action>

  <action>Capture issue number: {issue_number}</action>

  <action>Display: ✅ GitHub Issue #{issue_number} created: "Add {entity_name} entity"</action>

  <check if="GitHub Project is linked">
    <action>Add issue to GitHub Project:
    gh project item-add {project_number} --owner {owner} --url {issue_url}
    </action>

    <action>Set custom fields:
    - TDD Phase: "Not Started"
    - Layer: "Domain"
    - Component Type: "Entity"
    - Estimated Hours: {calculated_hours}
    </action>
  </check>
</check>

<check if="no github remote">
  <action>Set issue_number = "local" (proceed without GitHub integration)</action>
  <action>Display: ⚠️  No GitHub remote - proceeding in local-only mode</action>
</check>

<action>Create feature branch:
Branch name: feature/{issue_number}-add-{entity_name_kebab}-entity
</action>

<action>git checkout -b feature/{issue_number}-add-{entity_name_kebab}-entity</action>

<action>Display:
🌿 Feature branch created and checked out
📋 Branch: feature/{issue_number}-add-{entity_name_kebab}-entity
🎯 GitHub Issue: #{issue_number}
🔴 Starting TDD RED Phase: Writing tests first...
</action>
</step>

<step n="7" goal="🔴 TDD RED PHASE - Generate failing tests">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔴 TDD RED PHASE: Write Failing Tests First
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

We'll generate comprehensive tests BEFORE any implementation.
Tests will fail because the code doesn't exist yet - this is expected!
</action>

<action>Generate Value Object tests first (if needed):
For each Value Object in attribute_list:
- Create {ValueObjectName}Test.java in test/{base_package}/domain/valueobject/
- Test validation (invalid inputs throw exceptions)
- Test equality
- Test immutability (Records)
</action>

<action>Generate domain entity unit tests:
Create {entity_name}Test.java in test/{base_package}/domain/entity/

Test coverage:
- Constructor with valid data
- Constructor with invalid data (each validation rule)
- Business methods (from business_rules)
- Equals/hashCode contract
- Aggregate root behavior (if applicable)
- Relationship management (if applicable)

Use:
- JUnit 5 (@Test, @DisplayName)
- Parameterized tests (@ParameterizedTest)
- AssertJ for fluent assertions
- Given-When-Then structure
</action>

<action>Generate mapper unit tests:
Create {entity_name}MapperTest.java in test/{base_package}/infrastructure/adapter/persistence/

Test coverage:
- Map domain entity to JPA entity
- Map JPA entity to domain entity
- Handle null cases
- Map collections/relationships correctly
</action>

<action>Generate repository integration tests:
Create {entity_name}RepositoryIntegrationTest.java in test/{base_package}/infrastructure/

Test coverage:
- Save entity
- Find by ID
- Find all
- Delete entity
- Custom query methods
- Transaction rollback
- Database cleanup

Use:
- @DataJpaTest or @SpringBootTest
- @Testcontainers with PostgreSQL
- Database cleanup with @Transactional
</action>

<action>Display generated test files:
✅ {entity_name}Test.java (15 tests)
✅ {ValueObject}Test.java (5 tests each)
✅ {entity_name}MapperTest.java (8 tests)
✅ {entity_name}RepositoryIntegrationTest.java (10 tests)

Total: ~40 tests generated
</action>
</step>

<step n="8" goal="🔴 Run tests to verify they FAIL (RED confirmation)">
<action>Display:
Running tests to verify RED phase...
Expected: All tests should FAIL (code doesn't exist yet)
</action>

<action>Run tests:
cd {target_project_path}
mvn test -Dtest={entity_name}*Test
</action>

<action>Capture test results:
- Count compilation errors
- Count test failures
- Display failed test names
</action>

<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔴 RED PHASE CONFIRMED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
❌ {failed_count} tests failing (expected!)
📝 Tests describe what we need to implement

This is GOOD! Tests are our specification.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<check if="tests unexpectedly pass or don't compile correctly">
  <action>Report issue: Tests should fail but didn't - review test generation</action>
  <goto step="7">Regenerate tests</goto>
</check>
</step>

<step n="9" goal="🔴 Commit RED phase">
<action>Stage test files:
git add test/
</action>

<action>Commit RED phase:
git commit -m "test: add failing tests for {entity_name} entity (RED) #{issue_number}

Generated comprehensive test suite for {entity_name}:
- Domain entity tests: Constructor validation, business methods
- Value object tests: {list value objects}
- Mapper tests: Domain ↔ JPA conversion
- Repository integration tests: CRUD operations

TDD-Phase: RED
Layer: Domain
Component: Entity

All tests failing as expected - implementation pending."
</action>

<check if="GitHub remote exists">
  <action>Push RED phase:
  git push -u origin feature/{issue_number}-add-{entity_name_kebab}-entity
  </action>

  <action>Update GitHub issue:
  gh issue edit {issue_number} --add-label "tdd:red"
  gh issue comment {issue_number} --body "🔴 RED Phase complete: Tests written and failing as expected"
  </action>

  <check if="GitHub Project linked">
    <action>Update project item:
    Set "TDD Phase" field to "RED"
    </action>
  </check>
</check>

<action>Display:
✅ RED phase committed
📝 Commit: "test: add failing tests for {entity_name} entity (RED) #{issue_number}"
🔴 Label: tdd:red added to issue
</action>
</step>

<step n="10" goal="🟢 TDD GREEN PHASE - Implement to pass tests">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🟢 TDD GREEN PHASE: Make Tests Pass
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Now we'll implement JUST ENOUGH code to make tests pass.
Focus: Make it work, not make it perfect (that's refactor phase).
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Generate Value Objects first (if needed):
For each Value Object:
- Create {ValueObjectName}.java in {base_package}/domain/valueobject/
- Use Java Record with compact constructor
- Add validation in compact constructor
- Add factory methods if helpful
- Add business methods as needed
</action>

<action>Generate domain entity:
Create {entity_name}.java in {base_package}/domain/entity/

Implementation:
- Private fields for all attributes
- Constructor with required fields (invariant enforcement)
- Private setters, public getters
- Business methods implementing business_rules
- Validation logic
- Equals/hashCode based on ID
- NO framework annotations (pure domain)
- Comprehensive JavaDoc
</action>

<action>Generate repository interface:
Create {entity_name}Repository.java in {base_package}/domain/port/

Methods:
- {entity_name} save({entity_name} entity)
- Optional<{entity_name}> findById(Long id)
- List<{entity_name}> findAll()
- void delete(Long id)
- Custom query methods based on attributes
- NO Spring annotations (pure interface)
</action>

<action>Generate JPA entity:
Create {entity_name}JpaEntity.java in {base_package}/infrastructure/adapter/persistence/entity/

Implementation:
- @Entity, @Table annotations
- @Id, @GeneratedValue for ID
- JPA annotations for relationships
- All necessary mappings
- Package-private (not exposed outside infrastructure)
</action>

<action>Generate entity mapper:
Create {entity_name}Mapper.java in {base_package}/infrastructure/adapter/persistence/

Methods:
- {entity_name}JpaEntity toJpaEntity({entity_name} domain)
- {entity_name} toDomain({entity_name}JpaEntity jpa)
- Handle null cases
- Map collections/relationships
- MapStruct or manual mapping
</action>

<action>Generate Spring Data JPA repository:
Create {entity_name}JpaRepository.java in {base_package}/infrastructure/adapter/persistence/

Interface extends JpaRepository<{entity_name}JpaEntity, Long>
Add custom query methods using Spring Data conventions
</action>

<action>Generate repository implementation:
Create {entity_name}RepositoryImpl.java in {base_package}/infrastructure/adapter/persistence/

Implementation:
- @Repository annotation
- Implements domain {entity_name}Repository interface
- Injects {entity_name}JpaRepository
- Injects {entity_name}Mapper
- Implements all methods using mapper and JPA repository
</action>

<action>Display generated files:
✅ {entity_name}.java (domain entity)
✅ {ValueObject}.java (value objects)
✅ {entity_name}Repository.java (domain port)
✅ {entity_name}JpaEntity.java (JPA entity)
✅ {entity_name}Mapper.java (mapper)
✅ {entity_name}JpaRepository.java (Spring Data JPA)
✅ {entity_name}RepositoryImpl.java (adapter impl)
</action>
</step>

<step n="11" goal="🟢 Run tests to verify they PASS (GREEN confirmation)">
<action>Display:
Running tests to verify GREEN phase...
Expected: All tests should PASS
</action>

<action>Run tests:
cd {target_project_path}
mvn test -Dtest={entity_name}*Test
</action>

<action>Display test results with details:
- Tests run
- Tests passed
- Tests failed (if any)
- Code coverage percentage
</action>

<check if="tests still failing">
  <action>Display failures with details</action>
  <action>Analyze failure reasons</action>
  <ask>Fix implementation issues? [y/n]:</ask>

  <check if="yes">
    <action>Iteratively fix issues</action>
    <goto step="11">Re-run tests</goto>
  </check>
</check>

<check if="all tests pass">
  <action>Display:
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  🟢 GREEN PHASE CONFIRMED
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ✅ {passed_count} tests passing
  📊 Coverage: {coverage}%

  Implementation complete! Tests are green.
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  </action>
</check>
</step>

<step n="12" goal="🟢 Commit GREEN phase">
<action>Stage implementation files:
git add src/main/
</action>

<action>Commit GREEN phase:
git commit -m "feat: implement {entity_name} entity (GREEN) #{issue_number}

Implemented Clean Architecture entity with all layers:

Domain Layer:
- {entity_name}: Pure domain entity with business logic
- {list value objects}: Immutable value objects (Records)
- {entity_name}Repository: Domain repository interface

Infrastructure Layer:
- {entity_name}JpaEntity: JPA persistence entity
- {entity_name}Mapper: Domain ↔ JPA conversion
- {entity_name}RepositoryImpl: Repository adapter implementation

Business Rules Enforced:
{list business_rules}

TDD-Phase: GREEN
Layer: Domain, Infrastructure
Test Coverage: {coverage}%

All {passed_count} tests passing."
</action>

<check if="GitHub remote exists">
  <action>Push GREEN phase:
  git push origin feature/{issue_number}-add-{entity_name_kebab}-entity
  </action>

  <action>Update GitHub issue:
  gh issue edit {issue_number} --remove-label "tdd:red" --add-label "tdd:green"
  gh issue comment {issue_number} --body "🟢 GREEN Phase complete: Implementation passing all tests

Test Results:
- ✅ {passed_count} tests passing
- 📊 Coverage: {coverage}%
- 🏗️ Clean Architecture compliance verified

Ready for REFACTOR phase."
  </action>

  <check if="GitHub Project linked">
    <action>Update project item:
    Set "TDD Phase" field to "GREEN"
    </action>
  </check>
</check>

<action>Display:
✅ GREEN phase committed
📝 Commit: "feat: implement {entity_name} entity (GREEN) #{issue_number}"
🟢 Label: tdd:green added to issue
</action>
</step>

<step n="13" goal="🔵 TDD REFACTOR PHASE - Improve design">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔵 TDD REFACTOR PHASE: Improve Code Quality
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Tests are green! Now let's improve the design while keeping tests green.
Focus: Code quality, maintainability, Clean Architecture principles.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Analyze code for refactoring opportunities:
1. Extract methods that are too long
2. Identify potential Value Objects not yet extracted
3. Look for duplicated code
4. Check for complex conditionals that could be simplified
5. Review naming for clarity
6. Check for proper encapsulation
7. Verify Single Responsibility Principle
8. Look for magic numbers/strings
</action>

<action>Display refactoring suggestions:
📋 Refactoring Opportunities Identified:

{list of suggestions with rationale}

Example:
1. Extract {suggestion} - Would improve {benefit}
2. Rename {current} to {better} - More descriptive
3. Extract validation to private method - Better separation of concerns
</action>

<ask>Apply refactorings?
1. Apply all suggested refactorings
2. Apply selectively (I'll ask for each)
3. Skip refactor phase (code is good as-is)

Choose [1/2/3]:</ask>

<check if="apply all or selective">
  <action for-each="refactoring in approved_refactorings">
    Apply refactoring: {refactoring}
    Run tests after each refactoring: mvn test -Dtest={entity_name}*Test
    Verify tests still pass
  </action>

  <action>Display after each refactoring:
  🔵 Refactoring applied: {refactoring_name}
  ✅ Tests still passing
  </action>
</check>

<check if="skip">
  <action>Display: Skipping refactor phase - proceeding to validation</action>
</check>
</step>

<step n="14" goal="🔵 Commit REFACTOR phase (if changes made)">
<check if="refactorings were applied">
  <action>Stage refactored files:
  git add src/
  </action>

  <action>Commit REFACTOR phase:
  git commit -m "refactor: improve {entity_name} entity design #{issue_number}

  Refactorings applied:
  {list applied refactorings with brief description}

  TDD-Phase: REFACTOR
  Tests: Still passing ({passed_count} tests)
  Coverage: {coverage}%

  Code quality improvements while maintaining green tests."
  </action>

  <check if="GitHub remote exists">
    <action>Push REFACTOR phase:
    git push origin feature/{issue_number}-add-{entity_name_kebab}-entity
    </action>

    <action>Update GitHub issue:
    gh issue edit {issue_number} --remove-label "tdd:green" --add-label "tdd:refactor"
    gh issue comment {issue_number} --body "🔵 REFACTOR Phase complete: Code quality improved

    Refactorings:
    {list refactorings}

    Tests remain green: ✅ {passed_count} passing"
    </action>

    <check if="GitHub Project linked">
      <action>Update project item:
      Set "TDD Phase" field to "REFACTOR"
      </action>
    </check>
  </check>

  <action>Display:
  ✅ REFACTOR phase committed
  📝 Commit: "refactor: improve {entity_name} entity design #{issue_number}"
  🔵 Label: tdd:refactor added to issue
  </action>
</check>
</step>

<step n="15" goal="Run ArchUnit validation">
<action>Display:
🏛️ Running ArchUnit validation to ensure Clean Architecture compliance...
</action>

<action>Run ArchUnit tests:
mvn test -Dtest=ArchitectureTest
</action>

<action>Check for violations:
- Entity in correct package (domain.entity)
- Entity has no framework annotations
- Repository interface in domain.port
- Repository implementation in infrastructure
- Proper dependency direction (dependencies point inward)
- No circular dependencies
</action>

<check if="violations found">
  <action>Display violations with specific fixes needed</action>

  <ask>Fix ArchUnit violations? [y/n]:</ask>

  <check if="yes">
    <action>Apply fixes for each violation</action>
    <action>Commit fixes:
    git commit -m "fix: resolve ArchUnit violations for {entity_name} #{issue_number}"
    </action>
    <goto step="15">Re-validate</goto>
  </check>
</check>

<check if="no violations">
  <action>Display:
  ✅ ArchUnit Validation PASSED
  🏛️ Clean Architecture principles enforced
  </action>
</check>
</step>

<step n="16" goal="Run complete test suite">
<action>Display:
🧪 Running complete test suite to ensure no regressions...
</action>

<action>Run all tests:
cd {target_project_path}
mvn clean test
</action>

<action>Display comprehensive results:
- Total tests run
- Tests passed
- Tests failed
- Overall coverage
- Execution time
</action>

<check if="any tests failed">
  <action>Display: ⚠️  Some tests failed - investigate and fix before proceeding</action>
  <ask>Review failures and fix? [y/n]:</ask>

  <check if="yes">
    <goto step="16">Fix and re-test</goto>
  </check>
</check>

<check if="all tests pass">
  <action>Display:
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ✅ COMPLETE TEST SUITE PASSING
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  📊 {total_tests} tests passing
  📈 Coverage: {overall_coverage}%
  🏛️ ArchUnit: Clean Architecture enforced
  ⚡ Execution time: {execution_time}

  Entity implementation complete and validated!
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  </action>
</check>
</step>

<step n="17" goal="Create Pull Request">
<check if="GitHub remote exists">
  <action>Display:
  📋 Preparing Pull Request...
  </action>

  <action>Generate PR description from template:
  - PR title: "Add {entity_name} entity"
  - Description: {entity_description}
  - Type: Feature (entity)
  - Related issues: Closes #{issue_number}
  - TDD compliance: RED → GREEN → REFACTOR ✅
  - Files added: {list files}
  - Test coverage: {coverage}%
  - Architecture compliance: ✅ Verified
  </action>

  <action>Create PR:
  gh pr create \
    --title "Add {entity_name} entity" \
    --body-file pr_body.md \
    --base develop \
    --head feature/{issue_number}-add-{entity_name_kebab}-entity \
    --label "type:entity,layer:domain,tdd:complete"
  </action>

  <action>Capture PR number and URL</action>

  <action>Update GitHub issue:
  gh issue comment {issue_number} --body "🎯 Pull Request created: #{pr_number}

  TDD Cycle Complete:
  🔴 RED: Tests written first
  🟢 GREEN: Implementation passes all tests
  🔵 REFACTOR: Code quality improved

  Ready for review!"
  </action>

  <action>Update issue labels:
  gh issue edit {issue_number} --remove-label "tdd:refactor" --add-label "tdd:complete,status:in-review"
  </action>

  <check if="GitHub Project linked">
    <action>Move issue to "In Review" column in project</action>
    <action>Update TDD Phase field to "Complete"</action>
  </check>

  <action>Display:
  ✅ Pull Request created successfully!
  🔗 PR: {pr_url}
  📋 Issue #{issue_number} updated
  👀 Status: Ready for review
  </action>

  <ask>Merge PR now (if CI passes)?
  [y/n]:</ask>

  <check if="yes">
    <action>Wait for CI to complete...</action>

    <check if="CI passed">
      <action>Merge PR:
      gh pr merge {pr_number} --squash --delete-branch
      </action>

      <action>Close issue:
      gh issue close {issue_number} --reason "completed"
      </action>

      <action>Switch back to develop:
      git checkout develop
      git pull origin develop
      </action>

      <action>Display:
      ✅ PR merged successfully
      🗑️  Feature branch deleted
      ✅ Issue #{issue_number} closed
      📍 Back on develop branch
      </action>
    </check>

    <check if="CI failed">
      <action>Display: ⚠️  CI checks failed - review failures before merging</action>
    </check>
  </check>

  <check if="no">
    <action>Display: PR created but not merged - waiting for manual review</action>
  </check>
</check>

<check if="no GitHub remote">
  <action>Merge locally to develop:
  git checkout develop
  git merge feature/{issue_number}-add-{entity_name_kebab}-entity
  git branch -d feature/{issue_number}-add-{entity_name_kebab}-entity
  </action>

  <action>Display:
  ✅ Feature merged to develop locally
  🗑️  Feature branch deleted
  </action>
</check>
</step>

<step n="18" goal="Present summary and next steps">
<action>Display entity creation summary:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ {entity_name} ENTITY COMPLETE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 GitHub Issue: #{issue_number} (Closed)
🔗 Pull Request: #{pr_number} (Merged)
🌿 Branch: feature/{issue_number}-add-{entity_name_kebab}-entity (Deleted)

📦 Files Created:
Domain Layer:
  ✅ {entity_name}.java (domain entity)
  ✅ {list value objects}
  ✅ {entity_name}Repository.java (port interface)

Infrastructure Layer:
  ✅ {entity_name}JpaEntity.java
  ✅ {entity_name}Mapper.java
  ✅ {entity_name}JpaRepository.java
  ✅ {entity_name}RepositoryImpl.java

Tests:
  ✅ {entity_name}Test.java (unit tests)
  ✅ {entity_name}MapperTest.java
  ✅ {entity_name}RepositoryIntegrationTest.java

📊 Test Results:
  ✅ {total_tests} tests passing
  📈 Coverage: {coverage}%
  🏛️ ArchUnit: Clean Architecture verified

🎯 TDD Compliance:
  🔴 RED: Tests written first ✅
  🟢 GREEN: Implementation complete ✅
  🔵 REFACTOR: Code quality improved ✅

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Suggest next steps:
💡 Next Steps:

1. Add use case for {entity_name} (*add-use-case)
   Create business operations (Create, Update, Find, etc.)

2. Add REST endpoint for {entity_name} (*add-rest-endpoint)
   Expose {entity_name} through REST API

3. Add related entity (*add-entity)
   {suggest related entities from relationships}

4. Validate architecture (*validate-architecture)
   Run comprehensive ArchUnit validation

All workflows follow TDD (Red-Green-Refactor) automatically!
</action>

<ask>Would you like to:
1. Add a use case for {entity_name} (with TDD)
2. Add REST endpoint for {entity_name} (with TDD)
3. Add another entity (with TDD)
4. Exit

Choose [1/2/3/4]:</ask>

<check if="1">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml</invoke-workflow>
  <action>Pass entity_name and issue context</action>
</check>

<check if="2">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml</invoke-workflow>
  <action>Pass entity_name and issue context</action>
</check>

<check if="3">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-entity/workflow.yaml</invoke-workflow>
</check>

<check if="4">
  <action>Display:
  🎉 Entity creation complete!
  📍 Currently on: develop branch
  📖 View TDD workflow: docs/TDD-WORKFLOW-GUIDE.md

  Happy coding! 🚀
  </action>
</check>
</step>

</workflow>
