# Entity Templates - Spring Boot Clean Architecture

This directory contains templates for generating entities following Clean Architecture and Domain-Driven Design (DDD) principles.

## Overview

The entity templates provide a complete separation between domain models and infrastructure concerns:

- **Domain Layer** - Pure business entities with no framework dependencies
- **Infrastructure Layer** - JPA entities for persistence
- **Mapping Layer** - Bidirectional conversion between layers

## Template Files

### Core Templates

| Template | Layer | Description |
|----------|-------|-------------|
| `domain-entity.java.template` | Domain | Basic domain entity (original) |
| `domain-entity-enhanced.java.template` | Domain | Enhanced with audit fields, validation |
| `value-object.java.template` | Domain | Basic value object as Record (original) |
| `value-object-enhanced.java.template` | Domain | Enhanced with validation helpers |
| `aggregate-root.java.template` | Domain | DDD Aggregate Root with events |
| `jpa-entity.java.template` | Infrastructure | Basic JPA entity (original) |
| `jpa-entity-enhanced.java.template` | Infrastructure | Enhanced with indexes, lifecycle |
| `entity-mapper.java.template` | Infrastructure | Basic mapper (original) |
| `entity-mapper-enhanced.java.template` | Infrastructure | Enhanced with null-safety, collections |

## Clean Architecture Layers

```
┌─────────────────────────────────────────────────┐
│              Presentation Layer                 │
│            (REST Controllers)                   │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│            Application Layer                    │
│              (Use Cases)                        │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│             Domain Layer                        │
│   ┌──────────────────────────────────┐         │
│   │  Domain Entities                 │         │
│   │  - No framework dependencies     │         │
│   │  - Business logic & invariants   │         │
│   │  - Rich behavior                 │         │
│   └──────────────────────────────────┘         │
│   ┌──────────────────────────────────┐         │
│   │  Value Objects                   │         │
│   │  - Immutable (Java Records)      │         │
│   │  - Self-validating               │         │
│   │  - No identity                   │         │
│   └──────────────────────────────────┘         │
│   ┌──────────────────────────────────┐         │
│   │  Aggregate Roots                 │         │
│   │  - Consistency boundaries        │         │
│   │  - Domain events                 │         │
│   │  - Transaction scope             │         │
│   └──────────────────────────────────┘         │
└───────────────────┬─────────────────────────────┘
                    │
┌───────────────────▼─────────────────────────────┐
│         Infrastructure Layer                    │
│   ┌──────────────────────────────────┐         │
│   │  JPA Entities                    │         │
│   │  - @Entity, @Table annotations   │         │
│   │  - Database mapping              │         │
│   │  - Indexes & constraints         │         │
│   └──────────────────────────────────┘         │
│   ┌──────────────────────────────────┐         │
│   │  Entity Mappers                  │         │
│   │  - Domain ↔ JPA conversion       │         │
│   │  - Null-safe                     │         │
│   │  - Stateless                     │         │
│   └──────────────────────────────────┘         │
└─────────────────────────────────────────────────┘
```

## Template Selection Guide

### When to Use Each Template

| Use Case | Template | Reason |
|----------|----------|--------|
| Simple entity with few fields | `domain-entity.java.template` | Lightweight, minimal boilerplate |
| Entity requiring audit trails | `domain-entity-enhanced.java.template` | Includes createdAt, updatedAt, createdBy, updatedBy |
| Immutable concept (Money, Email) | `value-object.java.template` | Java 21 Record for immutability |
| Value object with complex validation | `value-object-enhanced.java.template` | Validation helpers included |
| Root entity with child entities | `aggregate-root.java.template` | DDD aggregate with consistency boundary |
| Basic persistence mapping | `jpa-entity.java.template` | Simple JPA entity |
| Production persistence | `jpa-entity-enhanced.java.template` | Optimistic locking, indexes, audit |
| Simple domain-JPA conversion | `entity-mapper.java.template` | Basic mapping |
| Production-ready mapping | `entity-mapper-enhanced.java.template` | Null-safe, collections, validation |

## Domain Entity Template

### Basic Domain Entity

**File:** `domain-entity.java.template`

**Purpose:** Framework-independent business entities with behavior and invariants.

**Key Features:**
- No JPA annotations (pure domain)
- Business methods
- Validation in constructor
- Controlled mutation via package-private setters
- Identity-based equality

**Example Usage:**

