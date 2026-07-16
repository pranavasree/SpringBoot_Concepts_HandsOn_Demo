package com.example.fintech.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.example.fintech.payment.dto.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentValidationServiceTest {

    @Autowired
    private PaymentValidationService paymentValidationService;

    @Test
    void shouldRunIndependentValidationsInParallel() {
        PaymentRequest request = new PaymentRequest(
                "parallel-test-1",
                501L,
                1001L,
                new BigDecimal("125.00"),
                "SAFE-MERCHANT"
        );

        var summary = paymentValidationService.validate(request);

        assertThat(summary.results()).hasSize(4);
        assertThat(summary.allValid()).isTrue();
        assertThat(summary.results())
                .allMatch(result -> result.threadName().startsWith("payment-validation-"));

        // Sequential simulated latency is about 1,850 ms. Parallel execution should
        // finish close to the slowest check (700 ms), with room for CI overhead.
        assertThat(summary.totalElapsedMs()).isLessThan(1_500L);
    }
}
