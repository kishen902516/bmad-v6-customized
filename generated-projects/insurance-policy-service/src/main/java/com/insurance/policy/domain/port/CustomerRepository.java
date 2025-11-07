package com.insurance.policy.domain.port;

import com.insurance.policy.domain.entity.Customer;
import com.insurance.policy.domain.valueobject.CustomerStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer.
 *
 * This is a port (interface) in the domain layer following Clean Architecture.
 * It defines what operations are needed without specifying implementation details.
 * The implementation will be in the infrastructure layer.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
public interface CustomerRepository {

    /**
     * Save a Customer.
     *
     * @param customer the entity to save
     * @return the saved entity with generated ID if new
     */
    Customer save(Customer customer);

    /**
     * Find Customer by ID.
     *
     * @param id the entity ID
     * @return Optional containing the entity if found
     */
    Optional<Customer> findById(Long id);

    /**
     * Find all Customer entities.
     *
     * @return list of all entities
     */
    List<Customer> findAll();

    /**
     * Delete Customer by ID.
     *
     * @param id the entity ID to delete
     */
    void deleteById(Long id);

    /**
     * Check if Customer exists by ID.
     *
     * @param id the entity ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Find customer by email address.
     *
     * @param email the email address
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find customer by phone number.
     *
     * @param phone the phone number
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * Find all customers with a specific status.
     *
     * @param status the customer status
     * @return list of customers with the specified status
     */
    List<Customer> findByStatus(CustomerStatus status);

    /**
     * Find all active customers registered after a specific date.
     *
     * @param date the registration date threshold
     * @return list of active customers registered after the date
     */
    List<Customer> findActiveCustomersRegisteredAfter(LocalDate date);
}
