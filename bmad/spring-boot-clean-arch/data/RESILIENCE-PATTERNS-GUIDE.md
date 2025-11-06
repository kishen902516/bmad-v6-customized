# Resilience4j Patterns Guide

## Overview

This guide covers fault tolerance patterns implemented with Resilience4j in Spring Boot Clean Architecture applications. These patterns protect your application from cascading failures, improve reliability, and provide graceful degradation when external dependencies fail.

## Table of Contents

1. [Resilience Patterns Overview](#resilience-patterns-overview)
2. [Circuit Breaker](#circuit-breaker)
3. [Retry](#retry)
4. [Bulkhead](#bulkhead)
5. [Rate Limiter](#rate-limiter)
6. [Time Limiter](#time-limiter)
7. [Combining Patterns](#combining-patterns)
8. [Configuration](#configuration)
9. [Testing](#testing)
10. [Monitoring](#monitoring)
11. [Best Practices](#best-practices)

---

## Resilience Patterns Overview

### When to Use Each Pattern

| Pattern | Use Case | Protects Against |
|---------|----------|------------------|
| **Circuit Breaker** | External service calls | Cascading failures, repeated failures |
| **Retry** | Transient failures | Temporary network glitches, timeouts |
| **Bulkhead** | Resource-intensive operations | Thread pool exhaustion, resource starvation |
| **Rate Limiter** | API endpoints | Request flooding, abuse |
| **Time Limiter** | Long-running operations | Hanging connections, slow responses |

### Pattern Selection Decision Tree

```
Is it an external service call?
  Yes → Use Circuit Breaker + Retry

Is it resource-intensive or potentially slow?
  Yes → Add Bulkhead

Do you need to limit request rate?
  Yes → Add Rate Limiter

Does it need a strict timeout?
  Yes → Add Time Limiter
```

---

## Circuit Breaker

### What is Circuit Breaker?

Circuit Breaker prevents cascading failures by stopping calls to a failing service, allowing it time to recover.

### States

```
CLOSED (Normal)
   ↓ (failure threshold exceeded)
OPEN (Blocking calls)
   ↓ (wait duration elapsed)
HALF_OPEN (Testing recovery)
   ↓ (success) → CLOSED
   ↓ (failure) → OPEN
```

### Configuration

**application.properties:**
```properties
# Circuit Breaker Configuration
resilience4j.circuitbreaker.instances.externalService.sliding-window-size=10
resilience4j.circuitbreaker.instances.externalService.minimum-number-of-calls=5
resilience4j.circuitbreaker.instances.externalService.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.externalService.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.instances.externalService.permitted-number-of-calls-in-half-open-state=3
resilience4j.circuitbreaker.instances.externalService.automatic-transition-from-open-to-half-open-enabled=true
```

**Key Parameters:**
- `sliding-window-size`: Number of calls tracked (e.g., last 10 calls)
- `minimum-number-of-calls`: Minimum calls before calculating failure rate
- `failure-rate-threshold`: Percentage of failures to open circuit (0-100)
- `wait-duration-in-open-state`: Time to wait before HALF_OPEN
- `permitted-number-of-calls-in-half-open-state`: Test calls in HALF_OPEN

### Code Example

```java
@Service
public class PolicyExternalService {

    private static final Logger log = LoggerFactory.getLogger(PolicyExternalService.class);
    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "externalService", fallbackMethod = "getPolicyFallback")
    public PolicyDTO getPolicy(String policyNumber) {
        log.info("Calling external policy service for: {}", policyNumber);

        return restTemplate.getForObject(
            "https://external-api.com/policies/{id}",
            PolicyDTO.class,
            policyNumber
        );
    }

    // Fallback method - must match original signature + Throwable
    private PolicyDTO getPolicyFallback(String policyNumber, Throwable throwable) {
        log.warn("Circuit breaker OPEN - Using fallback for policy: {}", policyNumber);

        // Option 1: Return cached data
        return cacheService.getCachedPolicy(policyNumber);

        // Option 2: Return default/empty response
        // return PolicyDTO.empty();

        // Option 3: Throw custom exception
        // throw new ExternalServiceUnavailableException("Policy service unavailable", throwable);
    }
}
```

### When Circuit Opens

The circuit opens when:
1. Failure rate exceeds threshold (e.g., 50% of last 10 calls failed)
2. Minimum number of calls reached (prevents premature opening)

### Fallback Strategies

1. **Cached Data**: Return previously cached successful response
2. **Default Values**: Return safe default or empty response
3. **Custom Exception**: Throw domain-specific exception for handling upstream

---

## Retry

### What is Retry?

Retry automatically retries failed operations with configurable backoff strategy.

### Configuration

**application.properties:**
```properties
# Retry Configuration
resilience4j.retry.instances.externalApi.max-attempts=5
resilience4j.retry.instances.externalApi.wait-duration=1s
resilience4j.retry.instances.externalApi.enable-exponential-backoff=true
resilience4j.retry.instances.externalApi.exponential-backoff-multiplier=2
resilience4j.retry.instances.externalApi.retry-exceptions=java.io.IOException,java.util.concurrent.TimeoutException
resilience4j.retry.instances.externalApi.ignore-exceptions=java.lang.IllegalArgumentException
```

**Key Parameters:**
- `max-attempts`: Total attempts (including initial call)
- `wait-duration`: Initial wait time between retries
- `enable-exponential-backoff`: Increase wait time exponentially
- `exponential-backoff-multiplier`: Multiply factor (2 = double each time)
- `retry-exceptions`: Which exceptions trigger retry
- `ignore-exceptions`: Which exceptions skip retry

### Backoff Strategy

**Exponential Backoff Example:**
```
Attempt 1: Immediate (0ms)
Attempt 2: 1s wait
Attempt 3: 2s wait (1s × 2)
Attempt 4: 4s wait (2s × 2)
Attempt 5: 8s wait (4s × 2)
```

### Code Example

```java
@Service
public class ClaimSubmissionService {

    private static final Logger log = LoggerFactory.getLogger(ClaimSubmissionService.class);

    @Retry(name = "externalApi", fallbackMethod = "submitClaimFallback")
    public ClaimResponse submitClaim(ClaimRequest request) {
        log.info("Attempting to submit claim: {}", request.getClaimNumber());

        // This will be retried up to 5 times with exponential backoff
        return externalClaimService.submit(request);
    }

    private ClaimResponse submitClaimFallback(ClaimRequest request, Throwable throwable) {
        log.error("All retry attempts exhausted for claim: {}", request.getClaimNumber());

        throw new ExternalServiceUnavailableException(
            String.format("Claim submission failed after %d attempts", 5),
            throwable
        );
    }
}
```

### Retry Best Practices

1. **Idempotent Operations**: Only retry operations that are safe to repeat
2. **Transient Failures**: Best for temporary network issues, not business logic errors
3. **Exponential Backoff**: Prevents overwhelming recovering services
4. **Max Attempts**: Limit retries to avoid infinite loops
5. **Exception Filtering**: Only retry recoverable exceptions

---

## Bulkhead

### What is Bulkhead?

Bulkhead isolates resources to prevent failure in one area from consuming all resources.

### Types

1. **Semaphore Bulkhead**: Limits concurrent calls (lightweight)
2. **Thread Pool Bulkhead**: Isolates execution in separate thread pool

### Configuration

**Semaphore Bulkhead:**
```properties
# Semaphore-based Bulkhead
resilience4j.bulkhead.instances.externalService.max-concurrent-calls=10
resilience4j.bulkhead.instances.externalService.max-wait-duration=10ms
```

**Thread Pool Bulkhead:**
```properties
# Thread Pool Bulkhead
resilience4j.thread-pool-bulkhead.instances.heavyOperations.max-thread-pool-size=8
resilience4j.thread-pool-bulkhead.instances.heavyOperations.core-thread-pool-size=4
resilience4j.thread-pool-bulkhead.instances.heavyOperations.queue-capacity=50
resilience4j.thread-pool-bulkhead.instances.heavyOperations.keep-alive-duration=20ms
```

### Code Example - Semaphore Bulkhead

```java
@Service
public class UnderwritingService {

    @Bulkhead(name = "externalService", type = Bulkhead.Type.SEMAPHORE)
    public UnderwritingResult evaluateRisk(PolicyApplication application) {
        // Only 10 concurrent calls allowed
        return externalUnderwritingEngine.evaluate(application);
    }
}
```

### Code Example - Thread Pool Bulkhead

```java
@Service
public class DocumentProcessingService {

    @Bulkhead(name = "heavyOperations", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<ProcessedDocument> processLargeDocument(Document document) {
        // Executed in isolated thread pool (max 8 threads)
        return CompletableFuture.supplyAsync(() -> {
            return documentProcessor.process(document);
        });
    }
}
```

### When to Use Which Type

- **Semaphore**: Lightweight, in-memory operations, blocking I/O
- **Thread Pool**: CPU-intensive tasks, long-running operations, full isolation needed

---

## Rate Limiter

### What is Rate Limiter?

Rate Limiter controls the rate of requests to prevent overloading services.

### Configuration

```properties
# Rate Limiter Configuration
resilience4j.ratelimiter.instances.apiRateLimit.limit-for-period=100
resilience4j.ratelimiter.instances.apiRateLimit.limit-refresh-period=1s
resilience4j.ratelimiter.instances.apiRateLimit.timeout-duration=500ms
```

**Key Parameters:**
- `limit-for-period`: Number of requests allowed per period
- `limit-refresh-period`: Time period for limit (e.g., 1 second)
- `timeout-duration`: How long to wait for permission

### Code Example

```java
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    @GetMapping("/{id}")
    @RateLimiter(name = "apiRateLimit")
    public ResponseEntity<PolicyDTO> getPolicy(@PathVariable String id) {
        // Limited to 100 requests per second
        return ResponseEntity.ok(policyService.getPolicy(id));
    }
}
```

---

## Time Limiter

### What is Time Limiter?

Time Limiter sets a maximum time for operations to complete.

### Configuration

```properties
# Time Limiter Configuration
resilience4j.timelimiter.instances.externalApiTimeout.timeout-duration=5s
resilience4j.timelimiter.instances.externalApiTimeout.cancel-running-future=true
```

### Code Example

```java
@Service
public class ExternalValidationService {

    @TimeLimiter(name = "externalApiTimeout")
    @CircuitBreaker(name = "externalService")
    public CompletableFuture<ValidationResult> validatePolicy(Policy policy) {
        return CompletableFuture.supplyAsync(() -> {
            // Must complete within 5 seconds
            return externalValidator.validate(policy);
        });
    }
}
```

---

## Combining Patterns

### Recommended Combinations

**External Service Call (Most Common):**
```java
@CircuitBreaker(name = "externalService", fallbackMethod = "fallback")
@Retry(name = "externalApi")
@Bulkhead(name = "externalService")
public ResponseDTO callExternalService(RequestDTO request) {
    // Protected by all three patterns
    return restTemplate.postForObject(url, request, ResponseDTO.class);
}
```

**Execution Order:**
```
Request → Bulkhead → TimeLimiter → CircuitBreaker → Retry → Target Method
```

### Pattern Interaction

1. **Bulkhead** checks first (resource available?)
2. **Time Limiter** starts timer
3. **Circuit Breaker** checks state (OPEN/CLOSED?)
4. **Retry** attempts call
5. Method executes
6. If failure, **Retry** attempts again
7. If still failing, **Circuit Breaker** may open
8. **Fallback** called if configured

---

## Configuration

### Properties-Based Configuration

**src/main/resources/application.properties:**
```properties
# Include all Resilience4j configuration
# See data/resilience-templates/resilience4j-config.properties
```

### Programmatic Configuration

```java
@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .build();

        return CircuitBreakerRegistry.of(config);
    }
}
```

### Which to Use?

- **Properties**: Most use cases, easy to change without recompilation
- **Programmatic**: Complex logic, environment-specific configs, custom calculators

---

## Testing

### Unit Testing Circuit Breaker

```java
@Test
void shouldOpenCircuitAfterFailureThreshold() {
    // Given
    CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
    CircuitBreaker circuitBreaker = registry.circuitBreaker("test");

    // When - trigger 6 failures (threshold is 50% of 10 calls)
    for (int i = 0; i < 6; i++) {
        try {
            CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> {
                throw new RuntimeException("Service failure");
            }).apply();
        } catch (Throwable ignored) {}
    }

    // Then
    assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
}
```

### Integration Testing with Retry

```java
@SpringBootTest
class RetryIntegrationTest {

    @MockBean
    private ExternalService externalService;

    @Autowired
    private MyService myService;

    @Test
    void shouldRetryThreeTimesBeforeFailing() throws Exception {
        // Given
        when(externalService.call())
            .thenThrow(new IOException("Network error"))
            .thenThrow(new IOException("Network error"))
            .thenReturn("success");

        // When
        String result = myService.callWithRetry();

        // Then
        assertThat(result).isEqualTo("success");
        verify(externalService, times(3)).call();
    }
}
```

### Testing Fallback Methods

```java
@Test
void shouldCallFallbackWhenCircuitIsOpen() {
    // Given
    CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("test");
    circuitBreaker.transitionToOpenState();

    // When
    String result = myService.callServiceWithFallback("input");

    // Then
    assertThat(result).isEqualTo("fallback-value");
}
```

---

## Monitoring

### Actuator Endpoints

Add to `application.properties`:
```properties
management.endpoints.web.exposure.include=health,circuitbreakers,circuitbreakerevents,retries,retryevents
management.endpoint.health.show-details=always
management.health.circuitbreakers.enabled=true
```

### Available Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Overall health including circuit breaker states |
| `/actuator/circuitbreakers` | All circuit breaker states |
| `/actuator/circuitbreakerevents` | Recent circuit breaker events |
| `/actuator/retries` | Retry configuration and status |
| `/actuator/retryevents` | Recent retry events |
| `/actuator/bulkheads` | Bulkhead status |
| `/actuator/ratelimiters` | Rate limiter status |

### Event Logging

```java
@Configuration
public class ResilienceMonitoringConfig {

    @Bean
    public RegistryEventConsumer<CircuitBreaker> circuitBreakerEventConsumer(
            CircuitBreakerRegistry registry) {

        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> event) {
                CircuitBreaker cb = event.getAddedEntry();

                cb.getEventPublisher()
                    .onStateTransition(e -> log.warn(
                        "Circuit Breaker '{}' state changed: {} -> {}",
                        cb.getName(),
                        e.getStateTransition().getFromState(),
                        e.getStateTransition().getToState()
                    ))
                    .onFailureRateExceeded(e -> log.error(
                        "Circuit Breaker '{}' failure rate exceeded: {}%",
                        cb.getName(), e.getFailureRate()
                    ));
            }
        };
    }
}
```

### Metrics with Micrometer

Resilience4j automatically exports metrics:
- `resilience4j.circuitbreaker.calls`
- `resilience4j.circuitbreaker.state`
- `resilience4j.retry.calls`
- `resilience4j.bulkhead.available.concurrent.calls`

---

## Best Practices

### 1. Always Provide Fallback Methods

```java
// ✅ Good - Has fallback
@CircuitBreaker(name = "external", fallbackMethod = "fallback")
public String call() { ... }

private String fallback(Throwable t) { ... }

// ❌ Bad - No fallback, throws exception to caller
@CircuitBreaker(name = "external")
public String call() { ... }
```

### 2. Use Meaningful Circuit Breaker Names

```java
// ✅ Good - Descriptive names
@CircuitBreaker(name = "paymentGateway")
@CircuitBreaker(name = "underwritingEngine")

// ❌ Bad - Generic names
@CircuitBreaker(name = "external")
@CircuitBreaker(name = "service")
```

### 3. Configure Per-Service Thresholds

```properties
# ✅ Good - Different configs per service
resilience4j.circuitbreaker.instances.paymentGateway.failure-rate-threshold=30
resilience4j.circuitbreaker.instances.notificationService.failure-rate-threshold=70

# ❌ Bad - One size fits all
resilience4j.circuitbreaker.configs.default.failure-rate-threshold=50
```

### 4. Only Retry Idempotent Operations

```java
// ✅ Good - GET is idempotent
@Retry(name = "api")
public PolicyDTO getPolicy(String id) { ... }

// ❌ Bad - POST may create duplicates
@Retry(name = "api")
public void createPolicy(Policy policy) { ... }
```

### 5. Use Exponential Backoff for Retries

```properties
# ✅ Good - Exponential backoff
resilience4j.retry.instances.api.enable-exponential-backoff=true
resilience4j.retry.instances.api.exponential-backoff-multiplier=2

# ❌ Bad - Fixed delay hammers recovering service
resilience4j.retry.instances.api.wait-duration=1s
```

### 6. Separate Bulkheads for Different Resources

```java
// ✅ Good - Separate bulkheads
@Bulkhead(name = "externalApi")    // Fast operations
@Bulkhead(name = "heavyCompute")   // CPU-intensive
@Bulkhead(name = "documentProcess") // Slow file operations

// ❌ Bad - Single bulkhead for everything
@Bulkhead(name = "default")
```

### 7. Monitor Circuit Breaker States

```java
// ✅ Good - Log state transitions
circuitBreaker.getEventPublisher()
    .onStateTransition(event ->
        alerting.sendAlert("Circuit breaker state change", event));

// ❌ Bad - No monitoring, failures go unnoticed
```

### 8. Test Resilience Patterns

```java
// ✅ Good - Test fallback behavior
@Test
void shouldUseFallbackWhenServiceFails() {
    // Test circuit breaker opens
    // Test fallback is called
    // Test system remains stable
}

// ❌ Bad - No resilience testing
```

### 9. Use Rate Limiters for Public APIs

```java
// ✅ Good - Protect public endpoints
@RestController
@RequestMapping("/api/public")
public class PublicApiController {

    @RateLimiter(name = "publicApi")
    @GetMapping("/quotes")
    public Quote getQuote() { ... }
}
```

### 10. Combine Patterns Appropriately

```java
// ✅ Good - Full protection
@CircuitBreaker(name = "payment", fallbackMethod = "fallback")
@Retry(name = "paymentRetry")
@Bulkhead(name = "payment")
@TimeLimiter(name = "paymentTimeout")
public PaymentResult processPayment(Payment payment) { ... }

// ❌ Bad - No protection
public PaymentResult processPayment(Payment payment) { ... }
```

---

## Common Pitfalls

### 1. Fallback Method Signature Mismatch

```java
// ❌ Wrong - Missing Throwable parameter
@CircuitBreaker(name = "api", fallbackMethod = "fallback")
public String call(String input) { ... }

private String fallback(String input) { ... }  // Missing Throwable!

// ✅ Correct
private String fallback(String input, Throwable t) { ... }
```

### 2. Not Making Operations Idempotent

```java
// ❌ Dangerous - Retry may create duplicates
@Retry(name = "api")
public void transferMoney(Account from, Account to, BigDecimal amount) {
    // May execute multiple times!
}

// ✅ Safe - Use idempotency key
@Retry(name = "api")
public void transferMoney(String idempotencyKey, Account from, Account to, BigDecimal amount) {
    if (transferAlreadyExists(idempotencyKey)) return;
    // Safe to retry
}
```

### 3. Circuit Breaker Opens Too Quickly

```properties
# ❌ Bad - Opens after just 2 failures
resilience4j.circuitbreaker.instances.api.sliding-window-size=2
resilience4j.circuitbreaker.instances.api.minimum-number-of-calls=1
resilience4j.circuitbreaker.instances.api.failure-rate-threshold=50

# ✅ Better - Requires pattern of failures
resilience4j.circuitbreaker.instances.api.sliding-window-size=10
resilience4j.circuitbreaker.instances.api.minimum-number-of-calls=5
```

### 4. Ignoring Business Exceptions

```properties
# ❌ Bad - Retries validation errors
resilience4j.retry.instances.api.retry-exceptions=java.lang.Exception

# ✅ Good - Only retry transient failures
resilience4j.retry.instances.api.retry-exceptions=java.io.IOException,java.util.concurrent.TimeoutException
resilience4j.retry.instances.api.ignore-exceptions=com.myapp.BusinessValidationException
```

### 5. No Timeout on External Calls

```java
// ❌ Bad - May hang forever
@CircuitBreaker(name = "external")
public String call() {
    return restTemplate.getForObject(url, String.class);
}

// ✅ Good - Has timeout
@TimeLimiter(name = "externalTimeout")
@CircuitBreaker(name = "external")
public CompletableFuture<String> call() {
    return CompletableFuture.supplyAsync(() ->
        restTemplate.getForObject(url, String.class));
}
```

---

## Conclusion

Resilience patterns are essential for building robust, production-ready microservices. Key takeaways:

1. **Circuit Breaker**: Prevents cascading failures
2. **Retry**: Handles transient errors automatically
3. **Bulkhead**: Isolates resources to prevent complete failure
4. **Rate Limiter**: Protects against overload
5. **Time Limiter**: Enforces operation timeouts

**Remember**:
- Combine patterns appropriately
- Always provide fallback methods
- Test your resilience behavior
- Monitor circuit breaker states
- Use meaningful configurations per service

For more examples, see:
- `/templates/resilience/` - Code templates
- `/data/resilience-templates/` - Configuration templates
