---
paths:
  - "src/main/java/**/adapter/in/web/**"
---

You are editing web adapter code.

Controllers are THIN — delegate to application services immediately.
NEVER put business logic in controllers.
Constructor injection only — no field @Autowired.
Use DTOs (records), not domain types, over HTTP.
@Valid on all request bodies.
HTTP status codes: 201 create, 200 query, 400 validation, 404 not found.
Map domain exceptions to HTTP status HERE — never swallow exceptions or leak infrastructure details.
Test with @WebMvcTest (one controller).
