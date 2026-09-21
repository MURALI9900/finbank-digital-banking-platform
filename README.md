# FinBank — Digital Banking & Financial Services Platform

FinBank is a production-style digital banking application designed to demonstrate modern Java full-stack and enterprise application development.

## Project Overview

The platform supports two primary user roles:

- Customer
- Bank Officer

Customers can manage accounts, perform banking transactions, manage beneficiaries, view statements, apply for loans, raise service requests, and receive notifications.

Bank officers can onboard customers, manage KYC verification, review transactions, process service requests, review loan applications, and access operational reports.

## Key Features

### Customer Banking

- Customer registration and login
- JWT-based authentication
- Customer dashboard
- Savings and current accounts
- Account balance and account details
- Deposits and withdrawals
- Fund transfers
- Beneficiary management
- Transaction history
- Account statements
- Scheduled transfers
- Bill payments
- Notifications
- Profile and KYC information
- Card management
- Service requests
- Loan applications
- Loan repayment tracking

### Bank Officer Operations

- Secure officer login
- Customer search and profile management
- Customer onboarding
- KYC verification
- Account opening and closure
- Transaction assistance
- Transaction review and approval
- Beneficiary approval
- Loan application review
- Loan approval workflow
- Loan repayment monitoring
- Service request management
- Customer complaint management
- Operational reports
- Audit history

## Enterprise Features

- Role-based access control
- JWT authentication
- RESTful APIs
- DTO-based API design
- Global exception handling
- Request validation
- Pagination and sorting
- Transaction reference generation
- Idempotent transaction processing
- Optimistic locking
- Audit logging
- Kafka-based event processing
- Notification service
- API Gateway
- Microservice architecture
- Database migrations
- Swagger/OpenAPI documentation
- Unit and integration testing
- Docker containerization
- Docker Compose
- GitHub Actions CI/CD

## Technology Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- REST APIs
- Maven

### Frontend

- Angular
- TypeScript
- HTML5
- CSS3
- Bootstrap

### Database

- PostgreSQL

### Messaging and Infrastructure

- Apache Kafka
- Docker
- Docker Compose
- GitHub Actions

### Development and Documentation

- Swagger/OpenAPI
- JUnit
- Mockito
- Git
- GitHub

## High-Level Architecture

```
                         Angular
                            |
                       API Gateway
                            |
          +-----------------+-----------------+
          |                 |                 |
     Customer Service  Account Service  Transaction Service
          |                 |                 |
          +-----------------+-----------------+
                            |
                       PostgreSQL
                            |
                          Kafka
                            |
                 +----------+----------+
                 |                     |
          Notification Service    Audit Service

                 Officer / Admin Services
                            |
                  +---------+---------+
                  |                   |
             KYC Service        Loan Service
                  |
             Support Service
```

## Running the Platform

### Docker Compose

```bash
docker compose up --build
```

- Frontend: http://localhost:4200
- API Gateway: http://localhost:8080
- Kafka UI: http://localhost:8099
- PostgreSQL: localhost:5432

The Angular frontend uses the gateway through `/api/v1`. In Docker, Nginx proxies `/api/` to the API Gateway. For local Angular development, run `npm install` and `npm start` from `frontend/`; the Angular proxy forwards `/api` to `http://localhost:8080`.

### API Documentation

Each backend service exposes Swagger UI at `/swagger-ui/index.html` on its service port.

## Project Goals

The project demonstrates how a real-world financial application can be designed using secure APIs, modular services, transactional processing, event-driven communication, database persistence, automated testing, and containerized deployment.

This is a fictional learning and portfolio project. It does not contain proprietary code, credentials, customer information, or business logic from any financial institution.

## Author

Muralidhar R

GitHub: https://github.com/MURALI9900
