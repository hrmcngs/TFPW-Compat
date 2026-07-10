package com.hrmcngs.tfpw_compat.block;

import com.hrmcngs.tfpw_compat.TfpwCompat;
import com.hrmcngs.tfpw_compat.block.entity.ElementalDynamoBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * TFPW-Compat の BlockEntity 登録 (issue #199-B)。
 */
public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TfpwCompat.MOD_ID);

    private ModBlockEntities() {}

    public static final RegistryObject<BlockEntityType<ElementalDynamoBlockEntity>> ELEMENTAL_DYNAMO =
            REGISTRY.register("elemental_dynamo",
                    () -> ElementalDynamoBlockEntity.makeType(ModBlocks.ELEMENTAL_DYNAMO.get()));
}
