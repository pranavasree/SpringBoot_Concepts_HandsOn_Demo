<h1><span style="color:#1f2937;">Spring & Spring Boot Core Concepts — Beginner Friendly README</span></h1>

This README explains Spring and Spring Boot concepts in a simple way, with real Java/Spring Boot code examples and clear practical explanations.

> **Note:** Section headings use HTML color styling. Most Markdown preview tools show the colors, but some platforms may simplify or strip custom colors.

> Simple mental model:  
> Think of your application like a big school. You need teachers, students, classrooms, books, and rules. Without organization, everyone gets confused. Spring is like the school manager that creates the right objects, connects them, and gives them what they need.

---

## Table of Contents

1. [Spring Definition](#1-spring-definition)
2. [Spring Boot Definition](#2-spring-boot-definition)
3. [Spring Core Features](#3-spring-core-features)
4. [Spring Bean](#4-spring-bean)
5. [Spring Bean Features](#5-spring-bean-features)
6. [Spring Bean Configuration](#6-spring-bean-configuration)
7. [Spring Bean Lifecycle](#7-spring-bean-lifecycle)
8. [Important Annotations](#8-important-annotations)
9. [Spring Components](#9-spring-components)
10. [Spring Bean Name](#10-spring-bean-name)
11. [Dependency Injection](#11-dependency-injection)
12. [Injection Types](#12-injection-types)
13. [@Qualifier and @Primary](#13-qualifier-and-primary)
14. [Bean Scope](#14-bean-scope)
15. [Environment Abstraction](#15-environment-abstraction)
16. [Bean Profiles](#16-bean-profiles)
17. [ApplicationContext](#17-applicationcontext)
18. [@Value](#18-value)
19. [Spring Configuration Best Practices](#19-spring-configuration-best-practices)
20. [Spring Boot Concepts](#20-spring-boot-concepts)
21. [@Bean, @Component, @Service, @Repository in Spring Boot](#21-bean-component-service-repository-in-spring-boot)
22. [ApplicationConfig Class and @Bean Name](#22-applicationconfig-class-and-bean-name)
23. [Autowired and DI Explained With Code](#23-autowired-and-di-explained-with-code)
24. [Two Beans for the Same Class](#24-two-beans-for-the-same-class)
25. [Environment Injection Example](#25-environment-injection-example)
26. [Custom Properties Files](#26-custom-properties-files)
27. [Inject application.properties to Variables](#27-inject-applicationproperties-to-variables)
28. [Final Best Practice Summary](#28-final-best-practice-summary)

---

<h1 id="1-spring-definition"><span style="color:#2563eb;">1. Spring Definition</span></h1>

## Definition

Spring is a Java framework used to build enterprise applications. It helps developers create objects, connect them together, manage configuration, build web APIs, connect to databases, handle transactions, and more.

## Simple explanation with example

Imagine you are building a LEGO city. You need houses, cars, roads, people, and shops. Instead of creating and connecting everything manually, Spring acts like a smart helper that builds the parts and connects them correctly.

## Why it is used

Without Spring, developers must manually create many objects using `new`, manually pass dependencies, manually manage lifecycle, and manually configure many things.

Spring helps with:

- Object creation
- Dependency Injection
- Database access
- Web applications
- Security
- Transactions
- Testing
- Cleaner architecture

## Example without Spring

```java
public class OrderService {
    private PaymentService paymentService = new PaymentService();

    public void placeOrder() {
        paymentService.pay();
    }
}
```

Problem: `OrderService` is tightly connected to `PaymentService`. If you want to replace `PaymentService`, you must change the code.

## Example with Spring

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();
    }
}
```

Here, Spring gives `PaymentService` to `OrderService` automatically.

## When to use Spring

Use Spring when building:

- REST APIs
- Microservices
- Backend systems
- Enterprise Java applications
- Database-driven applications

## Drawback

Spring has many concepts, so beginners may feel it is large at first. But once you understand beans, dependency injection, and ApplicationContext, it becomes easier.

---

<h1 id="2-spring-boot-definition"><span style="color:#7c3aed;">2. Spring Boot Definition</span></h1>

## Definition

Spring Boot is built on top of Spring. It helps you create Spring applications faster with less manual configuration.

Spring Boot gives you:

- Auto-configuration
- Starters
- Embedded server
- Standalone applications
- Production-ready features
- Less XML/manual setup

## Simple explanation with example

Spring is like ingredients for cooking. Spring Boot is like a ready-made cooking kit where many ingredients are already measured and packed for you.

## Why it is used

In normal Spring, you may need to configure many things manually. In Spring Boot, if you add the right dependency, Boot guesses what you need and configures it.

Example: If you add `spring-boot-starter-web`, Spring Boot automatically configures Tomcat, Spring MVC, JSON conversion, and REST support.

## Basic Spring Boot application

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

## Explanation

`@SpringBootApplication` combines three important things:

```java
@Configuration
@EnableAutoConfiguration
@ComponentScan
```

Meaning:

- `@Configuration`: this class can define beans
- `@EnableAutoConfiguration`: Spring Boot auto-configures based on dependencies
- `@ComponentScan`: Spring scans your package and finds components like `@Service`, `@Repository`, `@Controller`, etc.

## When to use Spring Boot

Use Spring Boot for most modern Java backend applications.

## Drawback

Auto-configuration can feel like magic. If something goes wrong, you need to understand what Boot configured automatically.

---

<h1 id="3-spring-core-features"><span style="color:#0f766e;">3. Spring Core Features</span></h1>

Spring has many features, but these are the core ones you asked about:

1. IoC
2. AOP
3. DAF / Data Access Framework
4. Spring MVC / DispatcherServlet

---

## 3.1 IoC — Inversion of Control

## Definition

IoC means Spring controls object creation and dependency connection instead of you manually creating everything with `new`.

## Simple explanation

Normally you say:

> I will create the object myself.

With Spring, you say:

> Spring, please create this object and give it to me when needed.

## Without IoC

```java
public class Car {
    private Engine engine = new Engine();
}
```

`Car` creates `Engine` by itself.

## With IoC

```java
@Component
public class Engine {
}

@Component
public class Car {
    private final Engine engine;

    public Car(Engine engine) {
        this.engine = engine;
    }
}
```

Spring creates `Engine`, creates `Car`, and injects `Engine` into `Car`.

## Why it is used

- Loose coupling
- Easy testing
- Easy replacement
- Cleaner code

## Drawback

You need to understand how Spring scans and creates beans.

---

## 3.2 AOP — Aspect-Oriented Programming

## Definition

AOP helps you separate common logic like logging, security, validation, and transaction handling from business logic.

## Simple explanation

Imagine every classroom must check attendance before class starts. Instead of each teacher writing attendance code, the school has one common attendance system. That is like AOP.

## Example use case

You want to log every method call.

```java
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.demo.service.*.*(..))")
    public void logBeforeMethod() {
        System.out.println("Method is starting...");
    }
}
```

## Example service

```java
@Service
public class OrderService {
    public void placeOrder() {
        System.out.println("Order placed");
    }
}
```

When `placeOrder()` runs, the aspect can run before it automatically.

## Why it is used

- Logging
- Security
- Transactions
- Auditing
- Performance monitoring

## Drawback

If overused, it becomes hard to trace because code runs indirectly.

---

## 3.3 DAF — Data Access Framework

> In many notes, DAF means Data Access Framework or Data Access support in Spring.

## Definition

Spring Data Access helps your application talk to databases with less repeated code.

## Simple explanation

Imagine you want books from a library. Instead of every person learning the full library system, you ask the librarian. Spring Data Access is like that librarian for the database.

## JDBC without Spring

You manually:

- Open connection
- Create statement
- Execute query
- Handle exception
- Close connection

## With Spring JDBC

```java
@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String findNameById(int id) {
        return jdbcTemplate.queryForObject(
                "SELECT name FROM users WHERE id = ?",
                String.class,
                id
        );
    }
}
```

## Why it is used

- Reduces JDBC boilerplate
- Handles resource closing
- Converts SQL exceptions to Spring exceptions
- Supports transactions

## Drawback

For complex queries, you still need SQL knowledge or ORM knowledge.

---

## 3.4 Spring MVC and DispatcherServlet

## Definition

Spring MVC is Spring's web framework for building web applications and REST APIs. The `DispatcherServlet` is the front controller that receives requests and sends them to the correct controller.

## Simple explanation

Imagine a school reception desk. Every visitor first goes to reception. Reception checks the request and sends the visitor to the correct teacher or office.

`DispatcherServlet` is that reception desk.

## Request flow

```text
Client Request
      |
      v
DispatcherServlet
      |
      v
HandlerMapping finds correct controller
      |
      v
Controller method runs
      |
      v
Service layer runs business logic
      |
      v
Repository talks to database
      |
      v
Response goes back to client
```

## Example controller

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }
}
```

## Why it is used

- Build REST APIs
- Map URLs to Java methods
- Handle JSON request/response
- Separate controller, service, repository layers

## Drawback

For beginners, the request flow may feel confusing until you understand DispatcherServlet.

---

<h1 id="4-spring-bean"><span style="color:#dc2626;">4. Spring Bean</span></h1>

## Definition

A Spring Bean is an object created, managed, and controlled by the Spring container.

## Simple explanation

A bean is like a toy in a toy box. You do not create it again and again. Spring keeps it ready and gives it to whoever needs it.

## Example

```java
@Component
public class EmailService {
    public void sendEmail() {
        System.out.println("Email sent");
    }
}
```

Because of `@Component`, Spring creates an `EmailService` bean.

## Why beans are used

- Spring can create objects
- Spring can inject dependencies
- Spring can manage lifecycle
- Spring can reuse objects

## When to create a bean

Create a bean when an object is part of your application logic and needs to be reused or injected.

Examples:

- Service class
- Repository class
- Controller class
- Configuration object
- External library object

## Drawback

Do not make everything a bean. Simple DTOs/entities usually do not need to be Spring beans.

---

<h1 id="5-spring-bean-features"><span style="color:#ca8a04;">5. Spring Bean Features</span></h1>

Spring beans support:

1. Automatic creation
2. Dependency Injection
3. Bean scopes
4. Lifecycle callbacks
5. Lazy initialization
6. Naming
7. Profiles
8. Configuration properties

## Example

```java
@Component
@Scope("singleton")
public class NotificationService {

    @PostConstruct
    public void init() {
        System.out.println("NotificationService created");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("NotificationService destroyed");
    }
}
```

## Explanation

- `@Component`: create bean automatically
- `@Scope("singleton")`: only one object in container
- `@PostConstruct`: runs after bean is created
- `@PreDestroy`: runs before bean is destroyed

---

<h1 id="6-spring-bean-configuration"><span style="color:#16a34a;">6. Spring Bean Configuration</span></h1>

There are multiple ways to configure beans.

---

## 6.1 Using @Component

Use this for your own classes.

```java
@Component
public class SmsService {
    public void sendSms() {
        System.out.println("SMS sent");
    }
}
```

## When to use

Use when the class belongs to your project and Spring can scan it.

---

## 6.2 Using @Bean inside @Configuration

Use this when you want to create a bean manually, especially for third-party classes.

```java
@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

## When to use

Use `@Bean` when:

- The class comes from an external library
- You need custom object creation logic
- You need multiple versions of the same class
- You need to pass constructor values manually

---

## 6.3 XML Configuration

Old Spring projects used XML.

```xml
<bean id="emailService" class="com.example.demo.EmailService" />
```

## When to use

Mostly for legacy projects. Modern Spring Boot projects usually use annotations and Java configuration.

---

<h1 id="7-spring-bean-lifecycle"><span style="color:#db2777;">7. Spring Bean Lifecycle</span></h1>

## Definition

Bean lifecycle means the full journey of a bean from creation to destruction.

## Simple explanation

Like a student:

1. Student joins school
2. Gets ID card
3. Attends classes
4. Leaves school

A bean also has a lifecycle.

## Bean lifecycle steps

```text
1. Spring creates bean object
2. Spring injects dependencies
3. Spring runs initialization callbacks
4. Bean is ready to use
5. Application uses bean
6. Spring runs destroy callbacks when app closes
```

## Example

```java
@Component
public class ReportService {

    public ReportService() {
        System.out.println("1. Constructor called");
    }

    @PostConstruct
    public void init() {
        System.out.println("2. Bean initialized");
    }

    public void generateReport() {
        System.out.println("3. Report generated");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("4. Bean destroyed");
    }
}
```

## Why it is used

Use lifecycle callbacks when you need to:

- Open resources after bean creation
- Validate setup
- Load cache
- Close resources before shutdown

## Drawback

Do not put heavy logic in constructors. Prefer `@PostConstruct` for initialization that needs injected dependencies.

---

<h1 id="8-important-annotations"><span style="color:#0891b2;">8. Important Annotations</span></h1>

---

## 8.1 @Configuration

## Definition

`@Configuration` marks a class as a source of bean definitions.

## Example

```java
@Configuration
public class PaymentConfig {

    @Bean
    public PaymentGateway paymentGateway() {
        return new StripePaymentGateway();
    }
}
```

## Why it is used

It tells Spring:

> This class contains methods that create beans.

## When to use

Use it for application setup/configuration.

## Drawback

Do not put business logic inside configuration classes.

---

## 8.2 @Bean

## Definition

`@Bean` tells Spring that the method return value should be registered as a Spring bean.

## Example

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## Explanation

Spring calls this method and stores the returned object in ApplicationContext.

## When to use

Use for external library objects or custom object creation.

---

## 8.3 @Component

## Definition

`@Component` marks a class as a Spring-managed component.

## Example

```java
@Component
public class FileStorageClient {
}
```

## When to use

Use for generic helper classes that do not clearly fit service, repository, or controller.

---

## 8.4 Constructor Dependency Injection

## Definition

Constructor injection means dependencies are passed through the class constructor.

## Example

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

## Why it is used

It makes required dependencies clear and supports immutable fields with `final`.

## Best practice

Constructor injection is usually the best practice for required dependencies.

---

## 8.5 @Autowired

## Definition

`@Autowired` asks Spring to inject a dependency automatically.

## Constructor example

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    @Autowired
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

In modern Spring, if a class has only one constructor, `@Autowired` is optional.

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

---

<h1 id="9-spring-components"><span style="color:#9333ea;">9. Spring Components</span></h1>

Spring gives special annotations for different layers.

---

## 9.1 @Repository

## Definition

`@Repository` is used for database access classes.

## Example

```java
@Repository
public class ProductRepository {

    public String findProductById(Long id) {
        return "Laptop";
    }
}
```

## Why it is used

- Marks database layer
- Supports exception translation
- Makes code clear

## When to use

Use on classes that talk to the database.

---

## 9.2 @Service

## Definition

`@Service` is used for business logic classes.

## Example

```java
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String getProduct(Long id) {
        return productRepository.findProductById(id);
    }
}
```

## Why it is used

It clearly shows this class contains business logic.

## When to use

Use on classes that process rules, calculations, validations, or workflows.

---

## 9.3 @Controller

## Definition

`@Controller` is used for web MVC controllers that return views like HTML pages.

```java
@Controller
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
```

## 9.4 @RestController

## Definition

`@RestController` is used for REST APIs that return JSON or text directly.

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public String getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}
```

## When to use what

| Annotation | Layer | Use for |
|---|---|---|
| `@Controller` | Web | MVC pages/views |
| `@RestController` | Web/API | REST APIs/JSON |
| `@Service` | Business | Business logic |
| `@Repository` | Data | Database access |
| `@Component` | Generic | Utility/helper Spring bean |

---

<h1 id="10-spring-bean-name"><span style="color:#ea580c;">10. Spring Bean Name</span></h1>

## Definition

Every Spring bean has a name inside the Spring container.

## Default bean name

If class name is:

```java
@Component
public class EmailService {
}
```

Default bean name is:

```text
emailService
```

Spring makes the first letter lowercase.

---

## Custom bean name with @Component

```java
@Component("mailService")
public class EmailService {
}
```

Bean name becomes:

```text
mailService
```

---

## Custom bean name with @Bean

```java
@Configuration
public class AppConfig {

    @Bean("myBean")
    public EmailService emailService() {
        return new EmailService();
    }
}
```

Bean name becomes:

```text
myBean
```

## Why bean names are useful

Bean names help when multiple beans have the same type.

---

<h1 id="11-dependency-injection"><span style="color:#15803d;">11. Dependency Injection</span></h1>

## Definition

Dependency Injection means giving an object the things it needs instead of making it create them by itself.

## Simple explanation

Imagine a child needs a pencil. Bad way: child must go to factory and make a pencil. Good way: teacher gives the pencil. Dependency Injection is like the teacher giving the pencil.

---

## Without Dependency Injection

```java
public class OrderService {
    private PaymentService paymentService = new PaymentService();
}
```

Problem: `OrderService` creates `PaymentService` by itself.

---

## With Dependency Injection

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Now Spring gives `PaymentService` to `OrderService`.

## Why it is used

- Loose coupling
- Easier testing
- Cleaner code
- Easy replacement of implementations

---

<h1 id="12-injection-types"><span style="color:#1d4ed8;">12. Injection Types</span></h1>

Spring supports multiple injection types.

---

## 12.1 Constructor Injection — Best Practice

```java
@Service
public class InvoiceService {
    private final EmailService emailService;

    public InvoiceService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

## Why it is best

- Required dependencies are clear
- Allows `final` fields
- Easier unit testing
- Object cannot be created without required dependencies

## Drawback

If constructor has too many parameters, class may be doing too many responsibilities.

---

## 12.2 Field Injection — Not Best Practice

```java
@Service
public class InvoiceService {

    @Autowired
    private EmailService emailService;
}
```

## Why it is not preferred

- Harder to test
- Cannot use `final`
- Dependencies are hidden
- Requires Spring/reflection to inject fields

## When to use

Use only for quick demos or very simple learning examples. Avoid in production code.

---

## 12.3 Setter Injection

```java
@Service
public class InvoiceService {
    private EmailService emailService;

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

## When to use

Use setter injection for optional dependencies or dependencies that can change after object creation.

## Drawback

Object can be created without the dependency, so it may be incomplete.

---

## 12.4 Method Injection

```java
@Service
public class ReportService {
    private EmailService emailService;
    private AuditService auditService;

    @Autowired
    public void prepare(EmailService emailService, AuditService auditService) {
        this.emailService = emailService;
        this.auditService = auditService;
    }
}
```

## When to use

Use rarely, when you need to inject multiple dependencies through a custom method.

---

## 12.5 Configuration Method Injection

```java
@Configuration
public class AppConfig {

    @Bean
    public Engine engine() {
        return new Engine();
    }

    @Bean
    public Car car(Engine engine) {
        return new Car(engine);
    }
}
```

## Explanation

Spring sees `car(Engine engine)` and injects the `Engine` bean into the `car` bean creation method.

## When to use

Use when creating beans manually in configuration classes.

---

<h1 id="13-qualifier-and-primary"><span style="color:#be123c;">13. @Qualifier and @Primary</span></h1>

Sometimes Spring finds more than one bean of the same type.

## Example problem

```java
public interface PaymentService {
    void pay();
}
```

```java
@Service
public class StripePaymentService implements PaymentService {
    public void pay() {
        System.out.println("Paid using Stripe");
    }
}
```

```java
@Service
public class PaypalPaymentService implements PaymentService {
    public void pay() {
        System.out.println("Paid using PayPal");
    }
}
```

Now this fails:

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

Why? Spring asks:

```text
I found StripePaymentService and PaypalPaymentService.
Which one should I inject?
```

---

## 13.1 Using @Primary

Use `@Primary` when one bean should be the default choice.

```java
@Service
@Primary
public class StripePaymentService implements PaymentService {
    public void pay() {
        System.out.println("Paid using Stripe");
    }
}
```

```java
@Service
public class PaypalPaymentService implements PaymentService {
    public void pay() {
        System.out.println("Paid using PayPal");
    }
}
```

Now Spring injects Stripe by default.

## When to use @Primary

Use when you have one common/default implementation.

## Drawback

It may hide which bean is being used if the project grows.

---

## 13.2 Using @Qualifier

Use `@Qualifier` when you want to choose a specific bean at a specific injection point.

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(@Qualifier("paypalPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

## When to use @Qualifier

Use when different classes need different implementations.

## Drawback

You must know the exact bean name or qualifier name.

---

## @Primary vs @Qualifier

| Situation | Use |
|---|---|
| One default bean should be used mostly everywhere | `@Primary` |
| Specific class needs a specific bean | `@Qualifier` |
| Many implementations and each injection point should be clear | `@Qualifier` |

---

<h1 id="14-bean-scope"><span style="color:#047857;">14. Bean Scope</span></h1>

## Definition

Bean scope controls how many objects Spring creates and how long they live.

## Simple explanation

Some toys are shared by everyone. Some toys are new for every kid. Bean scope decides that.

---

## Types of scopes

| Scope | Meaning | Use case |
|---|---|---|
| `singleton` | One object per Spring container | Most services/repositories/controllers |
| `prototype` | New object every time requested | Stateful temporary objects |
| `request` | One object per HTTP request | Web request data |
| `session` | One object per HTTP session | User session data |
| `application` | One object per ServletContext | App-wide web object |
| `websocket` | One object per WebSocket session | WebSocket apps |

---

## Singleton example

```java
@Component
@Scope("singleton")
public class AppLogger {
}
```

This is the default.

---

## Prototype example

```java
@Component
@Scope("prototype")
public class TaskProcessor {
}
```

Spring creates a new object every time it is requested.

## When to use what

- Use `singleton` for stateless services
- Use `prototype` for objects that hold temporary changing state
- Use `request` for per-request data
- Use `session` for per-user session data

## Drawback

Be careful with mutable state in singleton beans. If many users use the same singleton object, shared variables can cause bugs.

---

<h1 id="15-environment-abstraction"><span style="color:#a16207;">15. Environment Abstraction</span></h1>

## Definition

`Environment` is a Spring abstraction used to read properties and active profiles.

## Simple explanation

Environment is like a settings notebook. Your app can ask:

> What is the app name? Which profile is active? What is the database URL?

## Example application.properties

```properties
app.name=Spring Learning App
app.version=1.0
```

## Inject Environment

```java
@Service
public class AppInfoService {

    private final Environment environment;

    public AppInfoService(Environment environment) {
        this.environment = environment;
    }

    public void printInfo() {
        String appName = environment.getProperty("app.name");
        String version = environment.getProperty("app.version");

        System.out.println("App Name: " + appName);
        System.out.println("Version: " + version);
    }
}
```

## When to use Environment

Use `Environment` when:

- You need to read properties dynamically
- You need active profiles
- You need optional properties
- You are writing framework/helper/config logic

## Drawback

Using `environment.getProperty("some.key")` everywhere can create string-based code that is harder to maintain. For many related properties, prefer `@ConfigurationProperties`.

---

<h1 id="16-bean-profiles"><span style="color:#6d28d9;">16. Bean Profiles</span></h1>

## Definition

Profiles allow you to create/use different beans or settings in different environments.

Examples:

- `dev`
- `test`
- `prod`

## Simple explanation

At home, you wear casual clothes. At school, you wear a uniform. Same person, different environment. Profiles do that for your app.

---

## Example

```java
public interface DatabaseClient {
    void connect();
}
```

```java
@Component
@Profile("dev")
public class DevDatabaseClient implements DatabaseClient {
    public void connect() {
        System.out.println("Connected to DEV database");
    }
}
```

```java
@Component
@Profile("prod")
public class ProdDatabaseClient implements DatabaseClient {
    public void connect() {
        System.out.println("Connected to PROD database");
    }
}
```

## application.properties

```properties
spring.profiles.active=dev
```

Now Spring only creates the `dev` bean.

## When to use profiles

Use profiles for:

- Dev database vs production database
- Mock service vs real service
- Local config vs cloud config

## Drawback

Too many profiles can make the application hard to understand.

---

<h1 id="17-applicationcontext"><span style="color:#0369a1;">17. ApplicationContext</span></h1>

## Definition

`ApplicationContext` is the Spring container. It stores and manages beans.

## Simple explanation

ApplicationContext is like the school office. It knows all teachers, students, classrooms, and who needs what.

## What ApplicationContext does

- Creates beans
- Stores beans
- Injects dependencies
- Reads properties
- Handles profiles
- Publishes events
- Supports internationalization
- Manages lifecycle

## Example

```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        OrderService orderService = context.getBean(OrderService.class);
        orderService.placeOrder();
    }
}
```

## Why it is used

Without ApplicationContext, Spring cannot manage your beans and dependencies.

## What happens if ApplicationContext is not there?

Then you must manually do everything:

```java
PaymentService paymentService = new StripePaymentService();
OrderService orderService = new OrderService(paymentService);
```

For a small app, this is okay. For a large app, it becomes difficult because there may be hundreds of classes.

## Drawback

Do not overuse `context.getBean()` inside business code. Prefer dependency injection.

Bad:

```java
@Service
public class OrderService {
    @Autowired
    private ApplicationContext context;

    public void placeOrder() {
        PaymentService paymentService = context.getBean(PaymentService.class);
        paymentService.pay();
    }
}
```

Good:

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

---

<h1 id="18-value"><span style="color:#c2410c;">18. @Value</span></h1>

## Definition

`@Value` injects a property value into a field, constructor parameter, or method parameter.

## application.properties

```properties
app.title=Spring Boot Core Learning
app.max-users=100
```

## Field injection example

```java
@Component
public class AppSettings {

    @Value("${app.title}")
    private String title;

    @Value("${app.max-users}")
    private int maxUsers;

    public void printSettings() {
        System.out.println(title);
        System.out.println(maxUsers);
    }
}
```

## Constructor injection with @Value

```java
@Component
public class AppSettings {
    private final String title;
    private final int maxUsers;

    public AppSettings(
            @Value("${app.title}") String title,
            @Value("${app.max-users}") int maxUsers
    ) {
        this.title = title;
        this.maxUsers = maxUsers;
    }
}
```

## Default value

```java
@Value("${app.description:Default description}")
private String description;
```

If `app.description` is missing, Spring uses `Default description`.

## When to use @Value

Use `@Value` for one or two simple property values.

## Drawback

For many related properties, `@Value` becomes messy. Use `@ConfigurationProperties` instead.

---

<h1 id="19-spring-configuration-best-practices"><span style="color:#4d7c0f;">19. Spring Configuration Best Practices</span></h1>

## Goal

Keep configuration clean and split by responsibility.

## Bad configuration

```java
@Configuration
public class AppConfig {
    // database beans
    // security beans
    // web beans
    // payment beans
    // email beans
}
```

This becomes too large.

---

## Good configuration split

```text
config/
  DatabaseConfig.java
  SecurityConfig.java
  WebConfig.java
  PaymentConfig.java
  EmailConfig.java
```

## Example

```java
@Configuration
public class PaymentConfig {

    @Bean
    public PaymentGateway paymentGateway() {
        return new StripePaymentGateway();
    }
}
```

```java
@Configuration
public class EmailConfig {

    @Bean
    public EmailClient emailClient() {
        return new SendGridEmailClient();
    }
}
```

## Best practices

- Keep config classes small
- Group related beans together
- Use constructor injection
- Avoid field injection
- Avoid business logic in config classes
- Use meaningful bean names
- Use `@ConfigurationProperties` for grouped properties
- Use profiles carefully
- Prefer auto-configuration unless you need custom behavior

---

<h1 id="20-spring-boot-concepts"><span style="color:#0e7490;">20. Spring Boot Concepts</span></h1>

## What is Spring Boot?

Spring Boot is a Spring-based framework that helps create standalone, production-ready apps quickly.

---

## 20.1 Starters

## Definition

Starters are dependency bundles.

## Example Maven dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

This brings common dependencies needed for web APIs.

## Simple explanation

Instead of buying flour, sugar, chocolate, butter separately, you buy a cake mix. Starter is like cake mix.

## Common starters

| Starter | Use |
|---|---|
| `spring-boot-starter-web` | REST APIs / Spring MVC |
| `spring-boot-starter-data-jpa` | Database with JPA/Hibernate |
| `spring-boot-starter-security` | Security/authentication |
| `spring-boot-starter-test` | Testing |
| `spring-boot-starter-actuator` | Monitoring/health/metrics |

---

## 20.2 Auto-Configuration

## Definition

Auto-configuration means Spring Boot automatically configures your app based on dependencies in your classpath.

## Example

If you add:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Spring Boot configures:

- Embedded Tomcat
- Spring MVC
- JSON conversion
- DispatcherServlet
- Error handling

## Drawback

Sometimes Boot configures something you did not expect. You can use debug logs to see what auto-configuration happened.

---

## 20.3 Embedded Server

## Definition

Spring Boot includes an embedded server like Tomcat, Jetty, or Undertow.

## Simple explanation

You do not need to install a separate server. The server comes inside your app.

## Run app

```bash
mvn spring-boot:run
```

or

```bash
java -jar target/demo.jar
```

---

## 20.4 Production Ready Features

Spring Boot Actuator gives endpoints for:

- Health checks
- Metrics
- Info
- Monitoring

## Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## Example endpoint

```text
GET /actuator/health
```

---

## Why Spring Boot?

Use Spring Boot because it gives:

- Standalone apps
- Embedded server
- Starters
- Auto-configuration
- Production-ready features
- No XML configuration required for most apps
- Faster development

---

<h1 id="21-bean-component-service-repository-in-spring-boot"><span style="color:#b91c1c;">21. @Bean, @Component, @Service, @Repository in Spring Boot</span></h1>

## Example mini project structure

```text
src/main/java/com/example/demo/
  DemoApplication.java
  config/ApplicationConfig.java
  controller/OrderController.java
  service/OrderService.java
  service/PaymentService.java
  service/StripePaymentService.java
  repository/OrderRepository.java
  component/OrderMapper.java
```

---

## @Repository example

```java
@Repository
public class OrderRepository {

    public String findOrderById(Long id) {
        return "Order ID: " + id;
    }
}
```

Why used: database layer.

---

## @Service example

```java
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String getOrder(Long id) {
        return orderRepository.findOrderById(id);
    }
}
```

Why used: business logic layer.

---

## @RestController example

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }
}
```

Why used: API layer.

---

## @Component example

```java
@Component
public class OrderMapper {

    public String toResponse(String order) {
        return "Response: " + order;
    }
}
```

Why used: generic helper bean.

---

## @Bean example

```java
@Configuration
public class ApplicationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

Why used: manually create third-party library object.

---

<h1 id="22-applicationconfig-class-and-bean-name"><span style="color:#4338ca;">22. ApplicationConfig Class and Bean Name</span></h1>

## ApplicationConfig class

```java
@Configuration
public class ApplicationConfig {

    @Bean("myBean")
    public GreetingService greetingService() {
        return new GreetingService("Hello from myBean");
    }
}
```

## GreetingService

```java
public class GreetingService {
    private final String message;

    public GreetingService(String message) {
        this.message = message;
    }

    public String greet() {
        return message;
    }
}
```

## Use bean by name

```java
@Service
public class WelcomeService {

    private final GreetingService greetingService;

    public WelcomeService(@Qualifier("myBean") GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public void sayWelcome() {
        System.out.println(greetingService.greet());
    }
}
```

## Why use custom bean name?

Use a custom name when:

- Multiple beans have same class/interface
- You want clearer names
- You need to choose one bean using `@Qualifier`

---

<h1 id="23-autowired-and-di-explained-with-code"><span style="color:#a21caf;">23. Autowired and DI Explained With Code</span></h1>

## Concept first

Dependency Injection is not just a definition. It means one class does not create its own helper objects. Instead, Spring provides the needed object.

Imagine this:

```text
OrderService needs PaymentService.
OrderService should not build PaymentService.
Spring builds PaymentService and gives it to OrderService.
```

---

## Best practice: constructor injection

```java
public interface PaymentService {
    void pay();
}
```

```java
@Service
public class CardPaymentService implements PaymentService {
    public void pay() {
        System.out.println("Paid using card");
    }
}
```

```java
@Service
public class OrderService {
    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder() {
        paymentService.pay();
        System.out.println("Order placed");
    }
}
```

## Explanation

Spring sees:

```java
public OrderService(PaymentService paymentService)
```

Then Spring searches for a bean that implements `PaymentService`. It finds `CardPaymentService`, creates it, and injects it.

## Why constructor injection is best

- Dependency is required
- Field can be `final`
- Easy to test
- No hidden dependency

---

## Field injection using @Autowired

```java
@Service
public class OrderService {

    @Autowired
    private PaymentService paymentService;

    public void placeOrder() {
        paymentService.pay();
    }
}
```

## Why field injection is not best practice

It works, but it hides dependencies. You cannot easily create `OrderService` in a unit test without Spring.

---

## Setter injection

```java
@Service
public class OrderService {
    private PaymentService paymentService;

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
```

## When setter injection is useful

Use setter injection when dependency is optional.

---

<h1 id="24-two-beans-for-the-same-class"><span style="color:#166534;">24. Two Beans for the Same Class</span></h1>

## Problem

What happens if we have two beans for the same class in `ApplicationConfig`?

## Example

```java
public class MessageService {
    private final String message;

    public MessageService(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

```java
@Configuration
public class ApplicationConfig {

    @Bean("morningMessage")
    public MessageService morningMessage() {
        return new MessageService("Good morning");
    }

    @Bean("eveningMessage")
    public MessageService eveningMessage() {
        return new MessageService("Good evening");
    }
}
```

Now this will fail:

```java
@Service
public class GreetingPrinter {
    private final MessageService messageService;

    public GreetingPrinter(MessageService messageService) {
        this.messageService = messageService;
    }
}
```

Spring error idea:

```text
Expected single matching bean but found 2: morningMessage, eveningMessage
```

---

## Solution 1: Use @Qualifier

```java
@Service
public class GreetingPrinter {
    private final MessageService messageService;

    public GreetingPrinter(@Qualifier("morningMessage") MessageService messageService) {
        this.messageService = messageService;
    }

    public void print() {
        System.out.println(messageService.getMessage());
    }
}
```

Use `@Qualifier` when this specific class needs a specific bean.

---

## Solution 2: Use @Primary

```java
@Configuration
public class ApplicationConfig {

    @Bean("morningMessage")
    @Primary
    public MessageService morningMessage() {
        return new MessageService("Good morning");
    }

    @Bean("eveningMessage")
    public MessageService eveningMessage() {
        return new MessageService("Good evening");
    }
}
```

Now Spring chooses `morningMessage` by default.

Use `@Primary` when one bean is the default bean.

---

<h1 id="25-environment-injection-example"><span style="color:#b45309;">25. Environment Injection Example</span></h1>

## application.properties

```properties
app.name=Order Management App
app.currency=USD
app.tax-rate=8.5
```

## Service using Environment

```java
@Service
public class PricingService {

    private final Environment environment;

    public PricingService(Environment environment) {
        this.environment = environment;
    }

    public double calculateFinalPrice(double price) {
        double taxRate = environment.getProperty("app.tax-rate", Double.class, 0.0);
        return price + (price * taxRate / 100);
    }

    public void printAppName() {
        String appName = environment.getProperty("app.name", "Default App");
        System.out.println(appName);
    }
}
```

## Situation where we use Environment

Use it when the property name is dynamic or when you need profile/environment information.

Example:

```java
public boolean isProd() {
    return Arrays.asList(environment.getActiveProfiles()).contains("prod");
}
```

## When not to use

Do not use `Environment` everywhere for normal strongly typed config. Use `@ConfigurationProperties` for grouped settings.

---

<h1 id="26-custom-properties-files"><span style="color:#0284c7;">26. Custom Properties Files</span></h1>

You asked: How to get custom properties data other than `application.properties`? What if multiple custom files exist?

---

## 26.1 One custom properties file

Create file:

```text
src/main/resources/payment.properties
```

```properties
payment.provider=stripe
payment.currency=USD
payment.timeout-seconds=30
```

## Load it using @PropertySource

```java
@Configuration
@PropertySource("classpath:payment.properties")
public class PaymentPropertiesConfig {
}
```

## Read with @Value

```java
@Service
public class PaymentInfoService {

    @Value("${payment.provider}")
    private String provider;

    @Value("${payment.currency}")
    private String currency;

    public void printPaymentInfo() {
        System.out.println(provider + " - " + currency);
    }
}
```

---

## 26.2 Multiple custom properties files

Files:

```text
src/main/resources/payment.properties
src/main/resources/email.properties
```

`payment.properties`

```properties
payment.provider=stripe
payment.currency=USD
```

`email.properties`

```properties
email.from=noreply@example.com
email.support=support@example.com
```

## Load multiple files

```java
@Configuration
@PropertySource("classpath:payment.properties")
@PropertySource("classpath:email.properties")
public class CustomPropertiesConfig {
}
```

## Read values

```java
@Service
public class NotificationService {

    @Value("${email.from}")
    private String fromEmail;

    @Value("${payment.provider}")
    private String paymentProvider;

    public void printInfo() {
        System.out.println("From: " + fromEmail);
        System.out.println("Payment Provider: " + paymentProvider);
    }
}
```

---

## 26.3 Better way: @ConfigurationProperties

For many related properties, prefer `@ConfigurationProperties`.

## application.properties

```properties
payment.provider=stripe
payment.currency=USD
payment.timeout-seconds=30
```

## Properties class

```java
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {
    private String provider;
    private String currency;
    private int timeoutSeconds;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
```

## Use it

```java
@Service
public class PaymentService {
    private final PaymentProperties paymentProperties;

    public PaymentService(PaymentProperties paymentProperties) {
        this.paymentProperties = paymentProperties;
    }

    public void printPaymentConfig() {
        System.out.println(paymentProperties.getProvider());
        System.out.println(paymentProperties.getCurrency());
        System.out.println(paymentProperties.getTimeoutSeconds());
    }
}
```

## Add dependency for metadata support

Optional but useful:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 26.4 Spring Boot config import style

In modern Spring Boot, you can also import custom files from `application.properties`.

```properties
spring.config.import=classpath:payment.properties,classpath:email.properties
```

Then you can read properties normally using `@Value`, `Environment`, or `@ConfigurationProperties`.

## When to use what

| Approach | Use when |
|---|---|
| `@Value` | One or two simple values |
| `Environment` | Dynamic property access/profile checks |
| `@PropertySource` | Loading custom property files in config class |
| `spring.config.import` | Importing extra config files in Spring Boot |
| `@ConfigurationProperties` | Many related config values |

---

<h1 id="27-inject-applicationproperties-to-variables"><span style="color:#be185d;">27. Inject application.properties to Variables</span></h1>

## application.properties

```properties
app.name=Learning Spring Boot
app.owner=Nithin
app.max-login-attempts=5
```

---

## Method 1: @Value field injection

```java
@Component
public class AppDetails {

    @Value("${app.name}")
    private String appName;

    @Value("${app.owner}")
    private String owner;

    @Value("${app.max-login-attempts}")
    private int maxLoginAttempts;

    public void print() {
        System.out.println(appName);
        System.out.println(owner);
        System.out.println(maxLoginAttempts);
    }
}
```

---

## Method 2: @Value constructor injection

```java
@Component
public class AppDetails {
    private final String appName;
    private final String owner;

    public AppDetails(
            @Value("${app.name}") String appName,
            @Value("${app.owner}") String owner
    ) {
        this.appName = appName;
        this.owner = owner;
    }
}
```

---

## Method 3: Environment

```java
@Component
public class AppDetails {
    private final Environment environment;

    public AppDetails(Environment environment) {
        this.environment = environment;
    }

    public void print() {
        System.out.println(environment.getProperty("app.name"));
        System.out.println(environment.getProperty("app.owner"));
    }
}
```

---

## Method 4: @ConfigurationProperties — best for grouped properties

```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String owner;
    private int maxLoginAttempts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getMaxLoginAttempts() {
        return maxLoginAttempts;
    }

    public void setMaxLoginAttempts(int maxLoginAttempts) {
        this.maxLoginAttempts = maxLoginAttempts;
    }
}
```

Use it:

```java
@Service
public class AppService {
    private final AppProperties appProperties;

    public AppService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public void print() {
        System.out.println(appProperties.getName());
        System.out.println(appProperties.getOwner());
    }
}
```

---

<h1 id="28-final-best-practice-summary"><span style="color:#334155;">28. Final Best Practice Summary</span></h1>

## What to use when

| Need | Best choice |
|---|---|
| Create your own service class | `@Service` |
| Create database access class | `@Repository` |
| Create REST API class | `@RestController` |
| Create generic helper bean | `@Component` |
| Create external library bean | `@Bean` inside `@Configuration` |
| Required dependency | Constructor injection |
| Optional dependency | Setter injection |
| Multiple beans, one default | `@Primary` |
| Multiple beans, choose exact one | `@Qualifier` |
| One or two config values | `@Value` |
| Many config values | `@ConfigurationProperties` |
| Dynamic config access | `Environment` |
| Different env behavior | `@Profile` |
| Most service beans | `singleton` scope |
| Temporary stateful beans | `prototype` scope |

---

## Recommended Spring Boot project layering

```text
controller  -> receives request
service     -> business logic
repository  -> database logic
config      -> bean/config setup
component   -> reusable helper components
```

Example flow:

```text
User calls API
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

---

## Clean complete example

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }
}
```

```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    public String getOrder(Long id) {
        paymentService.checkPayment();
        return orderRepository.findOrderById(id);
    }
}
```

```java
@Repository
public class OrderRepository {
    public String findOrderById(Long id) {
        return "Order found: " + id;
    }
}
```

```java
public interface PaymentService {
    void checkPayment();
}
```

```java
@Service
@Primary
public class CardPaymentService implements PaymentService {
    public void checkPayment() {
        System.out.println("Card payment checked");
    }
}
```

```java
@Service
public class PaypalPaymentService implements PaymentService {
    public void checkPayment() {
        System.out.println("PayPal payment checked");
    }
}
```

---

## Beginner memory trick

| Concept | Simple meaning |
|---|---|
| Bean | Object managed by Spring |
| IoC | Spring creates objects for you |
| DI | Spring gives objects what they need |
| ApplicationContext | Spring's bean container |
| @Component | General Spring bean |
| @Service | Business logic bean |
| @Repository | Database bean |
| @Controller | Web page controller |
| @RestController | REST API controller |
| @Bean | Manual bean creation method |
| @Configuration | Class containing bean methods |
| @Autowired | Ask Spring to inject dependency |
| @Qualifier | Choose specific bean |
| @Primary | Default bean choice |
| @Profile | Bean active only in selected environment |
| @Value | Inject property value |
| Environment | Read properties/profiles programmatically |

---

## Official References

- Spring Framework Core Technologies: https://docs.spring.io/spring-framework/reference/core.html
- Spring Bean Scopes: https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html
- Spring Autowiring: https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/autowired.html
- Spring Component Scanning: https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html
- Spring MVC DispatcherServlet: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html
- Spring Data Access: https://docs.spring.io/spring-framework/reference/data-access.html
- Spring Boot Overview: https://docs.spring.io/spring-boot/index.html
- Spring Boot Auto-configuration: https://docs.spring.io/spring-boot/reference/using/auto-configuration.html
- Spring Boot Actuator / Production Ready Features: https://docs.spring.io/spring-boot/reference/actuator/index.html

---

## Final Advice

If you remember only one thing:

```text
Spring creates objects, stores them as beans, connects them using dependency injection, and Spring Boot makes all of that faster with auto-configuration and starters.
```
