# Review output format

Read this before writing the final review. Use this structure so the output is scannable and every recommendation is concrete and quantified.

## Structure

```
## Goal
One line restating what we are optimizing for (reliability / token cost / safety / maintainability) and any assumption made.

## Inventory
A short table or list of the artifacts found and the tier each occupies, with line or token counts for the Tier 0 items.

## Findings
Ordered by leverage, highest first. Each finding is:
- What: the problem, in one sentence.
- Why it costs: which tier, paid how often.
- Fix: the specific change, with a before and after.
- Impact: estimated tokens saved per turn or per session, or the reliability gain.

## Rewrites
The actual replacement text or files, ready to paste. Do not describe the change in the abstract when you can show it.

## Summary
The headline number: roughly how much Tier 0 tax was removed, and the single most important reliability fix.
```

## Worked finding, for shape

**Finding 2: a load-bearing rule lives only in CLAUDE.md prose.**

- What: "Never edit files under `.claude/`" is stated in CLAUDE.md but nothing enforces it.
- Why it costs: it is in Tier 0 (paid every turn) and still cannot guarantee the outcome, because a Bash `sed -i .claude/...` bypasses it entirely.
- Fix:

Before, in CLAUDE.md:
```
Important: never edit anything under the .claude directory, and never touch credentials or keystore files.
```

After, in `.claude/settings.json`:
```json
{
  "permissions": {
    "deny": [
      "Edit(./.claude/**)",
      "Write(./.claude/**)",
      "Read(./**/*.keystore)",
      "Read(./.env)"
    ]
  }
}
```
And a single CLAUDE.md line if a human-readable reminder is still wanted: `Protected paths are enforced in settings; do not work around them.`

- Impact: removes two prose lines from every turn and converts a hope into a guarantee. Note the Bash caveat is now closed by the declarative deny rule.

## Tone of the review

Direct and specific. The reader is competent; skip the throat-clearing. Lead with the highest-leverage change. Quantify wherever a number is available, and say plainly when a number is an estimate. If a recommendation trades one goal against another (smaller description versus trigger reliability), name the trade and recommend a side.