```java
// Generated Policy domain entity
package com.insurance.policy.domain.entity;

public class Policy {

    private Long id;
    private String policyNumber;
    private PolicyStatus status;
    private Money premium;

    public Policy(String policyNumber, Money premium) {
        if (policyNumber == null || policyNumber.isBlank()) {
            throw new IllegalArgumentException("Policy number is required");
        }
        if (premium == null || premium.amount() <= 0) {
            throw new IllegalArgumentException("Premium must be positive");
        }

        this.policyNumber = policyNumber;
        this.premium = premium;
        this.status = PolicyStatus.DRAFT;
    }

    // Business method
    public void activate() {
        if (this.status != PolicyStatus.DRAFT) {
            throw new IllegalStateException("Only draft policies can be activated");
        }
        this.status = PolicyStatus.ACTIVE;
    }

    // Getters...
}
```

### Enhanced Domain Entity

**File:** `domain-entity-enhanced.java.template`

**Additional Features:**
- Audit fields (createdAt, updatedAt, createdBy, updatedBy)
- `validate()` method for checking invariants
- `markUpdated()` helper method
- Reconstitution constructor for persistence

**Use When:**
- Audit trails are required
- Complex validation logic
- Multiple constructors needed (new vs reconstitute)
- Production applications

## Value Object Template

### Basic Value Object

**File:** `value-object.java.template`

**Purpose:** Immutable domain concepts implemented as Java 21 Records.

**Key Features:**
- Java 21 Record (immutable by default)
- Compact constructor with validation
- Factory methods (of, from)
- Derived properties
- Equality by value, not identity

**Example Usage:**

```java
// Generated Money value object
package com.insurance.policy.domain.valueobject;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    // Compact constructor with validation
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
    }

    // Factory method
    public static Money of(double amount, String currencyCode) {
        return new Money(
            BigDecimal.valueOf(amount),
            Currency.getInstance(currencyCode)
        );
    }

    // Business operation
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    // Derived property
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
}
```

### Enhanced Value Object

**File:** `value-object-enhanced.java.template`

**Additional Features:**
- Validation helper methods (requireNonBlank, requirePositive, requireInRange)
- Multiple factory methods
- Comprehensive JavaDoc
- Common validation patterns

**Use When:**
- Complex validation rules
- Multiple ways to create the value object
- Need reusable validation logic

## Aggregate Root Template

**File:** `aggregate-root.java.template`

**Purpose:** DDD Aggregate Root - transactional consistency boundary.

**Key Features:**
- Manages internal entities (aggregate members)
- Domain event publication
- Invariant enforcement across aggregate
- Optimistic locking (version field)
- External access only through root

**Aggregate Rules:**
1. External objects can only reference the Aggregate Root
2. Modifications to internal entities go through the root
3. The root enforces consistency boundaries
4. Transactions should not cross aggregate boundaries
5. Domain events are published for cross-aggregate communication

**Example Usage:**

```java
// Generated Order aggregate root
package com.ecommerce.order.domain.aggregate;

public class Order {

    private Long id;
    private OrderStatus status;
    private final List<OrderLine> orderLines = new ArrayList<>();
    private Money totalAmount;
    private Long version; // Optimistic locking

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Order(CustomerId customerId) {
        this.customerId = customerId;
        this.status = OrderStatus.DRAFT;
        this.totalAmount = Money.zero();

        registerEvent(new OrderCreated(customerId));
    }

    // Add item (controls internal entity)
    public void addItem(ProductId productId, int quantity, Money price) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify non-draft order");
        }

        OrderLine line = new OrderLine(productId, quantity, price);
        orderLines.add(line);
        recalculateTotal();

        registerEvent(new OrderLineAdded(productId, quantity));
    }

    // Remove item (controls internal entity)
    public void removeItem(Long orderLineId) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify non-draft order");
        }

        orderLines.removeIf(line -> line.getId().equals(orderLineId));
        recalculateTotal();
    }

    // Business method (aggregate-level operation)
    public void place() {
        checkInvariants();
        this.status = OrderStatus.PLACED;
        registerEvent(new OrderPlaced(this.id, this.totalAmount));
    }

    // Invariant enforcement
    private void checkInvariants() {
        if (orderLines.isEmpty()) {
            throw new IllegalStateException("Order must have at least one line");
        }
        if (totalAmount.isZero()) {
            throw new IllegalStateException("Order total must be positive");
        }
    }

    private void recalculateTotal() {
        this.totalAmount = orderLines.stream()
            .map(OrderLine::getSubtotal)
            .reduce(Money.zero(), Money::add);
    }

    // Unmodifiable view of internals
    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }
}
```

