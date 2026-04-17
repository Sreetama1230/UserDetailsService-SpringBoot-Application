# User Details Service

A Spring Boot-based microservice that manages user information with a focus on idempotency, clean API design, and robust error handling.

---

## 🚀 Features

### 1. Idempotent User Creation (Request ID Based)
- Each create request includes a **requestId**.
- Behavior:
  - First request with a new `requestId` → creates a new user.
  - Subsequent requests with differnt request payload and the same `requestId` → returns the already created user (no duplicate creation).
  - Subsequent requests with same request payload and the same `requestId` → returns the already created user (no duplicate creation).

#### ✅ Benefits
- Prevents duplicate records  
- Saves bandwidth  
- Ensures idempotent operations  

---

### 2. Global Exception Handling
- Implemented using:
  - `@ControllerAdvice`
  - `@ExceptionHandler`
- Provides consistent and structured error responses.

---

### 3. Unified Create & Update API (POST Based)
- Both create and update operations use **POST** requests.
- Endpoint - http://localhost:8080/user?requestid=<>

#### How it works:
- If the request payload contains an `id` → **Update operation**
- If the request payload does NOT contain an `id` → **Create operation**

---

### 4. GET APIs

#### Fetch All Users
- Retrieve all user records.
- Endpoint - http://localhost:8080/user/all

#### Fetch Users with Pagination
- Supports pagination with default values:
  - Page number: `0`
  - Page size: `10`
- Endpoint - http://localhost:8080/user?pageNumber=<>&pageSize=<>
---
### 4. DELETE API
- When a user is deleted, the associated `requestId` is also deleted.
- Endpoint: `DELETE http://localhost:8080/user/{id}`

---

## 🛠️ Tech Stack
- Java
- Spring Boot
- Spring Data JPA
- REST APIs

---

## 📌 API Overview


### 1. Create / Update User
**POST** `user?requestid=<id>`

**Description:**  
Creates a new user or updates an existing user. It is totally optional to provide the request id. <br>
The default value of requestid is `""`

**Request Body:**
```json
{
    "role":"ADMIN",
    "name":"Riya",
    "username":"riya12345",
    "email":"riya123gmail.com",
    "phoneNo":"9867893420",
    "country":"IND",
    "state":"WB",
    "city":"Kolkata"
}
