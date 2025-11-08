# Module Registration Script - Usage Guide

**Script:** `register-module.sh`
**Purpose:** Automate BMAD manifest registration for Spring Boot Clean Arch module
**Version:** 1.0.0
**Date:** 2025-11-07

---

## Quick Start

### Step 1: Dry Run (Safe Test)

```bash
cd bmad/spring-boot-clean-arch
./register-module.sh --dry-run
```

This shows what the script would do **without making any changes**.

### Step 2: Execute Registration

```bash
./register-module.sh
```

This performs the actual registration.

---

## What The Script Does

### Automated Actions

1. **Validates Environment** ✅
   - Checks BMAD project structure
   - Verifies all required files exist
   - Confirms module directory present
   - Warns if already registered

2. **Backs Up Files** ✅
   - Creates timestamped backups
   - Backup format: `{file}.backup-YYYYMMDD-HHMMSS`
   - Backs up: manifest.yaml, agent-manifest.csv, workflow-manifest.csv

3. **Registers Module** ✅
   - Adds `spring-boot-clean-arch` to manifest.yaml
   - Adds 4 agents to agent-manifest.csv
   - Adds 9 workflows to workflow-manifest.csv
   - Skips duplicates automatically

4. **Restores Config** ✅
   - Copies config.yaml.backup → config.yaml
   - Only if config.yaml doesn't exist

5. **Verifies Registration** ✅
   - Checks module in manifest.yaml
   - Counts agents (expects 4)
   - Counts workflows (expects 9)
   - Checks config.yaml exists
   - Reports success/issues

---

## Usage

### Basic Usage

```bash
# Navigate to module directory
cd bmad/spring-boot-clean-arch

# Run registration
./register-module.sh
```

### Dry Run Mode

```bash
# Test without making changes
./register-module.sh --dry-run
```

**Use dry run to:**
- See what would be changed
- Verify script logic
- Test before execution
- Safe to run multiple times

### Help

```bash
./register-module.sh --help
```

---

## Script Output

### Dry Run Example

```
═══════════════════════════════════════════════════════════════
  Spring Boot Clean Arch - Module Registration
═══════════════════════════════════════════════════════════════

⚠️  DRY RUN MODE - No changes will be made

ℹ️  Project Root: /c/development/bmad-v6-customized
ℹ️  Module: spring-boot-clean-arch

═══════════════════════════════════════════════════════════════
  Validating Environment
═══════════════════════════════════════════════════════════════

✅ BMAD project root found
✅ Module directory found
✅ All manifest files found
✅ Config backup found

✅ Environment validation complete

═══════════════════════════════════════════════════════════════
  Registering in manifest.yaml
═══════════════════════════════════════════════════════════════

ℹ️  Would add 'spring-boot-clean-arch' to modules list

═══════════════════════════════════════════════════════════════
  Registering Agents
═══════════════════════════════════════════════════════════════

ℹ️  Would add 4 agents to agent-manifest.csv
ℹ️    - spring-architect
ℹ️    - code-generator
ℹ️    - test-engineer
ℹ️    - arch-validator

═══════════════════════════════════════════════════════════════
  Registering Workflows
═══════════════════════════════════════════════════════════════

ℹ️  Would add 9 workflows to workflow-manifest.csv
ℹ️    - bootstrap-project
ℹ️    - add-entity
ℹ️    - add-use-case
ℹ️    - add-rest-endpoint
ℹ️    - add-repository
ℹ️    - scaffold-feature
ℹ️    - apply-pattern
ℹ️    - validate-architecture
ℹ️    - generate-documentation

═══════════════════════════════════════════════════════════════
  Restoring Configuration
═══════════════════════════════════════════════════════════════

ℹ️  Would restore: config.yaml.backup -> config.yaml

═══════════════════════════════════════════════════════════════
  Registration Summary
═══════════════════════════════════════════════════════════════

ℹ️  Dry run complete - no changes made
ℹ️  Run without --dry-run to perform actual registration

✅ Script execution complete!
```

### Actual Run Example

