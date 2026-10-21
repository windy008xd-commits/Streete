#!/usr/bin/env bash
set -eu
printf '%s\n' 'StreetEmu build preflight (launcher module only)'
command -v java >/dev/null || { echo 'Missing Java'; exit 1; }
java -version
if [ -z "${ANDROID_HOME:-}${ANDROID_SDK_ROOT:-}" ]; then
  echo 'ANDROID_HOME/ANDROID_SDK_ROOT is not set; Android SDK unavailable.'
  exit 2
fi
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
[ -d "$SDK/platforms" ] || { echo "No SDK platforms found under $SDK/platforms"; exit 2; }
ls "$SDK/platforms"
if [ ! -x ./gradlew ]; then
  echo 'Gradle wrapper is not included in this project snapshot; open in Android Studio or add wrapper from a trusted Gradle installation.'
  exit 3
fi
./gradlew :app:assembleDebug
