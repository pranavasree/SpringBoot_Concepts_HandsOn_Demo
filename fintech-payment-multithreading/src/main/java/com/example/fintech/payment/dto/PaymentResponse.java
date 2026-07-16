package com.example.fintech.payment.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.example.fintech.payment.model.PaymentStatus;

public record PaymentResponse(
        UUID paymentId,
        PaymentStatus status,
        String message,
        boolean replayedFromIdempotencyKey,
        BigDecimal balanceAfter,
        long validationTimeMs,
        List<ValidationResult> validationResults
) {
}
