package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 侵食ドラゴン (issue #199-A3)。属性 = corrosion。 */
public class CorrosionDragonEntity extends ElementDragonEntity {
    public CorrosionDragonEntity(EntityType<? extends CorrosionDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "corrosion";
    }
}
