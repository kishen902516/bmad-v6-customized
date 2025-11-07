# How to Test BMAD Workflows - Complete Step-by-Step Guide

**Date:** 2025-11-07
**Author:** BMad Builder
**Purpose:** Detailed instructions for testing workflows using the Task tool

---

## Overview: The Cooking Method

Testing a workflow is like following a recipe:

1. **Launch Task tool** (general-purpose agent) - Get your cooking assistant
2. **Point it to workflow.yaml** - Give it the recipe
3. **Provide test inputs** - Supply the ingredients
4. **Let it simmer** (execute autonomously) - Let it cook
5. **Serve** (review generated project) - Enjoy the result

---

## STEP 1: Launch Task Tool (General-Purpose Agent)

### What You're Doing

You're asking Claude Code to launch a specialized sub-agent that can autonomously execute your workflow by reading files, generating code, compiling, and testing.

### How to Do It

**In your Claude Code chat, type a message like:**

```
Please use the Task tool with the general-purpose agent to test my bootstrap-project workflow.

Here are the details you need...
[Then provide the information from Steps 2 and 3 below]
```

**OR, if you're comfortable with function calls, you can be more direct:**

Simply describe what you want the agent to do in natural language, and Claude Code will understand that you want to use the Task tool.

### Parameters You'll Provide

1. **subagent_type:** `general-purpose`
2. **description:** A short 3-5 word summary (e.g., "Execute bootstrap-project workflow")
3. **prompt:** Detailed instructions (see Step 2 & 3)

---

## STEP 2: Point to Workflow.yaml

### What You're Doing

You're telling the agent exactly which workflow to execute and where to find it.

### Information to Provide

In your prompt to the agent, include these file paths:

```
**Context Files:**
- Workflow configuration: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/workflows/bootstrap-project/workflow.yaml
- Workflow instructions: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/workflows/bootstrap-project/instructions.md
- Workflow execution engine: c:/development/bmad-v6-customized/bmad/core/tasks/workflow.xml
- Module config: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/config.yaml
```

### Why Each File Matters

| File | Purpose |
|------|---------|
| **workflow.yaml** | Configuration - defines variables, paths, settings |
| **instructions.md** | The actual workflow steps to execute |
| **workflow.xml** | The execution engine - rules for how to run workflows |
| **config.yaml** | Module configuration - values to substitute into workflow |

### Example Workflow Structure

```yaml
# workflow.yaml example
name: bootstrap-project
config_source: "{project-root}/bmad/spring-boot-clean-arch/config.yaml"
projects_output_path: "{config_source}:projects_output_path"
installed_path: "{project-root}/bmad/spring-boot-clean-arch/workflows/bootstrap-project"
instructions: "{installed_path}/instructions.md"
```

The agent will:
1. Read workflow.yaml
2. See that config_source points to config.yaml
3. Load config.yaml and resolve all {variables}
4. Read instructions.md
5. Execute each step

---

## STEP 3: Provide Test Inputs

### What You're Doing

You're simulating what a real user would enter when running the workflow interactively.

### Test Inputs for Bootstrap-Project Workflow

```
**Test Inputs:**
- Project name: Insurance Policy Service
- Base package: com.insurance.policy
- Project description: Manages insurance policies with Clean Architecture
- Scenario: enterprise (Enterprise Microservice)
- Database: postgresql
- Additional starters: spring-boot-starter-actuator, spring-boot-starter-validation
- ArchUnit strictness: standard (use configured)
- Include examples: yes (Policy Management example)
- Git Flow setup: no (skip for testing)
- GitHub integration: no (skip for testing)
```

### Why These Values?

- **Project name:** Clear, descriptive name for the test project
- **Base package:** Valid Java package naming (lowercase, dot-separated)
- **Scenario: enterprise:** Tests the full Clean Architecture setup
- **Database: postgresql:** Most common production database
- **Examples: yes:** Tests that example integration works
- **Git/GitHub: no:** Simplifies testing (can test separately)

### How to Format in Your Prompt

```
The agent should use these inputs when the workflow asks questions:
- When asked "What is your project name?": Answer "Insurance Policy Service"
- When asked "What is your base package?": Answer "com.insurance.policy"
- When asked "Which scenario?": Choose option 2 (Enterprise Microservice)
[etc...]
```

