# Add REST Endpoint Workflow Instructions (TDD + Git Flow)

<critical>This workflow adds a REST API endpoint following Test-Driven Development (TDD) and Git Flow</critical>
<critical>All code follows the Red-Green-Refactor cycle: 🔴 RED → 🟢 GREEN → 🔵 REFACTOR</critical>
<critical>Includes Pact consumer-driven contract testing for microservices</critical>

<workflow>

<step n="1" goal="Identify target project and verify git setup">
<check if="target_project_path not provided">
  <ask>Path to your Spring Boot Clean Architecture project?</ask>
  <action>Store target_project_path</action>
  <action>Scan project to detect base_package</action>
</check>

<action>Validate project structure</action>
<action>Scan for existing use cases and entities</action>

<action>Check git setup:
- Verify current branch (should be 'develop')
- Check GitHub remote
- Verify gh CLI authentication
</action>

<check if="not on develop branch">
  <ask>Switch to 'develop'? [y/n]:</ask>
  <check if="yes">
    <action>git checkout develop && git pull origin develop</action>
  </check>
</check>
</step>

<step n="2" goal="Define endpoint specification">
<ask>Which use case should this endpoint expose?
Available: {list scanned use cases}

Select use case:</ask>

<action>Store use_case_name</action>
<action>Load use case details (input/output DTOs)</action>

<ask>HTTP Method?
1. POST (Create resource)
2. GET (Read resource/collection)
3. PUT (Update resource)
4. PATCH (Partial update)
5. DELETE (Delete resource)

Choose [1/2/3/4/5]:</ask>

<action>Store http_method</action>

<ask>Endpoint path? (e.g., "/api/v1/policies", "/api/v1/policies/{id}"):</ask>

<action>Store endpoint_path</action>
<action>Extract path variables if any</action>
<action>Generate endpoint_name_kebab from path</action>

<ask>Brief description of endpoint purpose?</ask>

<action>Store endpoint_description</action>

<ask>HTTP status codes to return?
Select all that apply (comma-separated):
- 200 OK
- 201 Created
- 204 No Content
- 400 Bad Request
- 404 Not Found
- 409 Conflict
- 500 Internal Server Error

Default for {http_method}: {suggested_status_codes}</ask>

<action>Store status_codes</action>
</step>

<step n="3" goal="Define request and response DTOs">
<check if="http_method requires request body (POST, PUT, PATCH)">
  <ask>Use existing {use_case_name}Input as request DTO? [y/n]:</ask>

  <check if="yes">
    <action>Set request_dto = {use_case_name}Input</action>
    <action>Will generate {use_case_name}Request that wraps Input</action>
  </check>

  <check if="no">
    <ask>Define request DTO fields (format: "fieldName:type:description"):

    Enter fields (type 'done' when finished):</ask>

    <action>Collect request_fields until 'done'</action>
    <action>Set request_dto_name = {endpoint_name}Request</action>
  </check>
</check>

<check if="http_method returns data (GET, POST, PUT, PATCH)">
  <ask>Use existing {use_case_name}Output as response DTO? [y/n]:</ask>

  <check if="yes">
    <action>Set response_dto = {use_case_name}Output</action>
    <action>Will generate {use_case_name}Response that wraps Output</action>
  </check>

  <check if="no">
    <ask>Define response DTO fields (format: "fieldName:type:description"):

    Enter fields (type 'done' when finished):</ask>

    <action>Collect response_fields until 'done'</action>
    <action>Set response_dto_name = {endpoint_name}Response</action>
  </check>
</check>
</step>

<step n="4" goal="Define OpenAPI documentation">
<ask>Endpoint summary (short description for OpenAPI)?</ask>
<action>Store openapi_summary</action>

<ask>Tags for grouping in Swagger UI (comma-separated)?
Example: policies, insurance, claims</ask>
<action>Store openapi_tags</action>

<action>Generate OpenAPI specification:
- Operation summary and description
- Request body schema
- Response schemas for each status code
- Error response schemas
- Example requests and responses
</action>
</step>

