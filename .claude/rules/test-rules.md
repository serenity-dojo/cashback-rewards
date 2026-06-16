---
paths:
  - "src/test/java/**"
---

You are editing test code.

Tests are executable specifications.
@DisplayName on every class and method.
Use @Nested for grouping related tests (@Nested = rule, test method = example).
Use @ParameterizedTest to model data-driven tests.
Inline test data per test — no shared fixtures.
For money, assert with isEqualByComparingTo("1.60").

# Test types, locations and annotations
Acceptance tests → src/test/.../acceptance/, named *IT, @SpringBootTest + MockMvc (run during mvn verify).
Domain tests → beside production code, plain JUnit + AssertJ, NO Spring.
Repository tests → @DataJpaTest.
Web tests → @WebMvcTest (one controller).
All other (non-acceptance) tests → named *Test (run during mvn test).

NEVER recalculate expected values.
NEVER modify a test to make it pass.
