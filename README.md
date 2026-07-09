# TFPW-Compat

[The four primitives and Weapons](https://github.com/Drowse-Lab/The-four-primitives-and-Weapons) の **連携専用アドオン MOD**。
[issue #199](https://github.com/Drowse-Lab/The-four-primitives-and-Weapons/issues/199) の連携部分を独立 MOD として切り出したもの。

- **Minecraft**: 1.20.1
- **Loader**: Forge 1.20.1-47.4.0 (Java 17)
- **modId**: `tfpw_compat`

## 連携先 (すべて任意依存)

| 相手 MOD | modId | 目的 |
|----------|-------|------|
| The four primitives and Weapons | `the_four_primitives_and_weapons` | 属性システム提供元 (本体) |
| Ice and Fire | `iceandfire` | ドラゴン攻撃 (炎/氷/雷) ↔ 本 MOD 属性 (FIRE/ICE/THUNDER) の紐付け |
| Mekanism | `mekanism` / `mekanismgenerators` | 電気/雷属性で発電 (Forge Energy 経由) |

いずれの連携先も `mandatory=false`。**相手 MOD が無くてもロードは成功**し、該当連携が no-op になる。

## 設計方針

- 連携コードは **完全リフレクション** で相手 API にアクセスする (compile 依存ゼロ)。
  本家 `SpellbooksCompat.java` を手本にしている。相手 API が変わってもロード失敗しない。
- 実装は `src/main/java/com/hrmcngs/tfpw_compat/compat/` に集約。
  - `IceAndFireCompat.java` — ドラゴン攻撃 → 属性マッピング
  - `MekanismCompat.java` — 属性 → FE 発電量換算

## ビルド

```sh
./gradlew build
```

## 開発時に実際に動かして確認する

連携相手の **deobf jar** を `libs/local/` に置くと `runClient` に同梱される
(コード本体はリフレクションなので compile 依存は不要、実動作確認用のみ)。

```sh
mkdir -p libs/local
# 例: iceandfire-1.20.1-x.y.z.jar / Mekanism-1.20.1-x.y.z.jar を配置
./gradlew runClient
```

## 現状 (スキャフォールド)

エントリポイントとロード判定・マッピング骨格まで。実ロジックは各ファイルの `TODO(#199-...)` を参照。
