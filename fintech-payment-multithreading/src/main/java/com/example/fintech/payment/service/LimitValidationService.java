package com.example.fintech.payment.service;

import java.time.Duration;
import java.time.LocalDate;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.dto.ValidationResult;
import com.example.fintech.payment.entity.Account;
import com.example.fintech.payment.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class LimitValidationService {

    private final AccountRepository accountRepository;

    public LimitValidationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public ValidationResult validate(PaymentRequest request) {
        long startedAt = System.nanoTime();
        LatencySimulator.pause(350);

        Account account = accountRepository.findById(request.accountId()).orElse(null);
        long duration = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();

        if (account == null) {
            return ValidationResult.failure("DAILY_LIMIT", "Account not found", duration);
        }

        var projectedSpend = account.effectiveDailySpent(LocalDate.now()).add(request.amount());
        if (projectedSpend.compareTo(account.getDailyLimit()) > 0) {
            return ValidationResult.failure("DAILY_LIMIT", "Daily transaction limit exceeded", duration);
        }

        return ValidationResult.success("DAILY_LIMIT", duration);
    }
}
