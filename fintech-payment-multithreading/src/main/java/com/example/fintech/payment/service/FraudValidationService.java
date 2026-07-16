package com.example.fintech.payment.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Locale;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.dto.ValidationResult;
import org.springframework.stereotype.Service;

@Service
public class FraudValidationService {

    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("5000.00");

    public ValidationResult validate(PaymentRequest request) {
        long startedAt = System.nanoTime();
        LatencySimulator.pause(700);

        boolean riskyMerchant = request.merchantId()
                .toUpperCase(Locale.ROOT)
                .contains("RISK");
        boolean highValue = request.amount().compareTo(HIGH_VALUE_THRESHOLD) >= 0;

        long duration = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();

        if (riskyMerchant || highValue) {
            return ValidationResult.failure(
                    "FRAUD",
                    "Payment exceeded the sample fraud-risk threshold",
                    duration
            );
        }

        return ValidationResult.success("FRAUD", duration);
    }
}
