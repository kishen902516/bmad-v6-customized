# Spring Boot Clean Architecture Generator - Versioning & Upgrades

**Document Version:** 1.0.0
**Date:** 2025-11-07
**Status:** Architecture Analysis

---

## Current Version Architecture

### Module Versions

```
BMAD Core:                    6.0.0-alpha.5  (2025-11-04)
├── BMM Module:               6.0.0-alpha.5  (2025-11-04)
└── Spring Boot Clean Arch:   1.0.0          (2025-11-07)
```

### Version Independence

**Current Reality:**
- ✅ **BMM** and **Spring Boot Clean Arch** are **INDEPENDENT modules**
- ✅ They do NOT depend on each other
- ✅ They CAN be versioned independently
- ⚠️ They BOTH depend on **BMAD Core**

---

## Dependency Architecture

### Critical Dependency: BMAD Core workflow.xml

**Every workflow in every module depends on:**
```
{project-root}/bmad/core/tasks/workflow.xml
```

**What workflow.xml provides:**
- Workflow execution engine (the "OS" for all workflows)
- Variable resolution ({config_source}, {project-root}, etc.)
- Template processing
- Step execution logic
- User approval flows
- File reading/writing orchestration

**Impact:**
- If `workflow.xml` changes, ALL modules are affected
- BMAD Core version upgrades are CRITICAL
- Backward compatibility of workflow.xml is ESSENTIAL

### Dependency Chain

```
┌─────────────────────────────────────────────┐
│         BMAD Core 6.0.0-alpha.5             │
│         (workflow.xml, config system)        │
└──────────────┬──────────────────────────────┘
               │
               ├─────────────────────┬─────────────────────┐
               │                     │                     │
               ▼                     ▼                     ▼
    ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
    │  BMM Module      │  │ Spring Boot      │  │  Other Modules   │
    │  6.0.0-alpha.5   │  │ Clean Arch 1.0.0 │  │  (Future)        │
    └──────────────────┘  └──────────────────┘  └──────────────────┘
           │                       │
           │                       │
           └───────────────────────┘
                      │
                      ▼
           NO DIRECT DEPENDENCY
         (They are independent)
```

---

## Integration Points Between BMM and Spring Boot Clean Arch

### Current Integration: NONE (By Design)

**BMM does not call Spring Boot Clean Arch workflows**
**Spring Boot Clean Arch does not call BMM workflows**

They are **complementary** but **independent**.

### Potential Future Integration Points

If these modules were to integrate in the future, integration would happen at these levels:

1. **Workflow Level** (Loose Coupling - Recommended)
   - BMM dev-story workflow could *optionally* invoke Spring Boot workflows
   - Spring Boot workflows could *optionally* update BMM story status
   - Integration via: Workflow orchestration, not code coupling

2. **Configuration Level** (Data Sharing)
   - Shared project metadata
   - Common file paths
   - Both read from same source projects

3. **Slash Command Level** (User-Driven)
   - User manually switches between modules
   - User invokes workflows from each module as needed
   - Current state: This is how it works now

---

## Upgrade Scenarios

### Scenario 1: BMAD Core Upgrade (MOST CRITICAL)

**Example: BMAD Core 6.0.0-alpha.5 → 7.0.0**

**Impact Analysis:**

#### Breaking Changes in workflow.xml

**If workflow.xml changes significantly:**

```xml
<!-- OLD (6.0.0): -->
<step n="1">Load config</step>
<step n="2">Execute workflow</step>

<!-- NEW (7.0.0): -->
<phase n="1">Initialize</phase>
<phase n="2">Load config</phase>
<phase n="3">Execute workflow</phase>
```

**Impact:**
- ⚠️ **ALL workflows in ALL modules break**
- BMM workflows: BROKEN
- Spring Boot Clean Arch workflows: BROKEN
- All other module workflows: BROKEN

**Resolution Required:**
1. Update workflow.yaml format in ALL modules
2. Test ALL workflows
3. Update documentation
4. Migration guide needed

#### Variable Resolution Changes

**If {config_source} syntax changes:**

```yaml
# OLD (6.0.0):
config_source: "{project-root}/bmad/bmm/config.yaml"
user_name: "{config_source}:user_name"

# NEW (7.0.0):
config_source:
  path: "{project-root}/bmad/bmm/config.yaml"
  fields: ["user_name", "output_folder"]
```

**Impact:**
- ⚠️ ALL module configurations need updating
- BMM config.yaml: Update required
- Spring Boot Clean Arch config.yaml: Update required

