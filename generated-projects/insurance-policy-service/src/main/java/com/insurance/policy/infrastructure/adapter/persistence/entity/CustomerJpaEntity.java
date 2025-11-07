package com.insurance.policy.infrastructure.adapter.persistence.entity;

import com.insurance.policy.domain.valueobject.CustomerStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * JPA Entity for Customer persistence.
 *
 * This is the infrastructure layer representation with JPA annotations.
 * It maps to the database table and handles persistence concerns.
 * Separated from domain entity to maintain Clean Architecture.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
@Entity
@Table(name = "customers")
public class CustomerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerStatus status;

    @Column(name = "registered_date", nullable = false)
    private LocalDate registeredDate;

    /**
     * Default constructor for JPA.
     */
    public CustomerJpaEntity() {
    }

    /**
     * Constructor with all fields.
     */
    public CustomerJpaEntity(String firstName, String lastName, String email, String phone,
                            String address, CustomerStatus status, LocalDate registeredDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.registeredDate = registeredDate;
    }

    // Getters and Setters (required by JPA)

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerJpaEntity that = (CustomerJpaEntity) o;
        return customerId != null && customerId.equals(that.customerId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
