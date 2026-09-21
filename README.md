# FinBank — Digital Banking & Financial Services Platform

FinBank is a production-style digital banking application built to demonstrate modern Java full-stack, microservice, security, and enterprise application development.

## Project Overview

The platform provides customer banking capabilities and backend services for bank operations. The current implementation includes:

- Customer registration and JWT login
- Customer profile access
- Account management and balances
- Deposits, withdrawals, and fund transfers
- Transaction history and idempotent transaction processing
- Beneficiary management
- Loan applications and loan repayment APIs
- KYC applications and document management
- Customer support/service tickets
- Officer review workflows
- Notification and audit backend services
- API Gateway with role-based access control

The customer-facing Angular application currently covers authentication, dashboard, accounts, transactions, beneficiaries, loans, KYC, and support.

## Key Features

### Customer Banking

- Customer registration and login
- JWT-based authentication
- Customer dashboard
- Account creation and account details
- Account balance and available balance
- Deposits and withdrawals
- Fund transfers
- Transaction history
- Beneficiary management
- Loan applications
- KYC application and document submission
- Customer service requests and complaints
- Loan repayment tracking

### Bank Officer Operations

- Officer management and secure role-based APIs
- Customer lookup and profile access
- Review workflow management
- Pending review retrieval
- Beneficiary approval workflow
- Loan review and decision workflow
- KYC review workflow
- Support ticket management
- Audit and notification backend capabilities

## Enterprise Features

- Role-based access control
- JWT authentication
- RESTful APIs
- DTO-based API design
- Global exception handling
- Request validation
- Transaction reference generation
- Idempotent transaction processing
- Optimistic locking
- Audit logging
- Kafka-based event processing
- Notification service
- API Gateway
- Microservice architecture
- Flyway database migrations
- Swagger/OpenAPI documentation
- Unit testing with JUnit and Mockito
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

- Angular 22
- TypeScript
- HTML5
- CSS3

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
       +--------------------+--------------------+
       |                    |                    |
  Customer Service     Account Service     Transaction Service
       |                    |                    |
       +--------------------+--------------------+
                            |
                       PostgreSQL
                            |
                          Kafka
                     +------+------+
                     |             |
              Notification      Audit
                Service         Service

       Beneficiary | Auth | Officer | KYC | Loan | Support
```

## Microservices

The backend is split into 12 services:

1. Customer Service
2. Account Service
3. Transaction Service
4. Beneficiary Service
5. Auth Service
6. Officer Service
7. KYC Service
8. Loan Service
9. Support Service
10. Notification Service
11. Audit Service
12. API Gateway

## Running the Platform

### Docker Compose

```bash
docker compose up --build
```

Services:

- Frontend: http://localhost:4200
- API Gateway: http://localhost:8080
- Kafka UI: http://localhost:8099
- PostgreSQL: localhost:5432

The Angular frontend uses the gateway through `/api/v1`. In Docker, Nginx proxies `/api/` to the API Gateway. For local Angular development:

```bash
cd frontend
npm install
npm start
```

The Angular development proxy forwards `/api` requests to `http://localhost:8080`.

### API Documentation

Backend services expose Swagger UI at:

```
http://localhost:<service-port>/swagger-ui/index.html
```

The API Gateway runs on port 8080 and routes requests to the individual backend services.

## CI/CD

GitHub Actions validates:

- Maven builds for all backend services
- Angular production build
- Docker Compose configuration
- Docker image builds for all backend services

The current CI pipeline is passing across backend, frontend, and Docker validation/build jobs.

## Project Goals

This project demonstrates how a real-world financial application can be designed using secure APIs, modular services, transactional processing, event-driven communication, database persistence, automated testing, and containerized deployment.

This is a fictional learning and portfolio project. It does not contain proprietary code, credentials, customer information, or business logic from any financial institution.

## Author

Muralidhar R

GitHub: https://github.com/MURALI9900
