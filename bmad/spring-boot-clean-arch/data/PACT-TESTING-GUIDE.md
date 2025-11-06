# Pact Contract Testing Guide

This guide explains how to use Pact for consumer-driven contract testing in Spring Boot Clean Architecture projects.

## What is Pact?

**Pact** is a consumer-driven contract testing framework that helps ensure microservices can communicate correctly without requiring end-to-end integration tests.

### Key Concepts

1. **Consumer**: The service that makes HTTP requests (e.g., frontend, another microservice)
2. **Provider**: The service that responds to HTTP requests (your Spring Boot API)
3. **Contract (Pact)**: A JSON file describing expected interactions between consumer and provider
4. **Pact Broker**: Optional central repository for sharing and versioning contracts

## Why Pact Instead of REST Assured?

| Aspect | REST Assured | Pact |
|--------|--------------|------|
| **Focus** | API testing within same codebase | Contract testing between services |
| **Scope** | Single service | Consumer + Provider |
| **Contract** | Implicit (in test code) | Explicit (Pact JSON file) |
| **Verification** | Tests run against real implementation | Consumer tests generate contract, provider verifies |
| **Microservices** | Not designed for distributed systems | Purpose-built for microservices |
| **Evolution** | Manual coordination needed | Contracts versioned and shareable |

**Use Pact when:**
- Building microservices that communicate via HTTP
- Need to verify API contracts between teams
- Want to catch breaking changes early
- Working in distributed teams

**Use REST Assured when:**
- Testing single monolithic application
- Need detailed HTTP testing features
- Not working with microservices contracts

## Pact Workflow

```
┌─────────────┐                    ┌─────────────┐
│  Consumer   │                    │  Provider   │
│   Tests     │                    │   Tests     │
└──────┬──────┘                    └──────┬──────┘
       │                                  │
       │ 1. Write consumer tests          │
       │    defining expectations         │
       │                                  │
       ▼                                  │
┌─────────────┐                           │
│  Pact File  │◄──────────────────────────┤
│   (JSON)    │   2. Generate contract    │
└──────┬──────┘                           │
       │                                  │
       │ 3. Publish to Pact Broker        │
       │    (optional but recommended)    │
       │                                  │
       └──────────────►┌──────────────┐   │
                       │ Pact Broker  │   │
                       └──────┬───────┘   │
                              │           │
                              │ 4. Fetch  │
                              │ contract  │
                              │           │
                              └───────────►│
                                          │
                                  5. Verify provider
                                     satisfies contract
```

## Generated Test Structure

### Consumer Test (PolicyConsumerPactTest.java)

```java
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PolicyProvider")
class PolicyConsumerPactTest {

    // Define expected interaction
    @Pact(consumer = "PolicyConsumer")
    public V4Pact createPolicyPact(PactDslWithProvider builder) {
        return builder
            .given("Policy can be created")  // Provider state
            .uponReceiving("a request to create a policy")
                .method("POST")
                .path("/api/v1/policies")
                .body(new PactDslJsonBody()
                    .stringType("customerId", "CUST-001")
                    .date("effectiveDate", "yyyy-MM-dd"))
            .willRespondWith()
                .status(201)
                .body(new PactDslJsonBody()
                    .stringMatcher("policyNumber", "POL-\\d{4}-\\d{6}")
                    .decimalType("totalPremium", 800.00))
            .toPact(V4Pact.class);
    }

    // Test using the Pact
    @Test
    @PactTestFor(pactMethod = "createPolicyPact")
    void testCreatePolicy(MockServer mockServer) {
        // Test makes real HTTP call to mock server
        // If contract is satisfied, test passes and Pact file is generated
    }
}
```

### Provider Test (PolicyProviderPactTest.java)

```java
@WebMvcTest(PolicyController.class)
@Provider("PolicyProvider")
@PactFolder("target/pacts")  // Location of Pact files
class PolicyProviderPactTest {

    @MockBean
    private CreatePolicyUseCase createPolicyUseCase;

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();  // Verifies all Pact interactions
    }

    // Set up provider state
    @State("Policy can be created")
    void toPolicyCanBeCreated() {
        // Mock use case behavior to match consumer expectations
        when(createPolicyUseCase.execute(any()))
            .thenReturn(new CreatePolicyOutput(...));
    }
}
```

## Pact File Format

Generated Pact file (`PolicyConsumer-PolicyProvider.json`):

```json
{
  "consumer": {
    "name": "PolicyConsumer"
  },
  "provider": {
    "name": "PolicyProvider"
  },
  "interactions": [
    {
      "description": "a request to create a policy",
      "providerStates": [
        {
          "name": "Policy can be created"
        }
      ],
      "request": {
        "method": "POST",
        "path": "/api/v1/policies",
        "body": {
          "customerId": "CUST-001",
          "effectiveDate": "2025-12-01"
        }
      },
      "response": {
        "status": 201,
        "body": {
          "policyNumber": "POL-2025-000001",
          "totalPremium": 800.00
        }
      }
    }
  ],
  "metadata": {
    "pactSpecification": {
      "version": "4.0"
    }
  }
}
```

## Using Pact Broker (Optional but Recommended)

### Why Use Pact Broker?

- **Central Repository**: Share contracts between teams
- **Versioning**: Track contract evolution over time
- **CI/CD Integration**: Verify contracts before deployment
- **Can-I-Deploy**: Check if services can be safely deployed together
- **Webhooks**: Trigger provider verification when consumer changes

