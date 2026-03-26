# URL Shortener API

## 📌 Overview

This project implements a simple **URL Shortener API** using **Java and Spring Boot**, designed to prioritise:

- clarity
- simplicity
- clean separation of concerns
- correctness over overengineering

The service allows clients to:

- create a short URL from a long URL
- retrieve the original URL
- redirect via the shortened URL

---

## 🚀 How to Run

### Prerequisites

- Java 21+ (tested with Java 25)
- Gradle 9.3.0

### Run the application

```bash
./gradlew bootRun
```

Application starts on:
```
http://localhost:8080
```


## 📡 API Endpoints
1. Create Short URL
```
POST /api/short-urls
Content-Type: application/json
```
Request
```
{
  "originalUrl": "https://example.com/page"
}
```
Response 
```
{
  "code": "0MJEE3",
  "shortUrl": "/r/0MJEE3",
  "originalUrl": "https://example.com/page"
}
```

2. Get URL Info

```
GET /api/short-urls/{code}
```
Response
```
{
  "code": "0MJEE3",
  "originalUrl": "https://example.com/page"
}
```

3. Redirect

```
GET /r/{code}
```
Behaviour
* Returns 302 Found
* Redirects via Location header

Example:
```
curl -i http://localhost:8080/r/0MJEE3
```

## ❗ Error Handling

All errors return a consistent JSON structure:
```
{
  "errorCode": "ERROR_CODE",
  "message": "Description of the error"
}
```
Examples
Invalid URL
```
{
  "errorCode": "INVALID_URL",
  "message": "URL must be a valid absolute http/https URL"
}
```

## 🧠 Design Decisions

1. In-memory storage
* Uses ConcurrentHashMap
* thread safe for concurrent access
* Keeps implementation simple

2. Layered architecture
```
Controller → Service → Repository
```
* Controller: HTTP handling only
* Service: business logic
* Repository: data storage abstraction

3. Short code generation
* Base62 (alphanumeric)
* Fixed length (6 characters)
* random
* collision-safe with retry (10 times)

4. Idempotency
* Same orignal URL returns the same short code
* Achieved via reverse lookup map

5. Error handling
* Centralised using @RestControllerAdvice
* Consistent error structure
* Proper HTTP status mapping (400/404)

6. Routing design
* Redirect endpoint:
```
GET /r/{code}
```

Rationale:

* avoids collisions with root-level routes (e.g. /health, /actuator)
* keeps shortened URL compact
* balances usability and system safety

A regex constraint is applied:
```
[a-zA-Z0-9]{6}
```
to ensure only valid short codes are matched.

## ⚖️ Trade-offs
No persistence
* data is lost on restart
* acceptable for in-memory constraint

No expiration / TTL
* URLs do not expire

No rate limiting

## 🔮 Future Improvements
* persistent storage (PostgreSQL / DynamoDB)
* caching layer (Redis)
* URL expiration (TTL)
* rate limiting / abuse protection
* metrics & observability
* full short URL generation (host-aware)

## 🧪 Testing
* Service layer tested with unit tests
* Controller tested using MockMvc

Coverage includes:

* success scenarios
* validation errors
* not found handling
* redirect behaviour