package com.example.fintech.payment.service;

import java.time.LocalDate;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.entity.Account;
import com.example.fintech.payment.entity.Payment;
import com.example.fintech.payment.repository.AccountRepository;
import com.example.fintech.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentExecutionService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public PaymentExecutionService(
            AccountRepository accountRepository,
            PaymentRepository paymentRepository
    ) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment executeApproved(PaymentRequest request) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (!account.getCustomerId().equals(request.customerId())) {
            throw new IllegalArgumentException("Account does not belong to customer");
        }

        if (account.isBlocked()) {
            throw new IllegalStateException("Account is blocked by compliance rules");
        }

        // Recheck inside the write transaction. The earlier parallel checks improve
        // response time, but this method remains the final consistency boundary.
        account.debit(request.amount(), LocalDate.now());

        Payment payment = Payment.approved(
                request.idempotencyKey(),
                request.accountId(),
                request.customerId(),
                request.amount(),
                request.merchantId(),
                account.getAvailableBalance()
        );

        accountRepository.saveAndFlush(account);
        return paymentRepository.saveAndFlush(payment);
    }

    @Transactional
    public Payment recordRejected(PaymentRequest request, String reason) {
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Payment payment = Payment.rejected(
                request.idempotencyKey(),
                request.accountId(),
                request.customerId(),
                request.amount(),
                request.merchantId(),
                account.getAvailableBalance(),
                reason
        );

        return paymentRepository.saveAndFlush(payment);
    }
}