<step n="5" goal="Create GitHub issue and feature branch">
<check if="GitHub remote exists">
  <action>Generate issue body:
  - HTTP Method: {http_method}
  - Path: {endpoint_path}
  - Purpose: {endpoint_description}
  - Use Case: {use_case_name}
  - Request: {request_dto}
  - Response: {response_dto}
  - Status Codes: {status_codes}
  - OpenAPI: {openapi_summary}
  - Estimated effort: {calculated_hours} hours
  </action>

  <action>Create issue:
  gh issue create \
    --title "Add {http_method} {endpoint_path} endpoint" \
    --body-file issue_body.md \
    --label "type:endpoint,layer:presentation,tdd,status:todo" \
    --assignee "@me"
  </action>

  <action>Capture issue_number</action>

  <check if="GitHub Project linked">
    <action>Add to project:
    - TDD Phase: "Not Started"
    - Layer: "Presentation"
    - Component Type: "Controller"
    - Estimated Hours: {calculated_hours}
    </action>
  </check>
</check>

<check if="no GitHub remote">
  <action>Set issue_number = "local"</action>
</check>

<action>Create feature branch:
git checkout -b feature/{issue_number}-add-{endpoint_name_kebab}-endpoint
</action>

<action>Display:
🌿 Branch: feature/{issue_number}-add-{endpoint_name_kebab}-endpoint
📋 Issue: #{issue_number}
🔴 Starting TDD RED Phase...
</action>
</step>

<step n="6" goal="🔴 TDD RED PHASE - Generate failing tests">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔴 TDD RED PHASE: Write Failing Tests First
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Generating controller tests with MockMvc and Pact contracts.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Generate Request DTO tests (if exists):
Create {request_dto}Test.java in test/{base_package}/presentation/rest/model/

Test coverage:
- Record creation
- Jakarta Bean Validation annotations
- JSON serialization/deserialization
</action>

<action>Generate Response DTO tests (if exists):
Create {response_dto}Test.java in test/{base_package}/presentation/rest/model/

Test coverage:
- Record creation from use case output
- JSON serialization
- All fields mapped correctly
</action>

<action>Generate Controller MockMvc tests:
Create {controller_name}Test.java in test/{base_package}/presentation/rest/

Test coverage:
- Happy path: Valid request returns expected response with correct status
- Validation errors: Invalid request returns 400 Bad Request
- Not found: Missing resource returns 404
- Use case throws exception: Returns appropriate error response
- Request/Response JSON serialization
- OpenAPI annotations present

Test structure:
- @WebMvcTest({controller_name}.class)
- @MockBean for use case
- MockMvc for HTTP testing
- Given-When-Then pattern
- JSON path assertions

Use:
- Spring MockMvc
- Mockito for use case mocking
- @AutoConfigureMockMvc
- ObjectMapper for JSON
- JSONPath for response validation
</action>

<action>Generate Pact Consumer test:
Create {controller_name}ConsumerPactTest.java in test/{base_package}/presentation/rest/

Pact contract test:
- @ExtendWith(PactConsumerTestExt.class)
- @PactTestFor(providerName = "{service_name}Provider")
- Define Pact interaction:
  - Given: Provider state (e.g., "Policy can be created")
  - Upon receiving: Request description
  - Method: {http_method}
  - Path: {endpoint_path}
  - Headers: Content-Type, Accept
  - Body: Request JSON with matchers
  - Will respond with:
    - Status: {expected_status}
    - Headers: Content-Type
    - Body: Response JSON with matchers
- Test against Pact MockServer
- Generates Pact JSON file for provider verification

Use:
- Pact JVM JUnit5
- PactDslJsonBody for request/response matching
- Type matchers (stringType, numberType, dateType)
- Regex matchers for specific formats
</action>

<action>Generate Pact Provider test (for provider verification):
Create {controller_name}ProviderPactTest.java in test/{base_package}/presentation/rest/

Provider verification test:
- @WebMvcTest
- @Provider("{service_name}Provider")
- @PactFolder("target/pacts")
- @State annotations for provider states
- Mock use case to satisfy contract
- Verify provider satisfies consumer expectations

Use:
- Pact JVM Provider JUnit5
- Spring MockMvc integration
- State setup with @State annotation
</action>

<action>Display generated test files:
✅ {request_dto}Test.java (5 tests)
✅ {response_dto}Test.java (3 tests)
✅ {controller_name}Test.java (10 tests with MockMvc)
✅ {controller_name}ConsumerPactTest.java (Pact consumer)
✅ {controller_name}ProviderPactTest.java (Pact provider)

Total: ~20 tests + Pact contracts
</action>
</step>

