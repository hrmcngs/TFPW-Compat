package com.hrmcngs.tfpw_compat.block;

import com.hrmcngs.tfpw_compat.TfpwCompat;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * TFPW-Compat のブロック登録 (issue #199-B: 属性発電機)。
 */
public final class ModBlocks {
    public static final DeferredRegister<Block> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TfpwCompat.MOD_ID);

    private ModBlocks() {}

    public static final RegistryObject<Block> ELEMENTAL_DYNAMO = REGISTRY.register("elemental_dynamo",
            () -> new ElementalDynamoBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()));
}
