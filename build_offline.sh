#!/bin/bash
# TFPW-Compat オフラインビルド用の薄いラッパ。
#   build.sh に offline を付けて呼ぶだけ。 追加引数はそのまま渡す。
#
# 使い方:
#   bash build_offline.sh            オフラインビルド (キャッシュ済み依存のみ、 DL しない)
#   bash build_offline.sh clean      クリーン + オフライン
#
# 前提: 一度でもオンラインで build.sh を通し、 Forge/MC 依存が
#       ~/.gradle と build/fg_cache に取得済みであること。
#   ネット未接続 / Umbrella で握手できない環境でも、 キャッシュがあればこれで通る。

cd "$(dirname "$0")"
exec bash build.sh offline "$@"
