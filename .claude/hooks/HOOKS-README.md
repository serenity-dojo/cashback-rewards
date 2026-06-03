# Claude Code Hooks Demo

Five working hooks that form a complete safety and quality pipeline for the cashback-rewards project. Copy the `.claude/` directory into your project to try them.

## Setup

```bash
# Make all hook scripts executable
chmod +x .claude/hooks/*.sh

# Verify jq is installed (needed for JSON parsing in bash hooks)
jq --version
```

### Windows / Python alternative

The default hooks use bash + jq and work on Mac and Linux (including WSL on Windows). Python 3 equivalents are in `.claude/hooks/python/` — same behaviour, no jq dependency. To use them, change the commands in `settings.json` from `bash .claude/hooks/session-start.sh` to `python3 .claude/hooks/python/session_start.py` (and so on for each hook).

## The Five Hooks

### 1. SessionStart — `session-start.sh`

**When:** Every time you start a new Claude Code session.

**What it does:**
- Injects the current git branch, modified file count, and recent commits into Claude's context
- Highlights recently modified spec files
- Reminds Claude of the spec-driven workflow

**Demo:** Start a Claude session and notice the project context appearing before your first prompt.

### 2. UserPromptSubmit — `prompt-router.sh`

**When:** Every time you submit a prompt, before Claude processes it.

**What it does:**
- **Blocks** prompts containing destructive language ("delete all", "drop table", "rm -rf")
- **Routes** requirement questions → suggests `/discover`
- **Routes** test-writing requests → suggests `/accept`
- **Injects** BigDecimal reminders when prompts mention money/cashback

**Demo:** Try these prompts:
- "Delete all the test files" → **blocked**
- "I have a new feature for customer refunds" → routes to `/discover`
- "Calculate the cashback for a $100 purchase" → injects BigDecimal reminder

### 3. PreToolUse — `tool-guard.sh`

**When:** Before every tool call Claude makes.

**What it does:**
- **Auto-approves** Read, Glob, Grep (no permission prompts for reading)
- **Auto-approves** `./mvnw test`, `./mvnw verify`, and read-only git commands
- **Blocks** destructive commands: `rm -rf`, `git reset --hard`, `git push --force`
- **Blocks** edits to protected files: `pom.xml`, `application.yaml`, `settings.json`
- Falls through to normal permission flow for everything else

**Demo:** Ask Claude to read a file (silent auto-approve), then ask it to delete something (blocked with explanation).

### 4. Stop — `stop-gate.sh`

**When:** Claude thinks it's finished and tries to stop.

**What it does:**
- Runs `./mvnw verify` (unit + acceptance tests)
- If tests pass → Claude is allowed to stop
- If tests fail → **exit code 2 blocks Claude** and sends the failure summary back as context
- Claude reads the failures and keeps working to fix them

**Demo:** Introduce a deliberate bug, ask Claude to implement something, and watch it get blocked by failing tests. It will fix the issue and try again.

### 5. SessionEnd — `session-end.sh`

**When:** When the session terminates (can't block).

**What it does:**
- Calculates session duration (paired with the start time saved by SessionStart)
- Counts files changed and test files touched during the session
- Appends a JSON log entry to `.claude/session-log.jsonl`
- Prints a summary to the terminal

**Demo:** End a session and check `.claude/session-log.jsonl` — you'll see a structured record of what happened.

## Testing Hooks in Isolation

You can test each hook script without running Claude by piping test JSON to stdin:

```bash
# Test the prompt router
echo '{"prompt": "delete all test files"}' | bash .claude/hooks/prompt-router.sh
echo $?  # Should be 2 (blocked)

echo '{"prompt": "implement the cashback calculation"}' | bash .claude/hooks/prompt-router.sh
echo $?  # Should be 0 (allowed, with money reminder on stderr)

# Test the tool guard
echo '{"tool_name": "Read", "tool_input": {}}' | bash .claude/hooks/tool-guard.sh
echo $?  # Should be 0 (auto-approved)

echo '{"tool_name": "Bash", "tool_input": {"command": "rm -rf /"}}' | bash .claude/hooks/tool-guard.sh
echo $?  # Should be 2 (blocked)

echo '{"tool_name": "Write", "tool_input": {"file_path": "pom.xml"}}' | bash .claude/hooks/tool-guard.sh
echo $?  # Should be 2 (protected file)
```

## Files

```
.claude/
├── settings.json              # Wires all 5 hooks together
├── hooks/
│   ├── session-start.sh       # Inject project context
│   ├── prompt-router.sh       # Route prompts, block dangerous ones
│   ├── tool-guard.sh          # Guard tool calls
│   ├── stop-gate.sh           # Test gate before Claude can finish
│   ├── session-end.sh         # Log session summary
│   └── python/                # Cross-platform alternatives
│       ├── session_start.py
│       ├── prompt_router.py
│       ├── tool_guard.py
│       ├── stop_gate.py
│       └── session_end.py
└── session-log.jsonl          # Created automatically by session-end
```
