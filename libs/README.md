# libs/ — 前提 MOD ( 開発時のみ )

TFPW-Compat 自体は **compile 依存ゼロ**（完全リフレクション + Forge ネイティブ実装）なので、
ここに jar が無くても **ビルドは通り、MOD もロードに成功する**（連携は no-op になるだけ）。

`runClient` で実際に連携動作を確認したいときだけ、下記の deobf/production jar を
`libs/local/` に放り込む。build.gradle が再帰で拾い `fg.deobf` で自動取り込みする。

## 置き方

**`libs/local/` に .jar を放り込むだけ。名前も階層も問わない。**
build.gradle が再帰で全部拾い、Maven 階層へ自動転写して `fg.deobf` で取り込む。

```
libs/local/iceandfire-1.20.1-2.1.13.jar                          # フラット ( 推奨 )
libs/local/anything/nested/whatever.jar                          # サブフォルダでも可
libs/local/iceandfire/1.20.1-2.1.13/iceandfire-1.20.1-2.1.13.jar # Maven 階層でも可
```

座標の決め方:

| ファイル名 | 取り込み座標 |
|---|---|
| `iceandfire-1.20.1-2.1.13.jar` | `local:iceandfire:1.20.1-2.1.13` |
| `curios-forge-1.20.1-5.9.1.jar` | `local:curios-forge:1.20.1-5.9.1` |
| `embeddium-0.3.31+mc1.20.1.jar` | `local:embeddium:0.3.31+mc1.20.1` |
| `SomeMod.jar`（version 不明） | `local:SomeMod:0.0.0` ← **fallback で必ず取り込む** |
| `My Cool Mod (1).jar` | `local:My_Cool_Mod__1_:0.0.0` ← 記号は `_` に置換 |

`<artifact>-<version>.jar`（最初に数字で始まる token 以降が version）に沿っていれば座標がきれいになる。
沿っていなくても version `0.0.0` として読み込むので、**どんな MOD jar でも起動できる**。

一時的に無効化したい jar は `libs/local-disabled/` へ移動する（走査対象外なので読み込まれない）。

## 入れる MOD

| MOD | 用途 | 必須? |
|---|---|---|
| `the_four_primitives_and_weapons` | **本体**。属性システム(ElementType/ElementalDamageUtils)の提供元。武器モデルの親モデルもここ | 連携の主対象 |
| `curios` | 本体の依存（book スロット）。本体を動かすなら必要 | 本体を入れるなら必須 |
| `iceandfire` | Ice and Fire: Community Edition (IAFEnvoy)。ドラゴン攻撃↔属性の紐付け(A-4)・ドラゴン素材武器のレシピ素材 | 任意 |
| `uranus` / `jupiter` | Ice and Fire CE (Architectury) の依存ライブラリ | iceandfire を入れるなら必須 |
| `mekanism` | 属性発電機(FE)の受け先。ケーブル/機械で FE を吸えるか確認用 | 任意 |
| `mekanismgenerators` | Mekanism 発電系（任意） | 任意 |

すべて **MC 1.20.1 / Forge** 版を使うこと。

## 依存の性質

`META-INF/mods.toml` では上記すべて `mandatory=false`。
どれが欠けても TFPW-Compat はロードに成功し、該当連携だけが無効化される:

- 本体が無い → 属性 NBT は無視され、ドラゴン/武器は普通の Mob / 剣として動く。武器モデルの親が解決できず見た目が欠落する
- iceandfire が無い → A-4 のドラゴン判定が no-op、ドラゴン素材武器のレシピが自動無効化
- mekanism が無い → 属性発電機は FE を貯めるだけ（他の FE 機器があればそちらへ送電）

## 起動

```bash
bash run_client.sh            # 通常
bash run_client_offline.sh    # オフライン ( 依存キャッシュ済みのとき )
```
