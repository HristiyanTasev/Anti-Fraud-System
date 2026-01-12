# Anti-Fraud System

Backend service for evaluating payment transactions and managing fraud-prevention lists (suspicious IPs + stolen cards).
The API exposes endpoints for **transaction validation**, **feedback-driven limit tuning**, and **administration of security data**,
protected by **role-based access control** using Spring Security.

---

## Overview

This project is built with **Spring Boot** and follows a layered architecture (controller → service → repository)to keep REST endpoints thin and business rules centralized.

It supports:

- user registration and user administration (roles + access)
- checking transactions against fraud rules and dynamic limits
- storing and managing:
  - suspicious IP addresses
  - stolen card numbers (Luhn-validated)
- transaction history queries (global and per card)
- transaction feedback (with conflict checks) that updates per-card limits over time

---

## Key Features

- **Transaction Validation**
  - Evaluates transactions using:
    - amount thresholds (per card)
    - suspicious IP blacklist checks
    - stolen card checks
    - correlation rules within the last hour:
      - IP correlation
      - region correlation
  - Returns a decision result: `ALLOWED`, `MANUAL_PROCESSING`, or `PROHIBITED`
  - Returns human-readable reason(s) (or `none`)

- **Feedback & Adaptive Limits**
  - Support can submit feedback for a processed transaction.
  - Feedback is rejected if:
    - the transaction does not exist
    - feedback was already provided
    - feedback conflicts with the original result
  - Feedback updates per-card thresholds (allowed/manual limits) using weighted adjustments.

- **Fraud Lists Management**
  - Add / delete / list **suspicious IPs**
  - Add / delete / list **stolen cards**
  - Strong input validation:
    - IPv4 validation for IP addresses
    - Luhn validation for card numbers

- **Role-Based Security (Stateless)**
  - Uses **HTTP Basic Auth**
  - Stateless security (`SessionCreationPolicy.STATELESS`)
  - Per-endpoint access rules enforced by Spring Security authorities:
    - `MERCHANT`
    - `SUPPORT`
    - `ADMINISTRATOR`

- **Persistence**
  - Uses **H2 file-based database**, so data survives restarts.

- **Operational Endpoints**
  - Spring Boot Actuator enabled (including shutdown endpoint for controlled termination).

---

## Tech Stack

- Java 21
- Spring Boot (Web, Security, Validation, Data JPA, Actuator)
- H2 Database (file mode)
- Gradle

---

## Project Structure

Source code located in `src/main/java/antifraud`:

- `config`: Spring Security configuration + authentication entry point
- `controller`: REST endpoints for users and antifraud operations
- `service`: core business logic (transaction processing, feedback, administration rules)
- `repository`: Spring Data JPA repositories
- `model`: entities + DTOs for users, transactions, IPs, cards, etc.
- `validation`: custom validators/annotations (e.g., IPv4, Luhn)
- `exception`: custom exceptions and centralized error handling (where applicable)
- `util`: DTO/entity mappers

---

## Getting Started

### Prerequisites

- Java 21+
- Gradle (wrapper included)

### Running the App

**Windows:**
```
bash gradlew.bat bootRun
```

**Linux / macOS:**
```
bash ./gradlew bootRun
``` 

The server runs on port:

- `28852`

(From `src/main/resources/application.properties`.)

---

## Database (H2)

Configured as a file database:

- JDBC URL: `jdbc:h2:file:../anti_fraud_system_db`

---

## Security & Authorization

This API uses **HTTP Basic Authentication** and is **stateless** (no server session).

### Endpoint Access Summary (high level)

#### Public
- Register user:
  - `POST /api/auth/user`

#### Admin-only
- Delete user:
  - `DELETE /api/auth/user/{username}`
- Change user role:
  - `PUT /api/auth/role`
- Change user access (lock/unlock):
  - `PUT /api/auth/access`

#### Admin + Support
- List users:
  - `GET /api/auth/list`

#### Merchant
- Submit a transaction for evaluation:
  - `POST /api/antifraud/transaction`

#### Support
- Submit feedback for a transaction:
  - `PUT /api/antifraud/transaction`
- Get transaction history:
  - `GET /api/antifraud/history`
  - `GET /api/antifraud/history/{number}`
- Manage suspicious IPs:
  - `POST /api/antifraud/suspicious-ip`
  - `GET /api/antifraud/suspicious-ip`
  - `DELETE /api/antifraud/suspicious-ip/{ip}`
- Manage stolen cards:
  - `POST /api/antifraud/stolencard`
  - `GET /api/antifraud/stolencard`
  - `DELETE /api/antifraud/stolencard/{number}`

---

## API Examples

Base URL:
- `http://localhost:28852`

> All protected endpoints require Basic Auth:
>
> `Authorization: Basic <BASE64(username:password)>`

### Register a User (Public)
* First User is automatically an ADMINISTRATOR.

`POST /api/auth/user`

```
json { "name": "John Doe", "username": "ExampleUsername", "password": "changeMe123" }
```

### Admin: List All Users (Basic Auth)

`GET /api/auth/list`

Authenticate using **Basic Auth**:

- username: username
- password: admin password

---

### Admin: Change a User Role (Basic Auth)

`PUT /api/auth/role`

Authenticate using **Basic Auth** (ADMINISTRATOR required).

Request body example:

```
json { "username": "ExampleUsername", "role": "SUPPORT" }
```

---

### Admin: Lock / Unlock a User (Basic Auth)

`PUT /api/auth/access`

Authenticate using **Basic Auth** (ADMINISTRATOR required).

Lock example:

```
json { "username": "ExampleUsername", "operation": "LOCK" }
``` 

Unlock example:

```
json { "username": "ExampleUsername", "operation": "UNLOCK" }
```

---

### Admin: Delete a User (Basic Auth)

`DELETE /api/auth/user/{username}`

Example:

`DELETE /api/auth/user/ExampleUsername`


---

### List Users (ADMINISTRATOR or SUPPORT)

`GET /api/auth/list`

---

### Process a Transaction (MERCHANT)

`POST /api/antifraud/transaction`

```
json { "amount": 120, "ip": "192.168.1.10", "number": "4000008449433403", "region": "EAP", "date": "2026-01-12T10:15:30" }
``` 

Response (example shape):

```
json { "result": "ALLOWED", "info": "none" }
```

---

### Submit Feedback (SUPPORT)

`PUT /api/antifraud/transaction`

```
json { "transactionId": 1, "feedback": "PROHIBITED" }
``` 

Notes:
- Feedback cannot be the same as the original result.
- Feedback can be submitted only once per transaction.
- This updates per-card limits.

---

### Get Transaction History (SUPPORT)

All:
- `GET /api/antifraud/history`

By card number:
- `GET /api/antifraud/history/{number}`

Example:
- `GET /api/antifraud/history/4000008449433403`

---

### Add Suspicious IP (SUPPORT)

`POST /api/antifraud/suspicious-ip`

```
json { "ip": "203.0.113.7" }
```

### Delete Suspicious IP (SUPPORT)

`DELETE /api/antifraud/suspicious-ip/203.0.113.7`

---

### Add Stolen Card (SUPPORT)

`POST /api/antifraud/stolencard`

```
json { "number": "4000008449433403" }
``` 

### Delete Stolen Card (SUPPORT)

`DELETE /api/antifraud/stolencard/4000008449433403`

---