### Setup Pact Broker (Docker)

```bash
docker run -d --name pact-broker \
  -p 9292:9292 \
  -e PACT_BROKER_DATABASE_URL=sqlite:///pact_broker.sqlite \
  pactfoundation/pact-broker
```

### Configure Publishing

Set environment variables:

```bash
export PACT_BROKER_URL=http://localhost:9292
export PACT_PUBLISH=true
export PACT_CONSUMER_VERSION=$(git rev-parse HEAD)
export PACT_CONSUMER_TAG=main
```

### Maven Plugin Configuration

Add to `pom.xml`:

```xml
<plugin>
    <groupId>au.com.dius.pact.provider</groupId>
    <artifactId>maven</artifactId>
    <version>4.6.4</version>
    <configuration>
        <pactBrokerUrl>${env.PACT_BROKER_URL}</pactBrokerUrl>
        <pactBrokerToken>${env.PACT_BROKER_TOKEN}</pactBrokerToken>
        <projectVersion>${env.PACT_CONSUMER_VERSION}</projectVersion>
        <tags>
            <tag>${env.PACT_CONSUMER_TAG}</tag>
        </tags>
    </configuration>
</plugin>
```

## Best Practices

### 1. Use Provider States

Always set up provider states for different scenarios:

```java
@State("Policy with ID POL-2025-000001 exists")
void toPolicyExists() {
    // Mock specific behavior
}

@State("Policy with ID POL-9999-999999 does not exist")
void toPolicyDoesNotExist() {
    // Mock 404 behavior
}
```

### 2. Test One Interaction Per Test

```java
// ✅ Good: One interaction
@Test
@PactTestFor(pactMethod = "createPolicyPact")
void testCreatePolicy(MockServer mockServer) { ... }

// ❌ Avoid: Multiple interactions in one test
@Test
void testMultipleOperations() {
    // create, update, delete all in one test
}
```

### 3. Use Matchers for Flexible Contracts

```java
new PactDslJsonBody()
    .stringMatcher("policyNumber", "POL-\\d{4}-\\d{6}", "POL-2025-000001")
    .decimalType("premium", 800.00)  // Any decimal
    .date("effectiveDate", "yyyy-MM-dd")  // Date format
    .eachLike("coverages")  // Array with at least one item
```

### 4. Version Your Contracts

Use Git SHA or semantic versioning for consumer versions:

```bash
# Git SHA (recommended for CI/CD)
export PACT_CONSUMER_VERSION=$(git rev-parse HEAD)

# Semantic version
export PACT_CONSUMER_VERSION=1.2.3
```

### 5. Verify on Both Sides

- **Consumer**: Run tests before merging PR
- **Provider**: Run verification before deploying

## CI/CD Integration

### Consumer Pipeline

```yaml
# .github/workflows/consumer-tests.yml
name: Consumer Pact Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run Pact Consumer Tests
        run: mvn test -Dtest=*ConsumerPactTest

      - name: Publish Pacts
        if: github.ref == 'refs/heads/main'
        env:
          PACT_BROKER_URL: ${{ secrets.PACT_BROKER_URL }}
          PACT_BROKER_TOKEN: ${{ secrets.PACT_BROKER_TOKEN }}
          PACT_PUBLISH: true
          PACT_CONSUMER_VERSION: ${{ github.sha }}
        run: mvn pact:publish
```

### Provider Pipeline

```yaml
# .github/workflows/provider-tests.yml
name: Provider Pact Verification

on: [push, pull_request]

jobs:
  verify:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Can I Deploy?
        run: |
          pact-broker can-i-deploy \
            --pacticipant PolicyProvider \
            --version ${{ github.sha }} \
            --to-environment production

      - name: Verify Pacts
        env:
          PACT_BROKER_URL: ${{ secrets.PACT_BROKER_URL }}
        run: mvn test -Dtest=*ProviderPactTest
```

## Troubleshooting

### Pact File Not Generated

**Problem**: Consumer test passes but no Pact file created.

**Solution**: Ensure `@PactTestFor` annotation is present on test class and `pactMethod` matches the `@Pact` method name exactly.

### Provider Verification Fails

**Problem**: Provider test fails with "State not found" error.

**Solution**: Add `@State` method matching the provider state name in consumer Pact:

```java
// Consumer defines state
.given("Policy can be created")

// Provider must have matching @State
@State("Policy can be created")
void toPolicyCanBeCreated() { ... }
```

### Matcher Issues

**Problem**: Response doesn't match expected format.

**Solution**: Use appropriate matchers:

```java
// ❌ Exact match - brittle
.stringValue("id", "123")

// ✅ Type match - flexible
.stringType("id", "123")

// ✅ Regex match - format validation
.stringMatcher("policyNumber", "POL-\\d{4}-\\d{6}")
```

## Resources

- **Pact Documentation**: https://docs.pact.io/
- **Pact JVM**: https://github.com/pact-foundation/pact-jvm
- **Pact Broker**: https://docs.pact.io/pact_broker
- **Best Practices**: https://docs.pact.io/implementation_guides/jvm/readme#best-practices

## See Also

- Module test templates: `/templates/tests/`
- Test Engineer Agent documentation
- Policy Management example with Pact tests
