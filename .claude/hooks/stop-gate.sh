#!/bin/bash
# Stop hook — run tests before Claude can declare "done"
# exit 0: tests pass, Claude may stop
# exit 2: tests fail, Claude must keep working
# stderr: sent back to Claude as context

echo "Running mvn verify before finishing..." >&2

# Run tests and capture output
TEST_OUTPUT=$(./mvnw verify -q 2>&1)
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
  echo "TESTS FAILED — you are not done yet." >&2
  echo "" >&2
  echo "Fix the failing tests before finishing. Here are the failures:" >&2
  echo "" >&2
  # Extract just the failure summary
  echo "$TEST_OUTPUT" | grep -A 3 -E '(FAILURE|ERROR|Tests run:.*Failures: [1-9]|BUILD FAILURE)' | tail -20 >&2
  exit 2
fi

echo "All tests pass. You may finish." >&2
exit 0
