---
paths:
  - "src/test/java/**"
---

You are editing test code.

Tests are executable specifications.
@DisplayName on every class and method.
Use @Nested for grouping related tests.
Use @ParameterizedTest to model data-driven tests

# Naming conventions (Maven best practice)
Acceptance tests → *IT (integration tests, run during mvn verify).
All other tests → *Test (unit tests, run during mvn test).
Acceptance = @SpringBootTest + MockMvc.

NEVER recalculate expected values.
NEVER modify a test to make it pass.