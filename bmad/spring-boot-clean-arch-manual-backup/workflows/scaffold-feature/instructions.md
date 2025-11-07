# Scaffold Feature Workflow Instructions

<critical>Orchestrates creation of a complete feature across all Clean Architecture layers</critical>

<workflow>

<step n="1" goal="Understand feature requirements">
<ask>Describe the feature you want to add in natural language:
Example: "I want users to be able to submit insurance claims and track their status"</ask>
<action>Store feature_description</action>
<action>Use AI to analyze and extract:
- Entities needed
- Use cases needed
- API endpoints needed
</action>
</step>

<step n="2" goal="Present implementation plan">
<action>Display proposed implementation:
- Entities to create: {list}
- Use cases to create: {list}
- REST endpoints to create: {list}
- Estimated implementation steps: {count}
</action>

<ask>Approve this plan? [yes/modify/cancel]</ask>
<check if="modify">
  <ask>What would you like to change?</ask>
  <action>Adjust plan based on feedback</action>
</check>
</step>

<step n="3" goal="Create entities incrementally">
<action for-each="entity in entities_needed">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-entity/workflow.yaml</invoke-workflow>
  <action>Pass entity context from plan</action>
  <action>Validate after each entity creation</action>
</action>
</step>

<step n="4" goal="Create use cases incrementally">
<action for-each="use_case in use_cases_needed">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-use-case/workflow.yaml</invoke-workflow>
  <action>Pass use case context from plan</action>
  <action>Validate after each use case creation</action>
</action>
</step>

<step n="5" goal="Create REST endpoints">
<action for-each="endpoint in endpoints_needed">
  <invoke-workflow>{project-root}/bmad/spring-boot-clean-arch/workflows/add-rest-endpoint/workflow.yaml</invoke-workflow>
  <action>Pass endpoint context from plan</action>
</action>
</step>

<step n="6" goal="Validate complete feature">
<action>Run full architecture validation</action>
<action>Run all tests</action>
<action>Generate feature documentation</action>
<action>Present feature summary with next steps</action>
</step>

</workflow>
