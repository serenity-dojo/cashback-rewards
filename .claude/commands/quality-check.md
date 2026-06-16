# Quality Check Pipeline

Run the following quality agents in sequence
and produce a consolidated report.

## Step 1: Spec Compliance
Run the spec-compliance agent against docs/specs/
and src/test/. Capture its COVERED/PARTIAL/MISSING
output.

## Step 2: Architecture Review
Run the architecture-guardian agent against the
full codebase. Capture VIOLATION/WARNING/NOTE
findings.

## Step 3: Mutation Analysis
If target/pit-reports/ exists, run the
mutation-analyst agent against the latest report.
Otherwise, note that PIT has not been run.

## Output
Write a consolidated report to
quality-report.md with all findings
grouped by severity.
