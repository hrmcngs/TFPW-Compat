package com.hrmcngs.tfpw_compat.item;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.entity.ModEntities;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * TFPW-Compat が {@code tfpw_compat} 名前空間で追加するアイテム (issue #199-A1)。
 *
 * <p>ドラゴン素材武器 16 種 = dragonbone / dragonsteel 炎・氷・雷 × 刀・直刀・レイピア・ダガー。
 * 属性ごとの D 武器 (A-3) は dragonbone_katana + NBT で表現するため専用アイテムは持たない
 * (レシピ / クリエイティブタブ側で NBT を焼く)。
 */
public final class ModItems {
    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ITEMS, TfpwCompat.MOD_ID);

    private ModItems() {}

    // 武器タイプ別の基準値: 刀/直刀 = (3, -2.4)、 レイピア = (2, -2.4)、 ダガー = (2, -1.8)。
    private static RegistryObject<Item> reg(String id, Tier tier, int dmg, float speed) {
        return REGISTRY.register(id, () -> new DragonWeaponItem(tier, dmg, speed));
    }

    // ── dragonbone ──
    public static final RegistryObject<Item> DRAGONBONE_KATANA  = reg("dragonbone_katana",  DragonTiers.DRAGONBONE, 3, -2.4f);
    public static final RegistryObject<Item> DRAGONBONE_TYOKUTO = reg("dragonbone_tyokuto", DragonTiers.DRAGONBONE, 3, -2.4f);
    public static final RegistryObject<Item> DRAGONBONE_RAPIER  = reg("dragonbone_rapier",  DragonTiers.DRAGONBONE, 2, -2.4f);
    public static final RegistryObject<Item> DRAGONBONE_DAGGER  = reg("dragonbone_dagger",  DragonTiers.DRAGONBONE, 2, -1.8f);

    // ── dragonsteel (炎) ──
    public static final RegistryObject<Item> FIRE_DRAGONSTEEL_KATANA  = reg("fire_dragonsteel_katana",  DragonTiers.FIRE_DRAGONSTEEL, 3, -2.4f);
    public static final RegistryObject<Item> FIRE_DRAGONSTEEL_TYOKUTO = reg("fire_dragonsteel_tyokuto", DragonTiers.FIRE_DRAGONSTEEL, 3, -2.4f);
    public static final RegistryObject<Item> FIRE_DRAGONSTEEL_RAPIER  = reg("fire_dragonsteel_rapier",  DragonTiers.FIRE_DRAGONSTEEL, 2, -2.4f);
    public static final RegistryObject<Item> FIRE_DRAGONSTEEL_DAGGER  = reg("fire_dragonsteel_dagger",  DragonTiers.FIRE_DRAGONSTEEL, 2, -1.8f);

    // ── dragonsteel (氷) ──
    public static final RegistryObject<Item> ICE_DRAGONSTEEL_KATANA  = reg("ice_dragonsteel_katana",  DragonTiers.ICE_DRAGONSTEEL, 3, -2.4f);
    public static final RegistryObject<Item> ICE_DRAGONSTEEL_TYOKUTO = reg("ice_dragonsteel_tyokuto", DragonTiers.ICE_DRAGONSTEEL, 3, -2.4f);
    public static final RegistryObject<Item> ICE_DRAGONSTEEL_RAPIER  = reg("ice_dragonsteel_rapier",  DragonTiers.ICE_DRAGONSTEEL, 2, -2.4f);
    public static final RegistryObject<Item> ICE_DRAGONSTEEL_DAGGER  = reg("ice_dragonsteel_dagger",  DragonTiers.ICE_DRAGONSTEEL, 2, -1.8f);

    // ── dragonsteel (雷) ──
    public static final RegistryObject<Item> LIGHTNING_DRAGONSTEEL_KATANA  = reg("lightning_dragonsteel_katana",  DragonTiers.LIGHTNING_DRAGONSTEEL, 3, -2.4f);
    public static final RegistryObject<Item> LIGHTNING_DRAGONSTEEL_TYOKUTO = reg("lightning_dragonsteel_tyokuto", DragonTiers.LIGHTNING_DRAGONSTEEL, 3, -2.4f);
    public static final RegistryObject<Item> LIGHTNING_DRAGONSTEEL_RAPIER  = reg("lightning_dragonsteel_rapier",  DragonTiers.LIGHTNING_DRAGONSTEEL, 2, -2.4f);
    public static final RegistryObject<Item> LIGHTNING_DRAGONSTEEL_DAGGER  = reg("lightning_dragonsteel_dagger",  DragonTiers.LIGHTNING_DRAGONSTEEL, 2, -1.8f);

    // ── A-3: 属性ドラゴンのスポーンエッグ 11 種 (型別なので Forge 標準エッグを使用) ──
    public static final RegistryObject<Item> DRAGON_EGG_CORROSION = REGISTRY.register("corrosion_dragon_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.CORROSION_DRAGON, 0x6B8E23, 0x3B4A16, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_MIASMA    = REGISTRY.register("miasma_dragon_spawn_egg",    () -> new ForgeSpawnEggItem(ModEntities.MIASMA_DRAGON,    0x8A2BE2, 0x4B0082, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_SOUL      = REGISTRY.register("soul_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.SOUL_DRAGON,      0x5CE1E6, 0x2E8B8F, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_SOUL_FIRE = REGISTRY.register("soul_fire_dragon_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.SOUL_FIRE_DRAGON, 0x2CE8F5, 0x1A7A8A, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_WIND      = REGISTRY.register("wind_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.WIND_DRAGON,      0xB0E0E6, 0x5F9EA0, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_WATER     = REGISTRY.register("water_dragon_spawn_egg",     () -> new ForgeSpawnEggItem(ModEntities.WATER_DRAGON,     0x1E90FF, 0x104E8B, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_DARK      = REGISTRY.register("dark_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.DARK_DRAGON,      0x301934, 0x120018, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_HOLY      = REGISTRY.register("holy_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.HOLY_DRAGON,      0xFFF8DC, 0xCDC08B, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_ERASURE   = REGISTRY.register("erasure_dragon_spawn_egg",   () -> new ForgeSpawnEggItem(ModEntities.ERASURE_DRAGON,   0x4B0000, 0x1A0000, new Item.Properties()));
}
