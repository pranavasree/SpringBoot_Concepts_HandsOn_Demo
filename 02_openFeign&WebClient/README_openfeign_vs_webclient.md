# Spring Boot REST Clients: OpenFeign vs WebClient

This README explains **OpenFeign** and **WebClient** from definitions to code, using a simple example and code explanations.

---

## 1. What problem are we solving?

In backend applications, one service often needs to call another service.

Example:

```text
Order Service  --->  Book Service
```

The user wants to place an order.

Before placing the order, `Order Service` needs book details from `Book Service`.

So `Order Service` must call this API:

```text
GET http://localhost:8081/api/books/101
```

In Spring Boot, we can call another REST API using:

```text
1. OpenFeign
2. WebClient
```

Both can do the same job, but the coding style is different.

---

## 2. Explaination with Example

Imagine you want to order pizza.

### OpenFeign example

OpenFeign is like having a saved contact called **Pizza Shop**.

You just say:

```text
Order pizza number 101
```

You do not dial the number manually.  
You do not explain the address every time.  
You just call a saved method.

That is OpenFeign.

In code, it feels like this:

```java
BookDto book = bookFeignClient.getBookById(101L);
```

It looks like a normal Java method call, but internally it makes an HTTP API call.

---

### WebClient example

WebClient is like ordering pizza manually step by step.

You do this:

```text
Open phone
Type pizza shop number
Say what pizza you want
Wait for response
Read the response
```

That is WebClient.

In code, it looks like this:

```java
BookDto book = webClient
        .get()
        .uri("/api/books/{id}", 101)
        .retrieve()
        .bodyToMono(BookDto.class)
        .block();
```

Here, we clearly write every step of the HTTP request.

---

## 3. Definitions

### What is OpenFeign?

**OpenFeign is a declarative REST client.**

Declarative means:

```text
We describe what API we want to call.
Spring creates the actual HTTP client for us.
```

We create an interface:

```java
@FeignClient(name = "book-service", url = "${book-service.url}")
public interface BookFeignClient {

    @GetMapping("/api/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);
}
```

Then we call it like a normal Java method:

```java
BookDto book = bookFeignClient.getBookById(101L);
```

Simple meaning:

```text
OpenFeign = write an interface, Spring handles the API call.
```

---

### What is WebClient?

**WebClient is a fluent HTTP client from Spring WebFlux.**

Fluent means:

```text
We build the request step by step using chained methods.
```

Example:

```java
BookDto book = webClient
        .get()
        .uri("/api/books/{id}", 101)
        .retrieve()
        .bodyToMono(BookDto.class)
        .block();
```

Simple meaning:

```text
WebClient = manually build and send the API request.
```

---

## 4. Main difference

| Topic | OpenFeign | WebClient |
|---|---|---|
| Style | Interface-based | Method chaining |
| Code amount | Less code | More code |
| Learning difficulty | Easier | Slightly harder |
| Best use case | Simple microservice calls | High-performance or reactive calls |
| Default style | Synchronous/blocking | Non-blocking/reactive |
| Control | Less manual control | More control |
| Configuration | Uses `@FeignClient` and `@EnableFeignClients` | Uses `WebClient.Builder` or `WebClient` bean |

---

## 5. Project example

We will create two Spring Boot projects:

```text
book-service  -> runs on port 8081
order-service -> runs on port 8080
```

### What each service does

```text
book-service:
    Provides book details.

order-service:
    Calls book-service using OpenFeign and WebClient.
```

---

## 6. Final flow

```text
User
  |
  v
Order Service
  |
  |-- using OpenFeign ---> Book Service
  |
  |-- using WebClient --> Book Service
```

---

# Project 1: book-service

This is the service that provides book data.

---

## 7. book-service folder structure

```text
book-service
├── pom.xml
└── src/main
    ├── java/com/example/bookservice
    │   ├── BookServiceApplication.java
    │   ├── controller
    │   │   └── BookController.java
    │   └── dto
    │       └── BookDto.java
    └── resources
        └── application.yml
```

---

## 8. book-service `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>book-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Used to create REST APIs -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

</project>
```

### Explanation

```xml
spring-boot-starter-web
```

This dependency gives us:

```text
REST Controller
Tomcat server
JSON response support
Spring MVC
```

---

## 9. book-service `application.yml`

```yaml
server:
  port: 8081

