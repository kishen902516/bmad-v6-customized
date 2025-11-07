# End-to-End Testing Plan
## Spring Boot Clean Architecture Generator Module

**Date:** 2025-11-07
**Status:** Ready for Testing
**Tester:** Kishen Sivalingam

---

## Prerequisites

### 1. Update Configuration File

**File:** `bmad/spring-boot-clean-arch/config.yaml`

**Add these required fields:**

```yaml
# Project Generation Settings
projects_output_path: '{project-root}/generated-projects'
default_scenario: 'enterprise'  # Options: simple-crud, enterprise, complex
include_examples: 'yes'  # Options: yes, no
archunit_strictness: 'standard'  # Options: relaxed, standard, strict

# Default Database
default_database: 'postgresql'  # Options: postgresql, mysql, h2

# Module Version
module_version: '1.0.0'
```

### 2. Verify Prerequisites Installed

```bash
# Check Java 21
java -version
# Expected: java version "21"

# Check Maven
mvn -version
# Expected: Apache Maven 3.9+

# Check Git
git --version

# Check GitHub CLI (optional for full Git Flow testing)
gh --version
```

### 3. Create Output Directory

```bash
mkdir -p generated-projects
```

---

## Test Suite

### Test 1: Bootstrap Project Workflow ⭐ CRITICAL

**Objective:** Generate a complete Spring Boot 3.2 Clean Architecture project

**Steps:**

1. **Invoke the Spring Architect agent:**
   ```
   /bmad:spring-boot-clean-arch:agents:spring-architect
   ```

2. **Run bootstrap-project command:**
   ```
   *bootstrap-project
   ```

3. **Provide test inputs:**
   - **Project name:** "Insurance Policy Service"
   - **Base package:** "com.insurance.policy"
   - **Description:** "Manages insurance policies with Clean Architecture"
   - **Scenario:** 2 (Enterprise Microservice)
   - **Database:** 1 (PostgreSQL)
   - **Additional starters:** spring-boot-starter-actuator, spring-boot-starter-validation
   - **ArchUnit strictness:** 1 (Use configured: standard)
   - **Include examples:** 1 (Policy Management example)
   - **Git Flow setup:** Yes (if prompted)
   - **GitHub integration:** No (for initial test)

**Expected Outputs:**

```
generated-projects/insurance-policy-service/
├── pom.xml                                  # Maven POM with Java 21, Spring Boot 3.2
├── src/
│   ├── main/
│   │   ├── java/com/insurance/policy/
│   │   │   ├── domain/                      # Domain layer (pure business logic)
│   │   │   │   ├── entity/
│   │   │   │   │   └── Policy.java          # If example included
│   │   │   │   ├── valueobject/
│   │   │   │   │   ├── PolicyNumber.java
│   │   │   │   │   ├── Money.java
│   │   │   │   │   └── ...
│   │   │   │   ├── port/
│   │   │   │   │   └── PolicyRepository.java  # Interface
│   │   │   │   └── service/
│   │   │   │       └── PremiumCalculator.java
│   │   │   ├── application/                 # Application layer
│   │   │   │   ├── dto/
│   │   │   │   ├── usecase/
│   │   │   │   └── service/
│   │   │   ├── infrastructure/              # Infrastructure layer
│   │   │   │   ├── adapter/
│   │   │   │   │   └── persistence/
│   │   │   │   │       ├── entity/
│   │   │   │   │       │   └── PolicyJpaEntity.java
│   │   │   │   │       ├── jpa/
│   │   │   │   │       │   └── PolicyJpaRepository.java
│   │   │   │   │       ├── mapper/
│   │   │   │   │       │   └── PolicyMapper.java
│   │   │   │   │       └── PolicyRepositoryImpl.java
│   │   │   │   └── config/
│   │   │   │       ├── DatabaseConfig.java
│   │   │   │       └── OpenApiConfig.java
│   │   │   ├── presentation/                # Presentation layer
│   │   │   │   └── rest/
│   │   │   │       ├── PolicyController.java
│   │   │   │       ├── model/
│   │   │   │       └── exception/
│   │   │   │           └── GlobalExceptionHandler.java
│   │   │   └── InsurancePolicyServiceApplication.java  # Main class
│   │   └── resources/
│   │       ├── application.properties       # Runtime config
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── application-test.properties
│   └── test/
│       └── java/com/insurance/policy/
│           ├── domain/
│           │   └── entity/
│           │       └── PolicyTest.java      # Unit tests
│           ├── application/
│           │   └── service/
│           │       └── CreatePolicyServiceTest.java
│           ├── infrastructure/
│           │   └── adapter/
│           │       └── persistence/
│           │           └── PolicyRepositoryIntegrationTest.java
│           ├── presentation/
│           │   └── rest/
│           │       └── PolicyControllerTest.java
│           └── architecture/
│               └── ArchitectureTest.java    # ArchUnit tests
├── .gitignore                               # Git ignore file
├── .git/ (if Git Flow enabled)              # Git repository
├── README.md                                # Project documentation
└── docs/
    └── TDD-WORKFLOW-GUIDE.md (if enabled)
```

