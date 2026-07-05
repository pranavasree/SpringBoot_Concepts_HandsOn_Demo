# Spring Boot Circuit Breaker — Resilience4j Implementation

Project folder name:

```text
springboot-circuit-breaker-resilience4j
```

## What is this project?

This project shows how to use **Resilience4j Circuit Breaker** in a Spring Boot application.

Resilience4j is a library that already gives us circuit breaker logic, so we do not need to write the state-management code manually.

It helps us handle failures in a cleaner way:

```text
detect failures
open circuit
return fallback
wait
try again
recover automatically
```

---

# What Does Circuit Breaker Mean?

A **circuit breaker** is a safety switch.

In real life, an electrical circuit breaker protects a house. If too much electricity flows, it turns off the power before something gets damaged.

In software, a circuit breaker protects our application.

If another service keeps failing, the circuit breaker stops our application from calling that service again and again.

Simple meaning:

```text
A circuit breaker protects one service from wasting time on another service that is already failing.
```

In this project:

```text
Order Service  --->  Payment Service
```

If the payment service is down, Resilience4j can open the circuit and return a fallback response immediately.

---


# Explaination

Imagine you are trying to order pizza from a pizza shop using an app.

You tap **Place Order**.

The pizza shop system does not answer.

You tap again.

Still no answer.

You tap a third time.

Still not working.

Now the app says:

> “The pizza shop system seems down. I will stop trying for 10 seconds and show you a safe message instead.”

That is what Resilience4j circuit breaker does.

It says:

```text
This service is failing.
Stop calling it for a while.
Return a fallback response.
Try again later.
```

If the service works later, Resilience4j allows normal calls again.

---

# Why do we need this?

Suppose payment service is down.

Without circuit breaker:

```text
Order 1 -> calls payment -> fails
Order 2 -> calls payment -> fails
Order 3 -> calls payment -> fails
Order 100 -> calls payment -> fails
```

This wastes time and resources.

With circuit breaker:

```text
Payment failed too many times.
Circuit opens.
Do not call payment service.
Return fallback quickly.
```

This protects the order service.

---

# Circuit Breaker States

## 1. CLOSED

Normal state.

```text
Calls are allowed.
Failures are counted.
```

## 2. OPEN

Blocked state.

```text
Calls are not allowed.
Fallback is returned.
```

## 3. HALF_OPEN

Testing state.

```text
Allow a few test calls.
If test calls succeed -> CLOSED.
If test calls fail -> OPEN again.
```

---

# Project Structure

```text
springboot-circuit-breaker-resilience4j
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── example
        │           └── resilience4jcb
        │               ├── Resilience4jCircuitBreakerApplication.java
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
@CircuitBreaker from Resilience4j
     |
     v
PaymentClient
```

If payment works:

```text
Order placed successfully.
```

If payment fails:

```text
Fallback response is returned.
```

If payment keeps failing:

```text
Circuit opens.
PaymentClient is not called.
Fallback is returned directly.
```

---

# Dependency Explanation

## `pom.xml`

Important dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

This lets us build REST APIs.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

This is needed because `@CircuitBreaker` works using Spring AOP.

AOP means Spring adds extra behavior around your method.

Simple meaning:

