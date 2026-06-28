# Reviewing subagents

A subagent is a Markdown file in `.claude/agents/<name>.md` (project) or `~/.claude/agents/<name>.md` (personal). Frontmatter carries `name`, `description`, an optional `tools` list, and an optional `model`. The body is the subagent's system prompt. It runs in its own context window and returns only a final result to the main thread.

The two things a subagent buys you: **context isolation** (heavy or noisy work does not pollute the main transcript) and **a focused persona with scoped tools** (a narrow brief plus a narrow toolset behaves more predictably than the generalist main thread).

## When a subagent earns its keep

- The task reads or searches a lot (codebase exploration, log triage, multi-file analysis) and you only want the conclusion back.
- The task is a self-contained specialist step that recurs (test-writer, reviewer, security-checker) and benefits from a fixed brief.
- You want different tool permissions or a different model for one slice of work.

When it does NOT: a two-step task, or anything where the main thread needs the intermediate detail anyway. The dispatch plus summary overhead then costs more than it saves.

## Review checklist

- **Description triggers correctly.** Same discipline as skills: it must say when to delegate to this agent. Vague descriptions mean the agent is never auto-selected. Note whether the workflow relies on automatic selection or explicit invocation, and make the description match that intent.
- **Tools are scoped.** Omitting `tools` inherits everything, which is convenient but loose. A reviewer agent that only needs to read should not hold Write or Bash. Tight scoping is both a safety and a predictability win. Flag any agent holding tools it never uses.
- **The body is a real system prompt.** It should define the persona, the method, the output contract (exactly what to return to the caller), and the stop condition. A vague body produces a vague handback. The most valuable line is often "Return only X, in this format", because the main thread pays for whatever comes back.
- **Model choice is deliberate.** A cheap narrow task can specify a smaller model; a hard reasoning task should not be silently downgraded. Flag mismatches.
- **No duplication of main-thread context.** The subagent does not inherit the conversation, so anything it needs must be in its prompt or passed in. Conversely, do not restate the whole project's rules inside it; reference the canonical source.

## Common rewrites

- Add an explicit output contract to the body so the handback is short and structured rather than a wall of findings.
- Narrow `tools` to the minimum the brief requires.
- Move a recurring inline delegation ("go read the whole codebase and summarize") into a named exploration subagent so the main thread stays clean.
- Sharpen the description from a topic ("handles testing") to a trigger ("Use to write or repair unit tests after a feature is implemented, when the user asks for coverage, or when a test run fails").
