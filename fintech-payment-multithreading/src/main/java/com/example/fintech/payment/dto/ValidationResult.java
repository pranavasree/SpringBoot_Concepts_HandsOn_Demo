package com.example.fintech.payment.dto;

public record ValidationResult(
        String validationType,
        boolean valid,
        String message,
        long durationMs,
        String threadName
) {

    public static ValidationResult success(String type, long durationMs) {
        return new ValidationResult(
                type,
                true,
                "Validation successful",
                durationMs,
                Thread.currentThread().getName()
        );
    }

    public static ValidationResult failure(String type, String message, long durationMs) {
        return new ValidationResult(
                type,
                false,
                message,
                durationMs,
                Thread.currentThread().getName()
        );
    }
}
