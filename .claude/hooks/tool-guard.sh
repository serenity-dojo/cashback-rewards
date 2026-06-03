#!/bin/bash
# PreToolUse hook — guard tool calls
# stdin: JSON with tool_name, tool_input
# exit 0: allow (auto-approve), exit 2: block
# To just observe without deciding, exit 1 (non-blocking)

INPUT=$(cat)
TOOL=$(echo "$INPUT" | jq -r '.tool_name // empty')
TOOL_INPUT=$(echo "$INPUT" | jq -r '.tool_input // empty')

# ─── AUTO-APPROVE safe read-only tools ───
case "$TOOL" in
  Read|Glob|Grep)
    exit 0
    ;;
esac

# ─── GUARD Bash commands ───
if [ "$TOOL" = "Bash" ]; then
  COMMAND=$(echo "$TOOL_INPUT" | jq -r '.command // empty')

  # Block destructive patterns
  if echo "$COMMAND" | grep -qE 'rm -rf|drop table|truncate|DELETE FROM|format |mkfs'; then
    echo "BLOCKED: Destructive command detected: $COMMAND" >&2
    exit 2
  fi

  # Block commands that modify git history
  if echo "$COMMAND" | grep -qE 'git reset --hard|git push.*--force|git clean -fd'; then
    echo "BLOCKED: Destructive git operation: $COMMAND" >&2
    exit 2
  fi

  # Auto-approve safe build/test commands
  if echo "$COMMAND" | grep -qE '^\./mvnw (test|verify|clean|package|spring-boot:run)'; then
    exit 0
  fi

  # Auto-approve read-only git commands
  if echo "$COMMAND" | grep -qE '^git (status|log|diff|branch|show)'; then
    exit 0
  fi
fi

# ─── GUARD Write/Edit — log but allow ───
if [ "$TOOL" = "Write" ] || [ "$TOOL" = "Edit" ]; then
  FILE=$(echo "$TOOL_INPUT" | jq -r '.file_path // .path // empty')

  # Block writes to protected files
  if echo "$FILE" | grep -qE '(application\.yaml|pom\.xml|\.claude/settings\.json)$'; then
    echo "BLOCKED: Cannot modify protected file: $FILE. Edit these files manually." >&2
    exit 2
  fi
fi

# All other tools — let the normal permission flow handle it
exit 1
