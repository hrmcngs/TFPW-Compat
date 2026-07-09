package com.hrmcngs.tfpw_compat.compat;

import net.minecraftforge.fml.ModList;

/**
 * Mekanism 連携 (issue #199-B: 電気/雷属性で発電)。
 *
 * <p>Mekanism は Forge Energy (FE) 互換なので、発電は <b>FE ({@code IEnergyStorage})</b>
 * 経由で行うのが最も疎結合。Mekanism 専用 Joule API には触れずに済む。
 * このクラスはロード判定と発電量の橋渡し骨格のみを持つ。
 *
 * <p>結合は <b>完全リフレクション</b>。Mekanism が無くてもロード失敗しない。
 */
public final class MekanismCompat {
    public static final String MOD_ID = "mekanism";
    public static final String GENERATORS_MOD_ID = "mekanismgenerators";

    private static Boolean loaded;

    private MekanismCompat() {}

    /** MOD ロード時に一度呼ぶ (キャッシュを温める)。 */
    public static void init() {
        isLoaded();
    }

    public static boolean isLoaded() {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(MOD_ID);
        }
        return loaded;
    }

    /**
     * 電気/雷属性の発生イベントを FE エネルギー量に換算する仮関数。
     *
     * <p>TODO(#199-B): 攻撃ヒット/落雷等から得た「属性レベル」を FE 量に変換し、
     *   本 MOD 側の発電ブロック ({@code IEnergyStorage} を公開する BlockEntity) へ
     *   充填する。Mekanism のケーブル/機械が FE として吸い出せる。
     *
     * @param elementLevel 本 MOD の属性レベル (ELECTRIC / THUNDER)
     * @return 生成する FE 量
     */
    public static int feFromElementLevel(int elementLevel) {
        if (elementLevel <= 0) return 0;
        // 暫定換算: レベル 1 あたり 200 FE。実バランスは着手時に調整する。
        return elementLevel * 200;
    }
}
