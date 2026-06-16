# <Feature Title>

**As a <role>, I want <capability> so that <benefit>.**

<!--
  House style for doc/specs/. Keep this structure for every spec:
    - H1 title (Title Case), then the user story in bold on one line.
    - One "## Rules and Examples" section holding every rule.
    - One "### Rule: Must/Should …" per rule, separated by a "---" rule line.
    - Examples as "The one where …" bullets, OR a markdown table when the rule's
      inputs vary independently.
    - At least one "Counter-example" per rule that has a meaningful edge case
      (a boundary table row counts and need not be repeated as a bullet).
    - NO "Questions" section: every question must be resolved before saving.
  Delete every comment (including this block) from the finished spec.
-->

## Rules and Examples

### Rule: Must <single testable constraint stated as an obligation>

<Optional one- or two-sentence clarification: scope, a key term, or how this rule
relates to / supersedes a rule in another spec. Omit if the rule is self-evident.>

<!-- Table form — use when inputs vary independently. Include a boundary row. -->

| <Input A> | <Input B> | <Output> |
|---|---|---|
| <normal>   | <normal>   | <result> |
| <variant>  | <variant>  | <result> |
| <boundary> | <boundary> | <result> |

<One line naming the boundary row and why it matters.>

---

### Rule: Should <single testable constraint stated as an obligation>

<!-- Bullet form — use when there is a single primary scenario plus exclusions. -->

- **Example:** The one where <concrete normal case with real values and the outcome>.
- **Counter-example:** The one where <valid business boundary or exclusion — not a bug — and the resulting outcome>.

---

<!-- Optional. Add only once questions are resolved and decisions affect build. -->
## Resolved decisions (for implementation)

- **<Decision>:** <the answer the user chose during discovery>
- **Out of scope:** <anything explicitly excluded from this story>
