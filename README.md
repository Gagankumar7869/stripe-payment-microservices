# Stripe Payment Microservices

A distributed backend payment processing system built with **Java 17, Spring Boot, Spring Cloud, and Stripe API**. This monorepo contains three microservices designed to handle service discovery, payment request validation, and transaction processing through Stripe.

## System Architecture

The project consists of three core services communicating within a Spring Cloud ecosystem.

### 1. Eureka Server

* Acts as the central service registry for the microservices ecosystem.
* Allows microservices to register themselves at startup.
* Enables dynamic service discovery between services.
* Supports client-side load balancing through Spring Cloud LoadBalancer.

### 2. Payment Validation Service

* Acts as the entry point for incoming payment requests.
* Uses Spring Boot Validation to enforce request payload constraints.
* Performs field-level and business-level validation.
* Checks MySQL records for duplicate transactions and invalid payment states.
* Routes valid payment requests to the Stripe Provider Service.
* Handles validation failures and returns appropriate error responses.

### 3. Stripe Provider Service

* Handles communication with the external Stripe API.
* Creates Stripe payment requests and manages payment processing flows.
* Integrates with Stripe Checkout for payment sessions.
* Processes asynchronous Stripe webhook events.
* Updates transaction statuses based on payment events.
* Handles API errors and payment failures.

## Architecture Flow

```text
                Client Application
                       |
                       v
             Payment Validation Service
                       |
              Field & Business Validation
                       |
                 +-----+-----+
                 |           |
              Invalid       Valid
                 |           |
                 v           v
          Error Response   Stripe Provider Service
                                |
                                v
                            Stripe API
                                |
                                v
                         Payment Processing
                                |
                                v
                          Stripe Webhooks
                                |
                                v
                       Transaction Status Update
                                |
                                v
                           MySQL Database
```

**Service Discovery:** The Eureka Server maintains the service registry, allowing registered services to discover each other dynamically.

## Key Design Patterns and Resilience

### Circuit Breaker

* Implemented using Resilience4j for configured inter-service and external API calls.
* Helps prevent repeated calls to an unavailable downstream service.
* Supports fallback handling where configured.
* Reduces the risk of cascading failures during downstream outages.

### Strategy Pattern

* Helps separate payment processing logic into independent strategies.
* Makes the system easier to extend when introducing additional payment methods or processing flows.
* Reduces coupling between payment orchestration and individual processing implementations.

### Client-Side Load Balancing

* Uses Spring Cloud LoadBalancer for service-to-service communication.
* Distributes requests across available service instances when configured.
* Supports scalable deployments with multiple instances of a service.

### Webhook-Based Status Updates

* Uses Stripe webhooks to receive asynchronous payment events.
* Allows the application to update transaction statuses after Stripe reports an event.
* Helps maintain transaction status independently of the initial payment request.

## Tech Stack

| Technology                  | Purpose                                    |
| --------------------------- | ------------------------------------------ |
| Java 17                     | Backend programming language               |
| Spring Boot                 | Microservice development                   |
| Spring Cloud Netflix Eureka | Service discovery                          |
| Spring Cloud LoadBalancer   | Client-side load balancing                 |
| Resilience4j                | Circuit breaker and fault tolerance        |
| Spring Boot Validation      | Request validation                         |
| Spring JDBC.                | Database persistence                       |
| MySQL                       | Transaction data storage                   |
| Stripe API                  | Payment processing integration             |
| Stripe Webhooks             | Asynchronous payment event handling        |
| Maven                       | Dependency management and build automation |
| Git and GitHub              | Version control and source code hosting    |
| Lombok                      | Boilerplate code reduction.                |
| Flyway                      | Database migrations                        |
## Project Structure

```text
stripe-payment-microservices/
|
|-- eureka-server/
|   |-- src/
|   |   |-- main/
|   |       |-- java/
|   |       |-- resources/
|   |-- pom.xml
|
|-- payment-validation-service/
|   |-- src/
|   |   |-- main/
|   |       |-- java/
|   |       |-- resources/
|   |           |-- application.properties
|   |           |-- db/migration/
|   |-- pom.xml
|
|-- stripe-provider-service/
|   |-- src/
|   |   |-- main/
|   |       |-- java/
|   |       |-- resources/
|   |           |-- application.properties
|   |-- pom.xml
|
|-- README.md
```


