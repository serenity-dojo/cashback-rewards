# Reviewing CLAUDE.md and memory

CLAUDE.md is the highest-leverage file in any setup because it is Tier 0: read in full on every turn of every session. The memory hierarchy, all resident, is enterprise policy, then project files (the working directory's `CLAUDE.md` and those in parent directories up to the repo root), then `~/.claude/CLAUDE.md` for personal cross-project memory. Files can pull in others with `@path` imports, and imported content is resident too.

The governing question for every line: **does the model need this on a turn that has nothing to do with it?** If not, it does not belong here.

## What belongs in CLAUDE.md

- Project shape the model cannot infer fast: the architecture in a few lines, where things live, the build and test commands.
- Hard conventions that apply broadly: the package manager, the language version, naming rules, "tests live beside source".
- Pointers, not payloads: "For the deployment workflow, see the deploy skill" rather than the deployment workflow inline.

## What does not belong

- **Long style guides and aspirational prose.** A 200-line "how to write good code" essay is paid on every prompt and mostly ignored. Move it to a skill that triggers on the relevant work.
- **Restated tool or framework documentation.** The model already knows how `git` works.
- **Rules that must be enforced.** "Never commit secrets", "always run the build" are load-bearing and prose cannot guarantee them. Move to a hook or permission (see `hooks-and-settings.md`). Keep at most a one-line reminder.
- **Anything used in only one workflow.** That is a skill's job.
- **Duplicates of skill or agent content.** One source of truth.

## Review checklist

- Count the lines first (`wc -l`). A project CLAUDE.md over ~150 lines is almost always carrying Tier 1 or 2 material in Tier 0. State the number and the target.
- For each section, classify: always-relevant convention (keep), sometimes-relevant detail (move to skill), must-enforce rule (move to hook), or pure documentation (delete).
- Check `@imports`: each one is resident, so an import chain can hide a large always-on payload. Audit what they actually pull in.
- Check the user file (`~/.claude/CLAUDE.md`) too. Personal global rules leak into every project and are easy to forget. A growing user file taxes everything.
- Look for instructions phrased as hope ("please try to..."). If it matters, enforce it; if it does not, cut it.

## Common rewrites

- Replace an inline workflow with a one-line pointer to a skill that owns it.
- Lift a 50-line code-style section into a `code-style` skill triggered by editing source.
- Convert "always run tests before finishing" into a Stop hook and leave a single reminder line.
- Collapse three restatements of the same convention into one.
- Trim the architecture description to the few facts the model genuinely cannot derive in seconds.

A tight CLAUDE.md is not a sign of an under-documented project. It is a sign the documentation lives in the tier that is paid only when it is needed.
