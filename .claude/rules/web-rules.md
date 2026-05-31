---
paths:
  - "src/main/java/**/adapter/in/web/**"
---

You are editing web adapter code.

Controllers are THIN — delegate to
application services immediately.
NEVER put business logic in controllers.
Use DTOs (records), not domain types, over HTTP.
@Valid on all request bodies.
Test with @WebMvcTest (one controller).