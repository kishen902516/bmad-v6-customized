package com.insurance.policy.infrastructure.adapter.persistence;

import com.insurance.policy.domain.valueobject.CustomerStatus;
import com.insurance.policy.infrastructure.adapter.persistence.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for CustomerJpaEntity.
 *
 * Provides CRUD operations and custom queries for database access.
 * Used internally by CustomerRepositoryAdapter.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
public interface CustomerSpringDataRepository extends JpaRepository<CustomerJpaEntity, Long> {

    /**
     * Find customer by email address.
     *
     * Uses Spring Data JPA naming convention for simple queries.
     *
     * @param email the email address
     * @return Optional containing the customer if found
     */
    Optional<CustomerJpaEntity> findByEmail(String email);

    /**
     * Find customer by phone number.
     *
     * Uses Spring Data JPA naming convention for simple queries.
     *
     * @param phone the phone number
     * @return Optional containing the customer if found
     */
    Optional<CustomerJpaEntity> findByPhone(String phone);

    /**
     * Find all customers with a specific status.
     *
     * Uses Spring Data JPA naming convention for simple queries.
     *
     * @param status the customer status
     * @return list of customers with the specified status
     */
    List<CustomerJpaEntity> findByStatus(CustomerStatus status);

    /**
     * Find all active customers registered after a specific date.
     *
     * Uses @Query annotation with JPQL for complex query.
     * This query combines status filtering and date comparison.
     *
     * @param date the registration date threshold
     * @return list of active customers registered after the date
     */
    @Query("SELECT c FROM CustomerJpaEntity c WHERE c.status = 'ACTIVE' AND c.registeredDate > :date")
    List<CustomerJpaEntity> findActiveCustomersRegisteredAfter(@Param("date") LocalDate date);
}
