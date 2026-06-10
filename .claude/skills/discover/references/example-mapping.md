# Example Mapping — method and quality bar

Example Mapping is a short, structured conversation that breaks a user story into
the rules that govern it, grounds each rule in concrete examples, and exposes the
questions that block agreement. It produces a shared understanding *before* any
test or code is written. The four building blocks map to the classic four-colour
cards:

| Card (colour) | What it is | In our specs |
|---|---|---|
| **Story** (yellow) | The user story under discussion | The `As a… I want… so that…` heading |
| **Rule** (blue) | One testable business constraint | `### Rule: Must/Should …` |
| **Example** (green) | A concrete instance that makes a rule unambiguous | `The one where …` or a table row |
| **Question** (red) | An unknown only the user can resolve | `Questions:` — removed before saving |

A healthy map has a handful of rules, each with one or two examples, and few or
no remaining questions. Lots of red cards means the story isn't ready to build.

## Rules

- One rule = one testable business constraint. If a sentence needs an "and" to
  join two independent constraints, split it into two rules.
- State every rule as **"Should…"** or **"Must…"** so it reads as an obligation.
- Rules are about business behaviour, not mechanism. No endpoints, screens, SQL,
  or class names.
- A later spec may **supersede** a rule from an earlier one — say so explicitly
  and name the spec, rather than silently contradicting it.

## Examples

- An example makes a rule concrete and checkable. Default to the **"The one
  where…"** sentence form: *The one where a customer pays $80 at GreenGrocer
  (partner, 3%) and earns $2.40.*
- When a rule's inputs **vary independently**, use a markdown table instead —
  one column per input, one per output. Tables are denser and reveal gaps:

  | Merchant Rate | Purchase Amount | Cashback |
  |---|---|---|
  | 5%   | $100.00 | $5.00 |
  | 2.5% | $40.00  | $1.00 |
  | 0%   | $100.00 | $0.00 |

- Cover the **normal case first**. Only add more examples for a boundary or a
  genuinely different business outcome.
- Each example must show a **distinct** business behaviour, rule boundary, or
  decision outcome. Do NOT add examples that differ only in amount, wording,
  merchant name, or channel when the outcome is the same.

## Counter-examples

- A counter-example is a **valid business boundary or exclusion** — an input the
  rule deliberately does NOT cover, or where the outcome flips. It is never a bug
  report.
- Give at least one per rule **where a meaningful valid edge case exists** (e.g.
  non-partner merchant, $0 purchase, refund vs reversal, cap reached).
- A **boundary row in a table** (the `0%` row above, a `.5`-exact rounding row)
  already satisfies the counter-example requirement for that rule — don't restate
  it as a separate bullet.

## Questions

- Raise a question whenever the rule depends on a decision you can't make for the
  business: retention windows, timezone handling, whether totals can go negative,
  who can read whose data, etc.
- During the session, resolve questions **one at a time** with the
  `AskUserQuestion` tool, offering 3–4 sensible options.
- Fold each answer back into the affected rules/examples, then **delete the
  Questions section**. The saved spec must contain no open questions.

## Quality checklist (apply before showing or saving)

- [ ] Plain business language throughout. No UI steps, no implementation detail.
- [ ] Every rule starts with "Should" or "Must" and states one constraint.
- [ ] Normal case appears first for each rule.
- [ ] No two examples differ only in amount, wording, merchant, or channel.
- [ ] A rule with independently-varying inputs is a table, not a bullet list.
- [ ] When a rule is a table, the same scenarios are NOT also listed as bullets —
      a bullet appears only if it adds a distinct rule, boundary, or outcome the
      table can't capture.
- [ ] Each rule with a meaningful edge case has at least one counter-example
      (a boundary table row counts).
- [ ] Duplicate or near-duplicate examples merged away — the set is **minimal but
      complete**.
- [ ] No unresolved questions remain in the final spec.

The aim is the smallest set of rules and examples that fully pins down the
behaviour — compact enough to read in a minute, complete enough to drive the
acceptance tests.