#!/usr/bin/env python3
"""UserPromptSubmit hook — route prompts and block dangerous requests."""

import json
import re
import sys

data = json.load(sys.stdin)
prompt = data.get("prompt", "")
lower = prompt.lower()

# Block dangerous requests
if re.search(r"delete (all|everything)|drop (table|database)|rm -rf|format disk", lower):
    print("BLOCKED: This prompt contains a potentially destructive request.", file=sys.stderr)
    print("Please rephrase without destructive operations.", file=sys.stderr)
    sys.exit(2)

# Route: spec/discovery questions
if re.search(r"new feature|user story|requirement|what should|discover", lower):
    print("This looks like a requirements discovery question. Consider using /discover for structured Example Mapping.", file=sys.stderr)
    sys.exit(0)

# Route: test questions
if re.search(r"acceptance test|write.*test for|test.*spec|test.*rule", lower):
    print("This looks like a test-writing task. Consider using /accept to generate tests from a spec rule.", file=sys.stderr)
    sys.exit(0)

# Route: money/financial
if re.search(r"cashback|money|amount|price|balance|decimal|currency|financial", lower):
    print("IMPORTANT: This involves monetary values. Use BigDecimal with RoundingMode.DOWN and scale 2. Never use float or double. Hardcode expected values in tests — never recalculate.", file=sys.stderr)
    sys.exit(0)

sys.exit(0)
