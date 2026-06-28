# Reviewing slash commands and MCP

## Slash commands

A slash command is a Markdown file in `.claude/commands/<name>.md` (project) or `~/.claude/commands/<name>.md` (personal). The body is the prompt that runs when invoked. Frontmatter can set `description`, `argument-hint`, `allowed-tools`, and `model`. The body can take `$ARGUMENTS` or positional `$1`, `$2`, run shell with a `!` prefix, and pull files with `@`.

A command's body is Tier 1: paid only when the command is run. The command name and description sit in the resident list, so they are cheap but not free.

Skill versus command, the distinction to apply when reviewing:
- A **command** is user-invoked: the user types `/thing` when they want it. Good for deliberate, repeatable workflows the user drives.
- A **skill** is model-invoked: it fires when the description matches the situation. Good for capabilities the model should reach for on its own.

Flag the mismatch: a workflow the model should trigger automatically, trapped in a slash command the user has to remember to type, should usually be a skill. A deliberate ceremony the user wants to drive, buried in an auto-triggering skill, should usually be a command.

Review checklist:
- Does the body use `$ARGUMENTS` or positional args where it should, instead of hardcoding?
- Is `allowed-tools` scoped, especially for commands that run shell?
- Is the description accurate enough to show up usefully in the command list?
- Is the body lean, or is it carrying reference material that should be a bundled file?

## MCP configuration

MCP servers are declared in a project `.mcp.json` (shared, committed) or added per user. The critical fact for optimization: **every connected server's tool schemas are Tier 0**, loaded on every turn whether or not the tools are used. A verbose server, or several connected "just in case", is one of the largest hidden recurring costs in a setup.

Review checklist:
- List connected servers and ask which are in active use. Disconnect the rest. This is often the single biggest token saving available.
- For a heavy server, check whether the workflow genuinely needs it resident, or whether the task could run through a subagent that connects it only for that slice of work.
- Prefer project-scoped `.mcp.json` for servers the whole team needs, and personal scope for individual tools, so no one pays for tools they never call.
- Watch for overlap: two servers exposing similar tools double the schema cost and confuse selection.

## Common rewrites

- Convert an auto-relevant slash command into a skill so the model invokes it without the user remembering.
- Scope `allowed-tools` on a shell-running command down to what it needs.
- Disconnect idle MCP servers and document why, since the cost is invisible until measured.
- Move a rarely-needed heavy MCP server out of the always-on set and into a subagent-scoped use.
