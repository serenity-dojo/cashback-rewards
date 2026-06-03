#!/usr/bin/env python3
"""SessionEnd hook — log session summary."""

import json
import os
import subprocess
import sys
import time
from datetime import datetime

data = json.load(sys.stdin)
session_id = data.get("session_id", "unknown")

# Calculate duration
log_dir = ".claude"
start_file = os.path.join(log_dir, ".session-start-time")
duration = "unknown"

if os.path.exists(start_file):
    with open(start_file) as f:
        start_time = int(f.read().strip())
    elapsed = int(time.time()) - start_time
    minutes, seconds = divmod(elapsed, 60)
    duration = f"{minutes}m {seconds}s"
    os.remove(start_file)

# Count changes
def run(cmd):
    try:
        return subprocess.check_output(cmd, shell=True, stderr=subprocess.DEVNULL, text=True).strip()
    except subprocess.CalledProcessError:
        return ""

changed_files = run("git diff --name-only")
files_changed = len(changed_files.splitlines()) if changed_files else 0
tests_added = sum(1 for f in changed_files.splitlines() if "Test" in f or "IT" in f) if changed_files else 0
branch = run("git branch --show-current") or "detached"

# Append to session log
log_entry = {
    "session_id": session_id,
    "branch": branch,
    "duration": duration,
    "timestamp": datetime.now().isoformat(),
    "files_changed": files_changed,
    "tests_added": tests_added,
}

os.makedirs(log_dir, exist_ok=True)
with open(os.path.join(log_dir, "session-log.jsonl"), "a") as f:
    f.write(json.dumps(log_entry) + "\n")

print(f"Session summary: {duration} on {branch} — {files_changed} files changed, {tests_added} test files touched", file=sys.stderr)
