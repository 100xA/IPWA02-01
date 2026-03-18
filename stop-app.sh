#!/usr/bin/env bash
set -euo pipefail

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

resolve_tomee_home "${1:-}"

if [[ ! -x "$TOMEE_HOME/bin/shutdown.sh" ]]; then
  echo "ERROR: '$TOMEE_HOME/bin/shutdown.sh' nicht gefunden oder nicht ausführbar."
  exit 1
fi

echo "==> TomEE stoppen"
"$TOMEE_HOME/bin/shutdown.sh"
echo "Fertig."
