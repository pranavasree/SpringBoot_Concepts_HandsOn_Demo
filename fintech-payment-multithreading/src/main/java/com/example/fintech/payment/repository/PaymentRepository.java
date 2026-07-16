package com.example.fintech.payment.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.fintech.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
