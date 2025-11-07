# API Documentation

## Overview

The Insurance Policy Service provides RESTful APIs for managing insurance policies, claims, and payments. All APIs follow REST principles and return JSON responses.

**Base URL**: `http://localhost:8080`

**API Version**: v1

**Content-Type**: `application/json`

## Authentication

Currently, the API does not require authentication. In production, consider implementing:
- OAuth 2.0 / JWT tokens
- API key authentication
- Rate limiting

## Error Handling

All endpoints return consistent error responses:

```json
{
  "timestamp": "2025-11-07T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/policies"
}
```

### HTTP Status Codes

- `200 OK` - Successful GET request
- `201 Created` - Successful POST request (resource created)
- `400 Bad Request` - Invalid request data or validation failure
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violation or duplicate resource
- `500 Internal Server Error` - Unexpected server error

## API Endpoints

### 1. Policy Management APIs

#### 1.1 Create Policy

Creates a new insurance policy with coverages.

**Endpoint**: `POST /api/v1/policies`

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "customerId": "CUST-001",
  "effectiveDate": "2025-12-01",
  "coverages": [
    {
      "coverageType": "Liability",
      "premiumAmount": 500.00,
      "currency": "USD"
    },
    {
      "coverageType": "Collision",
      "premiumAmount": 300.00,
      "currency": "USD"
    }
  ]
}
```

**Request Fields**:
- `customerId` (string, required): Unique customer identifier
- `effectiveDate` (date, required): Policy effective date (must be in future)
- `coverages` (array, required): List of coverage items (at least one required)
  - `coverageType` (string, required): Type of coverage
  - `premiumAmount` (number, required): Premium amount (must be > 0)
  - `currency` (string, required): Currency code (e.g., USD)

**Response** (201 Created):
```json
{
  "policyNumber": "POL-2025-000001",
  "totalPremiumAmount": 800.00,
  "totalPremiumCurrency": "USD",
  "status": "DRAFT"
}
```

**Response Fields**:
- `policyNumber` (string): Auto-generated policy number in format POL-YYYY-NNNNNN
- `totalPremiumAmount` (number): Sum of all coverage premiums
- `totalPremiumCurrency` (string): Currency code
- `status` (string): Policy status (DRAFT, ACTIVE, CANCELLED, EXPIRED)

**Error Responses**:
- `400 Bad Request`: Validation failure (missing fields, invalid dates, negative amounts)
- `500 Internal Server Error`: Database or system error

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/policies \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST-001",
    "effectiveDate": "2025-12-01",
    "coverages": [
      {"coverageType": "Liability", "premiumAmount": 500.00, "currency": "USD"}
    ]
  }'
```

#### 1.2 Health Check (Policy Service)

Simple health check endpoint for the Policy API.

**Endpoint**: `GET /api/v1/policies/health`

**Response** (200 OK):
```
Policy Service is running
```

---

### 2. Claims Management APIs

#### 2.1 Submit Claim

Submits a new insurance claim for an existing policy.

**Endpoint**: `POST /api/v1/claims`

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "policyId": "1",
  "claimedAmount": 5000.00,
  "incidentDate": "2025-10-15",
  "description": "Vehicle collision on highway",
  "currency": "USD"
}
```

**Request Fields**:
- `policyId` (string, required): ID of the policy for this claim
- `claimedAmount` (number, required): Claimed amount (must be > 0)
- `incidentDate` (date, required): Date of incident (must not be in future)
- `description` (string, required): Description of the incident
- `currency` (string, required): Currency code (e.g., USD)

**Response** (201 Created):
```json
{
  "claimId": 1,
  "claimNumber": "CLM-2025-000001",
  "policyId": "1",
  "claimedAmount": 5000.00,
  "status": "SUBMITTED",
  "submittedDate": "2025-11-07"
}
```

**Response Fields**:
- `claimId` (number): Internal claim ID
- `claimNumber` (string): Auto-generated claim number in format CLM-YYYY-NNNNNN
- `policyId` (string): Associated policy ID
- `claimedAmount` (number): Claimed amount
- `status` (string): Claim status (SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID)
- `submittedDate` (date): Date claim was submitted

**Response Headers**:
```
Location: /api/v1/claims/1
```

**Error Responses**:
- `400 Bad Request`: Validation failure (missing fields, invalid dates, negative amounts, business rule violations)
- `404 Not Found`: Policy not found
- `409 Conflict`: Policy is not active or reached claim limits
- `500 Internal Server Error`: Database or system error

**Business Rules**:
- Policy must exist and be active
- Claimed amount must be greater than zero
- Incident date cannot be in the future
- Claimed amount must not exceed policy limits (if applicable)

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/claims \
  -H "Content-Type: application/json" \
  -d '{
    "policyId": "1",
    "claimedAmount": 5000.00,
    "incidentDate": "2025-10-15",
    "description": "Vehicle collision",
    "currency": "USD"
  }'
```

