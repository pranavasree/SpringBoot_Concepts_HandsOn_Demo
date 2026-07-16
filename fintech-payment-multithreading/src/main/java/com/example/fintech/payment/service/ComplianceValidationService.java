package com.example.fintech.payment.service;

import java.time.Duration;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.dto.ValidationResult;
import com.example.fintech.payment.entity.Account;
import com.example.fintech.payment.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class ComplianceValidationService {

    private final AccountRepository accountRepository;

    public ComplianceValidationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public ValidationResult validate(PaymentRequest request) {
        long startedAt = System.nanoTime();
        LatencySimulator.pause(600);

        Account account = accountRepository.findById(request.accountId()).orElse(null);
        long duration = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();

        if (account == null) {
            return ValidationResult.failure("COMPLIANCE", "Account not found", duration);
        }

        if (account.isBlocked()) {
            return ValidationResult.failure(
                    "COMPLIANCE",
                    "Account is blocked by compliance rules",
                    duration
            );
        }

        return ValidationResult.success("COMPLIANCE", duration);
    }
}