---

## STEP 4: Execute Autonomously

### What the Agent Will Do

Once you provide the workflow path and test inputs, the agent autonomously:

1. ✅ **Reads** workflow.yaml and config.yaml
2. ✅ **Resolves** all {variables} from config
3. ✅ **Reads** instructions.md (the workflow steps)
4. ✅ **Executes** each step sequentially
5. ✅ **Reads** templates from templates/ directory
6. ✅ **Reads** example code from data/examples/
7. ✅ **Generates** all project files (pom.xml, Java files, configs, tests)
8. ✅ **Compiles** code using `mvn clean compile`
9. ✅ **Runs** tests using `mvn test`
10. ✅ **Reports** results back to you

### What You'll See

The agent will show you:
- Files it's creating
- Code it's generating
- Commands it's running
- Test results
- Any errors encountered

### Expected Timeline

- **Simple workflow:** 30-60 seconds
- **Complex workflow (like bootstrap-project):** 2-5 minutes
- **With compilation and testing:** 5-10 minutes

### Signs It's Working

You'll see output like:
```
Reading workflow configuration...
✅ Loaded workflow.yaml
✅ Loaded config.yaml
✅ Resolved variables

Executing Step 1: Generate Maven project structure...
✅ Created pom.xml
✅ Created src/main/java directory structure

Executing Step 2: Generate domain layer...
✅ Created Policy.java
✅ Created PolicyNumber.java
...

Running compilation...
mvn clean compile
✅ BUILD SUCCESS

Running tests...
mvn test
✅ Tests run: 21, Failures: 0, Errors: 0
```

---

## STEP 5: Review Generated Project

### What to Check

After the agent completes, verify the output:

#### 1. **Project Structure**

```bash
cd generated-projects/insurance-policy-service
ls -la
```

Expected:
```
pom.xml
src/
├── main/
│   ├── java/
│   │   └── com/insurance/policy/
│   │       ├── domain/
│   │       ├── application/
│   │       ├── infrastructure/
│   │       └── presentation/
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/insurance/policy/
```

#### 2. **Compilation Success**

```bash
mvn clean compile
```

Expected output:
```
[INFO] BUILD SUCCESS
```

#### 3. **Tests Passing**

```bash
mvn test
```

Expected output:
```
Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

#### 4. **Architecture Validation**

```bash
mvn test -Dtest=ArchitectureTest
```

Expected: All ArchUnit rules passing

#### 5. **Code Quality**

Check that generated code has:
- ✅ Proper package structure
- ✅ Clean Architecture layers
- ✅ No compilation warnings
- ✅ Complete JavaDoc
- ✅ Tests for all layers

---

## Complete Example: Full Prompt

Here's what you would tell Claude Code to test the bootstrap-project workflow:

```
Please test my Spring Boot Clean Architecture Generator's bootstrap-project workflow
using the Task tool with a general-purpose agent.

**Workflow Files:**
- Workflow config: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/workflows/bootstrap-project/workflow.yaml
- Instructions: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/workflows/bootstrap-project/instructions.md
- Execution engine: c:/development/bmad-v6-customized/bmad/core/tasks/workflow.xml
- Module config: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/config.yaml

**Test Inputs:**
- Project name: Insurance Policy Service
- Base package: com.insurance.policy
- Description: Manages insurance policies with Clean Architecture
- Scenario: enterprise
- Database: postgresql
- Additional starters: spring-boot-starter-actuator, spring-boot-starter-validation
- ArchUnit strictness: standard
- Include examples: yes (Policy Management)
- Git Flow: skip
- GitHub: skip

**Output Location:**
c:/development/bmad-v6-customized/generated-projects/insurance-policy-service/

**Template Sources:**
- Templates: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/templates/
- Examples: c:/development/bmad-v6-customized/bmad/spring-boot-clean-arch/data/examples/

**Tasks for the Agent:**
1. Load the workflow.yaml and resolve all variables from config.yaml
2. Read and execute the instructions.md step-by-step
3. Generate the complete Spring Boot project structure
4. Use templates and example code as specified
5. Compile the generated code (mvn clean compile)
6. Run all tests (mvn test)
7. Report results including:
   - File structure created
   - Compilation status
   - Test results
   - Any issues found

