#!/bin/bash
# protect-files.sh
#
# Blocks modifications to protected files. Covers Edit/Write (via the
# tool_input.file_path) and best-effort Bash writes (via the command string).
# The real backstop is the permissions.deny block in settings.json; this hook
# is defense in depth, since a sufficiently obfuscated Bash write can still
# evade a string scan.

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')
COMMAND=$(echo "$INPUT" | jq -r '.tool_input.command // empty')

PROTECTED_PATTERNS=(
  # Production & secret config
  "application-prod"          # application-prod.yml / .properties / .yaml
  "application-secrets"
  ".env"

  # Credentials & Java keystores
  ".pem" ".key" ".p12" ".jks" ".keystore" ".truststore"

  # Build-tool integrity (controls what Maven downloads/runs)
  ".mvn/" "mvnw"

  # CI / deploy
  ".github/workflows" "Dockerfile" "docker-compose"

  # Claude's own guardrails — don't let it edit the hooks protecting it
  ".claude/"

  # VCS internals
  ".git/"
)

block() {
  echo "Blocked: $1 matches protected pattern '$2'" >&2
  exit 2
}

# Edit/Write: the file_path is always a write target.
if [[ -n "$FILE_PATH" ]]; then
  for pattern in "${PROTECTED_PATTERNS[@]}"; do
    [[ "$FILE_PATH" == *"$pattern"* ]] && block "$FILE_PATH" "$pattern"
  done
fi

# Bash: only treat the command as dangerous if it both names a protected path
# AND looks like a mutation (redirection, in-place edit, move/copy/remove, tee,
# chmod/chown, truncate, dd, install). This avoids blocking read-only commands.
if [[ -n "$COMMAND" ]]; then
  if [[ "$COMMAND" =~ (\>|sed[[:space:]]+-i|perl[[:space:]]+-i|(^|[[:space:]\|\;])(tee|mv|cp|rm|dd|install|truncate|chmod|chown)([[:space:]]|$)) ]]; then
    for pattern in "${PROTECTED_PATTERNS[@]}"; do
      [[ "$COMMAND" == *"$pattern"* ]] && block "command (write to protected path)" "$pattern"
    done
  fi
fi

exit 0
