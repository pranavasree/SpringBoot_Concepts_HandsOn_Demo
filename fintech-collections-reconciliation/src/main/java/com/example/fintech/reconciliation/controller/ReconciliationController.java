package com.example.fintech.reconciliation.controller;

import com.example.fintech.reconciliation.dto.ReconciliationReport;
import com.example.fintech.reconciliation.dto.ReconciliationRequest;
import com.example.fintech.reconciliation.service.ReconciliationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @PostMapping
    public ReconciliationReport reconcile(
            @Valid @RequestBody ReconciliationRequest request
    ) {
        return reconciliationService.reconcile(request);
    }
}