```text
Before calling the method, Resilience4j checks the circuit.
After the method fails or succeeds, Resilience4j updates the circuit.
```

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.4.0</version>
</dependency>
```

This gives us Resilience4j support for Spring Boot 3.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

This is optional, but useful for health and metrics.

---

# Configuration Explanation

## `application.yml`

```yaml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
```

This creates a circuit breaker named:

```text
paymentService
```

This name must match the annotation in service class:

```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
```

---

## `slidingWindowType: COUNT_BASED`

```yaml
slidingWindowType: COUNT_BASED
```

This means Resilience4j checks a fixed number of recent calls.

Example:

```text
Look at recent 5 calls.
How many failed?
```

---

## `slidingWindowSize: 5`

```yaml
slidingWindowSize: 5
```

This means Resilience4j looks at the last 5 calls.

Example:

```text
Call 1 failed
Call 2 failed
Call 3 success
Call 4 failed
Call 5 success
```

It calculates failure percentage from these calls.

---

## `minimumNumberOfCalls: 3`

```yaml
minimumNumberOfCalls: 3
```

This means Resilience4j will not decide too early.

It waits until at least 3 calls happen.

Example:

```text
Only 1 call failed -> do not open yet.
Only 2 calls failed -> do not open yet.
At least 3 calls happened -> now calculate failure rate.
```

---

## `failureRateThreshold: 50`

```yaml
failureRateThreshold: 50
```

This means:

```text
If 50% or more calls fail, open the circuit.
```

Example:

```text
3 calls happened.
2 failed.
Failure rate = 66%.
66% is greater than 50%.
Circuit opens.
```

---

## `waitDurationInOpenState: 10s`

```yaml
waitDurationInOpenState: 10s
```

This means:

```text
When circuit is OPEN, keep it open for 10 seconds.
```

During this time:

```text
Payment service is not called.
Fallback is returned.
```

---

## `permittedNumberOfCallsInHalfOpenState: 2`

```yaml
permittedNumberOfCallsInHalfOpenState: 2
```

After 10 seconds, circuit moves to HALF_OPEN.

This setting means:

```text
Allow 2 test calls.
```

If they succeed, circuit closes.

If they fail, circuit opens again.

---

## `automaticTransitionFromOpenToHalfOpenEnabled: true`

```yaml
automaticTransitionFromOpenToHalfOpenEnabled: true
```

This means Resilience4j automatically moves from OPEN to HALF_OPEN after the wait time.

So we do not need to manually trigger it.

---

# Main Code Explanation

## 1. `Resilience4jCircuitBreakerApplication.java`

```java
@SpringBootApplication
public class Resilience4jCircuitBreakerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Resilience4jCircuitBreakerApplication.class, args);
    }
}
```

This starts the Spring Boot application.

Simple meaning:

```text
Start the server.
Load all controllers, services, and configuration.
```

---

## 2. `PaymentClient.java`

This class is a fake external service.

```java
private final AtomicBoolean healthy = new AtomicBoolean(false);
```

This means payment service starts as DOWN.

```java
public String chargePayment(String orderId) {
    System.out.println("Calling payment service for orderId: " + orderId);

    if (!healthy.get()) {
        throw new RuntimeException("Payment service is DOWN");
    }

    return "Payment successful for orderId: " + orderId;
}
```

Simple meaning:

```text
If payment service is unhealthy, throw an error.
If payment service is healthy, return success.
```

This method helps us change payment status:

```java
public void setHealthy(boolean value) {
    healthy.set(value);
}
```

So we can test:

```text
POST /api/payment/healthy/true
POST /api/payment/healthy/false
```

---

## 3. `OrderService.java`

This class contains the main business logic.

```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
public String placeOrder(String orderId) {
    String paymentResponse = paymentClient.chargePayment(orderId);

    return "Order placed successfully. " + paymentResponse;
}
```

Simple meaning:

```text
Before calling paymentClient, Resilience4j checks the circuit.
If circuit is CLOSED, paymentClient is called.
If paymentClient succeeds, normal response is returned.
If paymentClient fails, fallback is called.
If failures are too many, circuit opens.
```

Important annotation:

```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
```

Meaning:

```text
Use the circuit breaker named paymentService.
If something goes wrong, call paymentFallback.
```

---

## 4. Fallback Method

```java
public String paymentFallback(String orderId, Exception exception) {
    return "Fallback response: Payment service is temporarily unavailable. "
            + "Order is saved as PENDING for orderId: " + orderId
            + ". Reason: " + exception.getMessage();
}
```

Fallback means safe backup response.

Instead of crashing, we say:

```text
Payment is not available now.
Order is saved as pending.
Try payment later.
```

The fallback method should have the same original method parameters plus an exception parameter.

Original method:

```java
placeOrder(String orderId)
```

Fallback method:

```java
paymentFallback(String orderId, Exception exception)
```

---

## 5. `OrderController.java`

This class exposes APIs.

### Place order API

```java
@GetMapping("/orders/{orderId}")
public String placeOrder(@PathVariable String orderId) {
    return orderService.placeOrder(orderId);
}
```

Endpoint:

```text
GET /api/orders/101
```

Meaning:

```text
Place order with orderId 101.
```

---

### Make payment healthy or unhealthy

```java
@PostMapping("/payment/healthy/{value}")
public String updatePaymentHealth(@PathVariable boolean value) {
    paymentClient.setHealthy(value);
    return "Payment service healthy = " + paymentClient.isHealthy();
}
```

Endpoints:

```text
POST /api/payment/healthy/true
POST /api/payment/healthy/false
```

Meaning:

```text
true  = payment service works
false = payment service fails
```

---

### Check payment health

```java
@GetMapping("/payment/healthy")
public String checkPaymentHealth() {
    return "Payment service healthy = " + paymentClient.isHealthy();
}
```

Endpoint:

```text
GET /api/payment/healthy
```

---

### Check circuit state

```java
@GetMapping("/circuit/state")
public String getCircuitState() {
    return circuitBreakerRegistry
            .circuitBreaker("paymentService")
            .getState()
            .name();
}
```

Endpoint:

```text
GET /api/circuit/state
```

This tells us if circuit is:

```text
CLOSED
OPEN
HALF_OPEN
```

---

# How to Run

```bash
mvn spring-boot:run
```

---

# How to Test

## Step 1: Payment service is down by default

Run:

```bash
curl http://localhost:8080/api/payment/healthy
```

Expected:

```text
Payment service healthy = false
```

---

## Step 2: Call order API 3 times

```bash
curl http://localhost:8080/api/orders/101
curl http://localhost:8080/api/orders/102
curl http://localhost:8080/api/orders/103
```

Expected response:

```text
Fallback response: Payment service is temporarily unavailable. Order is saved as PENDING...
```

---

## Step 3: Check circuit state

```bash
curl http://localhost:8080/api/circuit/state
```

Expected after enough failures:

```text
OPEN
```

---

## Step 4: Call order API again while circuit is OPEN

```bash
curl http://localhost:8080/api/orders/104
```

Expected:

```text
Fallback response...
```

But this time `PaymentClient` is not called.

You can check console logs.

When circuit is CLOSED, you will see:

```text
Calling payment service for orderId: 104
```

When circuit is OPEN, this line will not print because Resilience4j blocks the call.

---

## Step 5: Make payment healthy

```bash
curl -X POST http://localhost:8080/api/payment/healthy/true
```

Expected:

```text
Payment service healthy = true
```

---

## Step 6: Wait 10 seconds

The circuit will move from:

```text
OPEN -> HALF_OPEN
```

---

## Step 7: Call order API again

```bash
curl http://localhost:8080/api/orders/105
```

Expected:

```text
Order placed successfully. Payment successful for orderId: 105
```

Now check circuit state:

```bash
curl http://localhost:8080/api/circuit/state
```

Expected:

```text
CLOSED
```

---

# What Resilience4j Does Internally

When you call:

```java
orderService.placeOrder("101")
```

Resilience4j checks:

```text
Is circuit CLOSED?
    Yes -> call payment service.

