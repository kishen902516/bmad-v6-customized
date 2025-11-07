# Database Schema Documentation

## Overview

The Insurance Policy Service uses **PostgreSQL** as its primary database. The schema is managed by Hibernate (JPA) with `ddl-auto=update` in development.

**Database Name**: `insurance_policy_db`

**Database Version**: PostgreSQL 14+

---

## Tables

### 1. policy

Stores insurance policy information.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Internal database ID |
| policy_number | VARCHAR(20) | UNIQUE, NOT NULL | Auto-generated policy number (POL-YYYY-NNNNNN) |
| customer_id | VARCHAR(50) | NOT NULL | Reference to customer |
| effective_date | DATE | NOT NULL | When policy becomes effective |
| expiration_date | DATE | NOT NULL | When policy expires (auto-calculated: effective_date + 1 year) |
| total_premium_amount | DECIMAL(19,2) | NOT NULL | Total premium amount (sum of all coverages) |
| total_premium_currency | VARCHAR(3) | NOT NULL | Currency code (e.g., USD) |
| status | VARCHAR(20) | NOT NULL | Policy status (DRAFT, ACTIVE, CANCELLED, EXPIRED) |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Record last update timestamp |

**Indexes**:
```sql
CREATE INDEX idx_policy_number ON policy(policy_number);
CREATE INDEX idx_customer_id ON policy(customer_id);
CREATE INDEX idx_status ON policy(status);
CREATE INDEX idx_effective_date ON policy(effective_date);
```

**Constraints**:
```sql
ALTER TABLE policy ADD CONSTRAINT uk_policy_number UNIQUE (policy_number);
```

**Example Data**:
```sql
INSERT INTO policy VALUES (
    1,
    'POL-2025-000001',
    'CUST-001',
    '2025-12-01',
    '2026-12-01',
    800.00,
    'USD',
    'DRAFT',
    NOW(),
    NOW()
);
```

---

### 2. coverage

Stores coverage items for policies. Part of the Policy aggregate.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Internal database ID |
| policy_id | BIGINT | FOREIGN KEY (policy.id), NOT NULL | Reference to policy |
| coverage_type | VARCHAR(50) | NOT NULL | Type of coverage (Liability, Collision, etc.) |
| premium_amount | DECIMAL(19,2) | NOT NULL | Premium amount for this coverage |
| premium_currency | VARCHAR(3) | NOT NULL | Currency code (e.g., USD) |

**Foreign Keys**:
```sql
ALTER TABLE coverage
ADD CONSTRAINT fk_coverage_policy
FOREIGN KEY (policy_id) REFERENCES policy(id)
ON DELETE CASCADE;
```

**Indexes**:
```sql
CREATE INDEX idx_coverage_policy_id ON coverage(policy_id);
```

**Example Data**:
```sql
INSERT INTO coverage VALUES (1, 1, 'Liability', 500.00, 'USD');
INSERT INTO coverage VALUES (2, 1, 'Collision', 300.00, 'USD');
```

**Note**: Coverages are cascade-deleted when the parent policy is deleted.

---

### 3. claim

Stores insurance claim submissions.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Internal database ID |
| claim_number | VARCHAR(20) | UNIQUE, NOT NULL | Auto-generated claim number (CLM-YYYY-NNNNNN) |
| policy_id | VARCHAR(50) | NOT NULL | Reference to policy ID |
| claimed_amount | DECIMAL(19,2) | NOT NULL | Amount being claimed |
| incident_date | DATE | NOT NULL | When the incident occurred |
| status | VARCHAR(20) | NOT NULL | Claim status (SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID) |
| submitted_date | DATE | NOT NULL | When claim was submitted |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Record last update timestamp |

**Indexes**:
```sql
CREATE INDEX idx_claim_number ON claim(claim_number);
CREATE INDEX idx_claim_policy_id ON claim(policy_id);
CREATE INDEX idx_claim_status ON claim(status);
CREATE INDEX idx_claim_submitted_date ON claim(submitted_date);
```

**Constraints**:
```sql
ALTER TABLE claim ADD CONSTRAINT uk_claim_number UNIQUE (claim_number);
```