```
═══════════════════════════════════════════════════════════════
  Spring Boot Clean Arch - Module Registration
═══════════════════════════════════════════════════════════════

ℹ️  Project Root: /c/development/bmad-v6-customized
ℹ️  Module: spring-boot-clean-arch

[Validation output...]

✅ Backed up: manifest.yaml
✅ Added module to manifest.yaml

✅ Backed up: agent-manifest.csv
✅ Added agent: spring-architect
✅ Added agent: code-generator
✅ Added agent: test-engineer
✅ Added agent: arch-validator
✅ Registered 4 agents

✅ Backed up: workflow-manifest.csv
✅ Added workflow: bootstrap-project
✅ Added workflow: add-entity
✅ Added workflow: add-use-case
✅ Added workflow: add-rest-endpoint
✅ Added workflow: add-repository
✅ Added workflow: scaffold-feature
✅ Added workflow: apply-pattern
✅ Added workflow: validate-architecture
✅ Added workflow: generate-documentation
✅ Registered 9 workflows

✅ Restored config.yaml from backup

═══════════════════════════════════════════════════════════════
  Verifying Registration
═══════════════════════════════════════════════════════════════

✅ Module found in manifest.yaml
✅ All 4 agents found in agent-manifest.csv
✅ All 9 workflows found in workflow-manifest.csv
✅ config.yaml exists

✅ Verification complete - registration successful!

═══════════════════════════════════════════════════════════════
  Registration Summary
═══════════════════════════════════════════════════════════════

✅ Module registration complete!

ℹ️  What was done:
  ✅ Added module to manifest.yaml
  ✅ Registered 4 agents in agent-manifest.csv
  ✅ Registered 9 workflows in workflow-manifest.csv
  ✅ Restored config.yaml
  ✅ Created backups of all modified files

ℹ️  Backups created with timestamp suffix: .backup-YYYYMMDD-HHMMSS

ℹ️  Next steps:
  1. Verify slash commands work (if created)
  2. Test agent loading
  3. Test workflow execution
  4. Update documentation

✅ Script execution complete!
```

---

## Features

### Safety Features

- **Dry run mode** - Test before executing
- **Automatic backups** - All files backed up with timestamps
- **Duplicate detection** - Skips already registered items
- **Validation** - Checks environment before proceeding
- **Error handling** - Exits on errors (set -e)
- **User confirmation** - Asks before overwriting if already registered

### Smart Features

- **Colored output** - Green (success), Red (error), Yellow (warning), Blue (info)
- **Progress indicators** - Shows what's being done at each step
- **Verification** - Confirms registration after completion
- **Skip existing** - Won't duplicate if already registered
- **Atomic operations** - Backs up before modifying

---

## Verification After Registration

### Check Manifest Files

```bash
# Check module registered
cat bmad/_cfg/manifest.yaml | grep spring-boot-clean-arch

# Count agents (should be 4)
grep "spring-boot-clean-arch" bmad/_cfg/agent-manifest.csv | wc -l

# Count workflows (should be 9)
grep "spring-boot-clean-arch" bmad/_cfg/workflow-manifest.csv | wc -l
```

### Expected Results

- ✅ Module appears in manifest.yaml
- ✅ 4 lines in agent-manifest.csv contain "spring-boot-clean-arch"
- ✅ 9 lines in workflow-manifest.csv contain "spring-boot-clean-arch"
- ✅ config.yaml exists

---

## Troubleshooting

### Error: "Not in BMAD project root"

**Cause:** Script not run from correct directory

**Solution:**
```bash
cd /path/to/bmad-v6-customized
./bmad/spring-boot-clean-arch/register-module.sh
```

Or:
```bash
cd bmad/spring-boot-clean-arch
./register-module.sh
```

### Error: "Module already registered"

**Cause:** Module already in manifest.yaml

**Behavior:** Script asks for confirmation

**Options:**
- Continue: Press 'y' (will update agents/workflows)
- Cancel: Press 'n' (exits safely)

### Warning: "Agent already exists - skipping"

**Cause:** Agent already in agent-manifest.csv

**Behavior:** Skips that agent, continues with others

**Result:** Only new agents added

### Warning: "Workflow already exists - skipping"

**Cause:** Workflow already in workflow-manifest.csv

**Behavior:** Skips that workflow, continues with others

**Result:** Only new workflows added

### Error: Permission denied

**Cause:** Script not executable

**Solution:**
```bash
chmod +x bmad/spring-boot-clean-arch/register-module.sh
```

---

## Rollback

### If Registration Fails or Needs Reversal

The script creates timestamped backups. To rollback:

