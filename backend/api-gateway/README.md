# FinBank API Gateway

The API Gateway is the single HTTP entry point for the FinBank microservices.

## Responsibilities

- Central routing to backend services
- One public backend URL for the Angular applications
- Service boundary isolation
- Health and gateway actuator endpoints
- Foundation for centralized authentication, rate limiting, CORS and request tracing

## Port

8080

## Routes

| API path | Service |
|---|---|
| /api/v1/auth/** | Auth Service |
| /api/v1/customers/** | Customer Service |
| /api/v1/accounts/** | Account Service |
| /api/v1/transactions/** | Transaction Service |
| /api/v1/beneficiaries/** | Beneficiary Service |
| /api/v1/officers/** | Officer Service |
| /api/v1/kyc/** | KYC Service |
| /api/v1/loans/** | Loan Service |
| /api/v1/support/** | Support Service |
| /api/v1/notifications/** | Notification Service |
| /api/v1/audits/** | Audit Service |

The gateway currently focuses on routing. JWT validation and role-based authorization will be hardened as the security phase progresses.
