package com.example.fintech.payment.service;

import java.util.List;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.dto.PaymentResponse;
import com.example.fintech.payment.dto.PaymentValidationSummary;
import com.example.fintech.payment.entity.Payment;
import com.example.fintech.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentValidationService paymentValidationService;
    private final PaymentExecutionService paymentExecutionService;

    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentValidationService paymentValidationService,
            PaymentExecutionService paymentExecutionService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentValidationService = paymentValidationService;
        this.paymentExecutionService = paymentExecutionService;
    }

    public PaymentResponse process(PaymentRequest request) {
        Payment existingPayment = paymentRepository
                .findByIdempotencyKey(request.idempotencyKey())
                .orElse(null);

        if (existingPayment != null) {
            return toResponse(existingPayment, true, 0, List.of());
        }

        PaymentValidationSummary summary = paymentValidationService.validate(request);

        Payment payment = summary.allValid()
                ? paymentExecutionService.executeApproved(request)
                : paymentExecutionService.recordRejected(request, summary.rejectionReason());

        return toResponse(payment, false, summary.totalElapsedMs(), summary.results());
    }

    private PaymentResponse toResponse(
            Payment payment,
            boolean replayed,
            long validationTimeMs,
            List<com.example.fintech.payment.dto.ValidationResult> validationResults
    ) {
        return new PaymentResponse(
                payment.getId(),
                payment.getStatus(),
                payment.getDecisionReason(),
                replayed,
                payment.getBalanceAfter(),
                validationTimeMs,
                validationResults
        );
    }
}
