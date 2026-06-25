#!/bin/bash
# ──────────────────────────────────────────────────────────────────────
# Test-Convention Coach (PostToolUse)
#
# Fires after Claude writes or edits a test file. Checks two things:
#   1. Method names describe business rules, not operations
#      (catches testCalculateShipping, testPurchase, testProcess, etc.)
#   2. Every @Test method has a @DisplayName annotation
#
# The coach puts feedback into Claude's context so Claude self-corrects
# in the same turn. It never runs the tests and never blocks the workflow.
#
# ADAPTING FOR YOUR PROJECT:
#   - TEST_FILE_PATTERN: change the suffix to match your test files
#       Python:  "*_test.py" or "test_*.py"
#       JS/TS:   "*.test.ts" or "*.spec.ts"
#       C#:      "*Tests.cs"
#   - GENERIC_PATTERNS: add verbs common in your domain
#       e.g. "testFetch" "testParse" "testLoad" "testRender"
#   - @DisplayName check: remove or replace for non-JUnit frameworks
#       pytest: check for descriptive function names (no test_1, test_2)
#       Jest:   check for describe/it blocks with meaningful descriptions
# ──────────────────────────────────────────────────────────────────────

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

# Only check test files — skip everything else silently
# ADAPT: Change this pattern for your language/framework
TEST_FILE_PATTERN="*Test.java"
if [[ "$FILE_PATH" != $TEST_FILE_PATTERN ]]; then
  exit 0
fi

# Bail out if the file doesn't exist (e.g. it was deleted)
if [[ ! -f "$FILE_PATH" ]]; then
  exit 0
fi

ISSUES=""
FILENAME=$(basename "$FILE_PATH")

# ── Rule 1: Check for generic test method names ──────────────────────
# Catches: testCalculate, testProcess, testHandle, testGet, testCreate,
#          testUpdate, testDelete, testSave, testSend, testPurchase, etc.
# Passes: europeanParcelCostsExpected, domesticOrderOver75GetsFreeShipping
#
# ADAPT: Add verbs common in your domain
GENERIC_PATTERNS=(
  "void test[A-Z][a-z]+\("                    # testCalculate(, testPurchase(
  "void test[A-Z][a-z]+[A-Z][a-z]+\("         # testCalculateShipping(, testGetUser(
  "void test[A-Z][a-z]+[A-Z][a-z]+[A-Z]\w*\(" # testCalculateShippingCost(
)

for pattern in "${GENERIC_PATTERNS[@]}"; do
  MATCHES=$(grep -nE "$pattern" "$FILE_PATH" 2>/dev/null || true)
  if [[ -n "$MATCHES" ]]; then
    while IFS= read -r line; do
      LINE_NUM=$(echo "$line" | cut -d: -f1)
      # Extract the method name
      METHOD=$(echo "$line" | grep -oE 'test[A-Za-z]+' | head -1)
      if [[ -n "$METHOD" ]]; then
        ISSUES+="  \`$METHOD\` (line $LINE_NUM) names the operation, not the rule."$'\n'
        ISSUES+="  Rename to describe the business scenario, e.g."$'\n'
        ISSUES+="  europeanParcelOver2kgCostsExpectedRate"$'\n'$'\n'
      fi
    done <<< "$MATCHES"
  fi
done

# ── Rule 2: Check for @Test without @DisplayName ─────────────────────
# Finds @Test methods where @DisplayName is not adjacent (either the
# line before or the line after). In JUnit 5, both orderings are valid:
#   @DisplayName("...") @Test       — DisplayName first
#   @Test @DisplayName("...")       — Test first
#
# ADAPT: Remove this check for non-JUnit frameworks, or replace with
# your framework's equivalent (e.g. pytest marker, Jest describe text)

# Read file into an array for lookahead/lookbehind
mapfile -t LINES < "$FILE_PATH"
TOTAL=${#LINES[@]}
MISSING_DISPLAY=()

for ((i=0; i<TOTAL; i++)); do
  TRIMMED=$(echo "${LINES[$i]}" | sed 's/^[[:space:]]*//')
  if [[ "$TRIMMED" == "@Test" || "$TRIMMED" == "@Test "* ]]; then
    LINE_NUM=$((i + 1))
    HAS_DISPLAY=false

    # Check previous non-blank line
    for ((j=i-1; j>=0; j--)); do
      PREV=$(echo "${LINES[$j]}" | sed 's/^[[:space:]]*//')
      if [[ -n "$PREV" ]]; then
        [[ "$PREV" == *"@DisplayName"* ]] && HAS_DISPLAY=true
        break
      fi
    done

    # Check next non-blank line (DisplayName can come after @Test)
    if [[ "$HAS_DISPLAY" == false ]]; then
      for ((j=i+1; j<TOTAL; j++)); do
        NEXT=$(echo "${LINES[$j]}" | sed 's/^[[:space:]]*//')
        if [[ -n "$NEXT" ]]; then
          [[ "$NEXT" == *"@DisplayName"* ]] && HAS_DISPLAY=true
          break
        fi
      done
    fi

    if [[ "$HAS_DISPLAY" == false ]]; then
      MISSING_DISPLAY+=("$LINE_NUM")
    fi
  fi
done

if [[ ${#MISSING_DISPLAY[@]} -gt 0 ]]; then
  for ln in "${MISSING_DISPLAY[@]}"; do
    ISSUES+="  @Test at line $ln is missing @DisplayName."$'\n'
    ISSUES+="  Add @DisplayName(\"The one where ...\")"$'\n'$'\n'
  done
fi

# ── Output ────────────────────────────────────────────────────────────
if [[ -n "$ISSUES" ]]; then
  echo "[test-coach] $FILENAME:"
  echo ""
  echo "$ISSUES"
  echo "Fix these before continuing. Good test names are documentation."
fi

# Always exit 0 — this hook coaches, it never blocks
exit 0
