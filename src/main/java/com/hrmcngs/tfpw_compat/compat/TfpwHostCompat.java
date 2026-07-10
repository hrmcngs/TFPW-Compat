package com.hrmcngs.tfpw_compat.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

/**
 * 本体 the_four_primitives_and_weapons (属性システム提供元) への橋渡し。
 *
 * <p>本体が無い / API が変わっても落ちないよう <b>完全リフレクション</b>でアクセスする。
 * ElementType 名は文字列で受け渡し ({@code ElementType.valueOf(name)} で解決)。
 */
public final class TfpwHostCompat {
    public static final String MOD_ID = "the_four_primitives_and_weapons";

    private static final String ELEMENT_TYPE_CLASS =
            "the_four_primitives_and_weapons.damage.ElementType";
    private static final String DAMAGE_UTILS_CLASS =
            "the_four_primitives_and_weapons.damage.ElementalDamageUtils";

    private static Boolean loaded;
    private static Method mElementValueOf;        // ElementType valueOf(String)
    private static Method mIsNullifiedByBook;     // boolean isElementNullifiedByBook(LivingEntity, ElementType)
    private static Method mSetElement;            // void setElement(ItemStack, ElementType, int)
    private static Method mApplyElementalDamage;  // float applyElementalDamage(LivingEntity, LivingEntity, ItemStack, float)

    private TfpwHostCompat() {}

    public static void init() {
        isLoaded();
    }

    public static boolean isLoaded() {
        if (loaded == null) {
            boolean present = ModList.get().isLoaded(MOD_ID);
            if (present) present = initReflection();
            loaded = present;
        }
        return loaded;
    }

    private static boolean initReflection() {
        try {
            Class<?> elementCls = Class.forName(ELEMENT_TYPE_CLASS);
            Class<?> utilsCls = Class.forName(DAMAGE_UTILS_CLASS);
            // enum の valueOf(String)
            mElementValueOf = elementCls.getMethod("valueOf", String.class);
            mIsNullifiedByBook = utilsCls.getMethod(
                    "isElementNullifiedByBook", LivingEntity.class, elementCls);
            mSetElement = utilsCls.getMethod(
                    "setElement", ItemStack.class, elementCls, int.class);
            mApplyElementalDamage = utilsCls.getMethod(
                    "applyElementalDamage", LivingEntity.class, LivingEntity.class, ItemStack.class, float.class);
            return true;
        } catch (Throwable t) {
            // クラス/メソッドが見つからなければ無効化 (バージョン不整合でも落ちない)。
            return false;
        }
    }

    /**
     * 対象が {@code elementName} 属性ダメージを book スロットの魔導書で無効化できるか。
     *
     * <p>本体 {@code ElementalDamageUtils.isElementNullifiedByBook} に委譲する。
     * 未ロード / API 不一致 / 未知の属性名の場合は false (= 無効化しない)。
     *
     * @param target      ダメージを受けるエンティティ
     * @param elementName 本体 ElementType 名 ("FIRE" / "ICE" / "THUNDER" 等)
     */
    public static boolean isElementNullifiedByBook(LivingEntity target, String elementName) {
        if (target == null || elementName == null || !isLoaded()) return false;
        try {
            Object element = mElementValueOf.invoke(null, elementName);
            if (element == null) return false;
            Object result = mIsNullifiedByBook.invoke(null, target, element);
            return Boolean.TRUE.equals(result);
        } catch (Throwable t) {
            // 未知の属性名は valueOf が IllegalArgumentException を投げる → 無効化しない扱い。
            return false;
        }
    }

    /**
     * ドラゴンのブレス/攻撃を本体の属性ダメージ処理に通す。
     *
     * <p>属性を焼いた仮想武器 ItemStack を作り、本体
     * {@code ElementalDamageUtils.applyElementalDamage(attacker, target, weapon, baseDmg)} に委譲する。
     * これで対象の耐性/魔導書無効化/属性ハンドラ効果 (状態異常等) が本体側で発火する。
     *
     * @param elementName 本体 ElementType 名 ("FIRE"/"ICE"/"CORROSION" 等)
     * @param level       属性レベル (1〜10 程度)
     * @param baseDmg     元のダメージ
     * @return 本体が算出した属性込みダメージ。未ロード / 失敗時は {@code baseDmg} をそのまま返す。
     */
    public static float applyElementalDamage(LivingEntity attacker, LivingEntity target,
                                             String elementName, int level, float baseDmg) {
        if (target == null || elementName == null || !isLoaded()) return baseDmg;
        try {
            Object element = mElementValueOf.invoke(null, elementName);
            if (element == null) return baseDmg;
            ItemStack weapon = new ItemStack(Items.STICK);
            mSetElement.invoke(null, weapon, element, level);
            Object result = mApplyElementalDamage.invoke(null, attacker, target, weapon, baseDmg);
            return (result instanceof Number) ? ((Number) result).floatValue() : baseDmg;
        } catch (Throwable t) {
            return baseDmg;
        }
    }
}
