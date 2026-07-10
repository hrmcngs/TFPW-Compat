#!/bin/bash
# TFPW-Compat の開発クライアントを起動する（mac / WSL / Windows Git Bash 対応）。
#
# 本体リポジトリの run_client_mac.sh と同じ命名。中身は run_client.sh と同一なので薄いラッパ。
#
# 使い方:
#   bash run_client_mac.sh                通常 ( オンライン、 TLS workaround on )
#   bash run_client_mac.sh offline        オフライン ( キャッシュ済み依存のみ )
#   bash run_client_mac.sh notls          テザリング等 ( workaround off で素の TLS )
#   bash run_client_mac.sh offline notls  併用も可
#   bash run_client_mac.sh keepdaemon     gradle daemon を kill しない
#
# 起動前に本体 MOD ( the_four_primitives_and_weapons ) を毎回ビルドして jar を生成する。
#   抑止: SKIP_HOST_BUILD=1
#   場所: HOST_DIR=/path/to/The-four-primitives-and-Weapons
#   非対話: HOST_BUILD_ARGS="-Pmod_version_override=1.20.1-dev -Prelease_type=test"

cd "$(dirname "$0")"
exec bash run_client.sh "$@"
