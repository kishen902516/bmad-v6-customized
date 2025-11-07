# Add REST Endpoint Workflow Instructions

<critical>This workflow adds a REST API endpoint with OpenAPI documentation and complete contract tests</critical>
<critical>Communicate with {user_name} in {communication_language} throughout this workflow</critical>

<workflow>

<step n="1" goal="Identify target project and scan use cases">
<action>Validate target_project_path or ask for it</action>
<action>Scan for existing use cases in application.usecase package</action>
<action>Display available use cases to expose via REST</action>
</step>

<step n="2" goal="Define REST endpoint">
<ask>Which use case do you want to expose as REST endpoint?
{list available use cases}</ask>
<action>Store selected_use_case</action>

<ask>HTTP method?
1. POST (for commands/creation)
2. GET (for queries)
3. PUT (for updates)
4. DELETE (for deletion)
5. PATCH (for partial updates)

Choose [1/2/3/4/5]:</ask>
<action>Store http_method</action>

<ask>URL path? (e.g., "/api/v1/policies", "/api/v1/claims/{id}"):</ask>
<action>Store url_path</action>
<action>Validate path format and extract path variables</action>
</step>

<step n="3" goal="Define request and response models">
<action>Analyze selected use case input/output DTOs</action>

<ask>Use existing DTOs or create API-specific request/response models?
1. Use existing DTOs directly
2. Create API-specific models (recommended for API versioning)

Choose [1/2]:</ask>

<check if="create API models">
  <action>Generate {Entity}Request Record with API-specific validations</action>
  <action>Generate {Entity}Response Record with API-specific fields</action>
  <action>Create mapper between API models and use case DTOs</action>
</check>
</step>

<step n="4" goal="Generate REST controller">
<action>Load controller template from {controller_template}</action>

<action>Generate controller using template with replacements:
- {{ENTITY_NAME}} → Entity name
- {{BASE_PACKAGE}} → {base_package}
- {{HTTP_METHOD}} → {http_method}
- {{URL_PATH}} → {url_path}
- {{USE_CASE}} → {selected_use_case}
- Include @RestController and @RequestMapping
- Add OpenAPI annotations (@Operation, @ApiResponse, @Tag)
- Inject use case dependency
- Add endpoint method with proper annotations
- Include request validation (@Valid annotation)
- Return ResponseEntity with appropriate HTTP status
- Delegate exception handling to @ControllerAdvice
</action>
<action>Write to {base_package}/presentation/rest/{Entity}Controller.java</action>
</step>

<step n="5" goal="Generate exception handler if not exists">
<check if="GlobalExceptionHandler not exists">
  <action>Load exception handler template from {exception_handler_template}</action>
  <action>Generate @ControllerAdvice with handlers for common exceptions</action>
  <action>Write to {base_package}/presentation/rest/exception/GlobalExceptionHandler.java</action>
</check>
</step>

<step n="6" goal="Generate contract tests">
<action>Load Test Engineer agent context from {test_engineer_agent}</action>
<action>Load controller test template from {controller_test_template}</action>
<action>Reference PACT testing guide from {pact_testing_guide}</action>

<action>Generate contract tests using template:
- {{ENTITY_NAME}} → Entity name
- {{BASE_PACKAGE}} → {base_package}
- {{HTTP_METHOD}} → {http_method}
- {{URL_PATH}} → {url_path}
- Include @WebMvcTest({Entity}Controller.class)
- Add MockMvc setup
- Mock use case dependency
- Test successful request/response
- Test validation failures (400 Bad Request)
- Test not found scenarios (404)
- Test error handling (500 Internal Server Error)
- Verify OpenAPI compliance
</action>
<action>Write to test/{base_package}/presentation/rest/{Entity}ControllerContractTest.java</action>
</step>

<step n="7" goal="Validate and test">
<action>Load Architecture Validator agent context from {arch_validator_agent}</action>
<action>Run ArchUnit validation</action>
<action>Run contract tests: mvn test -Dtest=*Controller*</action>
<action>Start application and verify Swagger UI: http://localhost:8080/swagger-ui.html</action>
</step>

<step n="8" goal="Present summary">
<action>Display endpoint summary to {user_name}:
- Endpoint: {http_method} {url_path}
- Controller: {Entity}Controller
- Use Case: {selected_use_case}
- OpenAPI: ✅ Documented
- Tests: ✅ Contract tests passing
- Swagger UI: http://localhost:8080/swagger-ui.html
</action>

<ask>Add another endpoint? [yes/no]:</ask>
<check if="yes">
  <goto step="2">Add another endpoint</goto>
</check>
</step>

</workflow>
