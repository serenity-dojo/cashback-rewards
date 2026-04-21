# CLAUDE.md

## Project

SpringBoot microservice implementing a Cashback Rewards solution.

## Build & Run

Uses the Maven wrapper; Java 25 required (see `pom.xml` `<java.version>`).

```bash
./mvnw spring-boot:run           # run the app
./mvnw test                      # run all tests
./mvnw -Dtest=ClassName test     # run a single test class
./mvnw -Dtest=ClassName#method test   # run a single test method
./mvnw clean package             # build the jar
```

Stack: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, H2 (runtime). No explicit datasource config — Spring Boot auto-configures an in-memory H2 via `application.yaml`.

## Coding Conventions

### Money
BigDecimal for ALL monetary values. NEVER float, double, or int.
Always explicit RoundingMode. Cashback: RoundingMode.DOWN, scale 2.
BigDecimal.valueOf() or new BigDecimal("...") — NEVER new BigDecimal(double).

### Java 25
Records for value objects, sealed interfaces, pattern matching.
No Lombok — records replace it.

### REST & Spring
Constructor injection only (no field @Autowired).
@Valid on request bodies. 201 create, 200 query, 400 validation, 404 not found.
Domain exceptions for business rule violations. Map to HTTP in controller only.
Never swallow exceptions or leak infrastructure details.

## Project Structure

domain/ — business logic, models, ports. No Spring imports.
application/ — use-case orchestration.
adapter/in/web/ — REST controllers (Spring MVC).
adapter/out/persistence/ — JPA repositories and entities.

NEVER import adapter classes from domain.

## Development Process

Follow these steps for every feature. Do NOT skip steps.

Step 1: Discovery — Run /discover.
Propose rules, surface questions with options, let the user decide.
Save draft spec to docs/specs/.
STOP. User reviews, edits, and annotates the spec.
Do NOT proceed if the spec has unresolved questions.
Re-read the final spec before continuing.

Step 2: Acceptance Test — Write test for the NEXT rule only.
@Nested = rule, test = example. @SpringBootTest + MockMvc.
Complete Step 3 until this rule is GREEN before writing the next.

Step 3: TDD (Inner Loop) — RED → GREEN → REFACTOR.
Write ONE failing test. Minimum code to pass. Refactor.
Run ALL tests. STOP after each cycle.

Step 4: Review — Verify coverage, boundaries, no AI smells.
Update CLAUDE.md if new conventions emerged.

## Testing Standards

Acceptance: .../acceptance/ — @SpringBootTest + MockMvc
Domain: .../domain/ — plain JUnit + AssertJ, NO Spring
Repository: .../adapter/out/persistence/ — @DataJpaTest

Tests are executable specifications, not just checks.
Use @Nested classes for grouping — even in unit tests.
@DisplayName with business language on every class and method.
Use AssertJ for all assertions.
For money: isEqualByComparingTo("1.60").
Inline test data per test. No shared fixtures.
