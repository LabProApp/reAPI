#!/usr/bin/env bash
# ============================================================================
#  replaceproperties.sh — produce a deployable application.properties
# ----------------------------------------------------------------------------
#  1. Bootstrap secret.properties from .example if missing.
#  2. Generate JWT_SECRET inside secret.properties if blank (first run only).
#  3. Substitute ${KEY} / ${KEY:default} placeholders using secret.properties.
#  4. Write the result to target/application.properties.
#     The source application.properties is NEVER modified — re-runnable safely.
#
#  Usage:
#      ./replaceproperties.sh
#
#  After this runs, start the app from target/:
#      cd target && java -jar realestate-api-*.jar
#  Spring Boot will read target/application.properties automatically.
# ============================================================================

set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOURCE="$DIR/application.properties"
SECRETS="$DIR/secret.properties"
EXAMPLE="$DIR/secret.properties.example"
OUTDIR="$DIR/target"
OUT="$OUTDIR/application.properties"

echo "[prepare] source : $SOURCE"
echo "[prepare] secrets: $SECRETS"
echo "[prepare] output : $OUT"
echo

# ── 1. Ensure secret.properties exists ──────────────────────────────────────
if [[ ! -f "$SECRETS" ]]; then
  if [[ ! -f "$EXAMPLE" ]]; then
    echo "[ERROR] Neither secret.properties nor secret.properties.example found." >&2
    exit 1
  fi
  cp "$EXAMPLE" "$SECRETS"
  chmod 600 "$SECRETS"
  echo "[init]  Bootstrapped secret.properties from .example — fill in real values"
fi
chmod 600 "$SECRETS" 2>/dev/null || true

# ── 2. Generate JWT_SECRET on first run ─────────────────────────────────────
# A blank "JWT_SECRET=" line counts as missing.
if ! grep -qE '^[[:space:]]*JWT_SECRET=[^[:space:]]+' "$SECRETS"; then
  if command -v openssl >/dev/null 2>&1; then
    NEW_JWT="$(openssl rand -base64 64 | tr -d '\n\r')"
  elif [[ -r /dev/urandom ]]; then
    NEW_JWT="$(head -c 64 /dev/urandom | base64 | tr -d '\n\r')"
  else
    echo "[ERROR] Cannot generate JWT_SECRET — install openssl or provide /dev/urandom." >&2
    exit 1
  fi
  TMP="${SECRETS}.tmp.$$"
  if grep -qE '^[[:space:]]*JWT_SECRET=' "$SECRETS"; then
    awk -v s="$NEW_JWT" '
      /^[[:space:]]*JWT_SECRET=/ { print "JWT_SECRET=" s; next }
      { print }
    ' "$SECRETS" > "$TMP"
  else
    cp "$SECRETS" "$TMP"
    echo "JWT_SECRET=$NEW_JWT" >> "$TMP"
  fi
  mv "$TMP" "$SECRETS"
  chmod 600 "$SECRETS"
  echo "[init]  Generated JWT_SECRET (64 random bytes, Base64)"
fi

# ── 3. Build the sed substitution program from secret.properties ────────────
SED_PROG="$(mktemp)"
trap 'rm -f "$SED_PROG"' EXIT

loaded=0
while IFS= read -r line || [[ -n "$line" ]]; do
  line="${line%$'\r'}"                                  # strip CR
  line="${line#"${line%%[![:space:]]*}"}"              # trim leading ws
  [[ -z "$line" || "${line:0:1}" == "#" ]] && continue # skip blanks + comments

  key="${line%%=*}"
  val="${line#*=}"
  key="${key//[[:space:]]/}"
  [[ "$key" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]] || continue

  # Escape sed-special chars in the replacement: \ & and our delimiter |
  esc="$(printf '%s' "$val" | sed -e 's/[\\&|]/\\&/g')"
  # ${KEY:default}  →  value      (must come first; longer pattern)
  printf 's|\\${%s:[^}]*}|%s|g\n' "$key" "$esc" >> "$SED_PROG"
  # ${KEY}          →  value
  printf 's|\\${%s}|%s|g\n'        "$key" "$esc" >> "$SED_PROG"

  loaded=$((loaded + 1))
done < "$SECRETS"

# ── 4. Substitute into target/application.properties ────────────────────────
mkdir -p "$OUTDIR"
sed -f "$SED_PROG" "$SOURCE" > "$OUT"
chmod 644 "$OUT"
echo "[ok]    Wrote $OUT ($loaded substitution(s))"

# ── 5. Report leftover placeholders so missing secrets are visible ──────────
left="$(grep -oE '\$\{[A-Za-z_][A-Za-z0-9_]*(:[^}]*)?\}' "$OUT" | sort -u || true)"
if [[ -n "$left" ]]; then
  echo
  echo "[warn]  Unresolved placeholders in output (will fall back to inline defaults):"
  echo "$left" | sed 's/^/          /'
fi

echo
echo "[done]  Deploy with:  cd target && java -jar realestate-api-*.jar"
