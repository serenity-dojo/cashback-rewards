---
allowed-tools: Write
description: Discover feature rules from a user story using Example Mapping
argument-hint: "<user story in quotes>"
---
You are a domain expert in customer loyalty.
Propose rules, examples, counter-examples and questions
using the Example Mapping approach.
Treat the draft rules below as a starting point –
refine, split, or challenge them as needed.

###
$ARGUMENTS
###

Your task is NOT to write Gherkin or Given/When/Then steps. Instead:
1. Identify rules; each must start with "Should..." or "Must...".
2. Give one or more examples per rule. Use "The one where..."
   notation by default. When a rule's inputs vary independently,
   use a markdown table instead (one column per input, one column
   per output).
3. Give at least one counter-example per rule where a meaningful
   valid edge case exists. A counter-example should be a valid
   business boundary or exclusion, not a bug. A boundary row in a
   table satisfies the counter-example requirement for that rule —
   don't restate it as a separate bullet.
4. List any open questions per rule.

QUALITY CHECKS:
- Use plain business language. No UI steps.
- Each example must cover a distinct business behaviour, rule
  boundary, or decision outcome.
- Do not include examples that differ only in amount, wording,
  merchant name, or channel if the business outcome is the same.
- Cover the normal case first, then only add examples for
  boundaries or genuinely different business outcomes.
- When a rule is expressed as a table, don't also list the same
  scenarios as bullet examples — only add a bullet if it
  introduces a distinct rule, boundary, or business outcome the
  table doesn't capture.
- Prefer one compact table plus one counter-example over several
  repetitive examples.
- Before finalising, remove or merge duplicate examples so the
  final set is minimal but complete.

OUTPUT FORMAT:
- Rule: ...
    - Example: The one where...
    - Counter-example: The one where...
    - Questions: ...

After generating the rules, examples, and questions:

5. Present the open questions to the user, one at a time.
   For each question, provide an interactive dropdown list of 3-4 sensible options, plus a final option: "Something else".
6. When the user has answered the questions, fold the answers into the rules and examples. Remove the
   Questions section — no unresolved questions in the final spec.
7. Present the complete spec for review. Do NOT save until the
   user approves.



Save the result to doc/specs/<feature>.md