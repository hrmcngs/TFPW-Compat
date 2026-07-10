package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * 属性ドラゴンの卵アイテム (属性固定、8 種)。
 *
 * <p>Ice and Fire の {@code DragonEggItem} と同じ流儀: 使うとクリック面の隣に
 * {@link ElementDragonEggEntity} を設置し、一定時間後にその属性のドラゴンが孵化する。
 */
public class ElementDragonEggItem extends Item {
    private final int elementId;

    public ElementDragonEggItem(int elementId, Properties properties) {
        super(properties.stacksTo(1));
        this.elementId = elementId;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        ElementDragonEggEntity egg = ModEntities.ELEMENT_DRAGON_EGG.get().create(server);
        if (egg == null) {
            return InteractionResult.FAIL;
        }
        egg.setElementId(this.elementId);
        egg.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                level.getRandom().nextFloat() * 360.0f, 0.0f);
        server.addFreshEntity(egg);
        context.getItemInHand().shrink(1);
        return InteractionResult.CONSUME;
    }
}
