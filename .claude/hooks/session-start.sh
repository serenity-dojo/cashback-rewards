#!/bin/bash
# SessionStart hook — inject project context into every session
# stdout goes directly into Claude's context

BRANCH=$(git branch --show-current 2>/dev/null || echo "detached")
CHANGED=$(git diff --name-only 2>/dev/null | wc -l | tr -d ' ')
UNTRACKED=$(git ls-files --others --exclude-standard 2>/dev/null | wc -l | tr -d ' ')

echo "=== Project Context ==="
echo "Branch: $BRANCH"
echo "Modified files: $CHANGED"
echo "Untracked files: $UNTRACKED"

# Show recent commits for context
RECENT=$(git log --oneline -3 2>/dev/null)
if [ -n "$RECENT" ]; then
  echo ""
  echo "Recent commits:"
  echo "$RECENT"
fi

# Check for active spec work
SPECS=$(find doc/specs -name "*.md" -newer pom.xml 2>/dev/null)
if [ -n "$SPECS" ]; then
  echo ""
  echo "Recently modified specs:"
  echo "$SPECS"
fi

# Remind Claude of the development workflow
echo ""
echo "Remember: follow the spec-driven workflow — /discover → /accept → /tdd → /review"
