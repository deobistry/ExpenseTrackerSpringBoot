# Expense Manager – Implementation Plan

## Project Overview

A Spring Boot REST API for personal expense tracking with JWT-based authentication.
No JPA entity relationship mappings are used — all foreign keys are plain columns (`userId`, `categoryId`), and joins are done via explicit JPQL queries, not `@OneToMany`/`@ManyToOne`.

---

## Tech Stack

- Spring Boot
- Spring Data JPA
- MySQL (via `mysql-connector-j`)
- JWT via `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (**not** Spring Security)

## Dependencies (pom.xml)

Include only:

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `mysql-connector-j`
- `jjwt-api`
- `jjwt-impl` (runtime scope)
- `jjwt-jackson` (runtime scope)

**Explicitly excluded — do not add these:**

- `spring-security-crypto`
- `lombok`
- `spring-boot-starter-validation`

### Implications of exclusions

- **No Lombok:** all entities and DTOs must have explicit constructors, getters, and setters written out.
- **No validation starter:** no `@Valid` / `@NotNull` / `@NotBlank` annotations. All request validation (required fields, non-negative amounts, non-null dates, etc.) is done manually in the service layer with plain `if` checks, throwing custom exceptions on failure.
- **No spring-security-crypto:** no password hashing. `AuthService` stores and compares the `password` field as-is (plain field-to-field string comparison). Do not introduce BCrypt or any hashing utility.

---

## Database Credentials
Database Name: billdesk_expense_rec
```
DB_USERNAME=root
DB_PASSWORD=admin123
```

Use these in `application.properties` for `spring.datasource.username` and `spring.datasource.password`.

> Note: these are plaintext local dev credentials — don't commit this file to a public repo. Consider moving them to an untracked `application-local.properties` or environment variables before pushing anywhere.

---

## Data Model

### Users
| Column | Type | Notes |
|---|---|---|
| id | Long | PK, auto-generated |
| name | String | |
| email | String | unique, not null |
| password | String | stored as plain value, not null |

### Categories
| Column | Type | Notes |
|---|---|---|
| id | Long | PK, auto-generated |
| title | String | not null |
| userId | Long | not null (plain FK column, no mapping) |

### Expenses
| Column | Type | Notes |
|---|---|---|
| id | Long | PK, auto-generated |
| status | String | `ACTIVE` / `DELETED` (soft delete flag) |
| description | String | |
| amount | BigDecimal | use BigDecimal, not double/float |
| date | LocalDate | |
| categoryId | Long | not null (plain FK column) |
| userId | Long | not null (plain FK column) |

### ExpensesView (projection, not a table)

Result shape produced by joining `Expenses` + `Categories` on `categoryId = id` for read APIs:

```
expenseId, description, amount, date, userId, categoryId, categoryTitle
```

This is implemented as a JPQL query with a `new` expression into `ExpenseResponse`, not a database view or JPA relationship.

---

## Entity Classes

Plain JPA `@Entity` classes, no relationship annotations, manual getters/setters/constructors:

- `User` — id, name, email, password
- `Category` — id, title, userId
- `Expense` — id, status, description, amount, date, categoryId, userId

---

## DTO Classes

### Auth
- `SignupRequest` — name, email, password
- `LoginRequest` — email, password
- `LoginResponse` — token

### Category
- `CategoryCreateRequest` — title
- `CategoryUpdateRequest` — id, title
- `CategoryResponse` — id, title

### Expense
- `ExpenseCreateRequest` — description, amount, date, categoryId
- `ExpenseUpdateRequest` — id, description, amount, date, categoryId
- `ExpenseResponse` — expenseId, description, amount, date, userId, categoryId, categoryTitle (also serves as the `ExpensesView` projection shape; needs a matching all-args constructor for JPQL `new` expressions)
- `ExpenseFilterRequest` — categoryId, fromDate, toDate, minAmt, maxAmt (bound from query params)

---

## API Endpoints

### AuthController (`/auth`)
| Method | Path | Request Body | Response Body | Auth |
|---|---|---|---|---|
| POST | /auth/signup | name, email, password | — | public |
| POST | /auth/login | email, password | { token } | public |

### CategoryController (`/categories`)
| Method | Path | Request Body | Response Body | Auth |
|---|---|---|---|---|
| POST | /categories | { title } | — | Bearer token |
| GET | /categories | — | [{ id, title }] | Bearer token |
| PUT | /categories | { id, title } | — | Bearer token |
| DELETE | /categories/{categoryId} | — | — | Bearer token |

- DELETE must fail if any active expenses reference the category.
- PUT/DELETE must verify the category belongs to the current user (403 if not).

### ExpenseController (`/expenses`)
| Method | Path | Request Body | Response Body | Auth |
|---|---|---|---|---|
| GET | /expenses | — | last 20, see shape below | Bearer token |
| GET | /expenses/filter?categoryId=&fromDate=&toDate=&minAmt=&maxAmt= | — | filtered list, see shape below | Bearer token |
| POST | /expenses | { description, amount, date, categoryId } | — | Bearer token |
| PUT | /expenses | { id, description, amount, date, categoryId } | — | Bearer token |
| PUT | /expenses/delete/{expenseId} | — | — | Bearer token (soft delete) |

Response shape for both GET endpoints (array of):
```json
{ "expenseId": 0, "description": "", "amount": 0, "date": "", "userId": 0, "categoryId": 0, "categoryTitle": "" }
```

- PUT/soft-delete must verify the expense belongs to the current user (403 if not).
- All `userId` values used in queries/writes must come from the JWT in the `Authorization` header — never from the request body.

---

## Implementation Phases

### Phase 1 — Project Setup & Core Models
**Goal:** Bootable skeleton with DB connectivity, no business logic yet.

- Initialize Spring Boot project with only the approved dependencies
- Configure `application.properties` (MySQL URL, username, password, `ddl-auto`, dialect)
- Create package structure: `config`, `controller`, `service`, `repository`, `entity`, `dto.request`, `dto.response`, `security`, `exception`
- Create entities: `User`, `Category`, `Expense` (manual getters/setters/constructors, no relationship mappings)
- Create empty repository interfaces: `UserRepository`, `CategoryRepository`, `ExpenseRepository`
- Verify app boots and Hibernate creates the three tables correctly

**Exit criteria:** App starts, tables exist in MySQL, no endpoints yet.

---

### Phase 2 — Auth Module (Signup/Login + JWT)
**Goal:** Users can sign up, log in, and receive a valid JWT.

- DTOs: `SignupRequest`, `LoginRequest`, `LoginResponse`
- `JwtUtil`: generate token (embed `userId`, `email`, expiry), parse/validate token, extract claims
- `AuthService`:
  - `signup`: manual check for existing email via `UserRepository.findByEmail`, manual required-field checks, save user (password stored as-is)
  - `login`: fetch by email, compare password directly, issue JWT via `JwtUtil` on success, else throw `UnauthorizedException`
- `AuthController`: `POST /auth/signup`, `POST /auth/login`
- Basic `GlobalExceptionHandler`: handles `UnauthorizedException` and generic validation failures

**Exit criteria:** Can signup and login via Postman, receive a working JWT.

---

### Phase 3 — Request Authentication (JWT Filter)
**Goal:** Every protected endpoint resolves the current user from the token, not the request body.

- `JwtAuthFilter implements jakarta.servlet.Filter`: reads `Authorization` header, validates via `JwtUtil`, extracts `userId`
- `UserContext` (`@RequestScope` bean or request-attribute-based holder): exposes `getCurrentUserId()`
- `FilterRegistrationBean`: register filter on `/categories/*` and `/expenses/*`, exclude `/auth/*`
- On invalid/missing/expired token → short-circuit with `401`
- Extend `GlobalExceptionHandler` with `ForbiddenException` (403) and `ResourceNotFoundException` (404)

**Exit criteria:** Hitting `/categories` or `/expenses` without a valid token returns 401; a valid token makes `userId` available downstream.

---

### Phase 4 — Category Module
**Goal:** Full CRUD on categories, scoped and ownership-checked by token `userId`.

- DTOs: `CategoryCreateRequest`, `CategoryUpdateRequest`, `CategoryResponse`
- `CategoryRepository`: add `findAllByUserId(Long userId)`
- `CategoryService`:
  - `create`: manual title validation, save with `userId` from `UserContext`
  - `getAll`: return all categories for current user
  - `update`: fetch by id, verify `category.userId == currentUserId` else `ForbiddenException`, update title
  - `delete`: verify ownership, verify no active expenses reference this category (via `ExpenseRepository`), else reject with a conflict-style exception
- `CategoryController`: `POST /categories`, `GET /categories`, `PUT /categories`, `DELETE /categories/{categoryId}`

**Exit criteria:** Categories can be created/listed/updated/deleted, always scoped to the token's user, with ownership enforced.

---

### Phase 5 — Expense Module
**Goal:** Full CRUD + soft delete + last-20 + filter, all ownership-checked.

- DTOs: `ExpenseCreateRequest`, `ExpenseUpdateRequest`, `ExpenseResponse`, `ExpenseFilterRequest`
- `ExpenseRepository`:
  - `findLast20Expenses(userId, Pageable)` — JPQL projection joining `Expense` + `Category` on `categoryId`, filtered to `status = 'ACTIVE'`, ordered by date desc
  - `filterExpenses(userId, categoryId, fromDate, toDate, minAmt, maxAmt)` — same projection with optional dynamic filters
  - `existsByCategoryIdAndStatus(categoryId, status)` — used by `CategoryService.delete`
- `ExpenseService`:
  - `create`: manual field validation (amount > 0, required fields, date not null), save with `userId` from token, `status = ACTIVE`
  - `getLast20`: call repository with `PageRequest.of(0, 20)`
  - `filter`: manual parsing/validation of query params, call repository
  - `update`: fetch by id, ownership check, update fields
  - `softDelete`: fetch by id, ownership check, set `status = DELETED`, save
- `ExpenseController`:
  - `GET /expenses`
  - `GET /expenses/filter`
  - `POST /expenses`
  - `PUT /expenses`
  - `PUT /expenses/delete/{expenseId}`

**Exit criteria:** All expense endpoints work end-to-end, correctly scoped and ownership-checked, category deletion correctly blocked when active expenses exist.

---

### Phase 6 — Hardening & Cleanup
**Goal:** Polish before calling it done.

- Review `GlobalExceptionHandler` for completeness — consistent error response shape across all thrown exceptions
- Sweep all services for manual validation gaps (nulls, negative amounts, malformed dates in filter params)
- Confirm every mutating endpoint (update/delete) re-verifies ownership using `UserContext`, never trusts a `userId` in the request body
- Manual end-to-end test pass: signup → login → create category → create expense → filter → update → soft-delete → delete category (blocked) → delete expense → delete category (allowed)

**Exit criteria:** All 11 endpoints working, ownership enforced everywhere, no unhandled exceptions leak as raw 500s.