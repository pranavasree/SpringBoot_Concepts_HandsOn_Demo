package com.example.fintech.reconciliation.dto;

import com.example.fintech.reconciliation.model.IssueType;

public record ReconciliationIssue(
        String referenceId,
        IssueType issueType,
        String description
) {
}