<step n="7" goal="🔴 Run tests to verify FAIL (RED confirmation)">
<action>Display: Running tests to verify RED phase...</action>

<action>Run tests:
mvn test -Dtest={controller_name}*Test
</action>

<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔴 RED PHASE CONFIRMED
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
❌ {failed_count} tests failing (expected!)
📝 Tests define the API contract

Pact consumer test will generate contract when controller exists.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>
</step>

<step n="8" goal="🔴 Commit RED phase">
<action>Stage tests:
git add test/
</action>

<action>Commit:
git commit -m "test: add failing tests for {http_method} {endpoint_path} endpoint (RED) #{issue_number}

Generated comprehensive test suite:
- Request/Response DTO tests: Validation and JSON serialization
- Controller tests: MockMvc with {count} scenarios
- Pact consumer test: Contract definition for {service_name}
- Pact provider test: Contract verification setup

Endpoint:
- Method: {http_method}
- Path: {endpoint_path}
- Use Case: {use_case_name}
- Status Codes: {status_codes}

TDD-Phase: RED
Layer: Presentation
Component: Controller

All tests failing - implementation pending."
</action>

<check if="GitHub remote exists">
  <action>Push and update:
  git push -u origin feature/{issue_number}-add-{endpoint_name_kebab}-endpoint
  gh issue edit {issue_number} --add-label "tdd:red"
  gh issue comment {issue_number} --body "🔴 RED Phase complete: Tests and Pact contracts defined"
  </action>

  <check if="GitHub Project linked">
    <action>Set TDD Phase to "RED"</action>
  </check>
</check>

<action>Display: ✅ RED phase committed</action>
</step>

<step n="9" goal="🟢 TDD GREEN PHASE - Implement controller">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🟢 TDD GREEN PHASE: Make Tests Pass
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Implementing REST controller with OpenAPI documentation.
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Generate Request DTO (if custom):
Create {request_dto}.java in {base_package}/presentation/rest/model/

Implementation:
- Java Record
- Jakarta Bean Validation annotations
- JSON property annotations (if needed)
- Conversion to use case input method
- Factory methods
- JavaDoc with example JSON
</action>

<action>Generate Response DTO (if custom):
Create {response_dto}.java in {base_package}/presentation/rest/model/

Implementation:
- Java Record
- Factory method from use case output
- JSON property annotations (if needed)
- JavaDoc with example JSON
</action>

<action>Generate REST Controller:
Create {controller_name}.java in {base_package}/presentation/rest/

Implementation:
- @RestController
- @RequestMapping("{base_path}")
- @Tag(name = "{tag}", description = "...")
- @RequiredArgsConstructor for use case injection
- @Slf4j for logging

Controller method:
- @{HttpMethod}Mapping("{path}")
- @Operation(summary = "{openapi_summary}", description = "...")
- @ApiResponses for all status codes
- @RequestBody with @Valid for input validation
- @PathVariable for path parameters (if any)
- @RequestParam for query parameters (if any)

Method logic:
1. Log incoming request
2. Validate request (happens automatically with @Valid)
3. Convert request DTO to use case input
4. Execute use case
5. Convert use case output to response DTO
6. Log response
7. Return ResponseEntity with appropriate status

Error handling:
- Validation errors caught by GlobalExceptionHandler (400)
- Use case exceptions mapped to appropriate HTTP status
- Proper error response format
</action>

<action>Update/Create GlobalExceptionHandler (if not exists):
Create GlobalExceptionHandler.java in {base_package}/presentation/rest/exception/

Handle:
- MethodArgumentNotValidException → 400 Bad Request
- EntityNotFoundException → 404 Not Found
- IllegalArgumentException → 400 Bad Request
- Custom domain exceptions → appropriate status
- Generic Exception → 500 Internal Server Error

Error response format:
- timestamp
- status
- error
- message
- path
- validationErrors (for validation failures)
</action>

<action>Display generated files:
✅ {request_dto}.java (request DTO)
✅ {response_dto}.java (response DTO)
✅ {controller_name}.java (REST controller)
✅ GlobalExceptionHandler.java (updated/created)
</action>
</step>

<step n="10" goal="🟢 Run tests to verify PASS (GREEN confirmation)">
<action>Display: Running tests to verify GREEN phase...</action>

<action>Run MockMvc tests:
mvn test -Dtest={controller_name}Test
</action>

