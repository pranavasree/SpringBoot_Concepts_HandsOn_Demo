package com.example.fintech.reconciliation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.fintech.reconciliation.dto.BankTransaction;
import com.example.fintech.reconciliation.dto.InternalTransaction;
import com.example.fintech.reconciliation.dto.ReconciliationRequest;
import com.example.fintech.reconciliation.model.IssueType;
import com.example.fintech.reconciliation.model.TransactionStatus;
import org.junit.jupiter.api.Test;

class ReconciliationServiceTest {

    private final ReconciliationService service = new ReconciliationService();

    @Test
    void shouldDetectMissingDuplicateAndAmountMismatchTransactions() {
        LocalDate date = LocalDate.of(2026, 7, 16);

        ReconciliationRequest request = new ReconciliationRequest(
                List.of(
                        new InternalTransaction("TXN-1", new BigDecimal("100.00"), date, TransactionStatus.SETTLED),
                        new InternalTransaction("TXN-2", new BigDecimal("200.00"), date, TransactionStatus.SETTLED),
                        new InternalTransaction("TXN-4", new BigDecimal("400.00"), date, TransactionStatus.SETTLED)
                ),
                List.of(
                        new BankTransaction("TXN-1", new BigDecimal("100.00"), date, TransactionStatus.SETTLED),
                        new BankTransaction("TXN-2", new BigDecimal("250.00"), date, TransactionStatus.SETTLED),
                        new BankTransaction("TXN-3", new BigDecimal("300.00"), date, TransactionStatus.SETTLED),
                        new BankTransaction("TXN-3", new BigDecimal("300.00"), date, TransactionStatus.SETTLED)
                )
        );

        var report = service.reconcile(request);

        assertThat(report.issues())
                .extracting(issue -> issue.issueType())
                .contains(
                        IssueType.AMOUNT_MISMATCH,
                        IssueType.MISSING_INTERNAL_TRANSACTION,
                        IssueType.DUPLICATE_BANK_REFERENCE,
                        IssueType.MISSING_BANK_TRANSACTION
                );
    }
}
