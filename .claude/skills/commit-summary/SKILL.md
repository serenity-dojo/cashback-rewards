---
name: commit-summary
description: Summarize the current branch changes and suggest a commit message
context: fork
allowed-tools: Bash(git *), Bash(bash *), Read
---

## Change context

!`bash .claude/skills/commit-summary/scripts/change-context.sh`

## Your task

Summarize the changes on this branch using the structure in
`templates/summary.md` (relative to this skill) — keep its section order and the
🔎 header, and fill every placeholder from the change context above.

Keep the summary concise, practical, and developer-focused. Base everything on
the diff and commits shown — do not invent changes that aren't there. If a
section has nothing to report (e.g. no tests touched), say so briefly rather than
padding it.