**Resolution:**
- Migration script needed
- Or: Maintain backward compatibility

#### New Features in workflow.xml

**If workflow.xml adds NEW optional features:**

```xml
<!-- NEW in 7.0.0: -->
<feature name="parallel-execution" optional="true">
  Execute multiple steps in parallel
</feature>
```

**Impact:**
- ✅ No breaking changes
- ✅ Old modules continue to work
- ⚠️ Old modules don't get new features until updated

**Resolution:**
- Optional: Update modules to use new features
- Modules continue working on old behavior

---

### Scenario 2: BMM Module Upgrade Only

**Example: BMM 6.0.0-alpha.5 → 6.1.0**

**What changes:**
- BMM workflow implementations
- BMM agent definitions
- BMM configuration options
- BMM documentation

**Impact on Spring Boot Clean Arch:**
- ✅ **ZERO IMPACT** (modules are independent)
- Spring Boot Clean Arch continues working unchanged
- No updates needed

**Why?**
- Spring Boot Clean Arch doesn't call BMM workflows
- Spring Boot Clean Arch doesn't read BMM config
- No code coupling

**Potential Impact (IF integrated):**
- If BMM dev-story workflow was calling Spring Boot workflows, BMM changes might affect the integration point
- But this doesn't exist currently

---

### Scenario 3: Spring Boot Clean Arch Upgrade Only

**Example: Spring Boot Clean Arch 1.0.0 → 2.0.0**

**What changes:**
- Add CQRS pattern support
- Add Event Sourcing support
- Add GraphQL support
- New workflows (apply-cqrs, apply-event-sourcing)
- Enhanced templates
- New Java features support

**Impact on BMM:**
- ✅ **ZERO IMPACT** (modules are independent)
- BMM continues working unchanged
- No updates needed

**Why?**
- BMM doesn't call Spring Boot Clean Arch workflows
- BMM doesn't read Spring Boot config
- No code coupling

**User Impact:**
- Users get new Spring Boot capabilities
- Existing functionality unchanged
- New slash commands available

---

### Scenario 4: Both Modules Upgrade (Most Common)

**Example: BMAD ecosystem upgrade**

```
BEFORE:
BMAD Core 6.0.0-alpha.5
├── BMM 6.0.0-alpha.5
└── Spring Boot Clean Arch 1.0.0

AFTER:
BMAD Core 7.0.0
├── BMM 7.0.0
└── Spring Boot Clean Arch 2.0.0
```

**Upgrade Sequence (CRITICAL ORDER):**

```
1. BMAD Core Upgrade (FIRST - MANDATORY)
   └─→ Test workflow.xml compatibility
   └─→ Update core infrastructure
   └─→ Verify variable resolution

2. Test ALL Modules Against New Core
   └─→ BMM workflows: Test
   └─→ Spring Boot workflows: Test
   └─→ Identify breaking changes

3. Update Modules (If Needed)
   └─→ Update BMM to 7.0.0 (if needed for core changes)
   └─→ Update Spring Boot to 2.0.0 (if needed for core changes)

4. Verify Integration Points
   └─→ Test independent operation
   └─→ Test complementary usage
   └─→ Verify slash commands
```

**Risk Level:**
- BMAD Core changes: **HIGH RISK** (affects everything)
- Module changes: **LOW RISK** (independent)

---

## Version Compatibility Matrix

### BMAD Core Compatibility

| BMAD Core | BMM Module | Spring Boot Clean Arch | Status |
|-----------|------------|------------------------|--------|
| 6.0.0-alpha.5 | 6.0.0-alpha.5 | 1.0.0 | ✅ Current |
| 6.0.0-alpha.5 | 6.0.0-alpha.5 | 2.0.0 | ✅ Compatible |
| 6.0.0-alpha.5 | 7.0.0 | 1.0.0 | ⚠️ Depends on BMM changes |
| 7.0.0 (breaking) | 6.0.0-alpha.5 | 1.0.0 | ❌ Incompatible |
| 7.0.0 (breaking) | 7.0.0 (updated) | 1.0.0 | ⚠️ Needs testing |
| 7.0.0 (breaking) | 7.0.0 (updated) | 2.0.0 (updated) | ✅ Compatible |

### Module Independence

| Scenario | BMM Version | Spring Boot Version | Can Coexist? |
|----------|-------------|---------------------|--------------|
| Both Old | 6.0.0 | 1.0.0 | ✅ Yes |
| BMM New | 7.0.0 | 1.0.0 | ✅ Yes (if Core compatible) |
| BMM Old | 6.0.0 | 2.0.0 | ✅ Yes (if Core compatible) |
| Both New | 7.0.0 | 2.0.0 | ✅ Yes (if Core compatible) |

