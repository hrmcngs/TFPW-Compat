package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 燐火ドラゴン (issue #199-A3)。属性 = soul_fire。 */
public class SoulFireDragonEntity extends ElementDragonEntity {
    public SoulFireDragonEntity(EntityType<? extends SoulFireDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "soul_fire";
    }
}