spring:
  application:
    name: book-service
```

### Explanation

```yaml
server:
  port: 8081
```

This means `book-service` will run on:

```text
http://localhost:8081
```

---

## 10. `BookServiceApplication.java`

```java
package com.example.bookservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}
```

### Explanation

This is the main class.

It starts the Spring Boot application.

---

## 11. `BookDto.java`

```java
package com.example.bookservice.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        double price
) {
}
```

### Explanation

`BookDto` is the response object.

It contains:

```text
id
title
author
price
```

Example response:

```json
{
  "id": 101,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 39.99
}
```

---

## 12. `BookController.java`

```java
package com.example.bookservice.controller;

import com.example.bookservice.dto.BookDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return new BookDto(
                id,
                "Clean Code",
                "Robert C. Martin",
                39.99
        );
    }
}
```

### Explanation

```java
@RestController
```

This class exposes REST APIs.

```java
@RequestMapping("/api/books")
```

Base URL for this controller.

```java
@GetMapping("/{id}")
```

This handles:

```text
GET /api/books/101
```

```java
@PathVariable Long id
```

This reads `101` from the URL.

---

## 13. Test book-service

Run:

```bash
mvn spring-boot:run
```

Test:

```bash
curl http://localhost:8081/api/books/101
```

Expected response:

```json
{
  "id": 101,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 39.99
}
```

---

# Project 2: order-service

This service calls `book-service`.

It uses both:

```text
OpenFeign
WebClient
```

---

## 14. order-service folder structure

```text
order-service
├── pom.xml
└── src/main
    ├── java/com/example/orderservice
    │   ├── OrderServiceApplication.java
    │   ├── client
    │   │   └── BookFeignClient.java
    │   ├── config
    │   │   └── WebClientConfig.java
    │   ├── controller
    │   │   └── OrderController.java
    │   ├── dto
    │   │   └── BookDto.java
    │   └── service
    │       ├── BookWebClientService.java
    │       └── OrderService.java
    └── resources
        └── application.yml
```

---

## 15. order-service `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>order-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.6</spring-cloud.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Manages compatible Spring Cloud dependency versions -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>

        <!-- Used to create REST APIs -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Used for OpenFeign -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- Used for WebClient -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

    </dependencies>

</project>
```

---

## 16. Configuration difference

OpenFeign and WebClient need different setup.

### OpenFeign configuration

OpenFeign needs:

```xml
spring-cloud-starter-openfeign
```

And this annotation:

```java
@EnableFeignClients
```

Also, we create an interface using:

```java
@FeignClient
```

So OpenFeign setup is:

```text
Add OpenFeign dependency
Add @EnableFeignClients
Create @FeignClient interface
```

---

### WebClient configuration

WebClient needs:

```xml
spring-boot-starter-webflux
```

And we create a `WebClient` bean:

```java
@Bean
public WebClient bookWebClient(...) {
    return WebClient.builder()
            .baseUrl(bookServiceUrl)
            .build();
}
```

So WebClient setup is:

```text
Add WebFlux dependency
Create WebClient bean
Use WebClient in service class
```

---

## 17. order-service `application.yml`

```yaml
server:
  port: 8080

spring:
  application:
    name: order-service

book-service:
  url: http://localhost:8081
```

### Explanation

```yaml
server:
  port: 8080
```

This means `order-service` runs on:

```text
http://localhost:8080
```

```yaml
book-service:
  url: http://localhost:8081
```

This stores the `book-service` base URL.

Instead of hardcoding it everywhere, we use this property.

---

## 18. `OrderServiceApplication.java`

```java
package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

### Explanation

```java
@SpringBootApplication
```

Starts the Spring Boot app.

```java
@EnableFeignClients
```

This tells Spring:

```text
Find interfaces marked with @FeignClient and create real HTTP clients from them.
```

Without this, OpenFeign client will not work.

---

## 19. `BookDto.java`

```java
package com.example.orderservice.dto;

public record BookDto(
        Long id,
        String title,
        String author,
        double price
) {
}
```

### Explanation

This must match the response coming from `book-service`.

`book-service` returns:

```json
{
  "id": 101,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 39.99
}
```

So `order-service` needs a matching DTO.

---

# OpenFeign Code

