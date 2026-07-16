package com.example.fintech.payment.service;

final class LatencySimulator {

    private LatencySimulator() {
    }

    static void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Validation thread was interrupted", exception);
        }
    }
}
