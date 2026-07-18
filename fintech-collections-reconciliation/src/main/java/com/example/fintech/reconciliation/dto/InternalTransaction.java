package com.example.fintech.reconciliation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.fintech.reconciliation.model.TransactionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InternalTransaction(
        @NotBlank String referenceId,
        @NotNull @PositiveOrZero BigDecimal amount,
        @NotNull LocalDate settlementDate,
        @NotNull TransactionStatus status
) {
}