**Verification Checklist:**

- [ ] Project directory created at expected path
- [ ] pom.xml has correct dependencies (Spring Boot 3.2, Java 21, ArchUnit, TestContainers)
- [ ] All four layers (domain, application, infrastructure, presentation) created
- [ ] Domain layer has NO framework dependencies (only pure Java)
- [ ] Example code included (Policy entity if selected)
- [ ] application.properties configured correctly
- [ ] .gitignore file present
- [ ] README.md generated
- [ ] Git repository initialized (if enabled)

---

### Test 2: Compile Generated Project

**Objective:** Verify generated code compiles without errors

**Steps:**

```bash
cd generated-projects/insurance-policy-service
mvn clean compile
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time:  X.XXX s
[INFO] Finished at: YYYY-MM-DDTHH:mm:ss
```

**Verification Checklist:**

- [ ] No compilation errors
- [ ] All classes compile successfully
- [ ] No missing imports
- [ ] No syntax errors

---

### Test 3: Run Tests

**Objective:** Verify generated tests pass

**Steps:**

```bash
cd generated-projects/insurance-policy-service
mvn test
```

**Expected Output:**
```
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Verification Checklist:**

- [ ] All unit tests pass
- [ ] Integration tests pass (if TestContainers configured)
- [ ] ArchUnit tests pass
- [ ] No test failures
- [ ] Test coverage > 80% (check with mvn jacoco:report if configured)

---

### Test 4: Validate Architecture with ArchUnit

**Objective:** Ensure generated code follows Clean Architecture rules

**Steps:**

```bash
cd generated-projects/insurance-policy-service
mvn test -Dtest=ArchitectureTest
```

**Expected Output:**
```
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO] All architecture rules satisfied
```

**ArchUnit Rules Verified:**

- [ ] Domain layer has no framework dependencies
- [ ] Application layer depends only on domain
- [ ] Repository interfaces in domain, implementations in infrastructure
- [ ] Controllers only in presentation layer
- [ ] No JPA annotations in domain layer
- [ ] No circular dependencies
- [ ] Naming conventions followed

---

### Test 5: Add Entity Workflow (TDD-enabled)

**Objective:** Add a new entity to existing project using TDD workflow

**Steps:**

1. **Navigate to project:**
   ```bash
   cd generated-projects/insurance-policy-service
   ```

2. **Invoke spring-architect agent with project context**

3. **Run add-entity command:**
   ```
   *add-entity
   ```

4. **Provide inputs:**
   - **Entity name:** "Claim"
   - **Entity description:** "Insurance claim for policy coverage"
   - **Attributes:**
     - claimNumber: String
     - policyNumber: String (reference to Policy)
     - claimedAmount: BigDecimal
     - incidentDate: LocalDate
     - status: ClaimStatus (enum)
   - **Value Objects:**
     - ClaimNumber
     - ClaimAmount
   - **Follow TDD:** Yes
   - **Create GitHub issue:** No (for testing)

**Expected Workflow:**

1. **🔴 RED Phase:**
   - Generate ClaimTest.java with failing tests
   - Generate ClaimRepositoryIntegrationTest.java with failing tests
   - Run tests → All FAIL (expected)
   - Commit: "test: add failing tests for Claim entity (RED)"

2. **🟢 GREEN Phase:**
   - Generate Claim.java (domain entity)
   - Generate ClaimJpaEntity.java (JPA entity)
   - Generate ClaimMapper.java
   - Generate ClaimRepository.java (interface)
   - Generate ClaimRepositoryImpl.java
   - Run tests → All PASS
   - Commit: "feat: implement Claim entity (GREEN)"

3. **🔵 REFACTOR Phase:**
   - Analyze for improvements
   - Extract value objects
   - Improve validation
   - Run tests → Still PASSING
   - Commit: "refactor: improve Claim entity design"

**Verification Checklist:**

- [ ] Claim entity created in domain/entity/
- [ ] ClaimJpaEntity created in infrastructure/
- [ ] ClaimRepository interface in domain/port/
- [ ] ClaimRepositoryImpl in infrastructure/adapter/persistence/
- [ ] All tests pass
- [ ] TDD commits made (RED → GREEN → REFACTOR)
- [ ] ArchUnit validation still passes

---

### Test 6: Add Use Case Workflow

**Objective:** Add a use case to work with the Claim entity

**Steps:**

1. **Run add-use-case command:**
   ```
   *add-use-case
   ```

2. **Provide inputs:**
   - **Use case name:** "SubmitClaim"
   - **Description:** "Submit a new insurance claim"
   - **Input DTO:**
     - policyNumber: String
     - claimedAmount: BigDecimal
     - incidentDate: LocalDate
     - description: String
   - **Output DTO:**
     - claimNumber: String
     - status: String
     - submittedAt: LocalDateTime
   - **Dependencies:** ClaimRepository, PolicyRepository
   - **Follow TDD:** Yes

**Expected Outputs:**

```
application/
├── dto/
│   ├── SubmitClaimInput.java (Record)
│   └── SubmitClaimOutput.java (Record)
├── usecase/
│   └── SubmitClaimUseCase.java (Interface)
└── service/
    └── SubmitClaimService.java (Implementation with @Service, @Transactional)

