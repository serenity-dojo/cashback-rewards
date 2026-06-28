# CLAUDE.md

## Project

SpringBoot microservice implementing a Cashback Rewards solution.

## Build & Run

Maven wrapper (`./mvnw`, not `mvn`); Java 25 required (see `pom.xml`).

- `./mvnw test` — unit tests (H2 in PostgreSQL mode, no Docker needed).
- `./mvnw verify` — unit + acceptance (`*IT`) tests.
- `./mvnw -Dtest=ClassName#method test` — single test class/method.
- `./mvnw spring-boot:run` — needs a running PostgreSQL (see Database below).

Stack: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, Flyway, PostgreSQL driver (runtime), H2 (test).

### Database
Production persistence is PostgreSQL. The schema is owned by Flyway migrations in
`src/main/resources/db/migration` (`V<n>__description.sql`); Hibernate is `ddl-auto: validate`
and never generates schema. Connection is in `src/main/resources/application.yaml`,
overridable via `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`. (Local-run setup is in the README.)

## Coding Conventions

### Money
BigDecimal for ALL monetary values. NEVER float, double, or int.
Always explicit RoundingMode. Cashback: RoundingMode.HALF_EVEN, scale 2.
BigDecimal.valueOf() or new BigDecimal("...") — NEVER new BigDecimal(double).

### Java 25
Records for value objects, sealed interfaces, pattern matching.
No Lombok — records replace it.

### REST & Spring
Constructor injection only (no field @Autowired).
@Valid on request bodies. 201 create, 200 query, 400 validation, 404 not found.
Domain exceptions for business rule violations. Map to HTTP in controller only.
Never swallow exceptions or leak infrastructure details.

## Development Process

One feature at a time, in order, no steps skipped. Each skill owns the
detailed method and stop conditions — do not inline them here.

1. Discovery: use the `discover` skill. Save the spec to doc/specs/, then STOP for user review.
2. Acceptance test: use the `accept` skill for the NEXT rule only.
3. TDD inner loop: use the `tdd` skill, one cycle per invocation.
4. Review: use the `review` skill before committing.

## Testing Standards

Acceptance tests live in .../acceptance/, unit tests beside their production code.
Domain tests: plain JUnit + AssertJ, NO Spring.
Repository tests: @DataJpaTest.
Web tests: @WebMvcTest.
Acceptance tests: @SpringBootTest + MockMvc.
For money: isEqualByComparingTo("1.60").
Inline test data per test. No shared fixtures.

## Architecture: Hexagonal (Ports & Adapters)
Domain (domain/): Pure Java. NO Spring, NO framework dependencies.
    model/ — entities and value objects
    service/ — business rules
Application (application/): port/in/ and port/out/ interfaces.
    @Service orchestration only — no business logic here.
Adapters: 
    adapter/in/web/ — @RestController, DTOs only.
    adapter/out/persistence/ — JPA repos and entities (NOT in domain).

Domain NEVER imports org.springframework or jakarta.persistence.
Controllers NEVER contain business logic.
Dependencies flow inward: adapter → application → domain.