#### 2.2 Health Check (Claims Service)

Simple health check endpoint for the Claims API.

**Endpoint**: `GET /api/v1/claims/health`

**Response** (200 OK):
```
Claims Service is running
```

---

### 3. Payment Processing APIs

#### 3.1 Process Payment

Processes a payment for an approved insurance claim.

**Endpoint**: `POST /api/v1/payments`

**Request Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "claimId": 1,
  "amount": 5000.00,
  "paymentMethod": "BANK_TRANSFER",
  "transactionId": "TXN-2025-ABC123",
  "processedBy": "admin@insurance.com",
  "notes": "Full claim settlement"
}
```

**Request Fields**:
- `claimId` (number, required): ID of the approved claim
- `amount` (number, required): Payment amount (must be > 0)
- `paymentMethod` (string, required): Payment method (BANK_TRANSFER, CHECK, CARD, CASH)
- `transactionId` (string, required): Unique transaction identifier
- `processedBy` (string, required): User who processed the payment
- `notes` (string, optional): Additional payment notes

**Response** (201 Created):
```json
{
  "paymentId": 1,
  "claimId": 1,
  "amount": 5000.00,
  "paymentMethod": "BANK_TRANSFER",
  "paymentStatus": "COMPLETED",
  "transactionId": "TXN-2025-ABC123",
  "paymentDate": "2025-11-07",
  "processedBy": "admin@insurance.com",
  "notes": "Full claim settlement"
}
```

**Response Fields**:
- `paymentId` (number): Internal payment ID
- `claimId` (number): Associated claim ID
- `amount` (number): Payment amount
- `paymentMethod` (string): Payment method used
- `paymentStatus` (string): Payment status (PENDING, COMPLETED, FAILED)
- `transactionId` (string): Transaction identifier
- `paymentDate` (date): Date payment was processed
- `processedBy` (string): User who processed the payment
- `notes` (string): Additional notes

**Error Responses**:
- `400 Bad Request`: Validation failure (missing fields, negative amounts)
- `404 Not Found`: Claim not found
- `409 Conflict`: Duplicate transaction ID or claim not approved
- `500 Internal Server Error`: Database or system error

**Business Rules**:
- Claim must exist and be approved
- Transaction ID must be unique
- Payment amount must be greater than zero
- Payment amount must not exceed claim amount

**Example cURL**:
```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "claimId": 1,
    "amount": 5000.00,
    "paymentMethod": "BANK_TRANSFER",
    "transactionId": "TXN-2025-ABC123",
    "processedBy": "admin@insurance.com",
    "notes": "Full settlement"
  }'