test/
└── application/
    └── service/
        └── SubmitClaimServiceTest.java (with mocked repositories)
```

**Verification Checklist:**

- [ ] Use case interface created
- [ ] Use case service implementation created
- [ ] Input/Output DTOs created as Records
- [ ] Dependencies injected via constructor
- [ ] @Transactional annotation present
- [ ] Tests pass with mocked dependencies
- [ ] TDD workflow followed

---

### Test 7: Add REST Endpoint Workflow

**Objective:** Expose the SubmitClaim use case via REST API

**Steps:**

1. **Run add-rest-endpoint command:**
   ```
   *add-rest-endpoint
   ```

2. **Provide inputs:**
   - **Use case to expose:** SubmitClaimUseCase
   - **HTTP method:** POST
   - **Endpoint path:** /api/v1/claims
   - **Request DTO:** CreateClaimRequest
   - **Response DTO:** ClaimResponse
   - **Status code on success:** 201 (Created)
   - **Include OpenAPI docs:** Yes
   - **Follow TDD:** Yes

**Expected Outputs:**

```
presentation/rest/
├── ClaimController.java (@RestController with OpenAPI annotations)
├── model/
│   ├── CreateClaimRequest.java (Record)
│   └── ClaimResponse.java (Record)
└── exception/ (if not exists)
    └── GlobalExceptionHandler.java

test/presentation/rest/
└── ClaimControllerTest.java (MockMvc tests)
```

**Verification Checklist:**

- [ ] Controller created with @RestController
- [ ] POST /api/v1/claims endpoint defined
- [ ] OpenAPI annotations present (@Operation, @ApiResponse)
- [ ] Request validation with @Valid
- [ ] Proper HTTP status codes (201 Created, 400 Bad Request, etc.)
- [ ] MockMvc tests pass
- [ ] Integration with use case verified

---

### Test 8: Full Build and Package

**Objective:** Verify complete project builds and packages as JAR

**Steps:**

```bash
cd generated-projects/insurance-policy-service
mvn clean package
```

**Expected Output:**
```
[INFO] Building jar: target/insurance-policy-service-1.0.0-SNAPSHOT.jar
[INFO] BUILD SUCCESS
```

**Verification Checklist:**

- [ ] Project builds successfully
- [ ] All tests pass during build
- [ ] ArchUnit tests pass
- [ ] JAR file created in target/
- [ ] JAR includes all dependencies (if spring-boot-maven-plugin configured)

---

### Test 9: Run Application

**Objective:** Verify application starts successfully

**Steps:**

```bash
cd generated-projects/insurance-policy-service

# Update application.properties with test database
# OR use H2 in-memory for quick test

mvn spring-boot:run
```

**Expected Output:**
```
Started InsurancePolicyServiceApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

**Verification Checklist:**

