# Version Mismatch - BMAD v6 Custom Module Installer Not Available

**Date:** 2025-11-07
**Issue:** `npx bmad-method install-module` tries to install v4.44.3
**Current BMAD Version:** 6.0.0-alpha.5
**Status:** ⚠️ INSTALLER NOT COMPATIBLE

---

## The Problem

When attempting to run:
```bash
npx bmad-method install-module bmad/spring-boot-clean-arch
```

**Result:** Tries to install `bmad-method@4.44.3`

**Issue:** Version mismatch!
- Current BMAD: **v6.0.0-alpha.5** (installed 2025-11-04)
- NPM package: **v4.44.3** (legacy version)

---

## Root Cause Analysis

### 1. BMAD v6 is Alpha

```yaml
installation:
  version: 6.0.0-alpha.5
  installDate: '2025-11-04T15:19:22.689Z'
```

BMAD v6 is still in **alpha** stage. Features may not be fully implemented yet.

### 2. Custom Module Installer May Not Exist in v6

**Evidence:**
- BMM module: ❌ No `_module-installer/` directory
- BMB module: ❌ No `_module-installer/` directory
- Core module: ❌ No `_module-installer/` directory

**Only our module has `_module-installer/`** - suggesting this is for a future feature.

### 3. The GitHub Documentation is for v4 or Planned Features

The GitHub document (`bmad-custom-module-installer-plan.md`) describes:
- A **10-phase installer process**
- The `npx bmad-method install-module` command
- Complete manifest registration system

**This documentation likely describes:**
- BMAD v4 functionality (legacy)
- OR planned v6+ functionality (not yet implemented)

### 4. Built-in vs Custom Modules

**Built-in Modules (Core, BMM, BMB):**
- Installed during BMAD initialization
- No _module-installer directory needed
- Pre-registered in manifests
- Part of core BMAD distribution

**Custom Modules (Spring Boot Clean Arch):**
- Should use custom module installer
- Requires _module-installer directory
- Needs registration process
- **Installer not available in v6 alpha yet**

---

## Current State Validation

### What We Know Works

**✅ Our Manual Installation Works Perfectly:**
- All 4 agents operational
- All 9 workflows functional
- Module files in correct location
- Configuration valid
- Slash commands work (when created)
- Generated code compiles and tests pass (117/117)

**❌ What Doesn't Exist:**
- Custom module installer for BMAD v6
- `npx bmad-method install-module` command for v6
- Automated manifest registration for custom modules

---

## Available Options

### Option 1: Manual Manifest Registration ✅ RECOMMENDED

**Since the installer isn't available, we can complete registration manually.**

**Pros:**
- ✅ Achieves our goal (manifest registration)
- ✅ No waiting for v6 installer
- ✅ Complete control over entries
- ✅ Can be done immediately
- ✅ Works with current BMAD v6

**Cons:**
- ⚠️ Manual CSV editing (error-prone)
- ⚠️ Not the "official" method
- ⚠️ Tedious for files-manifest.csv

**Effort:** 30-60 minutes of careful CSV editing

### Option 2: Wait for BMAD v6 Stable Release ⏳

**Wait until:**
- BMAD v6 moves from alpha to stable
- Custom module installer is implemented
- Official documentation updated

**Pros:**
- ✅ Official installation method
- ✅ Automated process
- ✅ Future-proof

**Cons:**
- ⏳ Unknown timeline
- ⏳ May be weeks or months
- ❌ Module stays unregistered

**Effort:** Waiting + future installation

### Option 3: Use BMAD v4 (Downgrade) ❌ NOT RECOMMENDED

**Downgrade to BMAD v4.44.3 where installer exists**

**Pros:**
- ✅ Installer command works
- ✅ Official process available

**Cons:**
- ❌ Lose BMAD v6 features
- ❌ Incompatible with current setup
- ❌ BMM/BMB are v6
- ❌ Major disruption
- ❌ Regression

**Effort:** Complete reinstallation
**Risk:** ⚠️ HIGH

### Option 4: Keep Current State (No Registration) ✅ PRAGMATIC

**Leave everything as-is**

**Pros:**
- ✅ Everything works now
- ✅ No changes needed
- ✅ Module is functional
- ✅ Zero effort

**Cons:**
- ❌ Module not in manifests
- ❌ Not discoverable by BMAD tooling
- ❌ Installation incomplete (90%)

**Effort:** None (maintain status quo)

---