**When to Use:**
- Entity has child entities (Order → OrderLines)
- Need transactional consistency across multiple entities
- Complex business rules spanning multiple objects
- Need to publish domain events

## JPA Entity Template

### Basic JPA Entity

**File:** `jpa-entity.java.template`

**Purpose:** Infrastructure layer persistence mapping.

**Key Features:**
- JPA annotations (@Entity, @Table, @Id, etc.)
- Database table mapping
- Getters and setters (required by JPA)
- Separate from domain entity

**Example Usage:**

```java
// Generated PolicyJpaEntity
package com.insurance.policy.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "policies")
public class PolicyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", unique = true, nullable = false, length = 20)
    private String policyNumber;

    @Column(name = "premium_amount", nullable = false)
    private BigDecimal premiumAmount;

    @Column(name = "premium_currency", nullable = false, length = 3)
    private String premiumCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PolicyStatus status;

    // Getters and setters...
}
```

### Enhanced JPA Entity

**File:** `jpa-entity-enhanced.java.template`

**Additional Features:**
- Optimistic locking (@Version)
- Audit timestamps (@CreationTimestamp, @UpdateTimestamp)
- Database indexes for performance
- Unique constraints
- JPA lifecycle callbacks (@PrePersist, @PreUpdate, @PostLoad)

**Use When:**
- Production applications
- Need optimistic locking
- Require audit trails
- Performance optimization (indexes)
- Complex persistence logic

## Entity Mapper Template

### Basic Entity Mapper

**File:** `entity-mapper.java.template`

**Purpose:** Convert between domain entities and JPA entities.

**Key Features:**
- `toJpa(domain)` - Domain → JPA
- `toDomain(jpa)` - JPA → Domain
- `updateJpaFromDomain(jpa, domain)` - Update existing JPA

**Example Usage:**

```java
// Generated PolicyMapper
package com.insurance.policy.infrastructure.adapter.persistence.mapper;

public class PolicyMapper {

    public static PolicyJpaEntity toJpa(Policy domain) {
        if (domain == null) {
            return null;
        }

        PolicyJpaEntity jpa = new PolicyJpaEntity();
        jpa.setId(domain.getId());
        jpa.setPolicyNumber(domain.getPolicyNumber());
        jpa.setPremiumAmount(domain.getPremium().amount());
        jpa.setPremiumCurrency(domain.getPremium().currency().getCurrencyCode());
        jpa.setStatus(domain.getStatus());
        return jpa;
    }

    public static Policy toDomain(PolicyJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }

        Money premium = Money.of(jpa.getPremiumAmount(), jpa.getPremiumCurrency());
        return new Policy(
            jpa.getId(),
            jpa.getPolicyNumber(),
            premium,
            jpa.getCreatedAt(),
            jpa.getUpdatedAt()
        );
    }
}
```

### Enhanced Entity Mapper

**File:** `entity-mapper-enhanced.java.template`

**Additional Features:**
- Null-safe (handles null gracefully)
- List mapping (`toJpaList`, `toDomainList`)
- Bidirectional updates
- Validation helpers
- Private constructor (utility class)

**Use When:**
- Production code
- Need to map collections
- Require null safety
- Complex value object mapping

## Template Variables

### Common Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `{{base_package}}` | Base Java package | `com.insurance.policy` |
| `{{entity_name}}` | Entity name (PascalCase) | `Policy` |
| `{{entity_description}}` | Entity description | `Insurance policy domain entity` |
| `{{author}}` | Author name | `John Doe` |
| `{{id_type}}` | ID field type | `Long`, `UUID` |
| `{{table_name}}` | Database table name | `policies` |

### Domain Entity Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `{{field_declarations}}` | Private field declarations | `private String policyNumber;` |
| `{{constructor_params}}` | Constructor parameter list | `String policyNumber, Money premium` |
| `{{constructor_params_javadoc}}` | JavaDoc for parameters | `@param policyNumber the policy number` |
| `{{validations}}` | Validation logic in constructor | `if (policyNumber == null) throw...` |
| `{{field_assignments}}` | Field assignments | `this.policyNumber = policyNumber;` |
| `{{business_methods}}` | Business method implementations | `public void activate() {...}` |
| `{{getters}}` | Getter method implementations | `public String getPolicyNumber()` |
| `{{setters}}` | Setter implementations (package-private) | `void setPolicyNumber(String policyNumber)` |