- [ ] Application starts without errors
- [ ] No bean creation failures
- [ ] Database connection successful (or H2 in-memory works)
- [ ] Actuator endpoints accessible (if included)
- [ ] Swagger UI accessible at http://localhost:8080/swagger-ui.html
- [ ] OpenAPI docs at http://localhost:8080/api-docs

---

### Test 10: API Functionality Test

**Objective:** Test REST endpoints work correctly

**Steps:**

```bash
# Submit a claim
curl -X POST http://localhost:8080/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber": "POL-2024-000001",
    "claimedAmount": 5000.00,
    "incidentDate": "2024-11-01",
    "description": "Vehicle collision at intersection"
  }'

# Expected Response (201 Created):
{
  "claimNumber": "CLM-2024-000001",
  "status": "SUBMITTED",
  "submittedAt": "2024-11-07T10:30:00"
}
```

**Verification Checklist:**

- [ ] POST request succeeds
- [ ] Response has correct structure
- [ ] Claim saved to database
- [ ] Validation works (try invalid data)
- [ ] Error responses formatted correctly

---

## Test Results Summary

### Test Execution Tracking

| Test # | Test Name | Status | Duration | Issues Found |
|--------|-----------|--------|----------|--------------|
| 1 | Bootstrap Project | ⏳ Pending | - | - |
| 2 | Compile Generated Code | ⏳ Pending | - | - |
| 3 | Run Tests | ⏳ Pending | - | - |
| 4 | Validate Architecture | ⏳ Pending | - | - |
| 5 | Add Entity (TDD) | ⏳ Pending | - | - |
| 6 | Add Use Case | ⏳ Pending | - | - |
| 7 | Add REST Endpoint | ⏳ Pending | - | - |
| 8 | Full Build & Package | ⏳ Pending | - | - |
| 9 | Run Application | ⏳ Pending | - | - |
| 10 | API Functionality | ⏳ Pending | - | - |

**Legend:** ⏳ Pending | ✅ Passed | ❌ Failed | ⚠️ Issues Found

---

## Issues & Resolution Log

### Issue Template

```markdown
**Issue #:**
**Test:**
**Severity:** Critical / High / Medium / Low
**Description:**
**Steps to Reproduce:**
1.
2.
3.

**Expected Behavior:**

**Actual Behavior:**

**Root Cause:**

**Resolution:**

**Status:** Open / In Progress / Resolved
```

---

## Success Criteria

### Minimum Viable Product (MVP)

✅ **PASS Criteria:**
- [ ] bootstrap-project generates valid Spring Boot project
- [ ] Generated code compiles without errors
- [ ] All generated tests pass
- [ ] ArchUnit validation passes
- [ ] add-entity workflow creates entity with tests
- [ ] add-use-case workflow creates use case with tests
- [ ] add-rest-endpoint workflow creates controller with tests
- [ ] Full project builds and packages successfully
- [ ] Application starts and runs
- [ ] REST endpoints functional

### Quality Metrics

**Code Quality:**
- [ ] No compilation warnings
- [ ] Code follows Java naming conventions
- [ ] Proper JavaDoc on public methods
- [ ] No code smells (duplicate code, long methods, etc.)

**Test Quality:**
- [ ] Test coverage > 80%
- [ ] All layers tested (unit, integration, contract, architecture)
- [ ] Tests follow Given-When-Then structure
- [ ] Meaningful test names with @DisplayName

**Architecture Quality:**
- [ ] Clean Architecture layers respected
- [ ] Domain layer has zero framework dependencies
- [ ] Dependency rule enforced (dependencies point inward)
- [ ] Proper separation of concerns

---

## Next Steps After Testing

### If All Tests Pass ✅

1. **Document test results**
2. **Create video walkthrough** (optional)
3. **Update module README** with test results
4. **Mark module as "Production Ready"**
5. **Publish module** for wider use

### If Tests Fail ❌

1. **Log all issues** in Issues & Resolution Log
2. **Prioritize fixes** (Critical → High → Medium → Low)
3. **Fix issues** in templates, workflows, or agents
4. **Re-test** affected areas
5. **Iterate** until all tests pass

---

## Testing Notes

**Environment:**
- OS: Windows 11
- Java: 21
- Maven: 3.9+
- IDE: (your preferred IDE)

**Date Started:**
**Date Completed:**
**Total Issues Found:**
**Critical Issues:**
**Module Status:**

---

**Ready to begin testing!** 🚀

Start with **Test 1: Bootstrap Project Workflow** and work through sequentially.