---

## 20. `BookFeignClient.java`

```java
package com.example.orderservice.client;

import com.example.orderservice.dto.BookDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "book-service",
        url = "${book-service.url}"
)
public interface BookFeignClient {

    @GetMapping("/api/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);
}
```

### Explanation

```java
@FeignClient(
        name = "book-service",
        url = "${book-service.url}"
)
```

This tells Spring:

```text
Create an HTTP client for book-service.
Base URL is http://localhost:8081.
```

```java
@GetMapping("/api/books/{id}")
```

This means the client will call:

```text
GET /api/books/{id}
```

```java
BookDto getBookById(...)
```

This method returns the book response as a Java object.

When we call:

```java
bookFeignClient.getBookById(101L);
```

OpenFeign internally calls:

```text
GET http://localhost:8081/api/books/101
```

---

# WebClient Code

---

## 21. `WebClientConfig.java`

```java
package com.example.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient bookWebClient(
            @Value("${book-service.url}") String bookServiceUrl
    ) {
        return WebClient.builder()
                .baseUrl(bookServiceUrl)
                .build();
    }
}
```

### Explanation

```java
@Configuration
```

This tells Spring this class contains configuration.

```java
@Bean
```

This creates an object managed by Spring.

```java
@Value("${book-service.url}")
```

This reads the value from `application.yml`:

```yaml
book-service:
  url: http://localhost:8081
```

```java
WebClient.builder().baseUrl(bookServiceUrl).build()
```

This creates a WebClient with base URL:

```text
http://localhost:8081
```

---

## 22. `BookWebClientService.java`

```java
package com.example.orderservice.service;

import com.example.orderservice.dto.BookDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BookWebClientService {

    private final WebClient bookWebClient;

    public BookWebClientService(WebClient bookWebClient) {
        this.bookWebClient = bookWebClient;
    }

    public BookDto getBookById(Long id) {
        return bookWebClient
                .get()
                .uri("/api/books/{id}", id)
                .retrieve()
                .bodyToMono(BookDto.class)
                .block();
    }
}
```

### Explanation

```java
bookWebClient.get()
```

Use HTTP GET.

```java
.uri("/api/books/{id}", id)
```

Call:

```text
/api/books/101
```

```java
.retrieve()
```

Get the response from the API.

```java
.bodyToMono(BookDto.class)
```

Convert JSON response into a `BookDto`.

```java
.block()
```

Wait for the response and return the object.

Important:

```text
WebClient is naturally non-blocking.
But using .block() makes it behave like a normal synchronous call.
```

---

# Business Logic

---

## 23. `OrderService.java`

```java
package com.example.orderservice.service;

import com.example.orderservice.client.BookFeignClient;
import com.example.orderservice.dto.BookDto;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final BookFeignClient bookFeignClient;
    private final BookWebClientService bookWebClientService;

    public OrderService(
            BookFeignClient bookFeignClient,
            BookWebClientService bookWebClientService
    ) {
        this.bookFeignClient = bookFeignClient;
        this.bookWebClientService = bookWebClientService;
    }

    public String placeOrderUsingFeign(Long bookId) {
        BookDto book = bookFeignClient.getBookById(bookId);

        return "Order placed using OpenFeign for book: "
                + book.title()
                + " by "
                + book.author()
                + ". Price: $" + book.price();
    }

    public String placeOrderUsingWebClient(Long bookId) {
        BookDto book = bookWebClientService.getBookById(bookId);

        return "Order placed using WebClient for book: "
                + book.title()
                + " by "
                + book.author()
                + ". Price: $" + book.price();
    }
}
```

### Explanation

This service has two methods.

### OpenFeign method

```java
BookDto book = bookFeignClient.getBookById(bookId);
```

This is simple.

It looks like a normal Java method call.

OpenFeign hides HTTP request details.

### WebClient method

```java
BookDto book = bookWebClientService.getBookById(bookId);
```

This calls our WebClient service.

Inside that service, we manually build the API call.

---

# Controller

---

## 24. `OrderController.java`

```java
package com.example.orderservice.controller;

import com.example.orderservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/feign/{bookId}")
    public String orderUsingFeign(@PathVariable Long bookId) {
        return orderService.placeOrderUsingFeign(bookId);
    }

    @GetMapping("/webclient/{bookId}")
    public String orderUsingWebClient(@PathVariable Long bookId) {
        return orderService.placeOrderUsingWebClient(bookId);
    }
}
```

