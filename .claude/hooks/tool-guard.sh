#!/bin/bash  — .claude/hooks/tool-guard.sh
echo "PostToolUse fired" >> /tmp/hook-debug.log
exit 0

INPUT=$(cat)
TOOL=$(echo "$INPUT" | jq -r '.tool_name')

# Auto-approve read-only tools
case "$TOOL" in
  Read|Glob|Grep) echo '{"decision":"approve"}'; exit 0;;
esac

# Block edits to production config
if [ "$TOOL" = "Edit" ] || [ "$TOOL" = "Write" ]; then
  FILE=$(echo "$INPUT" | jq -r '.tool_input.file_path')
  if echo "$FILE" | grep -q 'application-prod'; then
    echo '{"decision":"deny","reason":"Production config is off-limits"}'; exit 0
  fi
fi
exit 0  # no opinion — use normal permissions