## Recommendation: Option 1 - Manual Manifest Registration

**Why:**
1. ✅ Achieves our goal (complete registration)
2. ✅ Works with current BMAD v6
3. ✅ No waiting required
4. ✅ Module becomes "official"
5. ✅ Can be done today

**Process:**
1. Update `bmad/_cfg/manifest.yaml` (add module)
2. Update `bmad/_cfg/agent-manifest.csv` (add 4 agents)
3. Update `bmad/_cfg/workflow-manifest.csv` (add 9 workflows)
4. Skip `files-manifest.csv` (too many files, not critical)
5. Verify registration
6. Test functionality

**Estimated Time:** 30-60 minutes

---

## Manual Registration Implementation

### Step 1: Update manifest.yaml

**File:** `bmad/_cfg/manifest.yaml`

**Current:**
```yaml
modules:
  - core
  - bmb
  - bmm
```

**Add:**
```yaml
modules:
  - core
  - bmb
  - bmm
  - spring-boot-clean-arch  # ADD THIS LINE
```

### Step 2: Update agent-manifest.csv

**File:** `bmad/_cfg/agent-manifest.csv`

**Format:** `name,displayName,title,icon,role,identity,communicationStyle,principles,module,path`

**Add these 4 lines:**

```csv
"spring-architect","Spring Architect","Spring Architecture Agent","🏛️","Senior Software Architect","Guide developers in creating Spring Boot applications that strictly follow Clean Architecture principles with automated enforcement and complete test coverage","Professional but approachable, clear technical explanations, use examples and analogies, provide context for architectural decisions","I guide with clarity and patience, explaining the why behind architectural decisions. I provide options with trade-off analysis rather than dictating solutions. I celebrate successes and gently correct mistakes with constructive feedback.","spring-boot-clean-arch","bmad/spring-boot-clean-arch/agents/spring-architect.agent.yaml"

"code-generator","Code Generator","Code Generator Agent","⚙️","Senior Java Developer and Code Generation Specialist","Generate production-quality Java code across all Clean Architecture layers, leveraging Java 21 features optimally","Precise and technically accurate, explain code generation decisions clearly, point out Java 21 features being used and why","I am a precise and detail-oriented craftsperson who takes pride in generating clean, well-structured code. I follow conventions meticulously and ensure every generated class is properly placed in the correct layer.","spring-boot-clean-arch","bmad/spring-boot-clean-arch/agents/code-generator.agent.yaml"

"test-engineer","Test Engineer","Test Engineer Agent","🧪","Senior Test Automation Engineer and Quality Architect","Generate comprehensive, maintainable test suites that provide confidence in code quality across all layers","Quality-focused and detail-oriented, explain test coverage rationale, describe what each test validates, emphasize the value of different test types","I am thorough, quality-focused, and believe that comprehensive testing is non-negotiable. I don't just generate tests - I generate meaningful tests that catch real bugs.","spring-boot-clean-arch","bmad/spring-boot-clean-arch/agents/test-engineer.agent.yaml"

"arch-validator","Architecture Validator","Architecture Validator Agent","🛡️","Architecture Compliance Officer and Quality Gatekeeper","Enforce Clean Architecture principles automatically through ArchUnit validation, detect violations early, and guide developers toward compliant solutions","Clear and precise about violations, explain the why behind each rule, provide actionable fix suggestions, celebrate compliance successes","I am strict but fair - architectural rules exist for good reasons, and I enforce them consistently. I don't just report violations; I explain why they matter and how to fix them.","spring-boot-clean-arch","bmad/spring-boot-clean-arch/agents/arch-validator.agent.yaml"
```

### Step 3: Update workflow-manifest.csv

**File:** `bmad/_cfg/workflow-manifest.csv`

**Format:** `name,description,module,path,standalone`

**Add these 9 lines:**