### Value Object Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `{{record_components}}` | Record component declarations | `BigDecimal amount, Currency currency` |
| `{{factory_params}}` | Factory method parameters | `double amount, String currencyCode` |
| `{{factory_args}}` | Factory method arguments | `BigDecimal.valueOf(amount), Currency.getInstance(currencyCode)` |
| `{{derived_methods}}` | Derived property methods | `public boolean isZero()` |
| `{{business_methods}}` | Value object operations | `public Money add(Money other)` |

### JPA Entity Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `{{jpa_field_declarations}}` | JPA-annotated field declarations | `@Column(name = "policy_number") private String policyNumber;` |
| `{{id_strategy}}` | ID generation strategy | `IDENTITY`, `SEQUENCE`, `UUID` |
| `{{table_indexes}}` | Index definitions | `@Index(name = "idx_policy_number", columnList = "policy_number")` |
| `{{unique_constraints}}` | Unique constraint definitions | `@UniqueConstraint(columnNames = {"policy_number"})` |
| `{{getters_and_setters}}` | Getter and setter implementations | Full getter/setter pairs |

### Mapper Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `{{field_mappings_to_jpa}}` | Field mapping domain → JPA | `jpa.setPolicyNumber(domain.getPolicyNumber());` |
| `{{constructor_args_from_jpa}}` | Constructor args from JPA | `jpa.getPolicyNumber(), Money.of(...)` |
| `{{field_updates_from_domain}}` | Update statements | `jpa.setPolicyNumber(domain.getPolicyNumber());` |
| `{{value_object_mapping_helpers}}` | Value object mappers | Helper methods for Money, Email, etc. |

## Best Practices

### Domain Entity Best Practices

1. **No Framework Dependencies**
   - Never use JPA annotations in domain entities
   - Keep domain pure and testable

2. **Validate in Constructor**
   - Fail fast with clear error messages
   - Ensure entities are always in valid state

3. **Rich Behavior, Not Anemic**
   - Add business methods, not just getters/setters
   - Encapsulate business rules in the entity

4. **Identity-Based Equality**
   - Entities are equal if IDs match
   - Two entities with null IDs are never equal

5. **Controlled Mutation**
   - Use package-private setters
   - Business methods for state changes

### Value Object Best Practices

1. **Always Immutable**
   - Use Java Records
   - No setters

2. **Self-Validating**
   - Validate in compact constructor
   - Impossible to create invalid value object

3. **Operations Return New Instances**
   ```java
   public Money add(Money other) {
       return new Money(this.amount.add(other.amount), this.currency);
   }
   ```

4. **Equality by Value**
   - Records provide this automatically

5. **Factory Methods for Clarity**
   ```java
   Money.of(100.0, "USD")  // Clear and readable
   ```

### Aggregate Root Best Practices

1. **One Aggregate = One Transaction**
   - Keep aggregates small
   - Favor small, focused aggregates over large ones

2. **Enforce Invariants**
   - Validate consistency rules in business methods
   - Call `checkInvariants()` after state changes

3. **Control Access to Internals**
   - Return unmodifiable collections
   - Don't expose child entities directly

4. **Use Domain Events**
   - Communicate with other aggregates via events
   - Don't navigate from one aggregate to another

5. **Optimistic Locking**
   - Use version field
   - Handle concurrent modifications gracefully

### JPA Entity Best Practices

1. **Separation from Domain**
   - JPA entities are DTOs, not domain models
   - Map between layers using mappers

2. **Indexes for Performance**
   - Add indexes on foreign keys
   - Add indexes on frequently queried fields

3. **Lazy vs Eager Fetching**
   - Default to LAZY for associations
   - Use EAGER sparingly

4. **Optimistic Locking in Production**
   - Always use @Version in production
   - Prevents lost updates

5. **Audit Fields**
   - Use @CreationTimestamp and @UpdateTimestamp
   - Track who created/updated records

### Mapper Best Practices

1. **Null-Safe**
   - Always check for null inputs
   - Return null or empty collections appropriately

2. **Stateless**
   - All methods static
   - No instance fields

3. **Bidirectional**
   - Provide both domain → JPA and JPA → domain
   - Provide update methods for existing entities

4. **Handle Collections**
   - Provide list mapping methods
   - Filter out nulls from streams

5. **Private Constructor**
   ```java
   private PolicyMapper() {
       throw new UnsupportedOperationException("Utility class");
   }
   ```

