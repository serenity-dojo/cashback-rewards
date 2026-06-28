# Reviewing skills

A skill is a `SKILL.md` (YAML frontmatter plus body) in `.claude/skills/<name>/` for a project or `~/.claude/skills/<name>/` for personal use, optionally with bundled `references/`, `scripts/`, and `assets/`. Only the frontmatter (name and description) is resident; the body loads on trigger; bundled files load on demand.

## The description is the product

Under-triggering, the model not using a skill that would have helped, is the most common skill failure. The description is the entire triggering surface, so review it first and hardest.

A strong description states **what the skill does and the specific situations that should trigger it**, in that order, and is slightly pushy because the model leans toward not triggering. All "when to use" information belongs here, never in the body, because the body is invisible at decision time.

**Weak:** `Helps with documents.`
**Strong:** `Create, edit, and reformat Word documents (.docx). Use whenever the user mentions a Word doc, .docx file, letterhead, table of contents, or wants a polished report as a Word file, even if they do not say "docx".`

Review checklist for the description:
- Does it name concrete trigger phrases and contexts, not just a topic?
- Is it discriminating against neighbours (would the model know when NOT to use it versus a sibling skill)?
- Has the body leaked "when to use" language that should be hoisted up?
- Is it free of filler ("This skill is designed to help you with...")? Every Tier 0 word is paid forever.

## Body review

- **Length.** Aim under ~500 lines. If it is longer, it is doing too much in Tier 1. Split by variant or workflow stage into references and route to them.
- **Progressive disclosure.** Detail used in only one branch of the workflow belongs in a reference, with a one-line pointer in the body saying when to read it. The classic defect is a 400-line body where 300 lines apply to an edge case.
- **Imperative voice.** Instructions read as commands ("Read the file first", not "You might want to read the file"). Explain why a step matters rather than stacking bare MUSTs; the model follows reasoning more reliably than decree.
- **Determinism offload.** Repetitive or exact work (validation, formatting, parsing) belongs in a bundled script the model runs, not in prose the model re-derives. The script executes without its source entering context.
- **One source of truth.** If the skill restates a rule that already lives in CLAUDE.md or an agent, cut the duplicate and reference the canonical home.

## Common rewrites

- Hoist every "use this when..." sentence out of the body into the description.
- Replace a long inline example set with a single representative example plus a reference for the rest.
- Convert a "follow these 12 validation rules" prose block into `scripts/validate.py` invoked by one body line.
- For a multi-framework skill, split `references/<framework>.md` so only the relevant one loads, rather than carrying all frameworks in the body.

## Triggering reality to keep in mind

Claude only reaches for a skill on tasks it cannot trivially handle alone. A perfectly worded description still will not fire on "read this file", because the model just does it. So when assessing whether a description is the problem, test it against substantive, multi-step prompts, not one-liners.
