package com.example.fintech.common;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        return build(HttpStatus.BAD_REQUEST, "Request validation failed", fieldErrors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler({
            ObjectOptimisticLockingFailureException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ApiError> handleConflict(Exception exception) {
        return build(
                HttpStatus.CONFLICT,
                "The request conflicted with another payment. Retry safely with the same idempotency key.",
                Map.of()
        );
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiError> handleBusinessException(RuntimeException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            Map<String, String> fieldErrors
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                fieldErrors
        );
        return ResponseEntity.status(status).body(error);
    }
}