```bash
# Find backup files
ls -la bmad/_cfg/*.backup-*

# Restore from backup (replace TIMESTAMP)
cp bmad/_cfg/manifest.yaml.backup-TIMESTAMP bmad/_cfg/manifest.yaml
cp bmad/_cfg/agent-manifest.csv.backup-TIMESTAMP bmad/_cfg/agent-manifest.csv
cp bmad/_cfg/workflow-manifest.csv.backup-TIMESTAMP bmad/_cfg/workflow-manifest.csv
```

### Manual Removal (if needed)

```bash
# Remove module from manifest.yaml
sed -i '/spring-boot-clean-arch/d' bmad/_cfg/manifest.yaml

# Remove agents
sed -i '/spring-boot-clean-arch/d' bmad/_cfg/agent-manifest.csv

# Remove workflows
sed -i '/spring-boot-clean-arch/d' bmad/_cfg/workflow-manifest.csv
```

---

## What Gets Registered

### manifest.yaml

**Added:**
```yaml
modules:
  - core
  - bmb
  - bmm
  - spring-boot-clean-arch  # ← Added by script
```

### agent-manifest.csv

**Added:** 4 agents
1. spring-architect - Spring Architecture Agent
2. code-generator - Code Generator Agent
3. test-engineer - Test Engineer Agent
4. arch-validator - Architecture Validator Agent

**Format:** CSV with columns:
`name,displayName,title,icon,role,identity,communicationStyle,principles,module,path`

### workflow-manifest.csv

**Added:** 9 workflows
1. bootstrap-project - Initialize Spring Boot project
2. add-entity - Add domain entity with TDD
3. add-use-case - Add application use case
4. add-rest-endpoint - Add REST API endpoint
5. add-repository - Add repository
6. scaffold-feature - Complete feature generation
7. apply-pattern - Apply design patterns
8. validate-architecture - ArchUnit validation
9. generate-documentation - Generate docs

**Format:** CSV with columns:
`name,description,module,path,standalone`

---

## Post-Registration Steps

### 1. Verify Registration

```bash
# Run verification commands
cat bmad/_cfg/manifest.yaml | grep spring-boot-clean-arch
grep -c "spring-boot-clean-arch" bmad/_cfg/agent-manifest.csv  # Should output: 4
grep -c "spring-boot-clean-arch" bmad/_cfg/workflow-manifest.csv  # Should output: 9
```

### 2. Update Documentation

Update these files to reflect 100% completion:
- `MODULE-INSTALLATION-COMPLETE.md`
- `INSTALLATION.md`
- `README.md`

### 3. Test Functionality

If slash commands were created, test them:
```
/bmad:spring-boot-clean-arch:agents:spring-architect
```

(Note: In BMAD v6 alpha, slash commands may need manual creation)

---

## Script Details

### Location

```
bmad/spring-boot-clean-arch/register-module.sh
```

### Requirements

- Bash shell
- BMAD v6 installed
- Run from project root or module directory
- Write permissions to bmad/_cfg/ files

### Exit Codes

- `0` - Success
- `1` - Error (validation failed, file not found, etc.)

### Backup Format

```
{original-file}.backup-YYYYMMDD-HHMMSS

Examples:
manifest.yaml.backup-20251107-220530
agent-manifest.csv.backup-20251107-220530
workflow-manifest.csv.backup-20251107-220530
```

---

## FAQ

**Q: Can I run this multiple times?**
A: Yes. It detects duplicates and skips them.

**Q: Will it break existing registrations?**
A: No. It only adds, never removes. Backups created before any changes.

**Q: What if I already manually registered some items?**
A: Script skips existing items, only adds missing ones.

**Q: Do I need to restart anything after registration?**
A: No. Changes take effect immediately (manifests are read on demand).

**Q: Can I customize the script?**
A: Yes. It's a standalone bash script, fully editable.

**Q: Does this work on Windows?**
A: Yes, if you have Git Bash, WSL, or similar bash environment.

**Q: What if the script fails mid-way?**
A: Backups exist. You can restore from them. Script uses `set -e` to exit on first error.

---

## Summary

**What:** Automated module registration script
**Why:** BMAD v6 alpha lacks custom module installer
**How:** Bash script that updates 3 manifest files
**Safety:** Dry run mode, backups, duplicate detection
**Effort:** 2 commands (dry run + execute)
**Time:** < 1 minute to run
**Result:** Module 100% registered in BMAD manifests

---

**Ready to register? Start with a dry run!**

```bash
cd bmad/spring-boot-clean-arch
./register-module.sh --dry-run
```

**Documentation:** See this file
**Script:** `register-module.sh`
**Version:** 1.0.0
**Status:** Ready for use
