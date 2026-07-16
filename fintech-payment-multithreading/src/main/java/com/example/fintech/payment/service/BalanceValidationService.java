package com.example.fintech.payment.service;

import java.time.Duration;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.dto.ValidationResult;
import com.example.fintech.payment.entity.Account;
import com.example.fintech.payment.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class BalanceValidationService {

    private final AccountRepository accountRepository;

    public BalanceValidationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public ValidationResult validate(PaymentRequest request) {
        long startedAt = System.nanoTime();
        LatencySimulator.pause(200);

        Account account = accountRepository.findById(request.accountId()).orElse(null);
        long duration = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();

        if (account == null) {
            return ValidationResult.failure("BALANCE", "Account not found", duration);
        }

        if (!account.getCustomerId().equals(request.customerId())) {
            return ValidationResult.failure("BALANCE", "Account does not belong to customer", duration);
        }

        if (account.getAvailableBalance().compareTo(request.amount()) < 0) {
            return ValidationResult.failure("BALANCE", "Insufficient available balance", duration);
        }

        return ValidationResult.success("BALANCE", duration);
    }
}
