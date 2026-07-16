package com.example.fintech.payment.dto;

import java.util.List;

public record PaymentValidationSummary(
        List<ValidationResult> results,
        long totalElapsedMs
) {

    public boolean allValid() {
        return results.stream().allMatch(ValidationResult::valid);
    }

    public String rejectionReason() {
        return results.stream()
                .filter(result -> !result.valid())
                .map(result -> result.validationType() + ": " + result.message())
                .reduce((left, right) -> left + "; " + right)
                .orElse("Unknown validation failure");
    }
}
