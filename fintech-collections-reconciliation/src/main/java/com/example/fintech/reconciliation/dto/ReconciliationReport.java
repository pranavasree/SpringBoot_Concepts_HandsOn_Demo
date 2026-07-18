package com.example.fintech.reconciliation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ReconciliationReport(
        int internalTransactionCount,
        int bankTransactionCount,
        int issueCount,
        List<ReconciliationIssue> issues,
        Map<LocalDate, BigDecimal> internalTotalsByDate,
        Map<LocalDate, BigDecimal> bankTotalsByDate
) {
}
