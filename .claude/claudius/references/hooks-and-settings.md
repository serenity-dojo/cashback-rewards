# Reviewing hooks and settings

Hooks and permissions are the deterministic layer: they enforce, where prose only suggests. They cost no model tokens to decide (they run as shell commands), so moving a load-bearing rule out of CLAUDE.md and into a hook saves Tier 0 tokens AND makes the rule actually hold. This is the highest-value rewrite the optimizer makes.

## Settings files

- `.claude/settings.json` - project settings, shared and committed.
- `.claude/settings.local.json` - personal project settings, gitignored.
- `~/.claude/settings.json` - user settings across all projects.

They carry `permissions`, `hooks`, `env`, `model`, and related keys. Project deny rules and personal rules merge, with deny taking precedence.

## Permissions

`permissions` holds `allow`, `deny`, and `ask` arrays of rules like `Bash(rm -rf:*)`, `Read(./.env)`, `Edit(./.claude/**)`. `deny` wins over `allow`. This is the cheapest enforcement of all, declarative and zero-token.

Review checklist:
- Is anything sensitive protected only by a CLAUDE.md sentence rather than a `deny` rule? Move it.
- Are `allow` rules broad enough to defeat the point (`Bash(:*)`)? Tighten.
- Do `ask` rules cover the genuinely risky operations without nagging on safe ones?

## Hooks

Hooks are configured under the `hooks` key, keyed by event, each with a matcher and a list of commands:

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          { "type": "command", "command": "your-command-here" }
        ]
      }
    ]
  }
}
```

The events:
- **PreToolUse** - before a tool runs. Can block the call (deny) or auto-allow. The place to guard against dangerous operations.
- **PostToolUse** - after a tool succeeds. The place to format, lint, or kick off a build on a change.
- **UserPromptSubmit** - when the user sends a prompt. Can inject context or block.
- **Stop** - when the main agent is about to finish. The place to run the build or tests and feed failures back so the model self-corrects before handing back.
- **SubagentStop** - the same, for a subagent finishing.
- **PreCompact** - before context compaction.
- **SessionStart / SessionEnd** - session boundaries, for setup or teardown.
- **Notification** - on Claude Code notifications.

A hook reads event JSON on stdin (session, tool input, and so on). For blocking events, exit code 2 blocks and feeds stderr back to the model; richer control comes from emitting JSON (for example a PreToolUse `permissionDecision` of allow, deny, or ask). A Stop hook that exits non-zero with a build error on stderr is how the model learns to fix its own build before stopping.

## The matcher trap to always flag

A `PreToolUse` or `PostToolUse` matcher on `Edit|Write` only sees the Edit and Write tools. It does **not** see file changes made through Bash (`sed -i`, `echo >`, `tee`, redirection). So a protected-files hook matched only on `Edit|Write` is bypassable by a Bash write, and a formatting hook matched only on those tools silently skips Bash-created files.

When enforcement must be airtight, either add a `Bash` matcher that inspects the command string, or back the rule with a `permissions.deny` rule (declarative, no bypass), or both. Always check whether a hook's guarantee has a Bash-shaped hole, and say so.

## Common rewrites

- Move a "never edit credentials / keystores / CI files / the .claude directory" prose rule into a `deny` rule plus, if active blocking is wanted, a `PreToolUse` hook, and note the Bash caveat.
- Convert "always run the build before finishing" from CLAUDE.md prose into a `Stop` hook that runs the build and feeds failures back.
- Move a "format on save" expectation into a `PostToolUse` hook on `Edit|Write`, and decide explicitly whether Bash-created files also need covering.
- Replace a broad `allow` with scoped rules and an `ask` for the genuinely risky operations.
