---
name: tdd
model: claude-sonnet-4-6
allowed-tools: Read, Write, Edit, Bash
description: >-
  Run ONE TDD inner-loop cycle — RED → GREEN → REFACTOR → CHALLENGE → STOP
  (Step 3 of the development process). Use to drive a single failing test to
  green with the minimum production code, then propose the next edge case. One
  cycle per invocation; stops and waits for the user before continuing.
argument-hint: "<test class or method to drive>"
---

Run ONE TDD cycle for: $ARGUMENTS

Read CLAUDE.md for architecture and testing conventions before writing any code.

## RED — confirm the failure

Run the failing test first. Read the failure message.
Understand WHY it fails before writing any production code.
If the test already passes, STOP — something is wrong.

## GREEN — minimum code to pass

Write the MINIMUM production code to make this one test pass.
Minimum means minimum:
- No extra methods "while we're here"
- No anticipating the next test
- No abstractions until refactoring demands them
- Hard-code if that's all this test requires

Respect architecture boundaries:
- Domain code: pure Java, no Spring, no JPA
- Controllers: thin delegation, no business logic
- Persistence: JPA entities stay in adapter layer
- Let the scoped rules guide you

## REFACTOR — clean up with confidence

All tests are green. Now improve the code:
- Remove duplication
- Extract clear names
- Simplify conditionals
- Check that the code reads like the spec

Run ALL tests after refactoring — not just the current one.
If anything breaks, fix it before moving on.

## CHALLENGE — drive out edge cases

Before stopping, ask yourself:
"What else should this do?"
"What input could break this?"

Consider: zero/empty input, not-found, boundary values, rounding, invalid state,
null, negative amounts, duplicate requests.

Propose at least one edge case to the user.
If approved, that edge case becomes the next RED.

## STOP

Report what you changed:
- Which test is now passing
- What production code you wrote or modified
- What you refactored
- What edge case you propose next

Do NOT write additional tests beyond the one specified.
Do NOT add unrequested features or "improvements".
Do NOT modify any existing test to make it pass — fix the production code instead.

Wait for the user before starting the next cycle.
