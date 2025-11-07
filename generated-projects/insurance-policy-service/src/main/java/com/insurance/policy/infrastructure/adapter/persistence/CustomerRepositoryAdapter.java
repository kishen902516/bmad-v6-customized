package com.insurance.policy.infrastructure.adapter.persistence;

import com.insurance.policy.domain.entity.Customer;
import com.insurance.policy.domain.port.CustomerRepository;
import com.insurance.policy.domain.valueobject.CustomerStatus;
import com.insurance.policy.infrastructure.adapter.persistence.entity.CustomerJpaEntity;
import com.insurance.policy.infrastructure.adapter.persistence.mapper.CustomerMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of CustomerRepository.
 *
 * This adapter implements the domain repository interface using Spring Data JPA.
 * It resides in the infrastructure layer and handles persistence details.
 * Maps between domain entities and JPA entities to maintain Clean Architecture.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerSpringDataRepository springDataRepository;

    public CustomerRepositoryAdapter(CustomerSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity jpaEntity = CustomerMapper.toJpa(customer);
        CustomerJpaEntity saved = springDataRepository.save(jpaEntity);
        return CustomerMapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return springDataRepository.findById(id)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return springDataRepository.findAll().stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return springDataRepository.existsById(id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return springDataRepository.findByEmail(email)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        return springDataRepository.findByPhone(phone)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findByStatus(CustomerStatus status) {
        return springDataRepository.findByStatus(status).stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Customer> findActiveCustomersRegisteredAfter(LocalDate date) {
        return springDataRepository.findActiveCustomersRegisteredAfter(date).stream()
                .map(CustomerMapper::toDomain)
                .collect(Collectors.toList());
    }
}
