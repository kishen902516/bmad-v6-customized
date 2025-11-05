# Apply Pattern Workflow Instructions

<critical>Applies advanced design patterns to enhance application architecture</critical>

<workflow>

<step n="1" goal="Select pattern">
<ask>Which pattern would you like to apply?
1. CQRS (Command Query Responsibility Segregation)
2. Event Sourcing
3. Saga Pattern (distributed transactions)
4. Specification Pattern (complex queries)
5. Strategy Pattern (algorithm variation)
6. Factory Pattern (object creation)

Choose [1/2/3/4/5/6]:</ask>
<action>Store selected_pattern</action>
</step>

<step n="2" goal="Analyze current architecture">
<action>Scan project structure</action>
<action>Identify components that will be affected by pattern</action>
<action>Assess compatibility and required changes</action>
</step>

<step n="3" goal="Explain pattern and trade-offs">
<action>Explain selected_pattern:
- What it is
- When to use it
- Benefits
- Trade-offs
- Complexity added
</action>

<ask>Proceed with applying {selected_pattern}? [yes/no]:</ask>
</step>

<step n="4" goal="Apply pattern based on selection">
<check if="CQRS">
  <action>Create command model and query model separation</action>
  <action>Generate command handlers and query handlers</action>
  <action>Set up CQRS infrastructure</action>
  <action>Update existing use cases to use CQRS pattern</action>
</check>

<check if="Event Sourcing">
  <action>Create event store infrastructure</action>
  <action>Generate domain events</action>
  <action>Create event handlers</action>
  <action>Set up event replay mechanism</action>
</check>

<check if="Saga">
  <action>Create saga coordinator</action>
  <action>Define compensation logic</action>
  <action>Set up distributed transaction handling</action>
</check>

<check if="Specification">
  <action>Create Specification interface</action>
  <action>Generate composite specifications (And, Or, Not)</action>
  <action>Update repositories to use specifications</action>
</check>

<check if="Strategy">
  <action>Create strategy interface</action>
  <action>Generate concrete strategy implementations</action>
  <action>Create strategy factory/selector</action>
</check>

<check if="Factory">
  <action>Create factory interface</action>
  <action>Generate factory implementations</action>
  <action>Update client code to use factory</action>
</check>
</step>

<step n="5" goal="Generate tests for pattern">
<action>Create pattern-specific tests</action>
<action>Validate pattern implementation</action>
</step>

<step n="6" goal="Document pattern usage">
<action>Generate pattern documentation</action>
<action>Update ARCHITECTURE.md with pattern details</action>
<action>Provide usage examples</action>
</step>

</workflow>
