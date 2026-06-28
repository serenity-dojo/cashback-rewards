# Context economics

Read this for any whole-setup audit or any "it feels slow or expensive" request. It explains what is paid when, and how to measure it.

## The loading lifecycle

A turn in Claude Code assembles context in this order:

1. **System and tool layer.** Built-in tools, then MCP tool schemas for every connected server. MCP schemas are often the single largest hidden cost: a chatty server can add thousands of tokens to every turn whether or not you use it.
2. **Memory layer.** Enterprise policy file (if present), then project `CLAUDE.md` files discovered from the working directory up to the repo root, then `~/.claude/CLAUDE.md`, plus anything those files `@import`. All of this is resident on every turn.
3. **Capability descriptions.** The name and description of each installed skill and each subagent, and the slash command list. Bodies are not loaded here, only the descriptions. This is what the model scans to decide what to reach for.
4. **Conversation.** The running transcript, which grows until compaction.

Tiers 1 to 3 from the SKILL.md then load reactively: a skill body when its description triggers, a reference when the model opens it, a subagent in its own window when dispatched.

## Where the money actually is

Order of leverage, highest first:

1. **MCP tool schemas.** Disconnect servers that are not in active use. A project that connects five servers "just in case" pays for all five on every prompt. See `commands-and-mcp.md`.
2. **CLAUDE.md bloat.** This is read every turn. Long preambles, restated tool docs, and aspirational style guides belong elsewhere or nowhere. See `memory-and-claude-md.md`.
3. **Skill and agent description sprawl.** Each is cheap alone, but thirty verbose ones add up, and vague ones cause mis-triggering which wastes whole turns. See `skills.md`.
4. **Skill body bloat.** Paid once per session when triggered, so lower leverage, but still worth pushing detail into references.

## How to measure

Do not eyeball. Quantify so the before and after is real.

```bash
# Tier 0 line counts, the always-on tax
find . -name "CLAUDE.md" -not -path "*/node_modules/*" -exec wc -l {} +
wc -l ~/.claude/CLAUDE.md 2>/dev/null

# Skill and agent description weight (frontmatter only is what is resident)
for f in .claude/skills/*/SKILL.md ~/.claude/skills/*/SKILL.md; do
  [ -f "$f" ] && echo "== $f ==" && sed -n '/^---$/,/^---$/p' "$f"
done

# Rough token estimate: characters / 4 is a serviceable approximation
wc -m CLAUDE.md | awk '{printf "~%d tokens\n", $1/4}'
```

For a precise count, the `anthropic` Python SDK exposes `client.messages.count_tokens(...)`, which is worth using when the difference is the whole point of the review.

## The decision the reviewer keeps making

For every line of configuration, ask: **what is the cheapest tier in which this still does its job?**

- A rule the model should follow when relevant but not always: a skill (Tier 1), not CLAUDE.md (Tier 0).
- Detail only needed inside one workflow: a reference (Tier 2), not the skill body (Tier 1).
- A rule that must never be violated: a hook or permission (event tier), not prose anywhere.
- A noisy exploration that pollutes the transcript: a subagent (Tier 3), not the main thread.

Most real-world defects are a single tier too high. The fix is almost always to move something down, not to delete it.

## Trade-offs to name out loud

- Splitting a skill into many references cuts resident tokens but adds round-trips (the model must choose to open each file). For a workflow that always needs all of it, one body is better than five references.
- Aggressive description trimming saves Tier 0 tokens but can hurt triggering. Reliability usually wins; trim filler, not the discriminating cues.
- Subagents isolate cost but pay a fixed dispatch and summary overhead. They earn their keep on genuinely heavy or noisy work, not on a two-step task.
