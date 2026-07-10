package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 魂ドラゴン (issue #199-A3)。属性 = soul。 */
public class SoulDragonEntity extends ElementDragonEntity {
    public SoulDragonEntity(EntityType<? extends SoulDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "soul";
    }
}
