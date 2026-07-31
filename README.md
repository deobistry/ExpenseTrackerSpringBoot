# 💰 Expense Manager REST API

A Spring Boot REST API for managing personal expenses with JWT-based authentication.

This project allows users to securely manage their own expense records and categories while ensuring complete ownership isolation through JWT authentication. The application intentionally avoids JPA entity relationship mappings and instead uses plain foreign key columns with explicit JPQL joins.

---

## ✨ Features

- User Signup & Login
- JWT Authentication (without Spring Security)
- Category Management
- Expense Management
- Soft Delete for Expenses
- Filter Expenses by:
  - Category
  - Date Range
  - Minimum Amount
  - Maximum Amount
- Last 20 Expenses API
- User-based Data Isolation
- Manual Request Validation
- Global Exception Handling

---

# 🛠 Tech Stack

| Technology | Version |
|------------|----------|
| Java | 17+ |
| Spring Boot | 3.x |
| Spring Data JPA | Latest |
| MySQL | 8.x |
| JWT (JJWT) | Latest |
| Maven | Build Tool |

---

# 📦 Dependencies

Only the following dependencies are used:

- spring-boot-starter-web
- spring-boot-starter-data-jpa
- mysql-connector-j
- jjwt-api
- jjwt-impl
- jjwt-jackson

### ❌ Intentionally Excluded

- Spring Security
- Lombok
- Spring Validation
- BCrypt Password Encoder

---

# 🏗 Architecture

```
src
│
├── controller
├── service
├── repository
├── entity
├── dto
│   ├── request
│   └── response
├── security
├── config
└── exception
```

The project follows a layered architecture:

```
Controller
      ↓
Service
      ↓
Repository
      ↓
MySQL Database
```

---

# 🗄 Database Design

## Users

| Column | Type |
|---------|------|
| id | Long |
| name | String |
| email | String |
| password | String |

---

## Categories

| Column | Type |
|---------|------|
| id | Long |
| title | String |
| userId | Long |

---

## Expenses

| Column | Type |
|---------|------|
| id | Long |
| status | String |
| description | String |
| amount | BigDecimal |
| date | LocalDate |
| categoryId | Long |
| userId | Long |

---

# 🚫 No Entity Relationships

This project deliberately avoids using:

- `@OneToMany`
- `@ManyToOne`
- `@JoinColumn`

Instead, only plain foreign key columns are stored.

Example:

```java
private Long userId;
private Long categoryId;
```

Read APIs use JPQL projection joins.

---

# 🔐 Authentication

Authentication is implemented using JWT.

### Login Flow

```
User Login
      │
      ▼
Validate Credentials
      │
      ▼
Generate JWT
      │
      ▼
Return Token
```

Every protected endpoint requires:

```
Authorization: Bearer <JWT_TOKEN>
```

The JWT contains:

- userId
- email
- expiration time

---

# 📚 REST API

## Authentication

| Method | Endpoint |
|---------|----------|
| POST | /auth/signup |
| POST | /auth/login |

---

## Categories

| Method | Endpoint |
|---------|----------|
| POST | /categories |
| GET | /categories |
| PUT | /categories |
| DELETE | /categories/{id} |

---

## Expenses

| Method | Endpoint |
|---------|----------|
| GET | /expenses |
| GET | /expenses/filter |
| POST | /expenses |
| PUT | /expenses |
| PUT | /expenses/delete/{id} |

---

# 📥 Expense Response

```json
{
  "expenseId": 1,
  "description": "Lunch",
  "amount": 250,
  "date": "2026-01-10",
  "userId": 1,
  "categoryId": 2,
  "categoryTitle": "Food"
}
```

---

# 🚀 Getting Started

## 1. Clone Repository

```bash
git clone https://github.com/yourusername/expense-manager.git
```

---

## 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE billdesk_expense_rec;
```

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/billdesk_expense_rec
spring.datasource.username=root
spring.datasource.password=admin123

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Note:** These credentials are intended for local development. Use environment variables or a local configuration file before publishing the project.

---

## 3. Run the Application

```bash
mvn spring-boot:run
```

Application starts on:

```
http://localhost:8080
```

---

# 🧪 Testing

You can test the APIs using:

- Postman
- Insomnia
- Thunder Client

Recommended flow:

1. Signup
2. Login
3. Copy JWT
4. Add Authorization Header
5. Create Categories
6. Create Expenses
7. Filter Expenses
8. Soft Delete Expense

---

# 📌 Business Rules

- Every user can only access their own data.
- Passwords are stored as plain text (for educational purposes only).
- No Spring Security is used.
- No Lombok is used.
- No Bean Validation annotations are used.
- Manual validation is performed in the service layer.
- Expenses use soft delete.
- Categories cannot be deleted while active expenses exist.
- Ownership is always verified using the JWT user ID.

---

# 📅 Project Roadmap

- ✅ Project Setup
- ✅ JWT Authentication
- ✅ Category Module
- ✅ Expense Module
- ✅ Expense Filtering
- ✅ Soft Delete
- ✅ Global Exception Handling

---

# 📄 License

This project is intended for educational and learning purposes.

---

# 👨‍💻 Author

Developed as a Spring Boot REST API project for learning backend development concepts, including JWT authentication, JPA, REST APIs, MySQL integration, and layered architecture.
