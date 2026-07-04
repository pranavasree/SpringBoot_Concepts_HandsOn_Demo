package com.example.demo.circuitbreaker;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class SimpleCircuitBreaker {

    private final ScheduledExecutorService scheduler;
    private final int failureThreshold;
    private final int halfOpenTrialLimit;
    private final Duration openCooldown;

    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger halfOpenTrials = new AtomicInteger(0);

    private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;

    public SimpleCircuitBreaker(
            ScheduledExecutorService scheduler,
            int failureThreshold,
            int halfOpenTrialLimit,
            Duration openCooldown
    ) {
        this.scheduler = scheduler;
        this.failureThreshold = failureThreshold;
        this.halfOpenTrialLimit = halfOpenTrialLimit;
        this.openCooldown = openCooldown;
    }

    public <T> T execute(Supplier<T> action) {
        synchronized (this) {
            if (state == CircuitBreakerState.OPEN) {
                throw new CircuitOpenException("Circuit breaker is OPEN. Request blocked.");
            }

            if (state == CircuitBreakerState.HALF_OPEN) {
                int trials = halfOpenTrials.incrementAndGet();

                if (trials > halfOpenTrialLimit) {
                    halfOpenTrials.decrementAndGet();
                    throw new CircuitOpenException("Circuit breaker is HALF_OPEN. Trial limit exceeded.");
                }
            }
        }

        try {
            T result = action.get();
            onSuccess();
            return result;
        } catch (RuntimeException ex) {
            onFailure();
            throw ex;
        }
    }

    private void onSuccess() {
        synchronized (this) {
            failureCount.set(0);

            if (state == CircuitBreakerState.HALF_OPEN) {
                transitionToClosed();
            }
        }
    }

    private void onFailure() {
        synchronized (this) {
            if (state == CircuitBreakerState.HALF_OPEN) {
                transitionToOpen();
                return;
            }

            int failures = failureCount.incrementAndGet();

            if (state == CircuitBreakerState.CLOSED && failures >= failureThreshold) {
                transitionToOpen();
            }
        }
    }

    private void transitionToOpen() {
        state = CircuitBreakerState.OPEN;
        failureCount.set(0);
        halfOpenTrials.set(0);

        System.out.println("Circuit moved to OPEN");

        scheduler.schedule(
                this::transitionToHalfOpen,
                openCooldown.toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    private void transitionToHalfOpen() {
        synchronized (this) {
            if (state == CircuitBreakerState.OPEN) {
                state = CircuitBreakerState.HALF_OPEN;
                halfOpenTrials.set(0);
                System.out.println("Circuit moved to HALF_OPEN");
            }
        }
    }

    private void transitionToClosed() {
        state = CircuitBreakerState.CLOSED;
        failureCount.set(0);
        halfOpenTrials.set(0);

        System.out.println("Circuit moved to CLOSED");
    }

    public CircuitBreakerState getState() {
        return state;
    }
}