**Key Principle:** Modules can have different versions as long as both are compatible with the **same BMAD Core version**.

---

## Upgrade Strategies

### Strategy 1: Conservative (Recommended for Production)

**Principle:** Only upgrade when necessary, test thoroughly

```
1. BMAD releases Core 7.0.0
   └─→ Wait for stable release (not alpha)
   └─→ Wait for module compatibility announcements

2. Check Compatibility Matrix
   └─→ Is BMM 6.0.0 compatible with Core 7.0.0? (check docs)
   └─→ Is Spring Boot 1.0.0 compatible with Core 7.0.0? (check docs)

3. If Compatible:
   └─→ Upgrade Core only
   └─→ Test all workflows
   └─→ Keep modules at current versions

4. If Incompatible:
   └─→ Wait for module updates
   └─→ Upgrade everything together
   └─→ Test thoroughly
```

**Benefits:**
- Minimal risk
- Stable operation
- Controlled changes

**Drawbacks:**
- Slower to get new features
- May miss improvements

---

### Strategy 2: Aggressive (Recommended for Development)

**Principle:** Always use latest versions, early adoption

```
1. BMAD releases Core 7.0.0-alpha
   └─→ Upgrade immediately
   └─→ Test workflows

2. If workflows break:
   └─→ Upgrade modules immediately
   └─→ Or fix workflows manually

3. Contribute fixes back to BMAD
   └─→ Report issues
   └─→ Submit PRs
   └─→ Help stabilize new version

4. Use latest features immediately
   └─→ Benefit from improvements
   └─→ Help find bugs early
```

**Benefits:**
- Latest features
- Help improve BMAD
- Early bug detection

**Drawbacks:**
- More frequent breakage
- More maintenance
- Less stability

---

### Strategy 3: Selective (Recommended for Most Users)

**Principle:** Upgrade modules independently based on need

```
1. BMAD Core: Upgrade when stable
   └─→ Wait for x.x.0 releases (not alpha)
   └─→ Test thoroughly

2. BMM: Upgrade when needed
   └─→ New workflow features needed? Upgrade BMM
   └─→ Current BMM works fine? Stay on current version

3. Spring Boot Clean Arch: Upgrade when needed
   └─→ Need CQRS support? Upgrade to 2.0.0
   └─→ Current features sufficient? Stay on 1.0.0

4. Independent module upgrades
   └─→ Upgrade BMM to 7.0.0, keep Spring Boot at 1.0.0
   └─→ Or: Keep BMM at 6.0.0, upgrade Spring Boot to 2.0.0
```

**Benefits:**
- Flexibility
- Upgrade only what you need
- Reduced risk

**Drawbacks:**
- Need to track compatibility
- May miss complementary improvements

---

## Breaking Change Management

### What Constitutes a Breaking Change?

#### BMAD Core Breaking Changes (CRITICAL)

1. **workflow.xml structure changes**
   - Step execution order changes
   - Required fields change
   - Variable syntax changes
   - Template processing changes

2. **Configuration system changes**
   - {config_source} syntax changes
   - Variable resolution changes
   - Path resolution changes

3. **File system expectations**
   - Directory structure requirements
   - File naming conventions
   - Required files

**Impact:** ALL modules affected
**Mitigation:** Semantic versioning, migration guides, backward compatibility

#### Module Breaking Changes (LOW IMPACT)

1. **Workflow YAML structure**
   - If Spring Boot 2.0.0 changes workflow.yaml format
   - Impact: Only Spring Boot Clean Arch users

2. **Agent definitions**
   - If BMM changes agent format
   - Impact: Only BMM users

3. **Configuration options**
   - If Spring Boot changes config.yaml schema
   - Impact: Only Spring Boot Clean Arch users

**Impact:** Single module only
**Mitigation:** Module-level versioning, changelog

---

## Version Declaration Best Practices

### Current State: Module Version in config.yaml

```yaml
# bmad/spring-boot-clean-arch/config.yaml
module_version: '1.0.0'
```

### Recommended Enhancement: Dependency Declaration

```yaml
# bmad/spring-boot-clean-arch/config.yaml
module_version: '1.0.0'
module_name: 'spring-boot-clean-arch'

dependencies:
  bmad_core:
    min_version: '6.0.0'
    max_version: '6.99.99'  # Compatible with all 6.x
    critical: true
  bmm:
    min_version: 'none'  # No dependency
    max_version: 'none'
    critical: false

compatibility:
  bmad_core_6: true
  bmad_core_7: false  # Not yet tested
```

