#!/bin/bash
BRANCH=$(git branch --show-current)
echo "Branch: $BRANCH"
echo ""
echo "Uncommitted changes:"
git --no-pager diff --stat
STAGED=$(git --no-pager diff --cached --stat)
if [ -n "$STAGED" ]; then
  echo ""
  echo "Staged:"
  echo "$STAGED"
fi
echo ""
echo "Workflow: /discover > /accept > /tdd"