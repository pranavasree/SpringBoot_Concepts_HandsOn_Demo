package com.example.fintech.payment.repository;

import com.example.fintech.payment.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
