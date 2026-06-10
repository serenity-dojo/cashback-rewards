# CLAUDE.md

## Project

SpringBoot microservice implementing a Cashback Rewards solution.

## Build & Run

Uses the Maven wrapper; Java 25 required (see `pom.xml` `<java.version>`).

```bash
./mvnw spring-boot:run           # run the app (needs a PostgreSQL instance — see below)
./mvnw test                      # run unit tests (use H2 in PostgreSQL mode)
./mvnw verify                    # run unit + acceptance (*IT) tests
./mvnw -Dtest=ClassName test     # run a single test class
./mvnw -Dtest=ClassName#method test   # run a single test method
./mvnw clean package             # build the jar
```

Stack: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, Flyway, PostgreSQL driver (runtime), H2 (test).

### Database
Production persistence is PostgreSQL. The schema is owned by Flyway migrations in
`src/main/resources/db/migration` (`V<n>__description.sql`); Hibernate is `ddl-auto: validate`
and never generates schema. Connection is configured in `src/main/resources/application.yaml`
and overridable via `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` env vars.

To run locally, start a PostgreSQL instance, e.g.:
```bash
docker run --name cashback-pg -e POSTGRES_DB=cashback_rewards \
  -e POSTGRES_USER=cashback -e POSTGRES_PASSWORD=cashback -p 5432:5432 -d postgres:17
```

Tests run against H2 in PostgreSQL compatibility mode (`src/test/resources/application.yaml`),
so the same Flyway migrations and JPA mappings are exercised without Docker. The persistence
rules call for Testcontainers + real PostgreSQL; switch the test datasource over once a Docker
daemon is available.

## Conventions & Architecture

Layer-specific conventions live in `.claude/rules/` and load automatically when you edit a
matching file — see the money (BigDecimal), domain, web, persistence and test rules. Those files
are the source of truth for coding conventions; keep them updated, not this file.

Hexagonal (Ports & Adapters). Dependencies flow inward: adapter → application → domain.

- `domain/` — pure-Java business logic, models, ports. No Spring, no `jakarta.persistence`.
- `application/` — `@Service` use-case orchestration only; `port/in` and `port/out` interfaces.
- `adapter/in/web/` — thin `@RestController` + DTOs.
- `adapter/out/persistence/` — JPA repositories and entities (never imported by domain).

## Development Process

Follow these steps for every feature; do NOT skip steps. Each step is a skill — invoke it.

1. **Discovery** — `/discover`. Propose rules, resolve open questions interactively, save a draft
   spec to `doc/specs/`. STOP for user review; do not proceed with unresolved questions, and
   re-read the final spec before continuing.
2. **Acceptance test** — `/accept`. One failing acceptance test for the NEXT rule only; keep it
   red until Step 3 turns it green, then move to the next rule.
3. **TDD inner loop** — `/tdd`. RED → GREEN → REFACTOR, one cycle, run all tests, then STOP.
4. **Review** — `/review`. Verify coverage and boundaries, no AI smells. Update the rule files
   above if new conventions emerged.