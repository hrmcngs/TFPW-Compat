package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 聖ドラゴン (issue #199-A3)。属性 = holy。 */
public class HolyDragonEntity extends ElementDragonEntity {
    public HolyDragonEntity(EntityType<? extends HolyDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "holy";
    }
}
