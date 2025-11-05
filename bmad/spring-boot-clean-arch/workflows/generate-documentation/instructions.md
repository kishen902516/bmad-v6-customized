# Generate Documentation Workflow Instructions

<critical>Auto-generates comprehensive documentation from codebase analysis</critical>

<workflow>

<step n="1" goal="Identify target project">
<check if="target_project_path not provided">
  <ask>Path to your Spring Boot Clean Architecture project?</ask>
  <action>Store target_project_path</action>
</check>

<action>Scan project structure</action>
<action>Detect base package</action>
</step>

<step n="2" goal="Analyze project structure">
<action>Scan and catalog:
- Domain entities
- Value objects
- Repositories
- Use cases
- REST endpoints
- Domain services
- ArchUnit rules
</action>

<action>Extract OpenAPI specification from controllers</action>
<action>Analyze test coverage</action>
</step>

<step n="3" goal="Generate project README">
<action>Create/update README.md with:
- Project title and description
- Architecture overview
- Technology stack (Spring Boot version, Java version, database)
- Project structure diagram
- How to build and run
- How to test
- API documentation link
- ArchUnit validation information
- Contributing guidelines
</action>

<template-output>README.md</template-output>
</step>

<step n="4" goal="Generate architecture documentation">
<action>Create/update ARCHITECTURE.md with:
- Clean Architecture explanation
- Layer descriptions
- Dependency rules
- Package structure
- Design patterns used
- ArchUnit rules explanation
- Architectural decision records (ADRs)
</action>

<template-output>ARCHITECTURE.md</template-output>
</step>

<step n="5" goal="Generate API documentation">
<action>Export OpenAPI specification to docs/openapi.yaml</action>

<action>Generate API.md with:
- API overview
- Base URLs
- Authentication (if applicable)
- Endpoint catalog:
  - For each endpoint: method, path, description, request, response
- Error responses
- Examples
</action>

<template-output>API.md</template-output>
</step>

<step n="6" goal="Generate domain model documentation">
<action>Create DOMAIN-MODEL.md with:
- Domain overview
- Entity descriptions
- Value object descriptions
- Aggregate boundaries
- Domain events (if any)
- Business rules
- UML-like diagrams (text-based)
</action>

<template-output>DOMAIN-MODEL.md</template-output>
</step>

<step n="7" goal="Generate testing documentation">
<action>Create TESTING.md with:
- Test strategy (test pyramid)
- How to run tests
- Test coverage report
- ArchUnit validation guide
- Test data setup
- TestContainers usage
</action>

<template-output>TESTING.md</template-output>
</step>

<step n="8" goal="Generate C4 architecture diagrams">
<action>Create docs/diagrams/ folder</action>

<action>Generate PlantUML diagrams:
- C4 Context diagram
- C4 Container diagram
- C4 Component diagram
- Layer dependency diagram
</action>

<action>Generate diagram source files (.puml) and instructions to render</action>
</step>

<step n="9" goal="Generate development guide">
<action>Create DEVELOPMENT.md with:
- Getting started
- Adding new entities (link to workflow)
- Adding new use cases (link to workflow)
- Adding new endpoints (link to workflow)
- Common patterns and examples
- Troubleshooting
- Resources and references
</action>

<template-output>DEVELOPMENT.md</template-output>
</step>

<step n="10" goal="Present summary">
<action>Display documentation generation summary:
- Documentation generated in: {target_project_path}/docs/
- Files created:
  - README.md
  - ARCHITECTURE.md
  - API.md
  - DOMAIN-MODEL.md
  - TESTING.md
  - DEVELOPMENT.md
  - diagrams/*.puml
- OpenAPI specification: docs/openapi.yaml
- Swagger UI: http://localhost:8080/swagger-ui.html (when running)
</action>

<action>Suggest:
- Commit documentation to version control
- Keep documentation updated as code evolves
- Share with team members
</action>
</step>

</workflow>
