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

| MOD | 用途 | 入手 |
|---|---|---|
| `the_four_primitives_and_weapons` | **本体**。属性システム(ElementType/ElementalDamageUtils)の提供元。武器モデルの親モデルもここ | **自動**: ソースから毎回ビルドして取り込み（下記） |
| `curios` | 本体の依存（book スロット） | **自動DL**（`maven.theillusivec4.top`） |
| `mekanism` | 属性発電機(FE)の受け先。ケーブル/機械で FE を吸えるか確認用 | **自動DL**（CurseMaven `mekanism-268560`） |
| `iceandfire` | **必須**。属性ドラゴンの描画に IaF のモデルを使う。A-4 の判定・武器レシピ素材も | **自動DL**（CurseMaven `iceandfire-ce-1040076`） |
| `uranus` | **必須**。`IafDragonRenderer` が `TabulaModel` を直接参照（compileOnly） | **自動DL**（`uranus-1010827`） |
| `jupiter` | Ice and Fire CE の mandatory 依存ライブラリ | **自動DL**（`jupiter-1072905`） |
| `mekanismgenerators` | Mekanism 発電系（任意） | 手動 or CurseMaven |

すべて **MC 1.20.1 / Forge** 版を使うこと。

### 本体 MOD は毎回ビルドして取り込む（Backpack-Arsenal と同方式）

`run_client.sh` が起動前に本体をビルドし、build.gradle が
`<本体>/build/libs` の最新 jar を `libs/local/the_four_primitives_and_weapons/1.20.1-dev/` へ複製する。
つまり**本体の最新ソースが常に反映される**（手動コピー不要）。

```bash
HOST_DIR=/path/to/The-four-primitives-and-Weapons  bash run_client.sh   # 場所を変える
SKIP_HOST_BUILD=1                                  bash run_client.sh   # 本体ビルドを抑止
```
build.gradle 側の上書き: `-PhostSourceProject=...` / `-Phost_version=...`

### 本体ビルドの対話プロンプト

本体の `build.sh` は version / release_type を対話で聞く。非対話で回すには:

```bash
HOST_BUILD_ARGS="-Pmod_version_override=1.20.1-dev -Prelease_type=test" bash run_client.sh
```


### 自動ダウンロードの制御

```bash
./gradlew build -PwithPrereqMods=false            # 前提MODを一切DLしない（完全オフライン compile）
./gradlew runClient -Pmekanism_curse_file=NNNN    # Mekanism の fileId 差し替え
./gradlew runClient -Piceandfire_curse_file=NNNN  # Ice and Fire CE の fileId 差し替え
./gradlew runClient -Piceandfire_curse_file=      # 空 → IaF CE + uranus + jupiter を丸ごと無効化
./gradlew runClient -Pcurios_version=5.10.0+1.20.1
```

現在の既定 fileId:

| MOD | CurseMaven 座標 | 既定 fileId |
|---|---|---|
| Ice and Fire CE | `curse.maven:iceandfire-ce-1040076` | `8378632` (1.2.6 / 1.20.1 forge) |
| uranus | `curse.maven:uranus-1010827` | `7745532` |
| jupiter | `curse.maven:jupiter-1072905` | `7738299` |
| Mekanism | `curse.maven:mekanism-268560` | `6552911` |

fileId は各 MOD の CurseForge `files` ページで確認して上書きできる。


## Ice and Fire を dev で動かす仕組み（自動）

`uranus` は **Architectury 9.2.14 を jar-in-jar (`META-INF/jars/`) で同梱**している。
ところが **`fg.deobf` は外側の jar しか remap せず、入れ子の jar は素通し**する
（原本と deobf 後の同梱 architectury は sha256 が完全一致することを確認済み）。

その結果 SRG のままの Architectury が読まれ、`@Shadow` フィールドが SRG 名のため
official マッピングの dev では解決できず、こうしてクラッシュしていた:

```
Mixin apply failed architectury.mixins.json:MixinFallingBlockEntity
  → @Shadow field f_31946_ was not located in FallingBlockEntity
```

**build.gradle が自動で対処する:**

1. 生の uranus jar を解決し、同梱 Architectury（と署名ファイル）を除去した
   `libs/local/uranus-stripped/<fileId>/uranus-stripped-<fileId>.jar` を生成
2. 同じ **Architectury 9.2.14 を CurseMaven から `fg.deobf` 付きで別途取得**
   （`curse.maven:architectury-api-419699:5137938`）

deobf 後は `private BlockState blockState;` と official 名になり、mixin が正常に適用される。

無効化したい場合:

```bash
./gradlew compileJava -PwithIceAndFire=false   # コンパイルだけ通したいとき
```

⚠ `mods.toml` で `iceandfire` / `uranus` は **`mandatory=true`** にしてある。
`-PwithIceAndFire=false` で `runClient` すると**必須依存が欠けて起動できない**。

## 依存の性質

`META-INF/mods.toml` では上記すべて `mandatory=false`。
どれが欠けても TFPW-Compat はロードに成功し、該当連携だけが無効化される:

- 本体が無い → 属性 NBT は無視され、ドラゴン/武器は普通の Mob / 剣として動く。武器モデルの親が解決できず見た目が欠落する
- **iceandfire / uranus は必須**（`mandatory=true`）。無いと TFPW-Compat 自体がロードされない
- mekanism が無い → 属性発電機は FE を貯めるだけ（他の FE 機器があればそちらへ送電）

## 起動

```bash
bash run_client.sh            # 通常
bash run_client_offline.sh    # オフライン ( 依存キャッシュ済みのとき )
```
