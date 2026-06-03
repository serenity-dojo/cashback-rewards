#!/usr/bin/env python3
"""SessionStart hook — inject project context into every session."""

import subprocess
import os

def run(cmd):
    try:
        return subprocess.check_output(cmd, shell=True, stderr=subprocess.DEVNULL, text=True).strip()
    except subprocess.CalledProcessError:
        return ""

branch = run("git branch --show-current") or "detached"
changed = run("git diff --name-only").count("\n") + (1 if run("git diff --name-only") else 0)
untracked = run("git ls-files --others --exclude-standard").count("\n") + (1 if run("git ls-files --others --exclude-standard") else 0)

print("=== Project Context ===")
print(f"Branch: {branch}")
print(f"Modified files: {changed}")
print(f"Untracked files: {untracked}")

recent = run("git log --oneline -3")
if recent:
    print(f"\nRecent commits:\n{recent}")

# Check for recently modified specs
if os.path.isdir("doc/specs"):
    pom_mtime = os.path.getmtime("pom.xml") if os.path.exists("pom.xml") else 0
    specs = [f for f in os.listdir("doc/specs") if f.endswith(".md") and os.path.getmtime(f"doc/specs/{f}") > pom_mtime]
    if specs:
        print(f"\nRecently modified specs:")
        for s in specs:
            print(f"  doc/specs/{s}")

print("\nRemember: follow the spec-driven workflow — /discover → /accept → /tdd → /review")
