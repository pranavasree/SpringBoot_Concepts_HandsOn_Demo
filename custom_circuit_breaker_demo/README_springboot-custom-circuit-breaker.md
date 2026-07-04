# Spring Boot Circuit Breaker — Custom Implementation

Project folder name:

```text
springboot-custom-circuit-breaker
```

## What is this project?

This project teaches how a **circuit breaker** works by building one manually using Java and Spring Boot.

You will not only use a library. You will understand the inside logic:

```text
count failures
open the circuit
block bad calls
wait for some time
try again
close if healthy
```

---

# What Does Circuit Breaker Mean?

A **circuit breaker** is a safety switch.

In real life, houses have electrical circuit breakers. If too much electricity flows through a wire, the circuit breaker turns off the power to protect the house from damage or fire.

In software, a circuit breaker does a similar job.

If one service keeps failing, the circuit breaker stops our app from calling that failing service again and again.

Simple meaning:

```text
A circuit breaker protects our app from repeatedly calling something that is already broken.
```

In our example:

```text
Order Service  --->  Payment Service
```

If the payment service keeps failing, the circuit breaker says:

```text
Stop calling payment service for a while.
Return a safe fallback response.
Try again later.
```

---


# Explanation

Imagine you are playing a video game and the game needs to connect to the online game server.

You press **Start Game**.

The server does not respond.

You press it again.

Still no response.

You press it a third time.

Still not working.

Now the game says:

> “The server looks busy right now. I will stop trying for 10 seconds and show you a safe message instead.”

That is what a circuit breaker does.

It watches for repeated failures.  
If another service fails again and again, it stops sending more requests there for a short time.

After some time, it tries one small test request.

If the service works, it allows normal requests again.

If the service still fails, it blocks again.

---

# Why do we need Circuit Breaker?

Without circuit breaker:

```text
User 1 calls payment service -> waits -> fails
User 2 calls payment service -> waits -> fails
User 3 calls payment service -> waits -> fails
User 100 calls payment service -> waits -> fails
```

Now our app also becomes slow.

With circuit breaker:

```text
Payment service failed many times.
Circuit breaker opens.
New calls are blocked quickly.
Fallback response is returned.
```

This protects our application.

---

# Circuit Breaker States

A circuit breaker has 3 main states.

## 1. CLOSED

This means normal mode.

```text
Requests are allowed.
Failures are counted.
```

Example:

```text
Payment service is working.
Order service can call payment service.
```

## 2. OPEN

This means blocked mode.

```text
Requests are not allowed.
Payment service is not called.
Fallback response is returned immediately.
```

Example:

```text
Payment service failed 3 times.
Circuit is opened.
Do not call payment service for 10 seconds.
```

## 3. HALF_OPEN

This means testing mode.

```text
Allow a small number of trial requests.
If trial succeeds -> CLOSED.
If trial fails -> OPEN again.
```

Example:

```text
After 10 seconds, test payment service once.
If it works, allow traffic again.
If it fails, block again.
```

---

# Project Structure

```text
springboot-custom-circuit-breaker
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── example
        │           └── customcb
        │               ├── CustomCircuitBreakerApplication.java
        │               ├── circuitbreaker
        │               │   ├── CircuitBreakerState.java
        │               │   ├── CircuitOpenException.java
        │               │   └── SimpleCircuitBreaker.java
        │               ├── config
        │               │   └── CircuitBreakerConfig.java
        │               ├── controller
        │               │   └── OrderController.java
        │               └── service
        │                   ├── OrderService.java
        │                   └── PaymentClient.java
        └── resources
            └── application.yml
```

---

# How the Request Flows

```text
Browser/Postman
     |
     v
OrderController
     |
     v
OrderService
     |
     v
SimpleCircuitBreaker
     |
     v
PaymentClient
```

If payment works:

```text
Order placed successfully.
```

If payment fails too many times:

```text
Circuit breaker opens.
Fallback response is returned.
```

---

# Main Code Explanation

## 1. `CircuitBreakerState.java`

```java
public enum CircuitBreakerState {
    CLOSED,
    OPEN,
    HALF_OPEN
}
```

