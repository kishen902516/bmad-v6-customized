# Validate Architecture Workflow Instructions

<critical>Runs comprehensive ArchUnit validation and reports architectural compliance</critical>

<workflow>

<step n="1" goal="Identify target project">
<check if="target_project_path not provided">
  <ask>Path to your Spring Boot Clean Architecture project?</ask>
  <action>Store target_project_path</action>
</check>

<action>Validate project has ArchUnit tests configured</action>
</step>

<step n="2" goal="Run ArchUnit validation">
<action>Execute: cd {target_project_path} && mvn test -Dtest=ArchitectureTest</action>
<action>Capture test results</action>
</step>

<step n="3" goal="Analyze results">
<check if="all tests pass">
  <action>Display success message:
  ✅ Architecture Validation PASSED
  - All layer dependencies correct
  - Naming conventions followed
  - No circular dependencies
  - Annotation usage compliant
  </action>
</check>

<check if="tests fail">
  <action>Parse failures and categorize violations:
  - Layer boundary violations
  - Naming convention violations
  - Annotation violations
  - Circular dependency violations
  </action>

  <action>For each violation:
  - Display violation details
  - Explain why it matters
  - Suggest specific fix
  </action>

  <action>Group violations by file/class for easier fixing</action>
</check>
</step>

<step n="4" goal="Generate compliance report">
<action>Create architecture-compliance-report.md with:
- Overall status (Pass/Fail)
- Violations count by category
- Detailed violation list with fixes
- Compliance score (% of rules passing)
- Recommendations
</action>

<action>Save report to project root</action>
</step>

<step n="5" goal="Offer to fix violations">
<check if="violations found">
  <ask>Would you like help fixing these violations?
  1. Show fix suggestions
  2. Auto-fix safe violations
  3. Exit (I'll fix them manually)

  Choose [1/2/3]:</ask>

  <check if="1">
    <action for-each="violation in violations">
      Display detailed fix suggestion for violation
    </action>
  </check>

  <check if="2">
    <action>Identify auto-fixable violations (e.g., moving files, renaming classes)</action>
    <ask>Apply {count} auto-fixes? [yes/no]:</ask>
    <check if="yes">
      <action for-each="fixable_violation in auto_fixable">
        Apply fix (move file, rename class, etc.)
      </action>
      <goto step="2">Re-validate after fixes</goto>
    </check>
  </check>

  <check if="3">
    <action>Provide report location and exit</action>
  </check>
</check>

<check if="no violations">
  <action>Celebrate clean architecture! 🎉</action>
</check>
</step>

<step n="6" goal="Present summary">
<action>Display validation summary:
- Project: {target_project_path}
- Validation Status: {Pass/Fail}
- Violations: {count}
- Report: architecture-compliance-report.md
- Next Steps: {suggested actions}
</action>
</step>

</workflow>
