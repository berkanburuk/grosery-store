# Grocery Store API

## Overview

Grocery Store API is a **Spring Boot** application that manages **Beers, Breads, and Vegetables**.  
It provides **CRUD operations**, **order processing with complex discounts**, and **centralized exception handling**.

- **Beers**: country-based discounts  
- **Breads**: age- and quantity-based discounts  
- **Vegetables**: weight-based discounts  

---

## Table of Contents
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [Database Configuration](#database-configuration)
- [API Endpoints](#api-endpoints)  
- [Order Processing & Discounts](#order-processing--discounts)  
- [Exception Handling](#exception-handling)  
- [Validation](#validation)  
- [Database](#database)  
- [Running the Application](#running-the-application)  


## Technologies

- **Java 21**
- **Spring Boot 3.2+**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Lombok**
- **JUnit 5**
- **Gradle 8**

---

## Getting Started

### Clone the repository

```bash
git clone https://github.com/berkanburuk/grosery-store.git
```

---

## Database Configuration

Docker Compose is provided for PostgreSQL:

```yaml
services:
  postgres:
    image: postgres:15
    container_name: grocery-store-db
    environment:
      POSTGRES_DB: grocery_store_db
    env_file:
      - src/main/resources/postgres.env
    ports:
      - "5432:5432"
    volumes:
      - pgdata-grocery-store:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  pgdata-grocery-store:
```

Environment file (`postgres.env`):

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

---

The API will be available at:
```
http://localhost:8080/api
```
---

## API Endpoints

### BeerController

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET    | `/api/v1/beers` | List all beers | None | 200 OK |
| GET    | `/api/v1/beers/{id}` | Get beer by ID | Path: `id` | 200 OK / 404 NOT FOUND |
| POST   | `/api/v1/beers` | Create a beer | JSON: `BeerDto` | 201 CREATED |
| PUT    | `/api/v1/beers/{id}` | Update a beer | JSON: `BeerDto` | 200 OK / 404 NOT FOUND |
| DELETE | `/api/v1/beers/{id}` | Delete a beer | Path: `id` | 204 NO CONTENT / 404 NOT FOUND |

### BreadController

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET    | `/api/v1/breads` | List all breads | None | 200 OK |
| GET    | `/api/v1/breads/{id}` | Get bread by ID | Path: `id` | 200 OK / 404 NOT FOUND |
| POST   | `/api/v1/breads` | Create a bread | JSON: `BreadDto` | 201 CREATED |
| PUT    | `/api/v1/breads/{id}` | Update a bread | JSON: `BreadDto` | 200 OK / 404 NOT FOUND |
| DELETE | `/api/v1/breads/{id}` | Delete a bread | Path: `id` | 204 NO CONTENT / 404 NOT FOUND |

### VegetableController

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| GET    | `/api/v1/vegetables` | List all vegetables | None | 200 OK |
| GET    | `/api/v1/vegetables/{id}` | Get vegetable by ID | Path: `id` | 200 OK / 404 NOT FOUND |
| POST   | `/api/v1/vegetables` | Create a vegetable | JSON: `VegetableDto` | 201 CREATED |
| PUT    | `/api/v1/vegetables/{id}` | Update a vegetable | JSON: `VegetableDto` | 200 OK / 404 NOT FOUND |
| DELETE | `/api/v1/vegetables/{id}` | Delete a vegetable | Path: `id` | 204 NO CONTENT / 404 NOT FOUND |

### OrderController

| Method | URL | Description | Request Body | Response |
|--------|-----|-------------|--------------|----------|
| POST   | `/api/v1/orders` | Process an order with discounts | JSON Array: `OrderRequestDto` | 201 CREATED: `OrderSummaryDto` |


### DiscountController

| Method | URL | Description                   | Request Body          | Response |
|--------|-----|-------------------------------|-----------------------|----------|
| GET    | `/api/v1/discounts` | List all discounts strategies | JSON: List of Strings | 200 OK |


### PriceController

| Method | URL | Description                            | Request Body        | Response |
|--------|-----|----------------------------------------|---------------------|----------|
| GET    | `/api/v1/prices` | List all prices of all items | JSON: Map | 200 OK |



---

## DTOs

### BeerDto

```json
{
  "id": 1,
  "name": "Duvel",
  "country": "BELGIUM",
  "price": 5.0
}
```

### BreadDto

```json
{
  "id": 1,
  "name": "White Bread",
  "bakingDate": "2026-01-16",
  "price": 3.5
}
```

### VegetableDto

```json
{
  "id": 1,
  "name": "Broccoli",
  "pricePer100Grams": 1.5
}
```

### OrderRequestDto

```json
{
  "type": "BEER",
  "itemId": 1,
  "amount": 6
}
```

### OrderSummaryDto

```json
{
  "lines": [
    {
      "description": "6 x Duvel (BELGIUM)",
      "total": 27.0
    }
  ],
  "total": 27.0
}
```

---

## Order Processing & Discounts

### Beer Discount

- Based on country:
  - BELGIUM: €3 per 6-pack  
  - NETHERLANDS: €2 per 6-pack  
  - GERMANY: €4 per 6-pack  

### Bread Discount

- Freshness & quantity based:
  - Age ≤ 1 day: No free
  - Age ≤ 3 days: buy 2 get 1 free  
  - Age 4–6 days: buy 3 get 2 free  
  - Older than 6 days: cannot sell  

### Vegetable Discount

- Based on total weight in the order:
  - 0–100g: 5% discount  
  - 101–500g: 7% discount  
  - 500g: 10% discount  

---

## Exception Handling

The application uses **`GlobalExceptionHandler`** for unified error responses:

| Exception | HTTP Status | Description |
|-----------|------------|------------|
| `MethodArgumentNotValidException` | 400 BAD REQUEST | Validation failed on request body |
| `NotFoundException` | 404 NOT FOUND | Resource not found |
| `AlreadyExistsException` | 409 CONFLICT | Resource already exists |
| `IllegalArgumentException` | 400 BAD REQUEST | Business rule violation |
| `HttpMessageNotReadableException` | 400 BAD REQUEST | Invalid JSON format |
| `Exception` | 500 INTERNAL SERVER ERROR | Unhandled server exception |

Error response format:

```json
{
  "status": "BAD_REQUEST",
  "message": "Validation failed",
  "timestamp": "2026-01-16T10:00:00",
  "validationErrors": {
    "field": "error message"
  }
}
```

---

## Validation

- `@NotNull` for required fields  
- `@Positive` for numeric fields  
- DTOs validate input before reaching service layer  

---

## Database

- Entities: `Beer`, `Bread`, `Vegetable`  
- Repository interfaces extend `JpaRepository`  
- Auto-generated IDs  
- Unique constraints and nullable validations applied  

---

## Running the Application

1. **Clone repo**  
2. **Run database**
```shell
docker-compose -f docker-compose-dev.yaml up -d
```
3. **Build SpringBoot App**:
```bash
./gradlew bootRun
```
4. **Access API** at: `http://localhost:8080/api/v1`  

---

## Notes

- Discounts are **calculated at order time**  
- Vegetables discounts are **applied proportionally** per item  
- Bread validation ensures **maximum age 6 days**  
- Beer discounts respect **country-specific packs**  

---
