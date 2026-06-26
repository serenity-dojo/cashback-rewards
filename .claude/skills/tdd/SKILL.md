---
name: tdd
model: claude-sonnet-4-6
allowed-tools: Read, Write, Edit, Bash
description: >-
  Run ONE TDD inner-loop cycle — RED → GREEN → REFACTOR → CHALLENGE → STOP
  (Step 3 of the development process). Use to drive a single failing test to
  green with the minimum production code, then propose the next edge case. One
  cycle per invocation; stops and waits for the user before continuing.
argument-hint: "<test class or method to drive> [--to-green <acceptance test>]"
---

Run ONE inner TDD cycle for: $ARGUMENTS — one failing test driven to green, then
refactored, then STOP.

"ONE cycle" means **one test**: a single new `@Test`, or one new row/case added
to a parameterized test (a new boundary is the next RED — add a row, don't spawn
a class). It does NOT mean "drive until the acceptance test passes" — that outer
ATDD loop usually takes several inner cycles across several invocations, and the
acceptance test stays red until the last one. After this cycle, stop and wait;
the edge case you propose in CHALLENGE becomes the next RED in a *new* invocation.
(The one exception is run-to-green mode — see ESCAPE below — which the user must
ask for explicitly with `--to-green`; never assume it.)

Read CLAUDE.md for architecture and testing conventions before writing any code.

**Where this fits — the double loop.** This is the *inner* loop. It usually runs
inside an outer ATDD loop: `/accept` has written one failing acceptance test for
the spec rule, and that test is your **goalpost, not your target**. Your job is
not to make it green in one cycle — it's to drive the sub-rules beneath it
through RED → GREEN → REFACTOR at the lowest tier that proves each, until the
acceptance test goes green *as a result*. Expect it to stay red across several
cycles; never weaken it to get an early green. The acceptance test documents the
end-to-end behaviour as living documentation; your inner cycles drive the logic
beneath it. Invoked without an outer
acceptance test (driving a value object or service rule directly), the same loop
applies — there's just no outer goalpost.

## AIM — choose the tier before you write the test

Decide the **lowest tier that can prove this rule** before writing the test —
see the tier table in `test-patterns.md` ("Pick the tier deliberately"), and read
that file's compiled example for the tier you pick before writing the RED test.
Pure logic — a calculation, a
classification, parsing, rounding — belongs in a service or value-object test
with no Spring context. Reach for a slice or full-context test only to prove
wiring you can't prove otherwise (HTTP mapping, serialization, security).

If the behaviour you're about to drive is a *branch inside* a method an
acceptance test already covers — case-folding, a null guard, a default case, a
boundary — **drop down**: drive it with its own RED at the lower tier. Don't let
it ride along inside the acceptance test, and don't reach through an aggregate to
assert a sub-value that wants its own seam.

## RED — write the test; watch it fail for the right reason

Write the one test at the tier you chose, then run it. It must fail with an
**assertion failure** — the rule is wrong — not a compile or wiring error. A test
that *errors* is testing plumbing, not the rule; fix the seam until it fails for
the right reason. Read the failure message; understand WHY it fails before
writing any production code. If it already passes, STOP — something is wrong.

## GREEN — minimum code to pass

Write the MINIMUM production code to make this one test pass.
Minimum means minimum:
- No extra methods "while we're here"
- No anticipating the next test
- No abstractions until refactoring demands them

Fake it — hard-code, return a constant — when one example can't yet justify the
general form, and triangulate to the real logic as the next example forces it.
But when the implementation is obvious and the test already pins the general form
(e.g. a parameterized table with several rows), just write it — don't fake what
you'd only undo next cycle.

Respect architecture boundaries:
- Domain code: pure Java, no Spring, no JPA
- Controllers: thin delegation, no business logic
- Persistence: JPA entities stay in adapter layer
- Let the scoped rules guide you

## REFACTOR — clean up production AND test code with confidence

