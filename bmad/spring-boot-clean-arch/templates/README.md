# Spring Boot Clean Architecture Templates

This directory contains code generation templates used by the Spring Boot Clean Architecture Generator module.

## Template Structure

```
templates/
├── entity/                    # Domain entity templates
│   ├── domain-entity.java.template
│   ├── value-object.java.template
│   ├── jpa-entity.java.template
│   └── entity-mapper.java.template
├── repository/                # Repository templates
│   ├── repository-interface.java.template
│   ├── repository-impl.java.template
│   └── jpa-repository.java.template
├── usecase/                   # Use case templates
│   ├── input-dto.java.template
│   ├── output-dto.java.template
│   ├── use-case-interface.java.template
│   └── use-case-service.java.template
├── controller/                # REST controller templates
│   ├── rest-controller.java.template
│   ├── request-dto.java.template
│   ├── response-dto.java.template
│   ├── exception-handler.java.template
│   └── entity-not-found-exception.java.template
└── tests/                     # Test templates
    ├── entity-unit-test.java.template
    ├── use-case-unit-test.java.template
    ├── repository-integration-test.java.template
    ├── pact-consumer-test.java.template
    ├── pact-provider-test.java.template
    └── pact-config.java.template
```

## Template Variables

Templates use `{{variable}}` placeholders that are replaced during code generation.

### Common Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `{{base_package}}` | Base Java package | `com.insurance.policy` |
| `{{entity_name}}` | Entity name (PascalCase) | `Policy` |
| `{{entity_name_camel}}` | Entity name (camelCase) | `policy` |
| `{{entity_description}}` | Entity description | `Insurance policy entity` |
| `{{author}}` | Author name | `Kishen Sivalingam` |
| `{{id_type}}` | ID field type | `Long` |
| `{{table_name}}` | Database table name | `policies` |

### Template-Specific Variables

**Entity Templates:**
- `{{field_declarations}}` - Private field declarations
- `{{constructor_params}}` - Constructor parameter list
- `{{validations}}` - Validation logic
- `{{business_methods}}` - Business method implementations
- `{{getters}}` - Getter methods
- `{{setters}}` - Setter methods

**Value Object Templates:**
- `{{record_components}}` - Record component declarations
- `{{validations}}` - Compact constructor validations
- `{{derived_methods}}` - Derived property methods
- `{{factory_params}}` - Factory method parameters

**Repository Templates:**
- `{{repository_name}}` - Repository interface name
- `{{custom_query_methods}}` - Custom query method signatures
- `{{custom_query_method_implementations}}` - Implementation of custom queries

**Use Case Templates:**
- `{{use_case_name}}` - Use case name | `CreatePolicy` |
- `{{input_type}}` - Input DTO type | `CreatePolicyInput` |
- `{{output_type}}` - Output DTO type | `CreatePolicyOutput` |
- `{{repository_dependencies}}` - Injected repositories
- `{{business_logic_steps}}` - Business logic implementation

**Controller Templates:**
- `{{endpoint_path}}` - REST endpoint path | `/api/v1/policies` |
- `{{http_method}}` - HTTP method | `POST`, `GET`, etc. |
- `{{request_dto}}` - Request DTO name
- `{{response_dto}}` - Response DTO name
- `{{endpoint_methods}}` - Controller method implementations

**Test Templates:**
- `{{test_data_setup}}` - Test data initialization
- `{{mock_repository_behavior}}` - Repository mock setup
- `{{assertions}}` - Test assertions
- `{{verify_interactions}}` - Mock verification

## Template Usage

Templates are used by workflow instructions to generate code. The Code Generator Agent processes templates with the workflow context.

### Example Usage Flow

1. **User runs workflow**: `*add-entity`
2. **Workflow collects data**: Entity name, fields, validations
3. **Code Generator Agent**:
   - Loads appropriate templates
   - Resolves variables from context
   - Generates Java files
4. **Test Engineer Agent**:
   - Generates corresponding tests
5. **Architecture Validator Agent**:
   - Validates generated code with ArchUnit

## Adding Custom Templates

To add a new template:

1. **Create template file** with `.template` extension
2. **Use variable placeholders** `{{variable_name}}`
3. **Update workflow** to use the new template
4. **Test generation** to ensure correct output

### Example Custom Template

```java
package {{base_package}}.custom;

/**
 * {{custom_description}}
 *
 * @author {{author}}
 */
public class {{custom_name}} {

    private {{field_type}} {{field_name}};

    public {{custom_name}}({{constructor_params}}) {
        {{field_assignments}}
    }

    {{custom_methods}}
}
```

## Template Best Practices

1. **Clean Architecture Compliance**
   - Domain templates: No framework annotations
   - Infrastructure templates: JPA, Spring annotations
   - Follow dependency rule (dependencies point inward)

2. **Java 21 Features**
   - Use Records for immutable DTOs and Value Objects
   - Leverage pattern matching where appropriate
   - Use sealed classes for domain type hierarchies

3. **Documentation**
   - Include comprehensive JavaDoc
   - Explain business rules in comments
   - Reference Clean Architecture principles

4. **Testability**
   - Generate testable code (constructor injection)
   - Include test templates for every production template
   - Follow test pyramid (many unit, some integration, few E2E)

5. **Consistency**
   - Use consistent naming conventions
   - Follow same structure across templates
   - Maintain uniform code style

## Template Maintenance

- **Version Control**: Templates are versioned with the module
- **Breaking Changes**: Update module version when templates change significantly
- **Backward Compatibility**: Maintain compatibility when possible
- **Testing**: Validate templates with example generations

## See Also

- `/data/examples/policy-management/` - Complete example using these templates
- `/data/archunit-templates/` - ArchUnit validation templates
- `/data/maven-templates/` - Maven project templates
- Module README.md - Overall module documentation
