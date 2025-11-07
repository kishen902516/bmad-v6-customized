package com.insurance.policy.domain.entity;

import com.insurance.policy.domain.valueobject.CustomerStatus;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Customer domain entity.
 *
 * Represents a customer in the insurance system.
 * This is a rich domain model following Clean Architecture principles.
 *
 * @author BMAD Spring Boot Clean Architecture Generator
 * @version 1.0
 */
public class Customer {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private CustomerStatus status;
    private LocalDate registeredDate;

    /**
     * Default constructor for frameworks.
     */
    public Customer() {
    }

    /**
     * Full constructor for creating a customer.
     *
     * @param customerId the customer ID
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email address
     * @param phone the phone number
     * @param address the physical address
     * @param status the customer status
     * @param registeredDate the registration date
     */
    public Customer(Long customerId, String firstName, String lastName, String email,
                   String phone, String address, CustomerStatus status, LocalDate registeredDate) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.registeredDate = registeredDate;
    }

    /**
     * Create a new customer (without ID).
     *
     * @param firstName the first name
     * @param lastName the last name
     * @param email the email address
     * @param phone the phone number
     * @param address the physical address
     * @param status the customer status
     * @param registeredDate the registration date
     * @return new Customer instance
     */
    public static Customer create(String firstName, String lastName, String email,
                                  String phone, String address, CustomerStatus status, LocalDate registeredDate) {
        return new Customer(null, firstName, lastName, email, phone, address, status, registeredDate);
    }

    /**
     * Check if customer is active.
     *
     * @return true if status is ACTIVE
     */
    public boolean isActive() {
        return CustomerStatus.ACTIVE.equals(this.status);
    }

    /**
     * Activate the customer account.
     */
    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }

    /**
     * Deactivate the customer account.
     */
    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }

    /**
     * Suspend the customer account.
     */
    public void suspend() {
        this.status = CustomerStatus.SUSPENDED;
    }

    // Getters and Setters

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
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId) &&
               Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, email);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                ", registeredDate=" + registeredDate +
                '}';
    }
}
