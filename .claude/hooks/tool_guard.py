#!/usr/bin/env python3
"""PreToolUse hook — guard tool calls."""

import json
import re
import sys

data = json.load(sys.stdin)
tool = data.get("tool_name", "")
tool_input = data.get("tool_input", {})

# Auto-approve safe read-only tools
if tool in ("Read", "Glob", "Grep"):
    sys.exit(0)

# Guard Bash commands
if tool == "Bash":
    command = tool_input.get("command", "") if isinstance(tool_input, dict) else ""

    if re.search(r"rm -rf|drop table|truncate|DELETE FROM|format |mkfs", command):
        print(f"BLOCKED: Destructive command detected: {command}", file=sys.stderr)
        sys.exit(2)

    if re.search(r"git reset --hard|git push.*--force|git clean -fd", command):
        print(f"BLOCKED: Destructive git operation: {command}", file=sys.stderr)
        sys.exit(2)

    if re.search(r"^\./mvnw (test|verify|clean|package|spring-boot:run)", command):
        sys.exit(0)

    if re.search(r"^git (status|log|diff|branch|show)", command):
        sys.exit(0)

# Guard Write/Edit — block protected files
if tool in ("Write", "Edit"):
    file_path = ""
    if isinstance(tool_input, dict):
        file_path = tool_input.get("file_path", tool_input.get("path", ""))

    if re.search(r"(application\.yaml|pom\.xml|\.claude/settings\.json)$", file_path):
        print(f"BLOCKED: Cannot modify protected file: {file_path}. Edit these files manually.", file=sys.stderr)
        sys.exit(2)

# Fall through to normal permission flow
sys.exit(1)
