# CLAUDE.md — TFPW-Compat 作業指針

`the_four_primitives_and_weapons` の **連携専用アドオン**。issue #199 の連携部分を独立 MOD 化したもの。

## ルール
- **返答は日本語**。
- **勝手にビルド / version bump しない**。`./gradlew build` はユーザー明示要請時のみ。
- 連携は **完全リフレクション**（compile 依存ゼロ、相手 MOD 不在でもロード成功）。本家 `compat/SpellbooksCompat.java` が手本。
- 相手 MOD の API へは `Class.forName` + `Method` でアクセスし、失敗時は無効化（落とさない）。

## バージョン / 座標
- MC 1.20.1 / Forge 1.20.1-47.4.0 / Java 17 / official mappings / ForgeGradle 5.1.+
- group `com.hrmcngs.tfpw_compat` / modId `tfpw_compat`

## 構成
```
src/main/java/com/hrmcngs/tfpw_compat/
├── TfpwCompat.java              # @Mod エントリポイント
└── compat/
    ├── IceAndFireCompat.java    # ドラゴン攻撃(炎/氷/雷) → 属性(FIRE/ICE/THUNDER)
    └── MekanismCompat.java      # 電気/雷属性 → FE 発電量換算
src/main/resources/META-INF/mods.toml  # 依存は全て mandatory=false
```

## TODO (issue #199)
- **A-4**: `IceAndFireCompat.elementForDamageSource()` を Ice and Fire の実 DamageSource/エンティティ型で判定。逆方向の弱点関係も検討。
- **B**: `MekanismCompat.feFromElementLevel()` を実際の発電経路（`IEnergyStorage` 公開 BlockEntity）に接続。Mekanism ケーブルが FE で吸える形にする。
- 本体側の武器追加 (A-1〜A-3: ドラゴン素材武器 / 剣3D化 / 属性D武器) は本家リポジトリ側で行う。ここは純粋な連携ロジックのみ。

## 本体 (属性システム提供元) 参照
本家 `the_four_primitives_and_weapons` の `damage/ElementType.java` / `ElementalDamageUtils.java` /
`mixin/LivingEntityDamageMixin.java` を参照。ElementType 名を文字列で受け渡す設計にしてある
（`ElementType.valueOf(name)` で解決する想定、compile 依存を避けるため）。