```

#### 3.2 Get Payment by ID

Retrieves payment details by payment ID.

**Endpoint**: `GET /api/v1/payments/{id}`

**Path Parameters**:
- `id` (number, required): Payment ID

**Response** (200 OK):
```json
{
  "paymentId": 1,
  "claimId": 1,
  "amount": 5000.00,
  "paymentMethod": "BANK_TRANSFER",
  "paymentStatus": "COMPLETED",
  "transactionId": "TXN-2025-ABC123",
  "paymentDate": "2025-11-07",
  "processedBy": "admin@insurance.com",
  "notes": "Full claim settlement"
}
```

**Error Responses**:
- `404 Not Found`: Payment not found

**Example cURL**:
```bash
curl -X GET http://localhost:8080/api/v1/payments/1
```

#### 3.3 Get Payments for Claim

Retrieves all payments associated with a specific claim.

**Endpoint**: `GET /api/v1/payments/claim/{claimId}`

**Path Parameters**:
- `claimId` (number, required): Claim ID

**Response** (200 OK):
```json
[
  {
    "paymentId": 1,
    "claimId": 1,
    "amount": 5000.00,
    "paymentMethod": "BANK_TRANSFER",
    "paymentStatus": "COMPLETED",
    "transactionId": "TXN-2025-ABC123",
    "paymentDate": "2025-11-07",
    "processedBy": "admin@insurance.com",
    "notes": "Full claim settlement"
  }
]
```

**Example cURL**:
```bash
curl -X GET http://localhost:8080/api/v1/payments/claim/1
```

#### 3.4 Get All Payments

Retrieves all payments with optional status filter.

**Endpoint**: `GET /api/v1/payments`

**Query Parameters**:
- `status` (string, optional): Filter by payment status (PENDING, COMPLETED, FAILED)

**Response** (200 OK):
```json
[
  {
    "paymentId": 1,
    "claimId": 1,
    "amount": 5000.00,
    "paymentMethod": "BANK_TRANSFER",
    "paymentStatus": "COMPLETED",
    "transactionId": "TXN-2025-ABC123",
    "paymentDate": "2025-11-07",
    "processedBy": "admin@insurance.com",
    "notes": "Full claim settlement"
  }
]
```

**Example cURL**:
```bash
# Get all payments
curl -X GET http://localhost:8080/api/v1/payments

# Get only completed payments
curl -X GET "http://localhost:8080/api/v1/payments?status=COMPLETED"
```

---

### 4. Health & Monitoring

#### 4.1 Application Health

Actuator health check endpoint.

**Endpoint**: `GET /actuator/health`

**Response** (200 OK):
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

#### 4.2 Application Info

Application information endpoint.

**Endpoint**: `GET /actuator/info`

**Response** (200 OK):
```json
{
  "app": {
    "name": "Insurance Policy Service",
    "version": "0.0.1-SNAPSHOT"
  }
}
```

#### 4.3 Metrics

Prometheus-compatible metrics endpoint.

**Endpoint**: `GET /actuator/prometheus`

Returns Prometheus-formatted metrics for monitoring.

---

## OpenAPI Specification

The complete OpenAPI 3.0 specification is available at:

**Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

**OpenAPI YAML**: [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml)

The Swagger UI provides an interactive interface to explore and test all API endpoints.

---

## API Versioning

The API uses URL-based versioning:
- Current version: `/api/v1/*`
- Future versions: `/api/v2/*`, etc.

Breaking changes will be introduced in new versions while maintaining backward compatibility for existing versions.

---

## Rate Limiting

Currently not implemented. Consider implementing rate limiting in production using:
- Spring Cloud Gateway
- Resilience4j RateLimiter
- API Gateway (AWS, Azure, GCP)

---

## CORS Configuration

CORS is configured to allow all origins in development. For production, configure allowed origins in `WebConfig.java`:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("https://your-domain.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE");
}
```

---

## Testing the API

### Using cURL

See individual endpoint documentation for cURL examples.

### Using Swagger UI

1. Start the application
2. Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
3. Click on an endpoint to expand
4. Click "Try it out"
5. Fill in parameters
6. Click "Execute"

### Using Postman

Import the OpenAPI specification:
1. Open Postman
2. Click Import > Link
3. Enter: `http://localhost:8080/v3/api-docs.yaml`
4. Collection will be created with all endpoints

---

## API Evolution Best Practices

1. **Backward Compatibility**: Never remove or rename existing fields
2. **Additive Changes**: New fields are acceptable
3. **Versioning**: Use new API version for breaking changes
4. **Deprecation**: Mark deprecated endpoints in OpenAPI annotations
5. **Documentation**: Keep API documentation up to date

---

## Support

For API support and questions:
- Check the [DEVELOPMENT.md](DEVELOPMENT.md) guide
- Review the [Swagger UI](http://localhost:8080/swagger-ui.html)
- Contact the development team

---

Generated by BMAD Spring Boot Clean Architecture Generator
