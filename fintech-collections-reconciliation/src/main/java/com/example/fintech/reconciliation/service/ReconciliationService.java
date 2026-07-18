package com.example.fintech.reconciliation.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.example.fintech.reconciliation.dto.BankTransaction;
import com.example.fintech.reconciliation.dto.InternalTransaction;
import com.example.fintech.reconciliation.dto.ReconciliationIssue;
import com.example.fintech.reconciliation.dto.ReconciliationReport;
import com.example.fintech.reconciliation.dto.ReconciliationRequest;
import com.example.fintech.reconciliation.model.IssueType;
import org.springframework.stereotype.Service;

@Service
public class ReconciliationService {

    public ReconciliationReport reconcile(ReconciliationRequest request) {
        // LinkedHashMap gives fast reference lookup and keeps input order.
        Map<String, InternalTransaction> internalByReference = new LinkedHashMap<>();

        // HashSet gives fast duplicate detection and matched-reference tracking.
        Set<String> duplicateInternalReferences = new HashSet<>();
        Set<String> processedBankReferences = new HashSet<>();
        Set<String> matchedReferences = new HashSet<>();

        // ArrayList is appropriate for an ordered report of discovered problems.
        List<ReconciliationIssue> issues = new ArrayList<>();

        // TreeMap keeps settlement dates naturally sorted.
        Map<LocalDate, BigDecimal> internalTotalsByDate = new TreeMap<>();
        Map<LocalDate, BigDecimal> bankTotalsByDate = new TreeMap<>();

        for (InternalTransaction internal : request.internalTransactions()) {
            addToDailyTotal(internalTotalsByDate, internal.settlementDate(), internal.amount());

            InternalTransaction previous = internalByReference.putIfAbsent(
                    internal.referenceId(),
                    internal
            );

            if (previous != null && duplicateInternalReferences.add(internal.referenceId())) {
                issues.add(new ReconciliationIssue(
                        internal.referenceId(),
                        IssueType.DUPLICATE_INTERNAL_REFERENCE,
                        "The internal ledger contains the reference more than once"
                ));
            }
        }

        for (BankTransaction bank : request.bankTransactions()) {
            addToDailyTotal(bankTotalsByDate, bank.settlementDate(), bank.amount());

            if (!processedBankReferences.add(bank.referenceId())) {
                issues.add(new ReconciliationIssue(
                        bank.referenceId(),
                        IssueType.DUPLICATE_BANK_REFERENCE,
                        "The bank settlement file contains the reference more than once"
                ));
                continue;
            }

            InternalTransaction internal = internalByReference.get(bank.referenceId());
            if (internal == null) {
                issues.add(new ReconciliationIssue(
                        bank.referenceId(),
                        IssueType.MISSING_INTERNAL_TRANSACTION,
                        "Transaction exists in the bank file but not in the internal ledger"
                ));
                continue;
            }

            matchedReferences.add(bank.referenceId());

            if (internal.amount().compareTo(bank.amount()) != 0) {
                issues.add(new ReconciliationIssue(
                        bank.referenceId(),
                        IssueType.AMOUNT_MISMATCH,
                        "Internal amount " + internal.amount() +
                                " does not match bank amount " + bank.amount()
                ));
            }

            if (internal.status() != bank.status()) {
                issues.add(new ReconciliationIssue(
                        bank.referenceId(),
                        IssueType.STATUS_MISMATCH,
                        "Internal status " + internal.status() +
                                " does not match bank status " + bank.status()
                ));
            }
        }

        for (String internalReference : internalByReference.keySet()) {
            if (!matchedReferences.contains(internalReference)) {
                issues.add(new ReconciliationIssue(
                        internalReference,
                        IssueType.MISSING_BANK_TRANSACTION,
                        "Transaction exists internally but not in the bank settlement file"
                ));
            }
        }

        return new ReconciliationReport(
                request.internalTransactions().size(),
                request.bankTransactions().size(),
                issues.size(),
                List.copyOf(issues),
                Collections.unmodifiableMap(new TreeMap<>(internalTotalsByDate)),
                Collections.unmodifiableMap(new TreeMap<>(bankTotalsByDate))
        );
    }

    private void addToDailyTotal(
            Map<LocalDate, BigDecimal> totals,
            LocalDate settlementDate,
            BigDecimal amount
    ) {
        totals.merge(settlementDate, amount, BigDecimal::add);
    }
}