**Example Data**:
```sql
INSERT INTO claim VALUES (
    1,
    'CLM-2025-000001',
    '1',
    5000.00,
    '2025-10-15',
    'SUBMITTED',
    '2025-11-07',
    NOW(),
    NOW()
);
```

**Status Flow**:
```
SUBMITTED → UNDER_REVIEW → APPROVED → PAID
                ↓
            REJECTED
```

---

### 4. payment

Stores payment transactions for approved claims.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| payment_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Internal database ID |
| claim_id | BIGINT | FOREIGN KEY (claim.id), NOT NULL | Reference to claim |
| amount | DECIMAL(19,2) | NOT NULL | Payment amount |
| payment_method | VARCHAR(20) | NOT NULL | Payment method (BANK_TRANSFER, CHECK, CARD, CASH) |
| payment_status | VARCHAR(20) | NOT NULL | Payment status (PENDING, COMPLETED, FAILED) |
| transaction_id | VARCHAR(50) | UNIQUE, NOT NULL | Unique transaction identifier |
| payment_date | DATE | NOT NULL | When payment was processed |
| processed_by | VARCHAR(100) | NOT NULL | User who processed the payment |
| notes | TEXT | | Additional payment notes |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Record last update timestamp |

**Foreign Keys**:
```sql
ALTER TABLE payment
ADD CONSTRAINT fk_payment_claim
FOREIGN KEY (claim_id) REFERENCES claim(id);
```

**Indexes**:
```sql
CREATE INDEX idx_payment_claim_id ON payment(claim_id);
CREATE INDEX idx_payment_transaction_id ON payment(transaction_id);
CREATE INDEX idx_payment_status ON payment(payment_status);
CREATE INDEX idx_payment_date ON payment(payment_date);
```

**Constraints**:
```sql
ALTER TABLE payment ADD CONSTRAINT uk_transaction_id UNIQUE (transaction_id);
```

**Example Data**:
```sql
INSERT INTO payment VALUES (
    1,
    1,
    5000.00,
    'BANK_TRANSFER',
    'COMPLETED',
    'TXN-2025-ABC123',
    '2025-11-07',
    'admin@insurance.com',
    'Full claim settlement',
    NOW(),
    NOW()
);
```

---

### 5. customer

Stores customer information (simplified for this service).

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Internal database ID |
| customer_id | VARCHAR(50) | UNIQUE, NOT NULL | Unique customer identifier |
| name | VARCHAR(100) | NOT NULL | Customer name |
| email | VARCHAR(100) | | Customer email |
| status | VARCHAR(20) | NOT NULL | Customer status (ACTIVE, INACTIVE, SUSPENDED) |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Record last update timestamp |

**Indexes**:
```sql
CREATE INDEX idx_customer_customer_id ON customer(customer_id);
CREATE INDEX idx_customer_email ON customer(email);
```

**Constraints**:
```sql
ALTER TABLE customer ADD CONSTRAINT uk_customer_id UNIQUE (customer_id);
```

**Example Data**:
```sql
INSERT INTO customer VALUES (
    1,
    'CUST-001',
    'John Doe',
    'john.doe@example.com',
    'ACTIVE',
    NOW(),
    NOW()
);
```

**Note**: In a microservices architecture, customer data would typically be managed by a separate Customer Service.

---

## Relationships

```
customer (1) ----< (N) policy
policy (1) ----< (N) coverage
policy (1) ----< (N) claim
claim (1) ----< (N) payment
```

**Cardinality**:
- One customer can have many policies
- One policy can have many coverages
- One policy can have many claims
- One claim can have many payments (partial payments, refunds)

---

## Enumerations

### Policy Status
- `DRAFT`: Policy is being created
- `ACTIVE`: Policy is active
- `CANCELLED`: Policy was cancelled
- `EXPIRED`: Policy has expired

### Claim Status
- `SUBMITTED`: Claim submitted by customer
- `UNDER_REVIEW`: Claim is being reviewed
- `APPROVED`: Claim approved for payment
- `REJECTED`: Claim rejected
- `PAID`: Payment completed

