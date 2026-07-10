#!/bin/bash
# 本体 MOD ( the_four_primitives_and_weapons ) の jar を生成する共通ヘルパ。
#
# ここで生成した <本体>/build/libs/*.jar を、 TFPW-Compat の build.gradle が
# libs/local/the_four_primitives_and_weapons/<version>/ へ自動取り込みする。
# ( Backpack-Arsenal と同じ仕組み )
#
# 使い方:
#   bash build_host.sh                # 本体をビルド
#   bash build_host.sh offline notls   # 引数はそのまま本体の build.sh へ渡す
#
# 環境変数:
#   SKIP_HOST_BUILD=1   本体ビルドを丸ごと skip
#   HOST_DIR=/path/to/The-four-primitives-and-Weapons   本体の場所
#   HOST_BUILD_ARGS="-Pmod_version_override=1.20.1-dev -Prelease_type=test"
#       本体の build.sh は version / release_type を対話で聞くので、
#       非対話 ( CI やスクリプト連鎖 ) ではこれで skip する。
#
# 終了コード: 0 = 生成成功 or skip、 1 = 本体のビルド失敗 ( 呼び出し側は続行してよい )

HOST_DIR="${HOST_DIR:-$HOME/The-four-primitives-and-Weapons}"

if [ "$SKIP_HOST_BUILD" = "1" ]; then
    echo "=== 本体MOD のビルドを skip (SKIP_HOST_BUILD=1) ==="
    exit 0
fi

if [ ! -d "$HOST_DIR" ]; then
    echo "=== 本体MOD ソースが無い ($HOST_DIR) — skip ( 連携は no-op で起動する ) ==="
    exit 0
fi

echo "=== 本体MOD をビルドして jar を生成: $HOST_DIR ==="
if [ -z "$HOST_BUILD_ARGS" ]; then
    echo "    ( 本体 build.sh が version を対話で聞く場合あり。 HOST_BUILD_ARGS で skip 可 )"
fi

if ( cd "$HOST_DIR" && bash build.sh "$@" $HOST_BUILD_ARGS ); then
    LATEST=$(ls -t "$HOST_DIR"/build/libs/*.jar 2>/dev/null | grep -vE -- '-(sources|dev|javadoc)\.jar$' | head -1)
    echo "=== 本体MOD jar 生成 OK ${LATEST:+( $(basename "$LATEST") )} ==="
    exit 0
else
    echo "!! 本体MOD のビルドに失敗 — libs/local に既存 jar があればそれを使います" >&2
    exit 1
fi
