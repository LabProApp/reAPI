#!/usr/bin/env bash
# ============================================================================
#  redeploy.sh — build, render properties, restart service.
#
#  Prereqs (one-time):
#    1. Copy secret.properties.example to secret.properties and fill in
#       every value (DB, AWS, Twilio, JWT_SECRET, etc.).
#    2. A systemd unit named 'keybricks' (override via $KEYBRICKS_SERVICE).
#
#  Run:
#    ./redeploy.sh
# ============================================================================

set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

SOURCE="$DIR/application.properties"
SECRETS="$DIR/secret.properties"
OUT="$DIR/target/application.properties"
SERVICE="${KEYBRICKS_SERVICE:-keybricks}"

[[ -f "$SOURCE"  ]] || { echo "[error] $SOURCE missing"  >&2; exit 1; }
[[ -f "$SECRETS" ]] || { echo "[error] $SECRETS missing — copy from .example and fill in" >&2; exit 1; }
chmod 600 "$SECRETS" 2>/dev/null || true

# ── 1. Build ────────────────────────────────────────────────────────────────
echo "▶ Build  (mvn clean package -DskipTests)"
mvn clean package -DskipTests

# ── 2. Substitute secret.properties → target/application.properties ─────────
echo "▶ Render target/application.properties"
SED_PROG="$(mktemp)"
trap 'rm -f "$SED_PROG"' EXIT
while IFS= read -r line || [[ -n "$line" ]]; do
  line="${line%$'\r'}"
  line="${line#"${line%%[![:space:]]*}"}"
  [[ -z "$line" || "${line:0:1}" == "#" ]] && continue
  key="${line%%=*}"
  val="${line#*=}"
  key="${key//[[:space:]]/}"
  [[ "$key" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]] || continue
  # Escape sed-special chars in the replacement value.
  esc="$(printf '%s' "$val" | sed -e 's/[\\&|]/\\&/g')"
  # ${KEY:default} first (longer match) then ${KEY}.
  printf 's|\\${%s:[^}]*}|%s|g\n' "$key" "$esc" >> "$SED_PROG"
  printf 's|\\${%s}|%s|g\n'        "$key" "$esc" >> "$SED_PROG"
done < "$SECRETS"

mkdir -p "$DIR/target"
sed -f "$SED_PROG" "$SOURCE" > "$OUT"
chmod 644 "$OUT"
echo "  → $OUT"

# ── 3. Restart service ──────────────────────────────────────────────────────
echo "▶ Restart $SERVICE"
sudo systemctl restart "$SERVICE"
sleep 2
if ! sudo systemctl is-active --quiet "$SERVICE"; then
  echo "[error] $SERVICE failed to start" >&2
  sudo systemctl status "$SERVICE" --no-pager || true
  exit 1
fi

echo "✓ Deployed."
