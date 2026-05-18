#!/usr/bin/env bash
# ============================================================================
#  generate-jwt-secret.sh
# ----------------------------------------------------------------------------
#  Generates a strong (64-byte, Base64-encoded) JWT signing key and bakes it
#  into the JWT_SECRET fallback inside application.properties:
#
#      jwt.secret=${JWT_SECRET:<new-key-here>}
#
#  The ${JWT_SECRET:...} pattern is preserved so a real env var in prod
#  still wins over the baked-in default.
#
#  Usage:
#      ./generate-jwt-secret.sh                       # writes application.properties
#      ./generate-jwt-secret.sh path/to/file.properties
#      ./generate-jwt-secret.sh --print-only          # print to stdout, don't write
#
#  Side effects:
#    - Creates a timestamped backup: application.properties.bak.YYYYMMDD_HHMMSS
#    - All existing JWTs become invalid after restart — users must log in again.
# ============================================================================

set -euo pipefail

PRINT_ONLY=0
FILE="application.properties"

# ── arg parsing ─────────────────────────────────────────────────────────────
for arg in "$@"; do
  case "$arg" in
    --print-only|-p)
      PRINT_ONLY=1
      ;;
    -h|--help)
      sed -n '2,/^# ==/p' "$0" | sed 's/^# \{0,1\}//' | sed '$d'
      exit 0
      ;;
    -*)
      echo "Unknown flag: $arg" >&2
      exit 2
      ;;
    *)
      FILE="$arg"
      ;;
  esac
done

# ── generate ────────────────────────────────────────────────────────────────
# Prefer openssl; fall back to /dev/urandom so the script works on a stripped
# container too. Both produce 64 random bytes, Base64-encoded — well above
# the 32-byte HS256 minimum that JwtUtil enforces.
if command -v openssl >/dev/null 2>&1; then
  SECRET="$(openssl rand -base64 64 | tr -d '\n\r')"
elif [[ -r /dev/urandom ]]; then
  SECRET="$(head -c 64 /dev/urandom | base64 | tr -d '\n\r')"
else
  echo "ERROR: neither 'openssl' nor /dev/urandom is available — cannot generate a secret." >&2
  exit 1
fi

if [[ -z "$SECRET" || ${#SECRET} -lt 80 ]]; then
  echo "ERROR: generated secret looks too short (length=${#SECRET}). Aborting." >&2
  exit 1
fi

# ── print-only mode: dump and exit ──────────────────────────────────────────
if [[ "$PRINT_ONLY" == "1" ]]; then
  echo "$SECRET"
  exit 0
fi

# ── verify target file ─────────────────────────────────────────────────────
if [[ ! -f "$FILE" ]]; then
  echo "ERROR: $FILE not found in $(pwd)" >&2
  exit 1
fi

# ── back up ────────────────────────────────────────────────────────────────
BACKUP="${FILE}.bak.$(date +%Y%m%d_%H%M%S)"
cp "$FILE" "$BACKUP"
echo "Backed up $FILE -> $BACKUP"

# ── replace or append ──────────────────────────────────────────────────────
# Replacement line we want in the file:
#     jwt.secret=${JWT_SECRET:<secret>}
# Notes on sed:
#   - Use | as the delimiter; base64 includes / which would conflict with /
#   - Base64 alphabet (A-Z a-z 0-9 + / =) has no sed-special chars in
#     replacement, so no escaping of the secret itself is needed.

if grep -q '^jwt\.secret=' "$FILE"; then
  # Portable in-place edit: write to a temp and move.
  TMP="${FILE}.tmp.$$"
  awk -v secret="$SECRET" '
    /^jwt\.secret=/ { print "jwt.secret=${JWT_SECRET:" secret "}"; next }
    { print }
  ' "$FILE" > "$TMP"
  mv "$TMP" "$FILE"
  echo "Replaced jwt.secret default in $FILE"
else
  # Append a fresh entry — file had none.
  {
    echo ""
    echo "# JWT signing key (generated $(date '+%Y-%m-%d %H:%M:%S'))"
    echo "jwt.secret=\${JWT_SECRET:${SECRET}}"
  } >> "$FILE"
  echo "Appended jwt.secret to $FILE"
fi

# ── verify the line is present ─────────────────────────────────────────────
if ! grep -q '^jwt\.secret=' "$FILE"; then
  echo "ERROR: write check failed; restoring backup." >&2
  cp "$BACKUP" "$FILE"
  exit 1
fi

# ── done ───────────────────────────────────────────────────────────────────
echo
echo "✓ JWT secret rotated."
echo
echo "Generated key (also written to ${FILE}):"
echo "  ${SECRET}"
echo
echo "Prod recommendation: don't rely on the baked-in default — set the env var:"
echo "  export JWT_SECRET=\"${SECRET}\""
echo
echo "Restart the app to pick up the new key."
echo "WARNING: every existing JWT becomes invalid; users must log in again."
