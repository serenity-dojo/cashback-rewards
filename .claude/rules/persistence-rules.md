---
paths:
  - "src/main/java/**/adapter/out/persistence/**"
---

You are editing persistence adapter code.

JPA entities live HERE, not in domain.
Map domain objects to/from JPA entities.
Implement outbound ports from application/.
NEVER expose JPA entities outside this layer.
Test with @DataJpaTest.
Use Testcontainers for integration tests.