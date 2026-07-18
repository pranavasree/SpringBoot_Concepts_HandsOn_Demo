package com.example.fintech.reconciliation.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record ReconciliationRequest(
        @NotEmpty List<@Valid InternalTransaction> internalTransactions,
        @NotEmpty List<@Valid BankTransaction> bankTransactions
) {
}
