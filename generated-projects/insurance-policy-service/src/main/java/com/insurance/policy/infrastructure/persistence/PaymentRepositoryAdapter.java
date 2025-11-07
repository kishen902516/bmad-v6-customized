package com.insurance.policy.infrastructure.persistence;

import com.insurance.policy.domain.entity.Payment;
import com.insurance.policy.domain.port.PaymentRepository;
import com.insurance.policy.domain.valueobject.PaymentStatus;
import com.insurance.policy.infrastructure.persistence.entity.PaymentJpaEntity;
import com.insurance.policy.infrastructure.persistence.mapper.PaymentMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of PaymentRepository
 * Bridges domain port with Spring Data JPA repository
 */
@Component
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentSpringDataRepository springDataRepository;
    private final PaymentMapper mapper;

    public PaymentRepositoryAdapter(
            PaymentSpringDataRepository springDataRepository,
            PaymentMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity jpaEntity = mapper.toJpaEntity(payment);
        PaymentJpaEntity savedEntity = springDataRepository.save(jpaEntity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return springDataRepository.findById(id)
            .map(mapper::toDomainEntity);
    }

    @Override
    public List<Payment> findByClaimId(Long claimId) {
        return springDataRepository.findByClaimId(claimId)
            .stream()
            .map(mapper::toDomainEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByPaymentStatus(PaymentStatus status) {
        return springDataRepository.findByPaymentStatus(status.name())
            .stream()
            .map(mapper::toDomainEntity)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        return springDataRepository.findByTransactionId(transactionId)
            .map(mapper::toDomainEntity);
    }

    @Override
    public List<Payment> findCompletedPaymentsAfter(LocalDate date) {
        return springDataRepository.findCompletedPaymentsAfter(date)
            .stream()
            .map(mapper::toDomainEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        return springDataRepository.findAll()
            .stream()
            .map(mapper::toDomainEntity)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean existsByTransactionId(String transactionId) {
        return springDataRepository.existsByTransactionId(transactionId);
    }
}