## Testing Strategy

### Domain Entity Tests

```java
@Test
void shouldCreateValidPolicy() {
    Money premium = Money.of(1000.0, "USD");
    Policy policy = new Policy("POL-001", premium);

    assertThat(policy.getPolicyNumber()).isEqualTo("POL-001");
    assertThat(policy.getStatus()).isEqualTo(PolicyStatus.DRAFT);
}

@Test
void shouldRejectNullPolicyNumber() {
    Money premium = Money.of(1000.0, "USD");

    assertThatThrownBy(() -> new Policy(null, premium))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Policy number is required");
}

@Test
void shouldActivateDraftPolicy() {
    Money premium = Money.of(1000.0, "USD");
    Policy policy = new Policy("POL-001", premium);

    policy.activate();

    assertThat(policy.getStatus()).isEqualTo(PolicyStatus.ACTIVE);
}
```

### Value Object Tests

```java
@Test
void shouldCreateValidMoney() {
    Money money = Money.of(100.0, "USD");

    assertThat(money.amount()).isEqualTo(BigDecimal.valueOf(100.0));
    assertThat(money.currency().getCurrencyCode()).isEqualTo("USD");
}

@Test
void shouldRejectNegativeAmount() {
    assertThatThrownBy(() -> Money.of(-100.0, "USD"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Amount cannot be negative");
}

@Test
void shouldAddMoneyWithSameCurrency() {
    Money money1 = Money.of(100.0, "USD");
    Money money2 = Money.of(50.0, "USD");

    Money result = money1.add(money2);

    assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(150.0));
}
```

### Mapper Tests

```java
@Test
void shouldMapDomainToJpa() {
    Money premium = Money.of(1000.0, "USD");
    Policy domain = new Policy(1L, "POL-001", premium, LocalDateTime.now(), LocalDateTime.now());

    PolicyJpaEntity jpa = PolicyMapper.toJpa(domain);

    assertThat(jpa.getId()).isEqualTo(1L);
    assertThat(jpa.getPolicyNumber()).isEqualTo("POL-001");
    assertThat(jpa.getPremiumAmount()).isEqualTo(BigDecimal.valueOf(1000.0));
    assertThat(jpa.getPremiumCurrency()).isEqualTo("USD");
}

@Test
void shouldHandleNullDomain() {
    PolicyJpaEntity jpa = PolicyMapper.toJpa(null);

    assertThat(jpa).isNull();
}
```

## Migration Guide

### From Anemic to Rich Domain Model

**Before (Anemic):**
```java
public class Policy {
    private Long id;
    private String policyNumber;
    private PolicyStatus status;

    // Only getters and setters, no behavior
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }
}

// Business logic in service layer
public class PolicyService {
    public void activatePolicy(Policy policy) {
        if (policy.getStatus() != PolicyStatus.DRAFT) {
            throw new IllegalStateException("Only draft policies can be activated");
        }
        policy.setStatus(PolicyStatus.ACTIVE);
    }
}
```

**After (Rich Domain Model):**
```java
public class Policy {
    private Long id;
    private String policyNumber;
    private PolicyStatus status;

    // Constructor with validation
    public Policy(String policyNumber, Money premium) {
        if (policyNumber == null || policyNumber.isBlank()) {
            throw new IllegalArgumentException("Policy number is required");
        }
        this.policyNumber = policyNumber;
        this.status = PolicyStatus.DRAFT;
    }

    // Business method (behavior in entity)
    public void activate() {
        if (this.status != PolicyStatus.DRAFT) {
            throw new IllegalStateException("Only draft policies can be activated");
        }
        this.status = PolicyStatus.ACTIVE;
    }

    // Only getters, controlled mutation
    public String getPolicyNumber() { return policyNumber; }
    public PolicyStatus getStatus() { return status; }
}

// Service just coordinates
public class PolicyService {
    public void activatePolicy(Long policyId) {
        Policy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new PolicyNotFoundException(policyId));

        policy.activate(); // Behavior in domain

        policyRepository.save(policy);
    }
}
```

## See Also

- [Maven Templates](../maven/README.md) - Project structure and dependencies
- [Repository Templates](../repository/README.md) - Persistence layer patterns
- [Use Case Templates](../usecase/README.md) - Application layer patterns
- [Test Templates](../tests/README.md) - Testing strategies

---

**Generated by:** BMad Builder
**Version:** 6.0.0
**Last Updated:** 2025-11-06
