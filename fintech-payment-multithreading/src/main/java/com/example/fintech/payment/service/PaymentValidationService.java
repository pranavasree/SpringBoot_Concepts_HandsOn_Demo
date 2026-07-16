package com.example.fintech.payment.service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.dto.PaymentValidationSummary;
import com.example.fintech.payment.dto.ValidationResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentValidationService {

    private final BalanceValidationService balanceValidationService;
    private final FraudValidationService fraudValidationService;
    private final LimitValidationService limitValidationService;
    private final ComplianceValidationService complianceValidationService;
    private final Executor paymentValidationExecutor;

    public PaymentValidationService(
            BalanceValidationService balanceValidationService,
            FraudValidationService fraudValidationService,
            LimitValidationService limitValidationService,
            ComplianceValidationService complianceValidationService,
            @Qualifier("paymentValidationExecutor") Executor paymentValidationExecutor
    ) {
        this.balanceValidationService = balanceValidationService;
        this.fraudValidationService = fraudValidationService;
        this.limitValidationService = limitValidationService;
        this.complianceValidationService = complianceValidationService;
        this.paymentValidationExecutor = paymentValidationExecutor;
    }

    public PaymentValidationSummary validate(PaymentRequest request) {
        long startedAt = System.nanoTime();

        CompletableFuture<ValidationResult> balanceFuture = runAsync(
                "BALANCE",
                () -> balanceValidationService.validate(request)
        );
        CompletableFuture<ValidationResult> fraudFuture = runAsync(
                "FRAUD",
                () -> fraudValidationService.validate(request)
        );
        CompletableFuture<ValidationResult> limitFuture = runAsync(
                "DAILY_LIMIT",
                () -> limitValidationService.validate(request)
        );
        CompletableFuture<ValidationResult> complianceFuture = runAsync(
                "COMPLIANCE",
                () -> complianceValidationService.validate(request)
        );

        CompletableFuture.allOf(
                balanceFuture,
                fraudFuture,
                limitFuture,
                complianceFuture
        ).join();

        List<ValidationResult> results = List.of(
                balanceFuture.join(),
                fraudFuture.join(),
                limitFuture.join(),
                complianceFuture.join()
        );

        long totalElapsedMs = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
        return new PaymentValidationSummary(results, totalElapsedMs);
    }

    private CompletableFuture<ValidationResult> runAsync(
            String validationType,
            Supplier<ValidationResult> validation
    ) {
        return CompletableFuture
                .supplyAsync(validation, paymentValidationExecutor)
                .orTimeout(2, TimeUnit.SECONDS)
                .exceptionally(exception -> ValidationResult.failure(
                        validationType,
                        "Validation unavailable: " + rootMessage(exception),
                        0
                ));
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null
                ? current.getClass().getSimpleName()
                : current.getMessage();
    }
}
