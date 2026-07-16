package com.example.fintech.payment.controller;

import com.example.fintech.payment.dto.AccountResponse;
import com.example.fintech.payment.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable Long accountId) {
        return accountRepository.findById(accountId)
                .map(AccountResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }
}