This file defines the three possible states.

Simple meaning:

```text
CLOSED    = normal
OPEN      = blocked
HALF_OPEN = testing
```

---

## 2. `CircuitOpenException.java`

```java
public class CircuitOpenException extends RuntimeException {

    public CircuitOpenException(String message) {
        super(message);
    }
}
```

This custom exception is thrown when the circuit is open.

Example:

```text
Circuit is OPEN.
Do not call payment service.
Throw CircuitOpenException.
```

---

## 3. `SimpleCircuitBreaker.java`

This is the most important file.

It has:

```java
private volatile CircuitBreakerState state = CircuitBreakerState.CLOSED;
```

This means the circuit starts in normal mode.

It also has:

```java
private final AtomicInteger failureCount = new AtomicInteger(0);
```

This counts how many times the payment service failed.

It also has:

```java
private final AtomicInteger halfOpenTrials = new AtomicInteger(0);
```

This counts how many test calls were allowed in half-open state.

---

## 4. `execute()` method

```java
public <T> T execute(Supplier<T> action)
```

This method receives the real work that we want to run.

Example:

```java
paymentCircuitBreaker.execute(
    () -> paymentClient.charge(orderId)
);
```

This means:

```text
Circuit breaker, please run this payment call only if it is safe.
```

Inside `execute()`:

### If state is OPEN

```java
if (state == CircuitBreakerState.OPEN) {
    throw new CircuitOpenException("Circuit breaker is OPEN. Request blocked.");
}
```

Meaning:

```text
Do not call payment service.
Return fallback.
```

### If state is HALF_OPEN

```java
if (state == CircuitBreakerState.HALF_OPEN) {
    int trials = halfOpenTrials.incrementAndGet();

    if (trials > halfOpenTrialLimit) {
        halfOpenTrials.decrementAndGet();
        throw new CircuitOpenException("Circuit breaker is HALF_OPEN. Trial limit exceeded.");
    }
}
```

Meaning:

```text
We are testing.
Allow only a few trial calls.
```

### If the call succeeds

```java
T result = action.get();
onSuccess();
return result;
```

Meaning:

```text
Payment service worked.
Reset failure count.
Maybe close the circuit.
```

### If the call fails

```java
catch (RuntimeException ex) {
    onFailure();
    throw ex;
}
```

Meaning:

```text
Payment service failed.
Increase failure count.
Maybe open the circuit.
```

---

## 5. `onSuccess()` method

```java
private void onSuccess() {
    synchronized (this) {
        failureCount.set(0);

        if (state == CircuitBreakerState.HALF_OPEN) {
            transitionToClosed();
        }
    }
}
```

Simple meaning:

```text
If payment works, reset failures.
If we were testing, go back to normal.
```

---

## 6. `onFailure()` method

```java
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
```

Simple meaning:

```text
If the service fails, count the failure.
If failures reach the limit, open the circuit.
```

Example:

```text
failureThreshold = 3

Failure 1 -> still CLOSED
Failure 2 -> still CLOSED
Failure 3 -> OPEN
```

---

## 7. `transitionToOpen()` method

```java
private void transitionToOpen() {
    state = CircuitBreakerState.OPEN;
    failureCount.set(0);
    halfOpenTrials.set(0);

    scheduler.schedule(
            this::transitionToHalfOpen,
            openCooldown.toMillis(),
            TimeUnit.MILLISECONDS
    );
}
```

Simple meaning:

```text
Circuit is now OPEN.
Block calls for some time.
After cooldown, move to HALF_OPEN.
```

Example:

```text
Open for 10 seconds.
After 10 seconds, try again.
```

---

## 8. `transitionToHalfOpen()` method

```java
private void transitionToHalfOpen() {
    synchronized (this) {
        if (state == CircuitBreakerState.OPEN) {
            state = CircuitBreakerState.HALF_OPEN;
            halfOpenTrials.set(0);
        }
    }
}
```

Simple meaning:

```text
Testing time started.
Allow trial requests.
```

---

## 9. `transitionToClosed()` method

