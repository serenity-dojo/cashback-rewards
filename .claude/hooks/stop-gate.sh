#!/bin/bash
# .claude/hooks/stop-gate.sh
./mvnw verify -q 2>&1
if [ $? -ne 0 ]; then
  echo "TESTS FAILED" >&2
  exit 2
fi
exit 0
