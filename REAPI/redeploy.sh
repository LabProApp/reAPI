#!/usr/bin/env bash
# ============================================================================
#  redeploy.sh — one-command redeploy for the REAPI backend
# ----------------------------------------------------------------------------
#  Runs end-to-end:
#    1. mvn clean package -DskipTests              (build the jar)
#    2. ./replaceproperties.sh                     (regen target/application.properties)
#    3. sudo systemctl restart <SERVICE>           (bounce the service)
#    4. sudo journalctl -u <SERVICE> -n 100 -f     (tail logs until Ctrl-C)
#
#  Usage:
#    ./redeploy.sh                                 # full cycle
#    ./redeploy.sh --skip-build                    # reuse existing target/*.jar
#    ./redeploy.sh --no-tail                       # don't follow logs after restart
#    ./redeploy.sh --service my-svc                # override the systemd unit name
#    ./redeploy.sh -h
#
#  Service name resolution order:
#    --service NAME flag  >  $KEYBRICKS_SERVICE env var  >  "keybricks"
# ============================================================================

set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SERVICE="${KEYBRICKS_SERVICE:-keybricks}"
SKIP_BUILD=0
NO_TAIL=0

# ── arg parsing ─────────────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    --skip-build)    SKIP_BUILD=1; shift ;;
    --no-tail)       NO_TAIL=1; shift ;;
    -s|--service)    SERVICE="$2"; shift 2 ;;
    -h|--help)
      sed -n '3,/^# ==/p' "$0" | sed 's/^# \{0,1\}//' | sed '$d'
      exit 0
      ;;
    *)
      echo "Unknown flag: $1" >&2
      echo "Run with -h for usage." >&2
      exit 2
      ;;
  esac
done

cd "$DIR"

# ── helpers ─────────────────────────────────────────────────────────────────
step()  { printf '\n\033[1;34m▶ %s\033[0m\n' "$*"; }
ok()    { printf '\033[1;32m✓ %s\033[0m\n' "$*"; }
fail()  { printf '\033[1;31m✗ %s\033[0m\n' "$*" >&2; }

# ── 1. Build ────────────────────────────────────────────────────────────────
if [[ "$SKIP_BUILD" == "0" ]]; then
  step "Build  —  mvn clean package -DskipTests"
  if ! command -v mvn >/dev/null 2>&1; then
    fail "mvn not on PATH. Install Maven or rerun with --skip-build."
    exit 1
  fi
  mvn clean package -DskipTests
  ok "build complete"
else
  step "Build skipped (--skip-build)"
fi

# ── 2. Verify jar landed in target/ ─────────────────────────────────────────
JAR="$(ls -t target/realestate-api-*.jar 2>/dev/null | head -n1 || true)"
if [[ -z "$JAR" ]]; then
  fail "No jar found in target/ — did the build succeed?"
  exit 1
fi
ok "jar    $JAR"

# ── 3. Render application.properties into target/ ───────────────────────────
step "Prepare  —  ./replaceproperties.sh"
./replaceproperties.sh
[[ -f target/application.properties ]] || {
  fail "target/application.properties missing after replaceproperties.sh"; exit 1; }
ok "config $DIR/target/application.properties"

# ── 4. Restart systemd service ──────────────────────────────────────────────
step "Restart  —  systemctl restart $SERVICE"
sudo systemctl restart "$SERVICE"

# Give the service a moment, then verify it's still up.
sleep 2
if sudo systemctl is-active --quiet "$SERVICE"; then
  ok "$SERVICE is active"
else
  fail "$SERVICE failed to start. Dumping status:"
  sudo systemctl status "$SERVICE" --no-pager || true
  echo
  echo "Recent logs:"
  sudo journalctl -u "$SERVICE" -n 80 --no-pager || true
  exit 1
fi

# ── 5. Tail logs ────────────────────────────────────────────────────────────
if [[ "$NO_TAIL" == "1" ]]; then
  ok "Skipping log tail (--no-tail). Watch with:  sudo journalctl -u $SERVICE -f"
  exit 0
fi

step "Logs  —  journalctl -u $SERVICE -n 100 -f   (Ctrl-C to exit)"
exec sudo journalctl -u "$SERVICE" -n 100 -f --no-pager