### Recommended Enhancement: Version Checking

```yaml
# Workflow could check compatibility at runtime
<step n="0" title="Version Check">
  <action>Check BMAD Core version</action>
  <require>
    <bmad_core min="6.0.0" max="6.99.99"/>
  </require>
  <on_incompatible>
    Warn user: "This module requires BMAD Core 6.x, but you have {current_version}"
  </on_incompatible>
</step>
```

---

## Migration Scenarios

### Migration 1: BMAD Core 6.x → 7.x (Breaking)

**Hypothetical Breaking Change: workflow.xml structure**

```xml
<!-- OLD workflow.xml (6.x): -->
<step n="1">Load config</step>
<step n="2">Execute</step>

<!-- NEW workflow.xml (7.x): -->
<stage n="1">
  <phase>Load config</phase>
  <phase>Execute</phase>
</stage>
```

**Migration Steps:**

1. **BMAD Team Actions:**
   - Release migration guide
   - Provide backward compatibility layer (if possible)
   - Update all official modules
   - Release 7.0.0-alpha for testing

2. **Module Maintainer Actions (Spring Boot Clean Arch):**
   - Test workflows against 7.0.0-alpha
   - If broken: Update all workflow.yaml files
   - Update documentation
   - Release spring-boot-clean-arch 2.0.0 (compatible with Core 7.x)
   - Update compatibility matrix

3. **User Actions:**
   - Read release notes
   - Choose upgrade path:
     - Option A: Stay on Core 6.x + Spring Boot 1.0.0 (stable)
     - Option B: Upgrade to Core 7.x + Spring Boot 2.0.0 (new features)
   - Test in development environment first
   - Upgrade production when stable

**Timeline Example:**
```
Month 1: BMAD Core 7.0.0-alpha released
Month 2: Module updates begin, testing
Month 3: BMAD Core 7.0.0-beta, modules in beta
Month 4: BMAD Core 7.0.0 stable, modules stable
Month 5+: Users upgrade gradually
```

---

### Migration 2: Spring Boot Clean Arch 1.0.0 → 2.0.0 (Non-Breaking)

**New Features: CQRS, Event Sourcing, GraphQL**

```yaml
# New workflows in 2.0.0
workflows:
  - apply-cqrs
  - apply-event-sourcing
  - add-graphql-endpoint
```

**Migration Steps:**

1. **Release 2.0.0:**
   - Add new workflows
   - Keep existing workflows unchanged
   - New templates for advanced patterns
   - Backward compatible with 1.0.0 projects

2. **User Actions:**
   - Upgrade when ready (optional)
   - Existing projects continue working
   - New projects can use new features
   - Gradual adoption of new patterns

**No Breaking Changes:**
- All 1.0.0 workflows work in 2.0.0
- All 1.0.0 projects continue working
- New features are additive

---

## Integration with BMM: Upgrade Considerations

### Current State: No Integration (Safe)

Since BMM and Spring Boot Clean Arch are independent:

**BMM Upgrade Scenarios:**
- ✅ BMM 6.0.0 → 7.0.0: Spring Boot Clean Arch unaffected
- ✅ No coordination needed
- ✅ Independent release cycles

**Spring Boot Upgrade Scenarios:**
- ✅ Spring Boot 1.0.0 → 2.0.0: BMM unaffected
- ✅ No coordination needed
- ✅ Independent release cycles

### Future State: If Integration Added

**Hypothetical: BMM dev-story invokes Spring Boot workflows**

```yaml
# bmm/workflows/dev-story/workflow.yaml (hypothetical)
- step: "Generate Spring Boot Entity"
  integration:
    module: "spring-boot-clean-arch"
    workflow: "add-entity"
    min_version: "1.0.0"
```

**Upgrade Concerns:**
1. **Version Compatibility Check Needed**
   - BMM must check Spring Boot version before invoking
   - Error handling if Spring Boot not installed
   - Error handling if Spring Boot version incompatible

2. **Coordinated Releases**
   - If Spring Boot 2.0.0 changes add-entity interface
   - BMM must update to support new interface
   - Or: Maintain backward compatibility

3. **Migration Complexity Increases**
   - Users must upgrade both modules together
   - Testing burden increases
   - Risk increases

**Recommendation:** Keep modules independent as long as possible

---

## Testing Strategy for Upgrades

### Pre-Upgrade Testing

