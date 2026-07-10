#!/bin/bash
# WSL から Windows 側の gradlew.bat を呼び出して TFPW-Compat のクライアントを起動する。
# ( 本体リポジトリの run_client_win.sh と同じ役割 )
#
# 使い方:
#   bash run_client_win.sh            通常
#   bash run_client_win.sh offline    オフライン ( キャッシュ済み依存のみ )
#
# 起動前に本体 MOD ( the_four_primitives_and_weapons ) を毎回ビルドして jar を生成する。
#   抑止: SKIP_HOST_BUILD=1
#   非対話: HOST_BUILD_ARGS="-Pmod_version_override=1.20.1-dev -Prelease_type=test"
#
# Windows 側のパスは環境に合わせて編集するか、 環境変数で上書きすること:
#   PROJECT_WIN_PATH  ... TFPW-Compat の Windows パス
#   HOST_WIN_PATH     ... 本体 MOD の Windows パス ( 本体ビルドに使う )
#   HOST_DIR          ... 本体 MOD の WSL 側パス ( /mnt/c/... )

# ── 環境に合わせて編集 ─────────────────────────────────────────────
PROJECT_WIN_PATH="${PROJECT_WIN_PATH:-C:\\Users\\hrmcn\\Documents\\github\\mods\\TFPW-Compat}"
HOST_WIN_PATH="${HOST_WIN_PATH:-C:\\Users\\hrmcn\\MCreatorWorkspaces\\the_four_primitives_and_weapons}"
# ──────────────────────────────────────────────────────────────────

OFFLINE="no"
GRADLE_ARGS="runClient"
if [ "$1" = "offline" ]; then
    GRADLE_ARGS="$GRADLE_ARGS --offline -x downloadAssets -Dnet.minecraftforge.gradle.check.certs=false"
    OFFLINE="yes"
    echo "=== Offline mode (using cached dependencies) ==="
fi

# --- 本体 MOD の jar を毎回生成 ( Windows 側 gradlew.bat で本体をビルド ) ---
if [ "$SKIP_HOST_BUILD" = "1" ]; then
    echo "=== 本体MOD のビルドを skip (SKIP_HOST_BUILD=1) ==="
else
    HOST_GRADLE_ARGS="build"
    [ "$OFFLINE" = "yes" ] && HOST_GRADLE_ARGS="$HOST_GRADLE_ARGS --offline -Dnet.minecraftforge.gradle.check.certs=false"
    echo "=== 本体MOD をビルドして jar を生成: $HOST_WIN_PATH ==="
    [ -z "$HOST_BUILD_ARGS" ] && echo "    ( 本体が version を対話で聞く場合あり。 HOST_BUILD_ARGS で skip 可 )"
    if cmd.exe /c "cd /d $HOST_WIN_PATH && gradlew.bat $HOST_GRADLE_ARGS $HOST_BUILD_ARGS"; then
        echo "=== 本体MOD jar 生成 OK ==="
    else
        echo "!! 本体MOD のビルドに失敗 — libs/local に既存 jar があればそれを使います" >&2
    fi
fi

# --- TFPW-Compat のクライアント起動 ---
#   build.gradle は WSL 側パスで本体 jar を探すので、 必要なら HOST_DIR も設定すること
#   ( 例: HOST_DIR=/mnt/c/Users/hrmcn/MCreatorWorkspaces/the_four_primitives_and_weapons )
echo "=== runClient: $PROJECT_WIN_PATH ==="
cmd.exe /c "cd /d $PROJECT_WIN_PATH && gradlew.bat $GRADLE_ARGS"
