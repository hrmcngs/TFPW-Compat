#!/bin/bash
# TFPW-Compat の開発クライアントを起動するスクリプト（mac / WSL / Windows Git Bash 対応）
#
# 使い方:
#   bash run_client.sh              通常 ( オンライン、 TLS workaround on )
#   bash run_client.sh offline      オフライン ( キャッシュ済み依存のみ、 downloadAssets skip )
#   bash run_client.sh notls        テザリング等 直接回線向け ( TLS workaround off )
#   bash run_client.sh offline notls 併用も可
#   bash run_client.sh keepdaemon   gradle daemon を kill しない ( デフォルトは kill )
#
# 連携先 MOD ( iceandfire / mekanism / 本体 the_four_primitives_and_weapons ) を
# 実際にロードして動作確認したい場合は、 deobf jar を libs/local/ に置くと
# build.gradle が runtimeOnly(fg.deobf) で自動取り込みする ( 追加フラグ不要 )。

cd "$(dirname "$0")"

GRADLE_ARGS="runClient"
USE_TLS_WORKAROUND="yes"
KILL_DAEMON="yes"
OFFLINE="no"

for arg in "$@"; do
    case "$arg" in
        offline)
            GRADLE_ARGS="$GRADLE_ARGS --offline -x downloadAssets"
            OFFLINE="yes"
            echo "=== Offline mode (cached deps, downloadAssets skip) ==="
            ;;
        notls|no-tls)
            USE_TLS_WORKAROUND="no"
            echo "=== TLS workaround OFF (素の TLS — テザリング等 直接回線向け) ==="
            ;;
        keepdaemon|keep-daemon)
            KILL_DAEMON="no"
            ;;
        *)
            GRADLE_ARGS="$GRADLE_ARGS $arg"
            ;;
    esac
done

# --- 本体 MOD (the_four_primitives_and_weapons) の jar を毎回生成 ---
#   build_host.sh が本体をビルドし、 build.gradle が build/libs の最新 jar を
#   libs/local/ に自動取り込みする ( SKIP_HOST_BUILD=1 / HOST_DIR / HOST_BUILD_ARGS で制御 )。
HOST_ARGS=""
[ "$OFFLINE" = "yes" ] && HOST_ARGS="$HOST_ARGS offline"
[ "$USE_TLS_WORKAROUND" = "no" ] && HOST_ARGS="$HOST_ARGS notls"
bash "$(dirname "$0")/build_host.sh" $HOST_ARGS || true

# --- gradle daemon を停止 ( JVM 引数 / 環境変数の変更を確実に反映 ) ---
if [ "$KILL_DAEMON" = "yes" ] && [ -x ./gradlew ]; then
    echo "=== Stopping any running gradle daemon (--stop) ==="
    ./gradlew --stop > /dev/null 2>&1 || true
fi

# --- TLS workaround (Cisco Umbrella SSL inspection 対策 / build.sh と同じ) ---
TLS_WORKAROUND_FILE="$(pwd)/tls_workaround.properties"
if [ "$USE_TLS_WORKAROUND" = "yes" ] && [ -f "$TLS_WORKAROUND_FILE" ]; then
    export JAVA_TOOL_OPTIONS="-Djava.security.properties=${TLS_WORKAROUND_FILE} -Djdk.tls.client.protocols=TLSv1.2 -Dhttps.protocols=TLSv1.2 -Djdk.tls.client.cipherSuites=TLS_RSA_WITH_AES_256_GCM_SHA384,TLS_RSA_WITH_AES_128_GCM_SHA256,TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_RSA_WITH_AES_128_CBC_SHA256"
    echo "=== TLS workaround ON (Umbrella 対策 / notls で無効化) ==="
else
    echo "=== TLS workaround OFF ==="
fi

# --- 連携先 MOD ( libs/local/ ) の同梱状況を表示 ( build.gradle が自動取り込み ) ---
if [ -d libs/local ]; then
    found=0
    while IFS= read -r jar; do
        echo "  → 外部mod同梱: $jar"
        found=$((found + 1))
    done < <(find libs/local -type f -name "*.jar" 2>/dev/null | sort)
    [ "$found" = "0" ] && echo "  ! libs/local/ に .jar なし ( iceandfire / mekanism / 本体 の deobf jar を置くと連携を実機確認できる )"
else
    echo "  ! libs/local/ が無い ( 連携先 MOD 無しで単体起動 — Compat は no-op で動作 )"
fi

case "$(uname -s)" in
    MINGW*|CYGWIN*|MSYS*)
        ./gradlew.bat $GRADLE_ARGS
        ;;
    *)
        ./gradlew $GRADLE_ARGS
        ;;
esac