### Payment Status
- `PENDING`: Payment is pending
- `COMPLETED`: Payment completed successfully
- `FAILED`: Payment failed

### Payment Method
- `BANK_TRANSFER`: Direct bank transfer
- `CHECK`: Paper check
- `CARD`: Credit/debit card
- `CASH`: Cash payment

### Customer Status
- `ACTIVE`: Customer account is active
- `INACTIVE`: Customer account is inactive
- `SUSPENDED`: Customer account is suspended

---

## Data Types

### Monetary Values

All monetary values use `DECIMAL(19,2)`:
- Precision: 19 digits total
- Scale: 2 decimal places
- Example: 99999999999999999.99

**Currency**: Stored as 3-character ISO 4217 codes (e.g., USD, EUR, GBP)

### Date Values

- `DATE`: For dates without time (effective_date, incident_date, payment_date)
- `TIMESTAMP`: For audit fields (created_at, updated_at)

---

## Indexes Strategy

### Primary Keys
All tables use auto-incrementing BIGINT primary keys.

### Unique Indexes
- `policy.policy_number`: Ensures unique policy numbers
- `claim.claim_number`: Ensures unique claim numbers
- `payment.transaction_id`: Prevents duplicate transactions
- `customer.customer_id`: Ensures unique customer identifiers

### Foreign Key Indexes
Automatically created for foreign key columns to improve join performance.

### Query Optimization Indexes
- Status columns (policy.status, claim.status, payment.payment_status)
- Date columns (effective_date, submitted_date, payment_date)
- Reference IDs (customer_id, policy_id, claim_id)

---

## Migration Strategy

### Development
Uses Hibernate `ddl-auto=update`:
```properties
spring.jpa.hibernate.ddl-auto=update
```

**Pros**: Automatic schema updates
**Cons**: Not safe for production

### Production

**Recommended**: Use **Flyway** or **Liquibase** for versioned migrations.

**Flyway Example**:

1. Add dependency:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

2. Create migration files in `src/main/resources/db/migration/`:
```
V1__create_policy_table.sql
V2__create_claim_table.sql
V3__create_payment_table.sql
```

3. Configure Flyway:
```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

---

## Backup and Recovery

### Backup Strategy

**Daily Full Backup**:
```bash
pg_dump -U postgres -d insurance_policy_db > backup_$(date +%Y%m%d).sql
```

**Automated Backups**:
```bash
# Cron job (daily at 2 AM)
0 2 * * * pg_dump -U postgres insurance_policy_db | gzip > /backups/insurance_$(date +\%Y\%m\%d).sql.gz
```

### Restore from Backup
```bash
psql -U postgres -d insurance_policy_db < backup_20251107.sql
```

---

## Performance Considerations

### Connection Pooling

Configure in `application.properties`:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### Query Performance

1. **Use indexes** on frequently queried columns
2. **Avoid N+1 queries** with proper JPA fetch strategies
3. **Use pagination** for large result sets
4. **Monitor slow queries** with pg_stat_statements

### Monitoring Slow Queries

Enable query logging:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

---

## Database Access

### Connection Details

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/insurance_policy_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
```

### Connecting with psql

```bash
psql -U postgres -d insurance_policy_db
```

### Common Queries

**Count policies by status**:
```sql
SELECT status, COUNT(*)
FROM policy
GROUP BY status;
```

**Find claims pending review**:
```sql
SELECT *
FROM claim
WHERE status = 'UNDER_REVIEW'
ORDER BY submitted_date;
```

**List completed payments**:
```sql
SELECT p.*, c.claim_number
FROM payment p
JOIN claim c ON p.claim_id = c.id
WHERE p.payment_status = 'COMPLETED'
ORDER BY p.payment_date DESC;
```

---

## Security

### Database Credentials

**Never commit credentials** to version control.

Use environment variables:
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=secure_password
```

### Access Control

Grant minimal required permissions:
```sql
CREATE USER insurance_app WITH PASSWORD 'secure_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO insurance_app;
```

---

## Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

---

Generated by BMAD Spring Boot Clean Architecture Generator
