#!/bin/bash
# UserPromptSubmit hook — route prompts and block dangerous requests
# stdin: JSON with the user's prompt
# stdout: injected into Claude's context
# stderr: message back to Claude (for routing instructions)
# exit 0: allow, exit 2: block

INPUT=$(cat)
PROMPT=$(echo "$INPUT" | jq -r '.prompt // empty')

# Lowercase for matching
LOWER=$(echo "$PROMPT" | tr '[:upper:]' '[:lower:]')

# ─── BLOCK dangerous requests ───
if echo "$LOWER" | grep -qE 'delete (all|everything)|drop (table|database)|rm -rf|format disk'; then
  echo "BLOCKED: This prompt contains a potentially destructive request." >&2
  echo "Please rephrase without destructive operations." >&2
  exit 2
fi

# ─── ROUTE: spec/discovery questions → remind about /discover ───
if echo "$LOWER" | grep -qE 'new feature|user story|requirement|what should|discover'; then
  echo "This looks like a requirements discovery question. Consider using /discover for structured Example Mapping." >&2
  exit 0
fi

# ─── ROUTE: test questions → remind about /accept ───
if echo "$LOWER" | grep -qE 'acceptance test|write.*test for|test.*spec|test.*rule'; then
  echo "This looks like a test-writing task. Consider using /accept to generate tests from a spec rule." >&2
  exit 0
fi

# ─── ROUTE: money/financial → inject precision reminder ───
if echo "$LOWER" | grep -qE 'cashback|money|amount|price|balance|decimal|currency|financial'; then
  echo "IMPORTANT: This involves monetary values. Use BigDecimal with RoundingMode.DOWN and scale 2. Never use float or double. Hardcode expected values in tests — never recalculate." >&2
  exit 0
fi

# No routing needed
exit 0
