#!/bin/bash
# protect-files.sh

INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')

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

for pattern in "${PROTECTED_PATTERNS[@]}"; do
  if [[ "$FILE_PATH" == *"$pattern"* ]]; then
    echo "Blocked: $FILE_PATH matches protected pattern '$pattern'" >&2
    exit 2
  fi
done

exit 0