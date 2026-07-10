package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 風ドラゴン (issue #199-A3)。属性 = wind。 */
public class WindDragonEntity extends ElementDragonEntity {
    public WindDragonEntity(EntityType<? extends WindDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "wind";
    }
}
