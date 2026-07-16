package com.example.fintech.payment.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.example.fintech.payment.dto.PaymentRequest;
import com.example.fintech.payment.model.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void shouldApprovePaymentAndReplaySameIdempotencyKey() {
        PaymentRequest request = new PaymentRequest(
                "idempotency-test-1",
                501L,
                1001L,
                new BigDecimal("100.00"),
                "SAFE-MERCHANT"
        );

        var first = paymentService.process(request);
        var second = paymentService.process(request);

        assertThat(first.status()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(second.paymentId()).isEqualTo(first.paymentId());
        assertThat(second.replayedFromIdempotencyKey()).isTrue();
    }
}
