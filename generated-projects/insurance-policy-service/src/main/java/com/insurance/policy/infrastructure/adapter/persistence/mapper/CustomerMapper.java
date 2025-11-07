package com.insurance.policy.infrastructure.adapter.persistence.mapper;

import com.insurance.policy.domain.entity.Customer;
import com.insurance.policy.infrastructure.adapter.persistence.entity.CustomerJpaEntity;

/**
 * Mapper between domain entity and JPA entity.
 *
 * Converts between Clean Architecture domain model and JPA persistence model.
 * This separation allows domain to remain framework-independent.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
public class CustomerMapper {

    /**
     * Convert domain entity to JPA entity.
     *
     * @param domain the domain entity
     * @return the JPA entity
     */
    public static CustomerJpaEntity toJpa(Customer domain) {
        if (domain == null) {
            return null;
        }

        CustomerJpaEntity jpa = new CustomerJpaEntity();
        jpa.setCustomerId(domain.getCustomerId());
        jpa.setFirstName(domain.getFirstName());
        jpa.setLastName(domain.getLastName());
        jpa.setEmail(domain.getEmail());
        jpa.setPhone(domain.getPhone());
        jpa.setAddress(domain.getAddress());
        jpa.setStatus(domain.getStatus());
        jpa.setRegisteredDate(domain.getRegisteredDate());
        return jpa;
    }

    /**
     * Convert JPA entity to domain entity.
     *
     * @param jpa the JPA entity
     * @return the domain entity
     */
    public static Customer toDomain(CustomerJpaEntity jpa) {
        if (jpa == null) {
            return null;
        }

        return new Customer(
            jpa.getCustomerId(),
            jpa.getFirstName(),
            jpa.getLastName(),
            jpa.getEmail(),
            jpa.getPhone(),
            jpa.getAddress(),
            jpa.getStatus(),
            jpa.getRegisteredDate()
        );
    }

    /**
     * Update JPA entity from domain entity (for updates).
     *
     * @param jpa the JPA entity to update
     * @param domain the source domain entity
     */
    public static void updateJpaFromDomain(CustomerJpaEntity jpa, Customer domain) {
        jpa.setFirstName(domain.getFirstName());
        jpa.setLastName(domain.getLastName());
        jpa.setEmail(domain.getEmail());
        jpa.setPhone(domain.getPhone());
        jpa.setAddress(domain.getAddress());
        jpa.setStatus(domain.getStatus());
        jpa.setRegisteredDate(domain.getRegisteredDate());
    }
}
