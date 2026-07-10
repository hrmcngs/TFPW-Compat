#!/bin/bash
# TFPW-Compat オフライン起動用の薄いラッパ。
#   run_client.sh に offline を付けて呼ぶだけ。 追加引数はそのまま渡す。
#
# 使い方:
#   bash run_client_offline.sh          オフライン起動 ( キャッシュ済み依存のみ )
#   bash run_client_offline.sh notls    オフライン + TLS workaround off
#
# 前提: 一度でもオンラインで runClient / build を通し、 Forge/MC 依存と
#       アセットが取得済みであること。

cd "$(dirname "$0")"
exec bash run_client.sh offline "$@"
