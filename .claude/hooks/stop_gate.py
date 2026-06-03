#!/usr/bin/env python3
"""Stop hook — run tests before Claude can declare done."""

import subprocess
import sys

print("Running mvn verify before finishing...", file=sys.stderr)

result = subprocess.run(
    ["./mvnw", "verify", "-q"],
    capture_output=True, text=True
)

if result.returncode != 0:
    print("TESTS FAILED — you are not done yet.", file=sys.stderr)
    print("", file=sys.stderr)
    print("Fix the failing tests before finishing. Here are the failures:", file=sys.stderr)
    print("", file=sys.stderr)
    output = result.stdout + result.stderr
    for line in output.splitlines():
        if any(kw in line for kw in ("FAILURE", "ERROR", "Failures:", "BUILD FAILURE")):
            print(line, file=sys.stderr)
    sys.exit(2)

print("All tests pass. You may finish.", file=sys.stderr)
sys.exit(0)
