package com.hrmcngs.tfpw_compat.entity;

import com.hrmcngs.tfpw_compat.TfpwCompat;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * TFPW-Compat のエンティティ登録 (issue #199-A3: 属性ドラゴン)。
 * Ice and Fire に倣い、属性ごとに独立した EntityType / entity クラスを持つ (11 種)。
 */
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TfpwCompat.MOD_ID);

    private ModEntities() {}

    public static final RegistryObject<EntityType<CorrosionDragonEntity>> CORROSION_DRAGON =
            REGISTRY.register("corrosion_dragon", () ->
                    EntityType.Builder.<CorrosionDragonEntity>of(CorrosionDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("corrosion_dragon"));
    public static final RegistryObject<EntityType<MiasmaDragonEntity>> MIASMA_DRAGON =
            REGISTRY.register("miasma_dragon", () ->
                    EntityType.Builder.<MiasmaDragonEntity>of(MiasmaDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("miasma_dragon"));
    public static final RegistryObject<EntityType<SoulDragonEntity>> SOUL_DRAGON =
            REGISTRY.register("soul_dragon", () ->
                    EntityType.Builder.<SoulDragonEntity>of(SoulDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("soul_dragon"));
    public static final RegistryObject<EntityType<SoulFireDragonEntity>> SOUL_FIRE_DRAGON =
            REGISTRY.register("soul_fire_dragon", () ->
                    EntityType.Builder.<SoulFireDragonEntity>of(SoulFireDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("soul_fire_dragon"));
    public static final RegistryObject<EntityType<WindDragonEntity>> WIND_DRAGON =
            REGISTRY.register("wind_dragon", () ->
                    EntityType.Builder.<WindDragonEntity>of(WindDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("wind_dragon"));
    public static final RegistryObject<EntityType<WaterDragonEntity>> WATER_DRAGON =
            REGISTRY.register("water_dragon", () ->
                    EntityType.Builder.<WaterDragonEntity>of(WaterDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("water_dragon"));
    public static final RegistryObject<EntityType<DarkDragonEntity>> DARK_DRAGON =
            REGISTRY.register("dark_dragon", () ->
                    EntityType.Builder.<DarkDragonEntity>of(DarkDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("dark_dragon"));
    public static final RegistryObject<EntityType<HolyDragonEntity>> HOLY_DRAGON =
            REGISTRY.register("holy_dragon", () ->
                    EntityType.Builder.<HolyDragonEntity>of(HolyDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("holy_dragon"));
    public static final RegistryObject<EntityType<ErasureDragonEntity>> ERASURE_DRAGON =
            REGISTRY.register("erasure_dragon", () ->
                    EntityType.Builder.<ErasureDragonEntity>of(ErasureDragonEntity::new, MobCategory.CREATURE)
                            .sized(1.4f, 1.6f).clientTrackingRange(10).updateInterval(3).build("erasure_dragon"));
}
