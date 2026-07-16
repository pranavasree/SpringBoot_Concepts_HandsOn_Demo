package com.example.fintech.payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PaymentRequest(
        @NotBlank @Size(max = 80) String idempotencyKey,
        @NotNull Long customerId,
        @NotNull Long accountId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank @Size(max = 80) String merchantId
) {
}
