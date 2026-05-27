#!/usr/bin/env bash

set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$script_dir"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven no esta instalado o no esta en PATH."
  exit 1
fi

mvn -q -DskipTests org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=app.Main