<check if="tests still failing">
  <action>Fix implementation</action>
  <goto step="10">Re-test</goto>
</check>

<action>Run Pact consumer test:
mvn test -Dtest={controller_name}ConsumerPactTest
</action>

<action>Display: ✅ Pact contract generated: target/pacts/{consumer}-{provider}.json</action>

<action>Run Pact provider verification:
mvn test -Dtest={controller_name}ProviderPactTest
</action>

<check if="all tests pass">
  <action>Display:
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  🟢 GREEN PHASE CONFIRMED
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  ✅ {passed_count} tests passing
  📊 Coverage: {coverage}%
  ✅ Pact contract generated and verified

  REST endpoint implementation complete!
  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  </action>
</check>
</step>

<step n="11" goal="🟢 Commit GREEN phase">
<action>Stage implementation:
git add src/main/
git add target/pacts/ (Pact contracts)
</action>

<action>Commit:
git commit -m "feat: implement {http_method} {endpoint_path} endpoint (GREEN) #{issue_number}

Implemented REST API endpoint:

Controller:
- {controller_name}: REST controller with OpenAPI annotations
- Method: {http_method} {endpoint_path}
- Use Case: {use_case_name}

DTOs:
- {request_dto}: Request with validation
- {response_dto}: Response from use case

OpenAPI Documentation:
- Summary: {openapi_summary}
- Tags: {openapi_tags}
- Status Codes: {status_codes}
- Request/Response schemas

Pact Contract Testing:
- Consumer contract: {consumer}-{provider}.json
- Provider verification: Passing

Error Handling:
- GlobalExceptionHandler updated
- Validation errors: 400
- Not found: 404
- Server errors: 500

TDD-Phase: GREEN
Layer: Presentation
Test Coverage: {coverage}%

All {passed_count} tests passing including Pact verification."
</action>

<check if="GitHub remote exists">
  <action>Push and update:
  git push origin feature/{issue_number}-add-{endpoint_name_kebab}-endpoint
  gh issue edit {issue_number} --remove-label "tdd:red" --add-label "tdd:green"
  gh issue comment {issue_number} --body "🟢 GREEN Phase complete: Endpoint implemented and tested

Test Results:
- ✅ {passed_count} tests passing
- 📊 Coverage: {coverage}%
- ✅ Pact contract verified
- 📄 OpenAPI documentation generated

API: {http_method} {endpoint_path}
Swagger UI: http://localhost:8080/swagger-ui.html"
  </action>

  <check if="GitHub Project linked">
    <action>Set TDD Phase to "GREEN"</action>
  </check>
</check>

<action>Display: ✅ GREEN phase committed</action>
</step>

<step n="12" goal="🔵 TDD REFACTOR PHASE - Improve code quality">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔵 TDD REFACTOR PHASE: Improve Code Quality
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Analyze for refactoring:
1. Improve OpenAPI documentation completeness
2. Add more detailed error responses
3. Extract request/response conversion methods
4. Review validation annotations
5. Check logging is appropriate
6. Verify HTTP status codes are RESTful
7. Consider pagination (for collection endpoints)
8. Consider HATEOAS links (if applicable)
</action>

<action>Display refactoring suggestions</action>

<ask>Apply refactorings? [1. All / 2. Selective / 3. Skip]:</ask>

<check if="apply">
  <action for-each="refactoring">
    Apply refactoring
    Run tests to verify still green
  </action>
</check>
</step>

<step n="13" goal="🔵 Commit REFACTOR phase (if changes)">
<check if="refactorings applied">
  <action>Commit:
  git commit -m "refactor: improve {endpoint_path} endpoint #{issue_number}

Refactorings:
{list refactorings}

TDD-Phase: REFACTOR
Tests still passing: {passed_count} tests
Pact contract: Still verified"
  </action>

  <check if="GitHub remote exists">
    <action>Push and update:
    git push origin feature/{issue_number}-add-{endpoint_name_kebab}-endpoint
    gh issue edit {issue_number} --remove-label "tdd:green" --add-label "tdd:refactor"
    </action>

    <check if="GitHub Project">
      <action>Set TDD Phase to "REFACTOR"</action>
    </check>
  </check>
</check>
</step>

<step n="14" goal="Verify OpenAPI documentation">
<action>Display: 📄 Verifying OpenAPI documentation generation...</action>

<action>Start application:
mvn spring-boot:run (in background or separate terminal)
</action>

