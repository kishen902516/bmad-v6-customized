package com.insurance.policy.infrastructure.adapter.persistence;

import com.insurance.policy.domain.entity.Customer;
import com.insurance.policy.domain.valueobject.CustomerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for CustomerRepositoryAdapter.
 *
 * Tests repository implementation with real database using TestContainers.
 * Verifies CRUD operations and custom queries.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CustomerRepositoryAdapter.class)
@DisplayName("Customer Repository Integration Tests")
class CustomerRepositoryAdapterIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private CustomerRepositoryAdapter repository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = createTestCustomer(
                "John",
                "Doe",
                "john.doe@example.com",
                "+1-555-1234",
                "123 Main St, City, State 12345",
                CustomerStatus.ACTIVE,
                LocalDate.of(2024, 1, 15)
        );
    }

    @Test
    @DisplayName("Should save and retrieve Customer")
    void shouldSaveAndRetrieve() {
        // When
        Customer saved = repository.save(testCustomer);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getCustomerId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Doe");
        assertThat(saved.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(saved.getPhone()).isEqualTo("+1-555-1234");
        assertThat(saved.getAddress()).isEqualTo("123 Main St, City, State 12345");
        assertThat(saved.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(saved.getRegisteredDate()).isEqualTo(LocalDate.of(2024, 1, 15));

        // Retrieve and verify
        Optional<Customer> retrieved = repository.findById(saved.getCustomerId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(saved);
    }

    @Test
    @DisplayName("Should find all Customer entities")
    void shouldFindAll() {
        // Given
        Customer customer1 = repository.save(testCustomer);
        Customer customer2 = repository.save(createTestCustomer(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "+1-555-5678",
                "456 Oak Ave, City, State 67890",
                CustomerStatus.ACTIVE,
                LocalDate.of(2024, 2, 20)
        ));

        // When
        List<Customer> all = repository.findAll();

        // Then
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).contains(customer1, customer2);
    }

    @Test
    @DisplayName("Should delete Customer by ID")
    void shouldDeleteById() {
        // Given
        Customer saved = repository.save(testCustomer);
        Long id = saved.getCustomerId();

        // When
        repository.deleteById(id);

        // Then
        Optional<Customer> retrieved = repository.findById(id);
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should check if Customer exists by ID")
    void shouldCheckExistence() {
        // Given
        Customer saved = repository.save(testCustomer);

        // When / Then
        assertThat(repository.existsById(saved.getCustomerId())).isTrue();
        assertThat(repository.existsById(999999L)).isFalse();
    }

    @Test
    @DisplayName("Should return empty Optional when entity not found")
    void shouldReturnEmptyWhenNotFound() {
        // When
        Optional<Customer> retrieved = repository.findById(999999L);

        // Then
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should find customer by email")
    void shouldFindByEmail() {
        // Given
        Customer saved = repository.save(testCustomer);

        // When
        Optional<Customer> found = repository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(saved.getEmail());
        assertThat(found.get().getCustomerId()).isEqualTo(saved.getCustomerId());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        // When
        Optional<Customer> found = repository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find customer by phone")
    void shouldFindByPhone() {
        // Given
        Customer saved = repository.save(testCustomer);

        // When
        Optional<Customer> found = repository.findByPhone("+1-555-1234");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getPhone()).isEqualTo(saved.getPhone());
        assertThat(found.get().getCustomerId()).isEqualTo(saved.getCustomerId());
    }

    @Test
    @DisplayName("Should return empty when phone not found")
    void shouldReturnEmptyWhenPhoneNotFound() {
        // When
        Optional<Customer> found = repository.findByPhone("+1-999-9999");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find customers by status")
    void shouldFindByStatus() {
        // Given
        Customer activeCustomer = repository.save(testCustomer);
        Customer inactiveCustomer = repository.save(createTestCustomer(
                "Bob",
                "Johnson",
                "bob.johnson@example.com",
                "+1-555-9999",
                "789 Pine Rd, City, State 11111",
                CustomerStatus.INACTIVE,
                LocalDate.of(2023, 6, 10)
        ));
        Customer suspendedCustomer = repository.save(createTestCustomer(
                "Alice",
                "Williams",
                "alice.williams@example.com",
                "+1-555-8888",
                "321 Elm St, City, State 22222",
                CustomerStatus.SUSPENDED,
                LocalDate.of(2023, 12, 5)
        ));

        // When
        List<Customer> activeCustomers = repository.findByStatus(CustomerStatus.ACTIVE);
        List<Customer> inactiveCustomers = repository.findByStatus(CustomerStatus.INACTIVE);
        List<Customer> suspendedCustomers = repository.findByStatus(CustomerStatus.SUSPENDED);

        // Then
        assertThat(activeCustomers).isNotEmpty();
        assertThat(activeCustomers).contains(activeCustomer);

        assertThat(inactiveCustomers).isNotEmpty();
        assertThat(inactiveCustomers).contains(inactiveCustomer);

        assertThat(suspendedCustomers).isNotEmpty();
        assertThat(suspendedCustomers).contains(suspendedCustomer);
    }

    @Test
    @DisplayName("Should find active customers registered after date")
    void shouldFindActiveCustomersRegisteredAfter() {
        // Given
        LocalDate cutoffDate = LocalDate.of(2024, 2, 1);

        // Before cutoff - should NOT be returned
        Customer oldActiveCustomer = repository.save(createTestCustomer(
                "Old",
                "Customer",
                "old.customer@example.com",
                "+1-555-1111",
                "111 Old St, City, State 33333",
                CustomerStatus.ACTIVE,
                LocalDate.of(2024, 1, 15)
        ));

        // After cutoff - ACTIVE - should be returned
        Customer newActiveCustomer1 = repository.save(createTestCustomer(
                "New",
                "Customer1",
                "new.customer1@example.com",
                "+1-555-2222",
                "222 New St, City, State 44444",
                CustomerStatus.ACTIVE,
                LocalDate.of(2024, 2, 15)
        ));

        Customer newActiveCustomer2 = repository.save(createTestCustomer(
                "New",
                "Customer2",
                "new.customer2@example.com",
                "+1-555-3333",
                "333 New Ave, City, State 55555",
                CustomerStatus.ACTIVE,
                LocalDate.of(2024, 3, 10)
        ));

        // After cutoff - INACTIVE - should NOT be returned
        Customer newInactiveCustomer = repository.save(createTestCustomer(
                "Inactive",
                "Recent",
                "inactive.recent@example.com",
                "+1-555-4444",
                "444 Inactive Rd, City, State 66666",
                CustomerStatus.INACTIVE,
                LocalDate.of(2024, 2, 20)
        ));

        // When
        List<Customer> results = repository.findActiveCustomersRegisteredAfter(cutoffDate);

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).contains(newActiveCustomer1, newActiveCustomer2);
        assertThat(results).doesNotContain(oldActiveCustomer, newInactiveCustomer);

        // Verify all returned customers are ACTIVE
        assertThat(results).allMatch(c -> c.getStatus() == CustomerStatus.ACTIVE);

        // Verify all returned customers are registered after cutoff date
        assertThat(results).allMatch(c -> c.getRegisteredDate().isAfter(cutoffDate));
    }

    @Test
    @DisplayName("Should update customer status")
    void shouldUpdateCustomerStatus() {
        // Given
        Customer saved = repository.save(testCustomer);
        assertThat(saved.getStatus()).isEqualTo(CustomerStatus.ACTIVE);

        // When - deactivate
        saved.deactivate();
        Customer updated = repository.save(saved);

        // Then
        assertThat(updated.getStatus()).isEqualTo(CustomerStatus.INACTIVE);

        // Verify persistence
        Optional<Customer> retrieved = repository.findById(updated.getCustomerId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getStatus()).isEqualTo(CustomerStatus.INACTIVE);
    }

    @Test
    @DisplayName("Should handle customer status transitions")
    void shouldHandleCustomerStatusTransitions() {
        // Given
        Customer saved = repository.save(testCustomer);
        assertThat(saved.isActive()).isTrue();

        // Suspend
        saved.suspend();
        saved = repository.save(saved);
        assertThat(saved.getStatus()).isEqualTo(CustomerStatus.SUSPENDED);
        assertThat(saved.isActive()).isFalse();

        // Reactivate
        saved.activate();
        saved = repository.save(saved);
        assertThat(saved.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(saved.isActive()).isTrue();

        // Deactivate
        saved.deactivate();
        saved = repository.save(saved);
        assertThat(saved.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
        assertThat(saved.isActive()).isFalse();
    }

    @Test
    @DisplayName("Should update customer information")
    void shouldUpdateCustomerInformation() {
        // Given
        Customer saved = repository.save(testCustomer);

        // When
        saved.setEmail("john.doe.new@example.com");
        saved.setPhone("+1-555-9999");
        saved.setAddress("999 Updated St, New City, State 99999");
        Customer updated = repository.save(saved);

        // Then
        assertThat(updated.getEmail()).isEqualTo("john.doe.new@example.com");
        assertThat(updated.getPhone()).isEqualTo("+1-555-9999");
        assertThat(updated.getAddress()).isEqualTo("999 Updated St, New City, State 99999");

        // Verify persistence
        Optional<Customer> retrieved = repository.findById(updated.getCustomerId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("john.doe.new@example.com");
        assertThat(retrieved.get().getPhone()).isEqualTo("+1-555-9999");
    }

    // Helper method
    private Customer createTestCustomer(String firstName, String lastName, String email,
                                       String phone, String address, CustomerStatus status,
                                       LocalDate registeredDate) {
        return Customer.create(firstName, lastName, email, phone, address, status, registeredDate);
    }
}
