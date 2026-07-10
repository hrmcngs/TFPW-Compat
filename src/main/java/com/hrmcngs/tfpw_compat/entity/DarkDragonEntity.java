package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 闇ドラゴン (issue #199-A3)。属性 = dark。 */
public class DarkDragonEntity extends ElementDragonEntity {
    public DarkDragonEntity(EntityType<? extends DarkDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "dark";
    }
}
