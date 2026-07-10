package com.hrmcngs.tfpw_compat.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/**
 * Ice and Fire 素材のドラゴン武器 (刀 / 直刀 / レイピア / ダガー 共通)。
 *
 * <p>issue #199-A1。刀身の種別差は攻撃力補正 / 攻撃速度 (コンストラクタ引数) と Tier のみ。
 * 属性は本体 the_four_primitives_and_weapons の NBT スキーマ ({@code ElementType}/{@code ElementLevel})
 * で後付けする (A-3)。本体が無くても単なる剣として成立する。
 */
public class DragonWeaponItem extends SwordItem {
    public DragonWeaponItem(Tier tier, int attackDamageModifier, float attackSpeedModifier) {
        super(tier, attackDamageModifier, attackSpeedModifier, new Item.Properties());
    }
}
