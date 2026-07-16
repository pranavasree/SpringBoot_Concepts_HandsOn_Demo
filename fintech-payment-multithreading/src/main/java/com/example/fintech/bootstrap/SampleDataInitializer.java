package com.example.fintech.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.fintech.payment.entity.Account;
import com.example.fintech.payment.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SampleDataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;

    public SampleDataInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(String... args) {
        accountRepository.save(new Account(
                1001L,
                501L,
                new BigDecimal("15000.00"),
                new BigDecimal("10000.00"),
                BigDecimal.ZERO,
                LocalDate.now(),
                false
        ));

        accountRepository.save(new Account(
                1002L,
                502L,
                new BigDecimal("5000.00"),
                new BigDecimal("3000.00"),
                BigDecimal.ZERO,
                LocalDate.now(),
                true
        ));
    }
}
