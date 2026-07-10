package com.hrmcngs.tfpw_compat.item;

import com.hrmcngs.tfpw_compat.TfpwCompat;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * TFPW-Compat のクリエイティブタブ。ドラゴン武器 16 種 (A-1) と、
 * 属性ごとの D 武器 11 種 (A-3: dragonbone_katana + 属性NBT) を並べる。
 *
 * <p>属性 NBT は本体 the_four_primitives_and_weapons の {@code ElementalDamageUtils} と
 * 同じスキーマ ({@code ElementType}:String / {@code ElementLevel}:int) を直書きする
 * (本体への compile 依存を避ける)。本体が無ければ NBT は無視されるだけ。
 */
public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TfpwCompat.MOD_ID);

    private ModTabs() {}

    public static final RegistryObject<CreativeModeTab> WEAPONS = REGISTRY.register("weapons", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + TfpwCompat.MOD_ID + ".weapons"))
                    .icon(() -> new ItemStack(ModItems.FIRE_DRAGONSTEEL_KATANA.get()))
                    .displayItems((params, output) -> {
                        // A-1: ドラゴン素材武器 16 種
                        output.accept(ModItems.DRAGONBONE_KATANA.get());
                        output.accept(ModItems.DRAGONBONE_TYOKUTO.get());
                        output.accept(ModItems.DRAGONBONE_RAPIER.get());
                        output.accept(ModItems.DRAGONBONE_DAGGER.get());
                        output.accept(ModItems.FIRE_DRAGONSTEEL_KATANA.get());
                        output.accept(ModItems.FIRE_DRAGONSTEEL_TYOKUTO.get());
                        output.accept(ModItems.FIRE_DRAGONSTEEL_RAPIER.get());
                        output.accept(ModItems.FIRE_DRAGONSTEEL_DAGGER.get());
                        output.accept(ModItems.ICE_DRAGONSTEEL_KATANA.get());
                        output.accept(ModItems.ICE_DRAGONSTEEL_TYOKUTO.get());
                        output.accept(ModItems.ICE_DRAGONSTEEL_RAPIER.get());
                        output.accept(ModItems.ICE_DRAGONSTEEL_DAGGER.get());
                        output.accept(ModItems.LIGHTNING_DRAGONSTEEL_KATANA.get());
                        output.accept(ModItems.LIGHTNING_DRAGONSTEEL_TYOKUTO.get());
                        output.accept(ModItems.LIGHTNING_DRAGONSTEEL_RAPIER.get());
                        output.accept(ModItems.LIGHTNING_DRAGONSTEEL_DAGGER.get());

                        // A-3: 属性ドラゴンのスポーンエッグ 11 種
                        output.accept(ModItems.DRAGON_EGG_CORROSION.get());
                        output.accept(ModItems.DRAGON_EGG_MIASMA.get());
                        output.accept(ModItems.DRAGON_EGG_SOUL.get());
                        output.accept(ModItems.DRAGON_EGG_SOUL_FIRE.get());
                        output.accept(ModItems.DRAGON_EGG_WIND.get());
                        output.accept(ModItems.DRAGON_EGG_WATER.get());
                        output.accept(ModItems.DRAGON_EGG_DARK.get());
                        output.accept(ModItems.DRAGON_EGG_HOLY.get());

                        // A-3: 属性ドラゴンの卵 8 種 ( 設置 → 時間経過で孵化 )
                        output.accept(ModItems.DRAGON_EGG_ITEM_CORROSION.get());
                        output.accept(ModItems.DRAGON_EGG_ITEM_MIASMA.get());
                        output.accept(ModItems.DRAGON_EGG_ITEM_SOUL.get());
                        output.accept(ModItems.DRAGON_EGG_ITEM_SOUL_FIRE.get());
                        output.accept(ModItems.DRAGON_EGG_ITEM_WIND.get());
                        output.accept(ModItems.DRAGON_EGG_ITEM_WATER.get());
                        output.accept(ModItems.DRAGON_EGG_ITEM_DARK.get());
                        output.accept(ModItems.DRAGON_EGG_ITEM_HOLY.get());

                        // B: 属性発電機
                        output.accept(ModItems.ELEMENTAL_DYNAMO.get());
                    })
                    .build());
}
