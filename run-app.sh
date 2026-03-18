#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="ghostnetfishing-1.0.0-SNAPSHOT"
WAR_PATH="$PROJECT_DIR/target/${APP_NAME}.war"
APP_PORT="${APP_PORT:-8080}"
DEFAULT_TOMEE_HOME="/opt/homebrew/opt/tomee-plume/libexec"

resolve_tomee_home() {
  if [[ -n "${1:-}" ]]; then
    export TOMEE_HOME="$1"
  fi

  if [[ -z "${TOMEE_HOME:-}" ]]; then
    export TOMEE_HOME="$DEFAULT_TOMEE_HOME"
    echo "==> TOMEE_HOME nicht gesetzt, verwende Default: $TOMEE_HOME"
  fi
}

print_usage() {
  cat <<EOF
Usage:
  TOMEE_HOME=/path/to/apache-tomee-9 ./run-app.sh
  ./run-app.sh /path/to/apache-tomee-9

Optional env vars:
  APP_PORT=8080       (default: 8080)
  SKIP_BUILD=1        (skip maven package)
  GHOSTNET_DB_URL
  GHOSTNET_DB_USER
  GHOSTNET_DB_PASSWORD
Default TOMEE_HOME:
  /opt/homebrew/opt/tomee-plume/libexec
EOF
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  print_usage
  exit 0
fi

resolve_tomee_home "${1:-}"

if [[ ! -x "$TOMEE_HOME/bin/startup.sh" ]]; then
  echo "ERROR: '$TOMEE_HOME/bin/startup.sh' nicht gefunden oder nicht ausführbar."
  exit 1
fi

# macOS: force Java 17 for maven and runtime if available
if [[ -x "/usr/libexec/java_home" ]]; then
  JAVA17_HOME="$(/usr/libexec/java_home -v 17 2>/dev/null || true)"
  if [[ -n "$JAVA17_HOME" ]]; then
    export JAVA_HOME="$JAVA17_HOME"
    export PATH="$JAVA_HOME/bin:$PATH"
  fi
fi

echo "==> Projektordner: $PROJECT_DIR"
echo "==> TOMEE_HOME:    $TOMEE_HOME"
echo "==> JAVA_HOME:     ${JAVA_HOME:-<nicht gesetzt>}"

# Pass optional DB credentials to TomEE/JPA via JVM system properties.
DB_PROPS=()
if [[ -n "${GHOSTNET_DB_URL:-}" ]]; then
  DB_PROPS+=("-Ddb.url=${GHOSTNET_DB_URL}")
fi
if [[ -n "${GHOSTNET_DB_USER:-}" ]]; then
  DB_PROPS+=("-Ddb.user=${GHOSTNET_DB_USER}")
fi
if [[ "${GHOSTNET_DB_PASSWORD+x}" == "x" ]]; then
  DB_PROPS+=("-Ddb.password=${GHOSTNET_DB_PASSWORD}")
fi
if [[ ${#DB_PROPS[@]} -gt 0 ]]; then
  export CATALINA_OPTS="${CATALINA_OPTS:-} ${DB_PROPS[*]}"
  echo "==> DB Overrides aktiv: url=${GHOSTNET_DB_URL:-<default>} user=${GHOSTNET_DB_USER:-<default>} password=<hidden>"
fi

if [[ "${SKIP_BUILD:-0}" != "1" ]]; then
  if ! command -v mvn >/dev/null 2>&1; then
    echo "ERROR: 'mvn' nicht gefunden. Bitte Maven installieren oder PATH/JAVA_HOME prüfen."
    exit 1
  fi
  echo "==> Maven Build (clean package)"
  (cd "$PROJECT_DIR" && mvn -q clean package)
else
  echo "==> Build übersprungen (SKIP_BUILD=1)"
fi

if [[ ! -f "$WAR_PATH" ]]; then
  echo "ERROR: WAR nicht gefunden: $WAR_PATH"
  exit 1
fi

echo "==> Alte Deployment-Artefakte entfernen"
rm -f "$TOMEE_HOME/webapps/${APP_NAME}.war"
rm -rf "$TOMEE_HOME/webapps/${APP_NAME}"

echo "==> WAR nach TomEE kopieren"
cp "$WAR_PATH" "$TOMEE_HOME/webapps/"

echo "==> TomEE starten"
"$TOMEE_HOME/bin/startup.sh"

echo
echo "Fertig."
echo "App URL: http://localhost:${APP_PORT}/${APP_NAME}/"
echo "Logs:    tail -f \"$TOMEE_HOME/logs/catalina.out\""