Did payment fail?
    Yes -> record failure.

Are failures too many?
    Yes -> open circuit.

Is circuit OPEN?
    Yes -> do not call payment service.
           directly call fallback.

Has 10 seconds passed?
    Yes -> move to HALF_OPEN.

Did test call succeed?
    Yes -> close circuit.
```

---

# Difference Between Custom and Resilience4j

| Topic | Custom Circuit Breaker | Resilience4j |
|---|---|---|
| Who writes logic? | We write everything | Library handles it |
| States | We manage manually | Resilience4j manages |
| Config | Java code | `application.yml` |
| Annotation support | No | Yes, `@CircuitBreaker` |
| Metrics | Need to build manually | Available with Actuator |
| Production use | Learning only | Recommended |

---

# Interview Explanation

You can say:

> I used Resilience4j circuit breaker in Spring Boot to protect the order service from payment service failures. I configured a circuit breaker instance named `paymentService` in `application.yml` with a count-based sliding window, minimum number of calls, failure rate threshold, open-state wait duration, and half-open permitted calls. In the service layer, I used `@CircuitBreaker` on the method that calls the payment service and provided a fallback method. When the payment service fails repeatedly, Resilience4j opens the circuit and stops calling the failing service. It returns fallback immediately. After the wait duration, it moves to half-open state, tests the service, and closes again if the service is healthy.

---

# Simple One-Line Summary

Resilience4j circuit breaker is like a smart guard.

```text
If a service keeps failing, stop calling it.
Return fallback.
Try again later.
If it works, allow calls again.
```