All tests are green. Now improve the code:
- Remove duplication; extract clear names; simplify conditionals
- Check that the code reads like the spec
- Refactor the **test** too — extract helpers, dedupe setup and builders
- Push *exhaustive* coverage down, not documentation. Once you've proven a rule's
  boundaries and edge cases at a lower tier, keep the full **enumeration** there —
  don't re-run every boundary through the whole stack. But **keep at least one
  clear, representative example of each rule at the acceptance tier**: it's living
  documentation and must still read as the feature's executable specification.
  Overlap on that headline example is intentional, not duplication to remove.
  Inner loops own the exhaustive logic; the acceptance suite documents the rules.

Run ALL tests after refactoring — not just the current one.
If anything breaks, fix it before moving on.

## LISTEN — what is the test telling you about the design?

Test pain is design feedback, not a testing inconvenience. If GREEN was hard to
write minimally, if you had to reach through an aggregate to assert a sub-value,
if there's no seam to test a rule in isolation, or if the same private logic
keeps accreting new cases — the design is asking for a change. The fix is usually
to **extract the concept into its own type** (value object / service) with its
own seam, then test it directly at the lower tier.

Surface this to the user as a proposed refactor with a one-line rationale; don't
silently work around it, and don't perform a large extraction without saying so.

## CHALLENGE — drive out edge cases, at the right tier

Before stopping, ask: "What else should this do?" "What input could break this?"

Consider: zero/empty input, not-found, boundary values, rounding, invalid state,
null, negative amounts, duplicate requests.

For each edge case you propose, **name the tier it belongs at**. Most edge cases
(null, empty, boundary, rounding, unknown-enum) live *below* the acceptance test
that surfaced them. Propose at least one to the user; if approved, that edge case
becomes the next RED — at its own tier.

## STOP

Report what you changed:
- Which test is now passing, and at which tier
- What production code you wrote or modified
- What you refactored (production and test)
- Any design pressure you noticed in LISTEN, and the refactor you propose
- What edge case you propose next, and the tier it belongs at

Do NOT write additional tests beyond the one specified.
Do NOT add unrequested features or "improvements".
Do NOT modify any existing test to make it pass — fix the production code instead.
Do NOT weaken the outer acceptance test to get an early green.

Wait for the user before starting the next cycle.

## ESCAPE — run-to-green mode (opt-in only)

The default is one inner cycle, then STOP. When — and only when — the user
explicitly passes `--to-green` (optionally naming the acceptance test to close),
run inner cycles back-to-back without pausing, until that acceptance test goes
green. Only the per-cycle STOP relaxes; the discipline does not:

- Each cycle still runs AIM → RED → GREEN → REFACTOR → LISTEN in full, at the
  lowest tier that proves its rule.
- CHALLENGE self-feeds: pick the next edge case yourself and make it the next
  RED, instead of waiting for approval.
- Stop early and hand back to the user if any of these hold: the acceptance test
  is green; you cannot write a cycle that fails for the right reason; or LISTEN
  surfaces a design change big enough to warrant a real extraction — never
  perform a large refactor unattended, that is the one thing escape mode still
  pauses for.
- Never weaken the acceptance test to reach green.
- Report once at the end: every cycle run (test + tier), the production code
  written, the refactors done, and any design pressure deferred for the user.

---

## Reference — test patterns by tier

Per-tier, compiled-and-run examples (Spring Boot 4 / Spring Framework 7 / JUnit
Jupiter 6) live in **`test-patterns.md`**, alongside this file. It holds the
"Pick the tier deliberately" table and one worked example per layer — value
object, service, `@WebMvcTest`, `@JsonTest`, `@DataJpaTest`, `@SpringBootTest`
acceptance, `RestTestClient` — plus the data-driven → `@ParameterizedTest`
pattern and naming reminders. In AIM, read the entry for the tier you chose
before writing the RED test; don't reproduce its imports from memory (Boot 4
moved several test packages and removed `@MockBean`).
