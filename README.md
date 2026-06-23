# Cashback Rewards

**A hands-on Spring Boot project showing how to use AI, Example Mapping, acceptance tests and TDD to build APIs in a more disciplined way.**

Most AI coding examples start with the code.

This project starts with the requirements.

The idea is simple: AI is much better at implementing requirements than discovering them by itself. So instead of asking Claude Code to “build a cashback rewards API”, we use a more structured workflow:

1. **Discover** the rules, examples, edge cases, assumptions, and open questions.
2. **Acceptance** — turn those examples into executable acceptance tests.
3. **TDD** — drive the implementation through red-green-refactor cycles.
4. **Review** — check the result beyond the passing tests.

The sample application is a cashback rewards API built with Spring Boot, hexagonal architecture, comprehensive test coverage, and Claude Code commands that guide each step of the workflow.

This repo is also the hands-on project for the Serenity Dojo Udemy course:

**[Spec-Driven Development and TDD with AI](https://bit.ly/spec-driven-development-in-java)**

The full course walks through the process step by step, but the repository is open so you can explore the approach, inspect the specs, and see how the workflow is put together.

**[Enrol in the course on Udemy →](https://bit.ly/spec-driven-development-in-java)**

---

## What You'll Build

A Spring Boot microservice that calculates cashback rewards for customer purchases at partner merchants.

Along the way, you'll learn to:

- **Discover requirements with AI** — use Example Mapping and a custom `/discover` command to turn user stories into precise specifications with rules, examples, and counter-examples
- **Drive development with acceptance tests** — write acceptance tests directly from specs, then implement features using red-green-refactor TDD cycles
- **Configure Claude Code for your project** — set up `CLAUDE.md`, path-scoped architecture rules, and custom commands that make Claude a project-aware pair programmer
- **Enforce architecture automatically** — use rules files and hooks to guarantee hexagonal architecture compliance, not just suggest it
- **Build with confidence** — every feature is backed by acceptance tests, domain unit tests, controller tests, application tests, and repository tests

## Prerequisites

- **Java 25** — download from [Adoptium](https://adoptium.net/) or [SDKMAN](https://sdkman.io/)
- **Maven** — included via Maven Wrapper (`./mvnw`), no separate install needed
- **Docker** (optional) — only needed for PostgreSQL in later sections
- **Claude Code** — install from [claude.ai/claude-code](https://claude.ai/claude-code) (required from Section 5 onwards)

## Quick Start

```bash
# Clone the repository
git clone https://github.com/serenity-dojo/cashback-rewards.git
cd cashback-rewards

# Check out the starting branch for your current section (see Branch Map below)
git checkout section-4/start

# Run the tests
./mvnw test

# Run unit + acceptance tests
./mvnw verify
```

If the tests pass, you're ready to go.

## Branch Map

Each course section has a **start** branch, where you begin working, and a **solution** branch, with the completed code.

Check out the start branch, follow along with the videos, and compare your work against the solution when you're done.

Sections 1–3 and Section 10 (How To Write Effective Prompts in Claude Code) are theory and slides — no code branches needed. Hands-on coding starts at Section 4.

| Course Section | Branches | What's Introduced |
|---|---|---|
| **Section 4 — AI-Driven Requirements Discovery** | `section-4/start` · `section-4/solution` | The custom `/discover` command. Using Example Mapping with AI to write feature specifications. |
| **Section 5 — The CLAUDE.md File** | `section-5/start` · `section-5/solution` | The `CLAUDE.md` instruction file. Build commands, coding conventions, architecture rules, and the development process. |
| **Section 6 — Automating Architecture Rules** | `section-6/start` · `section-6/solution` | Path-scoped rules in `.claude/rules/`. Domain rules, persistence rules, test rules, and web rules. |
| **Section 7 — TDD with AI** | `section-7/start` · `section-7/solution` | The `/acceptance`, `/tdd`, and `/review` commands. Full TDD cycle: acceptance tests, inner-loop red-green-refactor, and code review. |
| **Section 8 — API Contracts** | `section-8/start` · `section-8/solution` | OpenAPI contract-driven development. Defining the API contract first, then implementing against it. |
| **Section 9 — Refactoring & Persistence** | `section-9/start` · `section-9/solution` | Refactoring from in-memory to PostgreSQL with Flyway migrations. Acceptance tests, persistence adapters, and JPA entities. |
| **Section 11 — Hooks** | `section-11/start` · `section-11/solution` | Claude Code hooks in `.claude/hooks/` and `settings.json` — file-protection, session-start, and stop-gate hooks that enforce the workflow automatically. Adds a minimum-purchase-threshold feature. |
| **Section 12 — From Commands to Skills** | `section-12/start` · `section-12/solution` | Migrating `.claude/commands/` to model-invocable Skills (`.claude/skills/<name>/SKILL.md`) with references and templates. Adds the `/commit-summary` skill. |
| **Section 13 — Subagents & Mutation Testing** | `section-13/start` · `section-13/solution` | Custom subagents in `.claude/agents/` (architecture-guardian, mutation-analyst, spec-compliance) and a `/quality-check` pipeline. Adds PIT mutation testing to the build. |

### How to switch branches

```bash
# See all available branches
git branch -a

# Check out a starting point
git checkout section-7/start

# If you have local changes you want to keep
git stash
git checkout section-9/start
git stash pop
```

## Project Structure

The project follows **hexagonal architecture** (ports and adapters).

Dependencies flow inward: adapters depend on the application layer, which depends on the domain. The domain never imports Spring or JPA.

```text
src/main/java/com/serenitydojo/cashback_rewards/
├── domain/                          # Pure business logic — no frameworks
│   ├── model/                       # Entities and value objects (Java records)
│   │   ├── Merchant.java
│   │   ├── CashbackRecord.java
│   │   ├── ProductCategory.java
│   │   └── ProductCashbackTotal.java
│   ├── service/                     # Domain services (e.g. CashbackCalculator)
│   └── exception/                   # Business rule exceptions
│
├── application/                     # Use-case orchestration
│   ├── port/in/                     # Inbound ports (interfaces for controllers)
│   ├── port/out/                    # Outbound ports (interfaces for persistence)
│   └── service/                     # Application services (@Service beans)
│
└── adapter/                         # Framework-dependent code
    ├── in/web/                      # REST controllers (@RestController)
    └── out/persistence/             # JPA repositories and entities
```

### Test Structure

Tests mirror the production structure and follow Maven naming conventions:

```text
src/test/java/com/serenitydojo/cashback_rewards/
├── acceptance/                      # *IT.java — end-to-end, run with mvn verify
│   ├── BasicCashbackCalculationIT.java
│   ├── MerchantCategoriesAndEligibilityIT.java
│   └── TotalCashbackPerProductIT.java
│
├── domain/                          # Plain JUnit + AssertJ, no Spring
├── application/service/             # Unit tests for application services
├── adapter/in/web/                  # @WebMvcTest for controllers
└── adapter/out/persistence/         # @DataJpaTest for repositories
```

Run `./mvnw test` for unit tests only, or `./mvnw verify` for unit + acceptance tests.

## Specifications

Feature specifications live in `doc/specs/` and follow the **Example Mapping** format: rules, examples, and counter-examples.

These specs drive both the acceptance tests and the TDD implementation.

| Spec File | Feature |
|---|---|
| `earning-cashback.md` | Core user story for earning cashback on purchases |
| `basic-cashback-calculation.md` | Cashback rate calculation, time-effective rates, settlement rules |
| `merchant-categories-and-eligibility.md` | Category-based rates (MCC codes), eligibility rules, card status checks |
| `cashback-monthly-report.md` | Monthly cashback reporting |
| `total-cashback-per-product.md` | System-wide cashback totals by product category |

## Claude Code Setup

From Section 5 onwards, the project includes Claude Code configuration files that teach Claude how to work with this codebase.

### CLAUDE.md

The main instruction file at the project root.

It contains build commands, coding conventions, the four-step development process, testing standards, and architecture rules.

Examples include:

- use `BigDecimal` for money
- use Java 25 features where they make sense
- prefer constructor injection
- keep the domain model free from Spring and JPA
- follow the `Discover → Acceptance → TDD → Review` workflow

### Rules (`.claude/rules/`)

Path-scoped rules activate automatically when Claude edits files in specific directories:

| Rule File | Scope | Key Constraints |
|---|---|---|
| `domain-rules.md` | `src/**/domain/**` | No Spring imports, no JPA, pure Java only |
| `persistence-rules.md` | `src/**/adapter/out/persistence/**` | JPA entities here only, implement outbound ports |
| `test-rules.md` | `src/test/**` | Naming conventions (`*Test` vs `*IT`), never recalculate expected values |
| `web-rules.md` | `src/**/adapter/in/web/**` | Thin controllers, DTOs only, `@Valid` on request bodies |

### Commands (`.claude/commands/`)

Reusable prompts for the AI-assisted development workflow:

| Command | Model | Purpose |
|---|---|---|
| `/discover` | opus | Run Example Mapping to discover rules, examples, and questions from a user story |
| `/acceptance` | sonnet | Write a failing acceptance test for the next spec rule |
| `/tdd` | sonnet | Run one RED → GREEN → REFACTOR TDD cycle |
| `/review` | opus | Architecture and code quality review of uncommitted changes |

### Hooks (`.claude/settings.json`)

The project includes a `PostToolUse` hook that automatically runs `mvn test` after every file edit, ensuring Claude never moves forward with broken code.

## Architecture Decisions

**Money handling** — All monetary values use `BigDecimal` with explicit `RoundingMode.DOWN` and scale 2. Never `float`, `double`, or `int` for money.

**Hexagonal architecture** — Domain code is framework-free. Spring and JPA live only in adapters. This makes the domain testable with plain JUnit — no Spring context needed.

**In-memory repositories for tests** — Acceptance tests use in-memory implementations of outbound ports, so they run fast without a database. Repository tests use `@DataJpaTest` with H2 in PostgreSQL compatibility mode.

**Specs as the source of truth** — Feature specifications in `doc/specs/` define the contract. Acceptance tests verify the contract. Production code implements it. When specs change, tests change first.

## Useful Commands

```bash
./mvnw test                               # Unit tests only
./mvnw verify                             # Unit + acceptance tests
./mvnw -Dtest=CashbackCalculatorTest test # Single test class
./mvnw spring-boot:run                    # Run the app (needs PostgreSQL)
./mvnw clean package                      # Build the JAR
```

### With Claude Code

```bash
claude                                      # Start a Claude Code session
/discover "As a customer, I want..."        # Run Example Mapping on a user story
/acceptance Rule1 @doc/specs/feature.md     # Write an acceptance test for a spec rule
/tdd ClassName#methodName                   # Run one TDD cycle
/review                                     # Review uncommitted changes
```

---

## About the Course

This repository is the hands-on project for **[Spec-Driven Development and TDD with AI](https://bit.ly/spec-driven-development-in-java)**.

In the course, we build the cashback rewards API step by step using Claude Code as an AI pair programmer — but not just as a code generator.

The workflow is:

- discover requirements with Example Mapping
- write precise feature specifications
- generate acceptance tests from the specs
- implement with TDD
- keep the code aligned with hexagonal architecture
- use Claude Code commands, rules, and hooks to make the process repeatable

The goal is to show how to use AI in a disciplined development workflow, where tests, examples, and architecture rules keep the AI constrained.

The course is designed for Java developers who want to use AI effectively — not as a code generator, but as a disciplined development partner.

**[Enrol in the course on Udemy →](https://bit.ly/spec-driven-development-in-java)** · Built by [Serenity Dojo](https://www.serenity-dojo.com/)
