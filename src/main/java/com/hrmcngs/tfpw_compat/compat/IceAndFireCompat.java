package com.hrmcngs.tfpw_compat.compat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;

/**
 * Ice and Fire (ドラゴン MOD) 連携。
 *
 * <p>issue #199-A の受け皿。現時点ではロード判定とドラゴン攻撃 → 本 MOD 属性の
 * マッピング骨格のみを持つ。実際の属性ダメージ橋渡しは本体側 (LivingEntityDamageMixin /
 * LivingHurtEvent) から {@link #elementForDamageSource(DamageSource)} を呼ぶ形で拡張する。
 *
 * <p>結合は <b>完全リフレクション</b>。compile 依存は張らないので、
 * Ice and Fire が無くても / API が変わってもロード失敗しない。
 */
public final class IceAndFireCompat {
    public static final String MOD_ID = "iceandfire";

    private static Boolean loaded;

    private IceAndFireCompat() {}

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
     * ドラゴンの攻撃/ブレスに対応する本 MOD の属性名を返す。
     *
     * <p>戻り値は the_four_primitives_and_weapons の {@code ElementType} 名
     * ("FIRE" / "ICE" / "THUNDER" など)。橋渡し側で
     * {@code ElementType.valueOf(name)} で解決する想定。連携無効時や非該当時は null。
     *
     * <p>TODO(#199-A4): Ice and Fire の実 DamageSource / エンティティ型を判定して
     *   火ドラゴン → FIRE、氷ドラゴン → ICE、雷系 → THUNDER を返す。
     */
    public static String elementForDamageSource(DamageSource source) {
        if (!isLoaded() || source == null) return null;

        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();

        // Ice and Fire のエンティティ型はリフレクション/クラス名で判定する (compile 依存を避ける)。
        String name = classNameOf(direct);
        if (name == null) name = classNameOf(attacker);
        if (name == null) name = source.getMsgId(); // ブレス等の DamageType id を fallback で見る

        if (name == null) return null;
        String lower = name.toLowerCase();

        if (lower.contains("fire"))      return "FIRE";
        if (lower.contains("ice"))       return "ICE";
        if (lower.contains("lightning")) return "THUNDER";
        return null;
    }

    private static String classNameOf(Entity e) {
        if (e == null) return null;
        Class<?> c = e.getClass();
        String cn = c.getName();
        // Ice and Fire 由来のクラスだけを対象にする (誤爆防止)。
        return cn.contains("iceandfire") ? cn : null;
    }
}
