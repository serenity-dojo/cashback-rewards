#!/bin/bash
# SessionEnd hook — log session summary
# Cannot block (session is already ending)
# Logs to .claude/session-log.jsonl for team analytics

INPUT=$(cat)
SESSION_ID=$(echo "$INPUT" | jq -r '.session_id // "unknown"')

# Calculate duration if we saved start time
LOG_DIR=".claude"
START_FILE="$LOG_DIR/.session-start-time"
DURATION="unknown"

if [ -f "$START_FILE" ]; then
  START_TIME=$(cat "$START_FILE")
  END_TIME=$(date +%s)
  ELAPSED=$((END_TIME - START_TIME))
  MINUTES=$((ELAPSED / 60))
  SECONDS=$((ELAPSED % 60))
  DURATION="${MINUTES}m ${SECONDS}s"
  rm -f "$START_FILE"
fi

# Count what changed during this session
FILES_CHANGED=$(git diff --name-only 2>/dev/null | wc -l | tr -d ' ')
TESTS_ADDED=$(git diff --name-only 2>/dev/null | grep -c 'Test\|IT' || echo "0")
BRANCH=$(git branch --show-current 2>/dev/null || echo "detached")

# Append to session log
LOG_ENTRY=$(jq -n \
  --arg sid "$SESSION_ID" \
  --arg branch "$BRANCH" \
  --arg duration "$DURATION" \
  --arg timestamp "$(date -Iseconds)" \
  --arg files "$FILES_CHANGED" \
  --arg tests "$TESTS_ADDED" \
  '{session_id: $sid, branch: $branch, duration: $duration, timestamp: $timestamp, files_changed: ($files | tonumber), tests_added: ($tests | tonumber)}')

echo "$LOG_ENTRY" >> "$LOG_DIR/session-log.jsonl"

# Print summary to terminal
echo "Session summary: $DURATION on $BRANCH — $FILES_CHANGED files changed, $TESTS_ADDED test files touched" >&2
