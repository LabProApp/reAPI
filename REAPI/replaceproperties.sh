#!/usr/bin/env bash
#
# Substitute ${KEY} and ${KEY:default} placeholders in application.properties
# using values from secret.properties. Both files must sit alongside this script.
#
# Usage:
#   ./replaceproperties.sh
#
# Writes the result back to application.properties (a .bak copy is kept).
# Does NOT modify the running JAR — re-run before each deploy as needed.
#

set -euo pipefail

# ---------- Setup ----------
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SECRETS_FILE="$SCRIPT_DIR/secret.properties"
PROPS_FILE="$SCRIPT_DIR/application.properties"

echo "=================================================="
echo " replaceproperties.sh — $(date '+%Y-%m-%d %H:%M:%S')"
echo "=================================================="
echo "[INFO] Working directory : $SCRIPT_DIR"
echo "[INFO] Secrets file      : $SECRETS_FILE"
echo "[INFO] Target properties : $PROPS_FILE"
echo

# ---------- Pre-flight checks ----------
if [[ ! -f "$SECRETS_FILE" ]]; then
    echo "[ERROR] $SECRETS_FILE not found" >&2
    exit 1
fi
echo "[OK]   secret.properties found"

if [[ ! -f "$PROPS_FILE" ]]; then
    echo "[ERROR] $PROPS_FILE not found" >&2
    exit 1
fi
echo "[OK]   application.properties found"

# Protect the secrets file and back up the target.
chmod 600 "$SECRETS_FILE" 2>/dev/null || true
echo "[INFO] Hardened permissions on secret.properties (chmod 600)"

cp -f "$PROPS_FILE" "${PROPS_FILE}.bak"
echo "[INFO] Backup written    : ${PROPS_FILE}.bak"
echo

# ---------- Build sed substitution script ----------
tmp_sed="$(mktemp)"
trap 'rm -f "$tmp_sed"' EXIT

echo "[INFO] Reading secrets and building substitutions..."
loaded=0
skipped=0
while IFS= read -r line || [[ -n "$line" ]]; do
    # Strip Windows CR
    line="${line%$'\r'}"
    # Trim leading whitespace
    line="${line#"${line%%[![:space:]]*}"}"
    # Skip blanks and comments
    [[ -z "$line" || "$line" == \#* ]] && continue

    # Split on first '='
    key="${line%%=*}"
    value="${line#*=}"

    # Trim whitespace from key
    key="${key%"${key##*[![:space:]]}"}"
    key="${key#"${key%%[![:space:]]*}"}"

    # Strip surrounding quotes from value
    if   [[ "$value" =~ ^\"(.*)\"$ ]]; then value="${BASH_REMATCH[1]}"
    elif [[ "$value" =~ ^\'(.*)\'$ ]]; then value="${BASH_REMATCH[1]}"
    fi

    if [[ ! "$key" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]]; then
        echo "[WARN] skipping invalid key: '$key'"
        skipped=$((skipped + 1))
        continue
    fi

    if [[ -z "$value" ]]; then
        echo "[WARN] $key has empty value — placeholder will be cleared"
    fi

    # Escape backslash, forward slash, and ampersand in the value for sed.
    escaped="$(printf '%s' "$value" | sed -e 's/[\\/&]/\\&/g')"

    # Two substitutions per key: ${KEY:default} first (longer match), then ${KEY}.
    printf 's/\\${%s:[^}]*}/%s/g\n' "$key" "$escaped" >> "$tmp_sed"
    printf 's/\\${%s}/%s/g\n'        "$key" "$escaped" >> "$tmp_sed"

    # Mask value in log (show first 2 / last 2 chars only)
    vlen=${#value}
    if (( vlen > 6 )); then
        masked="${value:0:2}****${value: -2}"
    else
        masked="****"
    fi
    echo "[OK]   loaded $key = $masked"
    loaded=$((loaded + 1))
done < "$SECRETS_FILE"

echo

# ---------- Apply substitutions ----------
echo "[INFO] Applying $loaded substitution(s) to $PROPS_FILE..."
sed -i -f "$tmp_sed" "$PROPS_FILE"
echo "[OK]   sed completed"

# ---------- Verify ----------
remaining="$(grep -oE '\$\{[A-Za-z_][A-Za-z0-9_]*(:[^}]*)?\}' "$PROPS_FILE" | sort -u || true)"
if [[ -n "$remaining" ]]; then
    echo
    echo "[WARN] The following placeholders were not substituted:"
    echo "$remaining" | sed 's/^/         /'
fi

echo
echo "=================================================="
echo " Summary"
echo "=================================================="
echo " Keys loaded     : $loaded"
echo " Keys skipped    : $skipped"
echo " Target updated  : $PROPS_FILE"
echo " Backup at       : ${PROPS_FILE}.bak"
echo "=================================================="