Please create actual files (not just describe them) and run real compilation/tests.
```

---

## Testing Different Workflows

### Add-Entity Workflow

```
**Workflow:** add-entity
**Context:** Already have a generated project
**Test Inputs:**
- Entity name: Claim
- Attributes: claimNumber (String), amount (BigDecimal), status (ClaimStatus enum)
- Include repository: yes
- Include tests: yes
- Follow TDD: yes
```

### Add-Use-Case Workflow

```
**Workflow:** add-use-case
**Context:** Have a project with entities
**Test Inputs:**
- Use case name: SubmitClaim
- Input DTO: claimNumber, amount, description
- Output DTO: claimId, status, submittedAt
- Dependencies: ClaimRepository
```

### Add-REST-Endpoint Workflow

```
**Workflow:** add-rest-endpoint
**Context:** Have use cases defined
**Test Inputs:**
- Use case to expose: SubmitClaimUseCase
- HTTP method: POST
- Path: /api/v1/claims
- Include OpenAPI docs: yes
```

---

## Troubleshooting

### Issue: "Workflow file not found"

**Cause:** Incorrect path
**Solution:** Use absolute paths starting with c:/development/...

### Issue: "Variable not resolved"

**Cause:** Missing field in config.yaml
**Solution:** Add required field to config.yaml

### Issue: "Template not found"

**Cause:** Template file doesn't exist
**Solution:** Verify template exists at specified path

### Issue: "Compilation failed"

**Cause:** Invalid template or missing imports
**Solution:** Check generated code for syntax errors

### Issue: "Tests failed"

**Cause:** Test template issues or incomplete code
**Solution:** Review test failures and fix templates

### Issue: "Agent doesn't create files"

**Cause:** Not explicitly told to create files
**Solution:** Add "Please create actual files (not just describe them)" to prompt

---

## Tips for Success

### 1. Be Explicit
Don't say "generate a project", say "create actual files at [path]"

### 2. Provide All Paths
Give absolute paths to all required files

### 3. Use Test Inputs
Always provide specific test values, not "user will choose"

### 4. Request Verification
Ask agent to compile and test the generated code

### 5. Ask for Reports
Request detailed output showing what was created

### 6. One Workflow at a Time
Test workflows sequentially, not all at once

### 7. Check Prerequisites
Ensure config.yaml has all required fields first

### 8. Review Results
Always manually verify the generated output

---

## Comparison: Manual vs Task Tool

### Manual Testing

```
1. Read instructions.md yourself
2. Manually create each file
3. Copy-paste from templates
4. Run mvn commands yourself
5. Fix issues manually
```

**Time:** 2-4 hours
**Accuracy:** Depends on attention to detail
**Reproducibility:** Low (manual errors)

### Task Tool Testing

```
1. Provide workflow path and inputs
2. Agent generates everything
3. Agent compiles and tests
4. Agent reports results
```

**Time:** 5-10 minutes
**Accuracy:** High (follows instructions exactly)
**Reproducibility:** High (same inputs = same output)

---

## Next Steps

After successfully testing a workflow:

1. ✅ **Document test results** in TESTING-PLAN.md
2. ✅ **Review generated code** for quality
3. ✅ **Test edge cases** with different inputs
4. ✅ **Fix any issues** found in templates
5. ✅ **Test next workflow** in sequence
6. ✅ **Mark workflow as validated** when perfect

---

## Summary

**To test any BMAD workflow:**

1. Tell Claude Code: "Use the Task tool with general-purpose agent"
2. Provide: Workflow path, config path, test inputs, output location
3. Specify: "Create actual files and run tests"
4. Wait: Agent executes autonomously (5-10 min)
5. Verify: Check compilation, tests, and code quality

**The Task tool automates what would take hours manually!**

---

## Example Success Output

```
✅ Workflow Execution: SUCCESSFUL

Generated:
- 24 Java files
- Complete Clean Architecture structure
- pom.xml with all dependencies
- Complete test suite

Compilation: ✅ SUCCESS (6.2s)
Tests: ✅ 21/21 passing (10.4s)
Architecture: ✅ All rules enforced

Project ready at:
c:/development/bmad-v6-customized/generated-projects/insurance-policy-service/
```

---

**Ready to test your workflows!** 🚀