<action>Display:
🌐 OpenAPI Documentation Available:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

✅ Verify your endpoint appears in Swagger UI
✅ Test endpoint directly from Swagger UI
</action>

<ask>Is endpoint visible in Swagger UI and working correctly? [y/n]:</ask>

<check if="no">
  <action>Troubleshoot OpenAPI configuration</action>
</check>
</step>

<step n="15" goal="Run ArchUnit and complete test suite">
<action>Run ArchUnit: mvn test -Dtest=ArchitectureTest</action>

<check if="violations">
  <action>Fix and re-validate</action>
</check>

<action>Run complete test suite: mvn clean test</action>

<check if="all pass">
  <action>Display:
  ✅ Complete test suite passing
  ✅ ArchUnit validation passed
  📊 {total_tests} tests, {overall_coverage}% coverage
  </action>
</check>
</step>

<step n="16" goal="Create Pull Request">
<check if="GitHub remote exists">
  <action>Generate PR body</action>

  <action>Create PR:
  gh pr create \
    --title "Add {http_method} {endpoint_path} endpoint" \
    --body-file pr_body.md \
    --base develop \
    --head feature/{issue_number}-add-{endpoint_name_kebab}-endpoint \
    --label "type:endpoint,layer:presentation,tdd:complete"
  </action>

  <action>Update issue:
  gh issue comment {issue_number} --body "🎯 PR created: #{pr_number}

  API Endpoint: {http_method} {endpoint_path}
  Swagger UI: http://localhost:8080/swagger-ui.html
  Pact Contract: Verified ✅"
  gh issue edit {issue_number} --add-label "tdd:complete,status:in-review"
  </action>

  <check if="GitHub Project">
    <action>Move to "In Review", set TDD Phase to "Complete"</action>
  </check>

  <ask>Merge PR? [y/n]:</ask>

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
  <action>Merge locally</action>
</check>
</step>

<step n="17" goal="Present summary and next steps">
<action>Display:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ REST ENDPOINT COMPLETE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 Issue: #{issue_number} (Closed)
🔗 PR: #{pr_number} (Merged)

📦 Files Created:
Presentation Layer:
  ✅ {controller_name}.java (REST controller)
  ✅ {request_dto}.java (request DTO)
  ✅ {response_dto}.java (response DTO)
  ✅ GlobalExceptionHandler.java (error handling)

Tests:
  ✅ {controller_name}Test.java (MockMvc)
  ✅ {controller_name}ConsumerPactTest.java (Pact consumer)
  ✅ {controller_name}ProviderPactTest.java (Pact provider)

API Documentation:
  🌐 Swagger UI: http://localhost:8080/swagger-ui.html
  📄 OpenAPI Spec: http://localhost:8080/v3/api-docs

Contract Testing:
  ✅ Pact contract: target/pacts/{consumer}-{provider}.json
  ✅ Provider verification: Passing

📊 Test Results:
  ✅ {total_tests} tests passing
  📈 Coverage: {coverage}%

🎯 TDD Compliance:
  🔴 RED ✅ → 🟢 GREEN ✅ → 🔵 REFACTOR ✅

🚀 API Ready:
  {http_method} {endpoint_path}
  Status Codes: {status_codes}
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
</action>

<action>Suggest next steps:
💡 Next Steps:

1. Test API in Swagger UI
   - http://localhost:8080/swagger-ui.html
   - Try example requests
   - Verify responses

2. Add another endpoint (*add-rest-endpoint)
   - Build complete REST API

3. Add E2E tests
   - Test complete user journeys

4. Publish Pact contract
   - Share with other services
   - Enable contract testing in CI/CD

All workflows follow TDD automatically!
</action>

<ask>Would you like to:
1. Add another REST endpoint (with TDD)
2. Test current endpoint in Swagger UI
3. Exit

Choose [1/2/3]:</ask>

<check if="1">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml</invoke-workflow>
</check>

<check if="2">
  <action>Display:
  🌐 Open http://localhost:8080/swagger-ui.html in your browser
  📝 Find "{openapi_tags}" section
  ▶️ Click on {http_method} {endpoint_path}
  🧪 Try it out with example data!
  </action>
</check>

<check if="3">
  <action>Display: 🎉 REST endpoint complete! Happy coding! 🚀</action>
</check>
</step>

</workflow>
