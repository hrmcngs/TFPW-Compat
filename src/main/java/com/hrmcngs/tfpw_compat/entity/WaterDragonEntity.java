package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 水ドラゴン (issue #199-A3)。属性 = water。 */
public class WaterDragonEntity extends ElementDragonEntity {
    public WaterDragonEntity(EntityType<? extends WaterDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "water";
    }
}