**Before upgrading BMAD Core:**

1. **Backup Current State**
   ```bash
   cp -r bmad bmad-backup-$(date +%Y%m%d)
   ```

2. **Test Current Workflows**
   ```
   /bmad:spring-boot-clean-arch:workflows:bootstrap-project
   /bmad:spring-boot-clean-arch:workflows:add-entity
   /bmad:spring-boot-clean-arch:workflows:validate-architecture
   ```
   - All should pass
   - Establish baseline

3. **Document Current Versions**
   ```yaml
   pre_upgrade_state:
     bmad_core: 6.0.0-alpha.5
     bmm: 6.0.0-alpha.5
     spring_boot_clean_arch: 1.0.0
     last_tested: 2025-11-07
     all_tests_passing: true
   ```

### Post-Upgrade Testing

**After upgrading BMAD Core:**

1. **Test All Workflows**
   - Run every workflow in every module
   - Document failures
   - Compare to baseline

2. **Test Generated Code**
   - Generate new Spring Boot project
   - Run all tests (should pass)
   - Compile generated code
   - Validate architecture (ArchUnit)

3. **Test Integration Points**
   - Test slash commands still work
   - Test configuration loading
   - Test template processing

4. **Rollback Plan**
   ```bash
   # If upgrade fails:
   rm -rf bmad
   mv bmad-backup-20251107 bmad
   # Restore working state
   ```

---

## Recommendations

### For BMAD Core Development Team

1. **Maintain Backward Compatibility**
   - workflow.xml should be backward compatible when possible
   - Deprecate features before removing
   - Provide migration guides for breaking changes
   - Version workflow.xml itself

2. **Semantic Versioning**
   - Major version (7.0.0): Breaking changes
   - Minor version (6.1.0): New features, backward compatible
   - Patch version (6.0.1): Bug fixes

3. **Release Process**
   - Alpha → Beta → RC → Stable
   - Update all official modules before stable release
   - Provide compatibility matrix
   - Migration guides for breaking changes

4. **Version Declaration in workflow.xml**
   ```xml
   <workflow-engine version="6.0.0" min-module-version="1.0.0">
   ```

### For Module Developers (Spring Boot Clean Arch)

1. **Declare Dependencies Explicitly**
   ```yaml
   dependencies:
     bmad_core: "^6.0.0"  # Compatible with 6.x
   ```

2. **Test Against Multiple Core Versions**
   - Test against 6.0.0
   - Test against 6.1.0 (when available)
   - Maintain compatibility range

3. **Independent Versioning**
   - Version spring-boot-clean-arch independently
   - Don't tie version to BMAD Core version
   - Use semantic versioning

4. **Maintain Backward Compatibility**
   - Don't break existing workflows
   - Add features, don't remove
   - Deprecate before removing

### For Users

1. **Conservative Upgrades**
   - Wait for stable releases
   - Test in development first
   - Read release notes thoroughly

2. **Version Tracking**
   - Document current versions
   - Test before upgrading
   - Have rollback plan

3. **Selective Upgrades**
   - Upgrade BMAD Core only when needed
   - Upgrade modules independently based on needs
   - Don't upgrade everything at once unless necessary

---

## Conclusion

### Key Takeaways

1. **BMM and Spring Boot Clean Arch are INDEPENDENT**
   - No code coupling
   - No version dependencies between them
   - Can be upgraded independently

2. **Both depend on BMAD Core**
   - BMAD Core upgrades are CRITICAL
   - BMAD Core breaking changes affect ALL modules
   - BMAD Core must maintain backward compatibility when possible

3. **Current Integration: User-Driven, Manual**
   - User switches between modules manually
   - No automatic integration
   - Low upgrade risk

4. **Future Integration: Would Increase Coupling**
   - If BMM calls Spring Boot workflows
   - Version compatibility becomes critical
   - Coordinated releases needed
   - Recommend: Keep modules independent

5. **Upgrade Strategy**
   - BMAD Core: Upgrade carefully, test thoroughly
   - Modules: Upgrade independently based on need
   - Always have rollback plan
   - Test in development first

### Current Status: SAFE

With the current architecture:
- ✅ BMM and Spring Boot Clean Arch are safely independent
- ✅ Can upgrade either module without affecting the other
- ✅ BMAD Core is the only critical dependency
- ✅ Low risk of version conflicts
- ✅ Simple upgrade paths

---

**Document Version:** 1.0.0
**Last Updated:** 2025-11-07
**Next Review:** When BMAD Core 7.0.0 is announced
