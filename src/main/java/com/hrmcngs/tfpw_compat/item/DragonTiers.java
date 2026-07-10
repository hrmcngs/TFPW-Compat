package com.hrmcngs.tfpw_compat.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * ドラゴン素材の武器 Tier 定義。
 *
 * <p>修理素材は Ice and Fire のアイテムを {@link ForgeRegistries} 経由で解決する
 * (compile 依存ゼロ / 未ロード時は空 = 修理不可)。
 * <ul>
 *   <li>dragonbone — 中位 (diamond 相当)</li>
 *   <li>dragonsteel 炎 / 氷 / 雷 — 最高位 (netherite 超)</li>
 * </ul>
 */
public final class DragonTiers {
    private DragonTiers() {}

    private static Tier of(int uses, float speed, float bonus, int level, int ench, String repairItemId) {
        return new Tier() {
            @Override public int getUses() { return uses; }
            @Override public float getSpeed() { return speed; }
            @Override public float getAttackDamageBonus() { return bonus; }
            @Override public int getLevel() { return level; }
            @Override public int getEnchantmentValue() { return ench; }
            @Override public Ingredient getRepairIngredient() {
                Item mat = ForgeRegistries.ITEMS.getValue(new ResourceLocation(repairItemId));
                return mat == null ? Ingredient.of() : Ingredient.of(mat);
            }
        };
    }

    public static final Tier DRAGONBONE =
            of(1800, 8f, 4f, 3, 12, "iceandfire:dragonbone");
    public static final Tier FIRE_DRAGONSTEEL =
            of(4000, 9f, 6f, 4, 18, "iceandfire:dragonsteel_fire_ingot");
    public static final Tier ICE_DRAGONSTEEL =
            of(4000, 9f, 6f, 4, 18, "iceandfire:dragonsteel_ice_ingot");
    public static final Tier LIGHTNING_DRAGONSTEEL =
            of(4000, 9f, 6f, 4, 18, "iceandfire:dragonsteel_lightning_ingot");
}