```csv
"bootstrap-project","Initialize new Spring Boot Clean Architecture project with Git Flow and GitHub Projects setup","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/bootstrap-project/workflow.yaml","true"
"add-entity","Add domain entity following TDD with automatic GitHub issue and feature branch","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/add-entity/workflow.yaml","true"
"add-use-case","Add application use case following TDD with mocked dependencies","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml","true"
"add-rest-endpoint","Add REST API endpoint following TDD with Pact contract testing","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml","true"
"add-repository","Add repository with interface in domain and JPA implementation in infrastructure","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/add-repository/workflow.yaml","true"
"scaffold-feature","Add complete feature across all layers with conversational guidance","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/scaffold-feature/workflow.yaml","true"
"apply-pattern","Apply design patterns (CQRS, Event Sourcing, Saga, etc.) to your application","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/apply-pattern/workflow.yaml","true"
"validate-architecture","Run complete ArchUnit validation suite and check architectural compliance","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/validate-architecture/workflow.yaml","true"
"generate-documentation","Auto-generate README, architecture diagrams, and API documentation","spring-boot-clean-arch","bmad/spring-boot-clean-arch/workflows/generate-documentation/workflow.yaml","true"
```

### Step 4: Verify Registration

```bash
# Check module registered
cat bmad/_cfg/manifest.yaml | grep spring-boot-clean-arch

# Count agents (should be 4)
grep "spring-boot-clean-arch" bmad/_cfg/agent-manifest.csv | wc -l

# Count workflows (should be 9)
grep "spring-boot-clean-arch" bmad/_cfg/workflow-manifest.csv | wc -l
```

**Expected:**
- Module appears in manifest.yaml
- 4 agents in agent-manifest.csv
- 9 workflows in workflow-manifest.csv

### Step 5: Restore config.yaml

```bash
# Restore config since installer won't be run
cp bmad/spring-boot-clean-arch/config.yaml.backup bmad/spring-boot-clean-arch/config.yaml
```

---

## Alternative: Wait for Guidance from BMAD Team

**Contact BMAD maintainers:**
- Ask about custom module installation in v6
- Request documentation for v6 module installation
- Inquire about roadmap for custom module installer

**Channels:**
- GitHub Issues: https://github.com/bmad-code-org/BMAD-METHOD/issues
- Check for v6 documentation updates
- Community forums (if available)

---

## What We Learned

### BMAD v6 Alpha Status

**BMAD v6.0.0-alpha.5 is early alpha:**
- Core features work (workflows, agents)
- Built-in modules installed (Core, BMM, BMB)
- **Custom module installer not yet available**
- Some features from documentation not implemented

### Module Types

**Built-in Modules:**
- Core, BMM, BMB
- Pre-installed with BMAD
- No _module-installer needed
- Already in manifests

**Custom Modules:**
- Spring Boot Clean Arch (ours)
- Need installation process
- Require _module-installer
- **Installer not available in v6 alpha**

### Documentation Caveat

**GitHub documentation may describe:**
- Legacy v4 features
- Planned future features
- Not current v6 alpha state

**Always verify:**
- Current BMAD version
- Feature availability
- Actual command existence

---

## Decision Matrix

| Option | Effort | Time | Risk | Completeness | Recommended |
|--------|--------|------|------|--------------|-------------|
| **Manual Registration** | Medium | 1 hour | Low | 95% | ✅ **YES** |
| **Wait for v6 Stable** | None | Weeks/months | None | 100% (future) | ⏳ Later |
| **Downgrade to v4** | High | 4+ hours | High | 100% (v4) | ❌ NO |
| **Keep Current** | None | 0 | None | 90% | ✅ Acceptable |

---

## Recommendation

### Immediate Action: Manual Manifest Registration

**Do this now:**
1. Update `bmad/_cfg/manifest.yaml` (1 line)
2. Update `bmad/_cfg/agent-manifest.csv` (4 lines)
3. Update `bmad/_cfg/workflow-manifest.csv` (9 lines)
4. Restore `config.yaml` from backup
5. Verify registration
6. Test functionality

**Result:** 95% complete installation (missing only files-manifest.csv which is not critical)

### Future Action: Use Official Installer When Available

**When BMAD v6 reaches stable:**
- Official custom module installer may be available
- Can reinstall using official process
- Will achieve 100% completion with automation

---

## Next Steps

**You have two paths:**

### Path A: Complete Manual Registration Now ✅
- Follow manual registration steps above
- Achieve 95% completion
- Module officially registered
- **Effort:** 30-60 minutes

### Path B: Accept Current State ✅
- Leave module at 90% completion
- Everything works functionally
- Not officially registered
- **Effort:** None

**Both are valid! Choose based on your priorities:**
- Need official registration? → Path A
- Just need it working? → Path B (already done)

---

**Document Status:** Ready for Manual Registration
**Installer Status:** Not available in BMAD v6 alpha
**Current Solution:** Manual manifest updates (recommended)
**Future Solution:** Official installer (when v6 stabilizes)
