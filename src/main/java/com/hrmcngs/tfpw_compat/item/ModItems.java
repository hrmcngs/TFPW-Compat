package com.hrmcngs.tfpw_compat.item;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.block.ModBlocks;
import com.hrmcngs.tfpw_compat.entity.DragonFeedItem;
import com.hrmcngs.tfpw_compat.entity.ElementDragonEggItem;
import com.hrmcngs.tfpw_compat.entity.ModEntities;

import net.minecraft.world.item.BlockItem;
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
    public static final RegistryObject<Item> DRAGON_EGG_CORROSION = REGISTRY.register("corrosion_dragon_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.CORROSION_DRAGON, 0xFF55FF, 0xB03BB0, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_MIASMA    = REGISTRY.register("miasma_dragon_spawn_egg",    () -> new ForgeSpawnEggItem(ModEntities.MIASMA_DRAGON,    0x8A2BE2, 0x4B0082, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_SOUL      = REGISTRY.register("soul_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.SOUL_DRAGON,      0x5CE1E6, 0x2E8B8F, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_SOUL_FIRE = REGISTRY.register("soul_fire_dragon_spawn_egg", () -> new ForgeSpawnEggItem(ModEntities.SOUL_FIRE_DRAGON, 0x2CE8F5, 0x1A7A8A, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_WIND      = REGISTRY.register("wind_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.WIND_DRAGON,      0xB0E0E6, 0x5F9EA0, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_WATER     = REGISTRY.register("water_dragon_spawn_egg",     () -> new ForgeSpawnEggItem(ModEntities.WATER_DRAGON,     0x1E90FF, 0x104E8B, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_DARK      = REGISTRY.register("dark_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.DARK_DRAGON,      0x301934, 0x120018, new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_EGG_HOLY      = REGISTRY.register("holy_dragon_spawn_egg",      () -> new ForgeSpawnEggItem(ModEntities.HOLY_DRAGON,      0xFFF8DC, 0xCDC08B, new Item.Properties()));

    // ── A-3: 属性ドラゴンの卵 8 種 ( 設置すると一定時間後に孵化する ) ──
    //   index は ModEntities.ELEMENT_ORDER に対応。
    private static RegistryObject<Item> regDragonEgg(String id, int elementId) {
        return REGISTRY.register(id, () -> new ElementDragonEggItem(elementId, new Item.Properties()));
    }
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_CORROSION = regDragonEgg("corrosion_dragon_egg", 0);
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_MIASMA    = regDragonEgg("miasma_dragon_egg",    1);
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_SOUL      = regDragonEgg("soul_dragon_egg",      2);
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_SOUL_FIRE = regDragonEgg("soul_fire_dragon_egg", 3);
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_WIND      = regDragonEgg("wind_dragon_egg",      4);
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_WATER     = regDragonEgg("water_dragon_egg",     5);
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_DARK      = regDragonEgg("dark_dragon_egg",      6);
    public static final RegistryObject<Item> DRAGON_EGG_ITEM_HOLY      = regDragonEgg("holy_dragon_egg",      7);

    // ── A-3: 属性ドラゴンの餌 8 種 ( 対応属性のドラゴンに使うと回復+成長 ) ──
    private static RegistryObject<Item> regFeed(String id, String element) {
        return REGISTRY.register(id, () -> new DragonFeedItem(element, new Item.Properties()));
    }
    public static final RegistryObject<Item> FEED_CORROSION = regFeed("corrosion_dragon_feed", "corrosion");
    public static final RegistryObject<Item> FEED_MIASMA    = regFeed("miasma_dragon_feed",    "miasma");
    public static final RegistryObject<Item> FEED_SOUL      = regFeed("soul_dragon_feed",      "soul");
    public static final RegistryObject<Item> FEED_SOUL_FIRE = regFeed("soul_fire_dragon_feed", "soul_fire");
    public static final RegistryObject<Item> FEED_WIND      = regFeed("wind_dragon_feed",      "wind");
    public static final RegistryObject<Item> FEED_WATER     = regFeed("water_dragon_feed",     "water");
    public static final RegistryObject<Item> FEED_DARK      = regFeed("dark_dragon_feed",      "dark");
    public static final RegistryObject<Item> FEED_HOLY      = regFeed("holy_dragon_feed",      "holy");

    // ── B (issue #199): 属性発電機ブロックの BlockItem ──
    public static final RegistryObject<Item> ELEMENTAL_DYNAMO = REGISTRY.register("elemental_dynamo",
            () -> new BlockItem(ModBlocks.ELEMENTAL_DYNAMO.get(), new Item.Properties()));
}