### Explanation

This exposes two APIs.

### OpenFeign endpoint

```text
GET /api/orders/feign/101
```

This calls book-service using OpenFeign.

### WebClient endpoint

```text
GET /api/orders/webclient/101
```

This calls book-service using WebClient.

---

## 25. Run the projects

### Start book-service

```bash
cd book-service
mvn spring-boot:run
```

It runs on:

```text
http://localhost:8081
```

### Start order-service

Open another terminal:

```bash
cd order-service
mvn spring-boot:run
```

It runs on:

```text
http://localhost:8080
```

---

## 26. Test APIs

### Test book-service directly

```bash
curl http://localhost:8081/api/books/101
```

Expected:

```json
{
  "id": 101,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "price": 39.99
}
```

---

### Test OpenFeign from order-service

```bash
curl http://localhost:8080/api/orders/feign/101
```

Expected:

```text
Order placed using OpenFeign for book: Clean Code by Robert C. Martin. Price: $39.99
```

---

### Test WebClient from order-service

```bash
curl http://localhost:8080/api/orders/webclient/101
```

Expected:

```text
Order placed using WebClient for book: Clean Code by Robert C. Martin. Price: $39.99
```

---

## 27. Code difference side by side

### OpenFeign

```java
@FeignClient(name = "book-service", url = "${book-service.url}")
public interface BookFeignClient {

    @GetMapping("/api/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);
}
```

Usage:

```java
BookDto book = bookFeignClient.getBookById(bookId);
```

### WebClient

```java
BookDto book = bookWebClient
        .get()
        .uri("/api/books/{id}", id)
        .retrieve()
        .bodyToMono(BookDto.class)
        .block();
```

---

## 28. Configuration difference side by side

| Setup | OpenFeign | WebClient |
|---|---|---|
| Dependency | `spring-cloud-starter-openfeign` | `spring-boot-starter-webflux` |
| Main annotation | `@EnableFeignClients` | No enable annotation needed |
| Client creation | `@FeignClient` interface | `WebClient` bean |
| URL config | `url = "${book-service.url}"` | `baseUrl(bookServiceUrl)` |
| Call style | Method call | Chained request |
| Example | `client.getBookById(id)` | `.get().uri(...).retrieve()` |

---

## 29. Which one should I use?

### Use OpenFeign when:

```text
You want simple service-to-service API calls.
You are building normal Spring Boot microservices.
You want less code.
You want clean interfaces.
```

Example:

```text
Order Service calls Book Service
Order Service calls Payment Service
Product Service calls Inventory Service
```

### Use WebClient when:

```text
You need non-blocking calls.
You are working with Spring WebFlux.
You need more control.
You call many slow external APIs.
You want better handling for high concurrency.
```

Example:

```text
One service calls 10 third-party APIs at the same time.
You do not want threads waiting for each API.
```

---

## 30. Industry importance

In real industry applications, backend services rarely work alone.

One service may call:

```text
Payment Service
User Service
Inventory Service
Notification Service
Shipping Service
Third-party APIs
```

So backend developers must know:

```text
How to call another API
How to pass headers
How to handle errors
How to set timeouts
How to retry failed requests
How to add circuit breakers
How to trace API failures
```

OpenFeign and WebClient are important because they are common tools for service-to-service communication.

---

## 31. Interview explanation

You can say:

> OpenFeign and WebClient are both used to call REST APIs from a Spring Boot application. OpenFeign is declarative, where we define an interface using `@FeignClient`, and Spring creates the implementation. It is simple and useful for normal synchronous microservice communication. WebClient is a fluent HTTP client from Spring WebFlux. It gives more control and supports non-blocking reactive calls. In a normal Spring Boot microservice, I would use OpenFeign for cleaner service-to-service calls. If the application needs reactive behavior, high concurrency, or more customized request handling, I would use WebClient.

---

## 32. Final simple summary

```text
OpenFeign = easy and clean
WebClient = flexible and powerful
```

For most normal Spring Boot microservice calls:

```text
Use OpenFeign.
```

For reactive or high-concurrency calls:

```text
Use WebClient.
```
