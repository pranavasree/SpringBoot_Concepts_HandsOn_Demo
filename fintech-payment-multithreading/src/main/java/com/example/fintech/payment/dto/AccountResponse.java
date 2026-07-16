package com.example.fintech.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.fintech.payment.entity.Account;

public record AccountResponse(
        Long accountId,
        Long customerId,
        BigDecimal availableBalance,
        BigDecimal dailyLimit,
        BigDecimal dailySpent,
        LocalDate spendingDate,
        boolean blocked,
        Long version
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getCustomerId(),
                account.getAvailableBalance(),
                account.getDailyLimit(),
                account.getDailySpent(),
                account.getSpendingDate(),
                account.isBlocked(),
                account.getVersion()
        );
    }
}
