package com.example.fintech.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal dailyLimit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal dailySpent;

    @Column(nullable = false)
    private LocalDate spendingDate;

    @Column(nullable = false)
    private boolean blocked;

    @Version
    private Long version;

    protected Account() {
    }

    public Account(
            Long id,
            Long customerId,
            BigDecimal availableBalance,
            BigDecimal dailyLimit,
            BigDecimal dailySpent,
            LocalDate spendingDate,
            boolean blocked
    ) {
        this.id = id;
        this.customerId = customerId;
        this.availableBalance = availableBalance;
        this.dailyLimit = dailyLimit;
        this.dailySpent = dailySpent;
        this.spendingDate = spendingDate;
        this.blocked = blocked;
    }

    public void debit(BigDecimal amount, LocalDate today) {
        resetDailyWindowIfRequired(today);

        if (availableBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient available balance");
        }

        if (dailySpent.add(amount).compareTo(dailyLimit) > 0) {
            throw new IllegalStateException("Daily transaction limit exceeded");
        }

        availableBalance = availableBalance.subtract(amount);
        dailySpent = dailySpent.add(amount);
    }

    private void resetDailyWindowIfRequired(LocalDate today) {
        if (!today.equals(spendingDate)) {
            spendingDate = today;
            dailySpent = BigDecimal.ZERO;
        }
    }

    public BigDecimal effectiveDailySpent(LocalDate today) {
        return today.equals(spendingDate) ? dailySpent : BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public BigDecimal getDailySpent() {
        return dailySpent;
    }

    public LocalDate getSpendingDate() {
        return spendingDate;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public Long getVersion() {
        return version;
    }
}
