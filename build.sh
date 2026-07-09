#!/bin/bash
# TFPW-Compat の jar をビルドするスクリプト（mac / WSL / Windows Git Bash 対応）
#
# 使い方:
#   bash build.sh                 通常ビルド (オンライン、 TLS workaround on)
#   bash build.sh clean           クリーンビルド (build/ を掃除。 fg_cache は残す)
#   bash build.sh offline         オフラインビルド (キャッシュ済み依存のみ、 DL しない)
#   bash build.sh clean offline   クリーン + オフライン (順不同)
#   bash build.sh notls           テザリング等 直接回線向け (TLS workaround off で素の TLS)
#   bash build.sh keepdaemon      gradle daemon を kill しない (デフォルトは kill)
#
# 生成物: build/libs/TFPW-Compat-forge-1.20.1-<version>.jar
#
# TFPW-Compat の連携コードは完全リフレクションなので、 ビルド自体に相手 MOD
# (iceandfire / mekanism / 本体) の jar は不要。 依存は Forge / Minecraft のみ。
# 初回のオンラインビルドで Forge/MC 依存を取得すれば、 以降は offline で通る。

cd "$(dirname "$0")"

TASKS="build"
GRADLE_ARGS=""
LABEL="Build"
DO_CLEAN=0
USE_TLS_WORKAROUND="yes"
KILL_DAEMON="yes"

for arg in "$@"; do
    case "$arg" in
        clean)
            DO_CLEAN=1
            LABEL="Clean Build"
            ;;
        offline)
            GRADLE_ARGS="$GRADLE_ARGS --offline -Dnet.minecraftforge.gradle.check.certs=false"
            LABEL="$LABEL (Offline)"
            ;;
        notls|no-tls)
            USE_TLS_WORKAROUND="no"
            ;;
        keepdaemon|keep-daemon)
            KILL_DAEMON="no"
            ;;
        *)
            # それ以外は Gradle にそのまま渡す (-Pmod_version=X 等)
            GRADLE_ARGS="$GRADLE_ARGS $arg"
            ;;
    esac
done

echo "=== $LABEL ==="

# --- クリーン: build/ を掃除するが build/fg_cache ( 再コンパイル済み Minecraft 依存 ) は残す ---
#   gradle の `clean` タスクは fg_cache も消すため、 同一実行の compileJava が
#   「設定時にマップ済み MC jar が未生成」 で net.minecraft.* を解決できず失敗する。
#   依存キャッシュを残せば 1 回の build で通る。 MC を完全再生成したい時のみ手動で `rm -rf build`。
if [ "$DO_CLEAN" = "1" ] && [ -d build ]; then
    find build -mindepth 1 -maxdepth 1 ! -name fg_cache -exec rm -rf {} +
fi

# --- gradle daemon を停止 ( JVM 引数 / 環境変数の変更を確実に反映 ) ---
if [ "$KILL_DAEMON" = "yes" ] && [ -x ./gradlew ]; then
    echo "=== Stopping any running gradle daemon (--stop) ==="
    ./gradlew --stop > /dev/null 2>&1 || true
fi

# --- TLS workaround (Cisco Umbrella SSL inspection 対策) ---
#   開発端末のネットが Cisco Umbrella の透過 SSL 検査を経由しており、 中間 Proxy が
#   TLS 1.3 / ECDHE 系 cipher を理解できず JDK 17 の握手が handshake_failure になる。
#   TLS 1.2 + RSA cipher に限定して maven.minecraftforge.net へ到達させる。
#   `notls` 引数で無効化 (テザリング等 直接回線の時)。 offline なら DL しないので無関係。
TLS_WORKAROUND_FILE="$(pwd)/tls_workaround.properties"
if [ "$USE_TLS_WORKAROUND" = "yes" ] && [ -f "$TLS_WORKAROUND_FILE" ]; then
    export JAVA_TOOL_OPTIONS="-Djava.security.properties=${TLS_WORKAROUND_FILE} -Djdk.tls.client.protocols=TLSv1.2 -Dhttps.protocols=TLSv1.2 -Djdk.tls.client.cipherSuites=TLS_RSA_WITH_AES_256_GCM_SHA384,TLS_RSA_WITH_AES_128_GCM_SHA256,TLS_RSA_WITH_AES_256_CBC_SHA256,TLS_RSA_WITH_AES_128_CBC_SHA256"
    echo "=== TLS workaround ON (Umbrella 対策 / notls で無効化) ==="
else
    echo "=== TLS workaround OFF ==="
fi

case "$(uname -s)" in
    MINGW*|CYGWIN*|MSYS*)
        ./gradlew.bat $TASKS $GRADLE_ARGS
        ;;
    *)
        ./gradlew $TASKS $GRADLE_ARGS
        ;;
esac

STATUS=$?
if [ "$STATUS" = "0" ]; then
    echo "=== Done. 生成物: build/libs/ ==="
    ls -1 build/libs/*.jar 2>/dev/null | sed 's/^/  → /'
fi
exit $STATUS
