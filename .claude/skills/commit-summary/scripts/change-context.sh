#!/usr/bin/env bash
#
# Print the change context for the current branch: base + current branch names,
# changed files, commits ahead of base, and the full diff. Used by the
# commit-summary skill via a !`…` injection, so its stdout lands in the model's
# context before the summary is written.
#
# Resolves the base branch instead of hardcoding "main", so it works on master
# repos and degrades gracefully when run while ON the base branch.
set -euo pipefail

# Base branch: prefer the remote's default (origin/HEAD), then main, then master.
base="$(git symbolic-ref --quiet --short refs/remotes/origin/HEAD 2>/dev/null | sed 's@^origin/@@' || true)"
if [ -z "${base:-}" ]; then
  if git rev-parse --verify --quiet main >/dev/null; then
    base="main"
  elif git rev-parse --verify --quiet master >/dev/null; then
    base="master"
  fi
fi

branch="$(git branch --show-current)"

echo "Base branch:    ${base:-<none found>}"
echo "Current branch: ${branch:-<detached HEAD>}"
echo

# If we couldn't resolve a base, or we're sitting on it, there's no range to diff.
if [ -z "${base:-}" ]; then
  echo "No base branch found — cannot compute a diff range."
  exit 0
fi
if [ "$branch" = "$base" ]; then
  echo "Currently on the base branch ($base) — no branch changes to summarize."
  echo "Showing uncommitted working-tree changes instead:"
  echo
  echo "Changed files:"
  git diff --name-only
  echo
  echo "Code diff:"
  git diff
  exit 0
fi

echo "Changed files:"
git diff --name-only "$base...HEAD"
echo
echo "Recent commits:"
git log --oneline "$base..HEAD"
echo
echo "Code diff:"
git diff "$base...HEAD"
