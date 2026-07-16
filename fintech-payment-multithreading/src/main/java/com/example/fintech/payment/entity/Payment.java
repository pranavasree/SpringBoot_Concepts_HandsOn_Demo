package com.example.fintech.payment.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.example.fintech.payment.model.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "payments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_payment_idempotency_key",
                columnNames = "idempotency_key"
        )
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String merchantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(length = 500)
    private String decisionReason;

    protected Payment() {
    }

    private Payment(
            String idempotencyKey,
            Long accountId,
            Long customerId,
            BigDecimal amount,
            String merchantId,
            PaymentStatus status,
            BigDecimal balanceAfter,
            String decisionReason
    ) {
        this.idempotencyKey = idempotencyKey;
        this.accountId = accountId;
        this.customerId = customerId;
        this.amount = amount;
        this.merchantId = merchantId;
        this.status = status;
        this.balanceAfter = balanceAfter;
        this.createdAt = Instant.now();
        this.decisionReason = decisionReason;
    }

    public static Payment approved(
            String idempotencyKey,
            Long accountId,
            Long customerId,
            BigDecimal amount,
            String merchantId,
            BigDecimal balanceAfter
    ) {
        return new Payment(
                idempotencyKey,
                accountId,
                customerId,
                amount,
                merchantId,
                PaymentStatus.APPROVED,
                balanceAfter,
                "All validations passed"
        );
    }

    public static Payment rejected(
            String idempotencyKey,
            Long accountId,
            Long customerId,
            BigDecimal amount,
            String merchantId,
            BigDecimal currentBalance,
            String reason
    ) {
        return new Payment(
                idempotencyKey,
                accountId,
                customerId,
                amount,
                merchantId,
                PaymentStatus.REJECTED,
                currentBalance,
                reason
        );
    }

    public UUID getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getDecisionReason() {
        return decisionReason;
    }
}
