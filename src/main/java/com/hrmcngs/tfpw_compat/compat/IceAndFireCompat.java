package com.hrmcngs.tfpw_compat.compat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;

/**
 * Ice and Fire (ドラゴン MOD) 連携。
 *
 * <p>issue #199-A4: ドラゴンの属性攻撃 (炎/氷/雷ブレス・噛みつき等) を本 MOD の
 * {@code ElementType} 名にマッピングする。橋渡し側 (LivingHurtEvent 等) で
 * {@link #elementForDamageSource(DamageSource)} を呼び、返った属性名を
 * {@code ElementType.valueOf(name)} で解決する想定。
 *
 * <p>結合は <b>完全リフレクション</b>。compile 依存は張らないので、
 * Ice and Fire が無くても / API が変わってもロード失敗しない。
 */
public final class IceAndFireCompat {
    public static final String MOD_ID = "iceandfire";

    /**
     * Ice and Fire のエンティティが属する Java パッケージ接頭辞 (複数フォーク対応)。
     * <ul>
     *   <li>{@code com.iafenvoy.iceandfire} — Community Edition (IAFEnvoy/IceAndFire-CE)</li>
     *   <li>{@code com.github.alexthe666.iceandfire} — 本家 (alexthe666)</li>
     * </ul>
     * どちらの jar が入っていても連携できるよう両方を対象にする。
     */
    private static final String[] IAF_PACKAGES = {
        "com.iafenvoy.iceandfire",
        "com.github.alexthe666.iceandfire",
    };

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
     * ("FIRE" / "ICE" / "THUNDER")。橋渡し側で {@code ElementType.valueOf(name)} で
     * 解決する想定。連携無効時や非該当時は null。
     *
     * <p>判定順は <b>lightning → ice → fire</b>。Ice and Fire のエンティティ単純名
     * ({@code EntityFireDragon} / {@code EntityIceDragon} / {@code EntityLightningDragon} /
     * ブレス弾 {@code EntityDragonFire*} 等) を対象にする。
     *
     * <p><b>注意:</b> 完全修飾名 (例
     * {@code com.github.alexthe666.iceandfire.entity.EntityLightningDragon}) に対して
     * {@code contains("fire")} すると、名前空間 {@code iceandfire} が "ice"/"fire" を
     * 部分文字列として含むため必ず誤爆する。ここでは必ず<b>単純名</b>で判定する。
     */
    public static String elementForDamageSource(DamageSource source) {
        if (!isLoaded() || source == null) return null;

        // 1) 直接エンティティ (ブレス弾 / チャージ) → 攻撃者エンティティ (ドラゴン本体) の順で見る。
        String element = elementForEntity(source.getDirectEntity());
        if (element == null) element = elementForEntity(source.getEntity());
        if (element != null) return element;

        // 2) エンティティが取れない場合 (DamageType のみ) は msgId を fallback で判定。
        //    バニラの "onFire"/"lightningBolt" 等を拾わないよう "dragon" を含む id に限定する。
        return elementForMessageId(source.getMsgId());
    }

    /** Ice and Fire 由来エンティティの単純名から属性名を判定。非該当は null。 */
    public static String elementForEntity(Entity e) {
        if (e == null) return null;
        Class<?> c = e.getClass();
        // Ice and Fire のクラスだけを対象にする (誤爆防止)。
        String fqn = c.getName();
        boolean fromIaf = false;
        for (String pkg : IAF_PACKAGES) {
            if (fqn.startsWith(pkg)) { fromIaf = true; break; }
        }
        if (!fromIaf) return null;
        return elementFromKeyword(simpleName(c));
    }

    /** DamageType の msgId から属性名を判定 (ドラゴン系ブレスの DamageSource 用)。 */
    private static String elementForMessageId(String msgId) {
        if (msgId == null) return null;
        String lower = msgId.toLowerCase();
        // バニラ火/雷 (onFire, inFire, lightningBolt) と区別するため dragon 系に限定。
        if (!lower.contains("dragon")) return null;
        return elementFromKeyword(lower);
    }

    /**
     * 単純名 / id 文字列にドラゴン属性キーワードが含まれるかで属性名を返す。
     * lightning を最優先で判定する (氷/炎キーワードとの取り違えを防ぐ)。
     */
    private static String elementFromKeyword(String s) {
        if (s == null) return null;
        String lower = s.toLowerCase();
        if (lower.contains("lightning")) return "THUNDER";
        if (lower.contains("ice"))       return "ICE";
        if (lower.contains("fire"))      return "FIRE";
        return null;
    }

    /**
     * ドラゴンの属性攻撃に対する<b>防御 (無効化) 側</b>のカウンター属性名を返す。
     *
     * <p>本体 {@code ElementType.getCounterElement()} と同じ対応関係:
     * FIRE→WATER / ICE→FIRE / THUNDER→WIND。対象がこの属性の魔導書をスロットに
     * 装備していればブレスを無効化する、といった逆方向連携に使う。非該当は null。
     */
    public static String counterElementForDamageSource(DamageSource source) {
        String element = elementForDamageSource(source);
        if (element == null) return null;
        switch (element) {
            case "FIRE":    return "WATER";
            case "ICE":     return "FIRE";
            case "THUNDER": return "WIND";
            default:        return null;
        }
    }

    /** 完全修飾名からパッケージを除いた単純クラス名 (内部クラスの $ 以降も落とす)。 */
    private static String simpleName(Class<?> c) {
        String cn = c.getName();
        int dot = cn.lastIndexOf('.');
        if (dot >= 0) cn = cn.substring(dot + 1);
        int dollar = cn.lastIndexOf('$');
        if (dollar >= 0) cn = cn.substring(dollar + 1);
        return cn;
    }
}