## Prerequisites

Before running the project, ensure you have installed:

* Java Development Kit (JDK) 17 or higher
* Apache Maven
* MySQL Server
* Git
* A Stripe account with access to test API keys

## Local Setup Instructions

### 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd stripe-payment-microservices
```

Replace `<YOUR_GITHUB_REPOSITORY_URL>` with your repository's Git URL.

### 2. Database Configuration

Start your local MySQL server and create a database for the Payment Validation Service.

```sql
CREATE DATABASE payment_db;
```

Configure the database connection in the `application.properties` file inside `payment-validation-service/src/main/resources/`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/payment_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Set `DB_USERNAME` and `DB_PASSWORD` in your local environment before starting the service. Alternatively, configure your local credentials in an untracked development configuration file.

Ensure that the MySQL driver and the required database dependencies are configured in the service's `pom.xml`.

### 3. Stripe Configuration

Navigate to the configuration files in `stripe-provider-service`.

Configure your Stripe test secret key using an environment variable:

```properties
stripe.api.key=${STRIPE_API_KEY}
```

Set the environment variable to your Stripe **test-mode secret key** before running the service.

**Security note:** Never commit Stripe secret keys, database passwords, or other credentials to GitHub. Keep them in environment variables or an untracked local configuration file. Use test-mode credentials during development.

### 4. Run the Services

Start the services in the following order. Open three separate terminal tabs from the project root directory.

**Terminal 1 — Eureka Server**

```bash
cd eureka-server
mvn spring-boot:run
```

The Eureka dashboard is typically available at:

http://localhost:8761

**Terminal 2 — Payment Validation Service**

```bash
cd payment-validation-service
mvn spring-boot:run
```

**Terminal 3 — Stripe Provider Service**

```bash
cd stripe-provider-service
mvn spring-boot:run
```

Wait for the Eureka Server to start before launching the other services. Ensure that each service's configured port is available and that the service instances register successfully with Eureka.

## Payment Processing Workflow

1. The client sends a payment request to the Payment Validation Service.
2. The service validates the request fields and applicable business rules.
3. Invalid requests are rejected with an appropriate error response.
4. Valid requests are forwarded to the Stripe Provider Service.
5. The provider communicates with Stripe to initiate the configured payment flow.
6. Stripe processes the payment according to the selected flow.
7. Stripe sends webhook events to the configured webhook endpoint.
8. The provider handles the events and updates the relevant transaction status in the database, where persistence is configured.
9. The client can retrieve transaction details through the available APIs.

## Testing

The following areas can be tested during development:

* Field-level validation for missing or invalid request values.
* Business validation for duplicate or invalid transaction states.
* Successful and unsuccessful payment requests using Stripe test mode.
* Stripe API error handling.
* Webhook event handling and transaction status updates.
* Circuit breaker behavior during simulated downstream failures.
* Service registration and discovery through Eureka.
* Database persistence and transaction status retrieval.

Use Stripe's test environment and test payment methods. Do not use real payment credentials for local development.

## Future Enhancements

* Add authentication and authorization for payment endpoints.
* Improve idempotency handling to prevent duplicate payment operations.
* Add structured logging and distributed tracing.
* Introduce centralized configuration management.
* Add automated unit and integration tests.
* Containerize the services with Docker and Docker Compose.
* Implement monitoring and health checks.
* Improve webhook signature verification and event deduplication.

## Learning Outcomes

This project demonstrates practical experience with:

* Java and Spring Boot application development.
* Microservices architecture and service discovery.
* REST API development and request validation.
* Inter-service communication.
* Third-party payment gateway integration.
* Asynchronous event processing with webhooks.
* Database integration with MySQL.
* Resilience patterns and error handling.
* Maven-based project management.

## Author

**Gagan Kumar Pradhan**

GitHub: https://github.com/Gagankumar7869

LinkedIn: https://www.linkedin.com/in/gagan-kumar-pradhan-0225a1212

---

*This project is intended for learning and development. Verify service configurations, payment flows, and security settings before deploying to a production environment.*