```java
private void transitionToClosed() {
    state = CircuitBreakerState.CLOSED;
    failureCount.set(0);
    halfOpenTrials.set(0);
}
```

Simple meaning:

```text
Service recovered.
Everything is normal again.
```

---

# Spring Configuration Explanation

## `CircuitBreakerConfig.java`

This file creates a circuit breaker object for Spring.

```java
@Bean
public SimpleCircuitBreaker paymentCircuitBreaker(ScheduledExecutorService scheduler) {
    return new SimpleCircuitBreaker(
            scheduler,
            3,
            1,
            Duration.ofSeconds(10)
    );
}
```

Meaning:

```text
3 failures -> open circuit
1 trial call in HALF_OPEN state
10 seconds cooldown when OPEN
```

So the rule is:

```text
If payment fails 3 times, stop calling it for 10 seconds.
After 10 seconds, allow 1 test call.
```

---

# Service Code Explanation

## `PaymentClient.java`

This file pretends to be an external payment service.

```java
private final AtomicBoolean healthy = new AtomicBoolean(false);
```

By default, the payment service is unhealthy.

```java
if (!healthy.get()) {
    throw new RuntimeException("Payment service is DOWN");
}
```

Meaning:

```text
If payment service is not healthy, throw error.
```

We can make it healthy using an API:

```text
POST /api/payment/healthy/true
```

---

## `OrderService.java`

This file places the order.

Important code:

```java
String paymentResult = paymentCircuitBreaker.execute(
        () -> paymentClient.charge(orderId)
);
```

Simple meaning:

```text
Ask circuit breaker if it is safe to call payment service.
If safe, call payment service.
If not safe, return fallback.
```

Fallback code:

```java
catch (CircuitOpenException ex) {
    return "Fallback: Payment system is temporarily unavailable. Please try again later.";
}
```

Meaning:

```text
Payment system is having problems.
Do not crash the app.
Give a safe response.
```

---

## `OrderController.java`

This file exposes APIs.

### Place order

```text
GET /api/orders/{orderId}
```

Example:

```bash
curl http://localhost:8080/api/orders/101
```

### Check circuit state

```text
GET /api/circuit/state
```

Example:

```bash
curl http://localhost:8080/api/circuit/state
```

### Make payment healthy or unhealthy

```text
POST /api/payment/healthy/true
POST /api/payment/healthy/false
```

Example:

```bash
curl -X POST http://localhost:8080/api/payment/healthy/true
```

---

# How to Run

```bash
mvn spring-boot:run
```

---

# How to Test

## Step 1: Payment service is down by default

Call this 3 times:

```bash
curl http://localhost:8080/api/orders/101
```

You will see payment failure or fallback.

## Step 2: Check circuit state

```bash
curl http://localhost:8080/api/circuit/state
```

After 3 failures:

```text
OPEN
```

## Step 3: Call again

```bash
curl http://localhost:8080/api/orders/102
```

Now payment service will not be called.

You get fallback immediately.

## Step 4: Make payment healthy

```bash
curl -X POST http://localhost:8080/api/payment/healthy/true
```

## Step 5: Wait 10 seconds

After 10 seconds, circuit moves to HALF_OPEN.

## Step 6: Try again

```bash
curl http://localhost:8080/api/orders/103
```

If payment succeeds, circuit becomes:

```text
CLOSED
```

---

# Interview Explanation

You can say:

> I built a custom circuit breaker in Spring Boot to understand how resilience patterns work internally. The circuit breaker has three states: CLOSED, OPEN, and HALF_OPEN. In CLOSED state, calls are allowed and failures are counted. If the failure count reaches the configured threshold, the circuit moves to OPEN state and blocks calls immediately. After a cooldown time, it moves to HALF_OPEN and allows a limited trial request. If the trial succeeds, the circuit closes again; if it fails, the circuit opens again. This protects the application from repeatedly calling a failing external service.

---

# One-Line Summary

A circuit breaker is like a safety switch.

```text
If another service keeps failing, stop calling it for a while.
Try again later.
If it works, continue.
If it fails, block again.
```
