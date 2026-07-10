package com.hrmcngs.tfpw_compat.block;

import com.hrmcngs.tfpw_compat.block.entity.ElementalDynamoBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * 属性発電機ブロック (issue #199-B)。{@link ElementalDynamoBlockEntity} を持ち、
 * サーバ側で発電/送電を tick する。
 */
public class ElementalDynamoBlock extends Block implements EntityBlock {
    public ElementalDynamoBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElementalDynamoBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof ElementalDynamoBlockEntity dynamo) {
                ElementalDynamoBlockEntity.serverTick(lvl, pos, st, dynamo);
            }
        };
    }
}
