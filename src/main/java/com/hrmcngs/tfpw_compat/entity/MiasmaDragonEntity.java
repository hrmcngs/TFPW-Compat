package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 瘴気ドラゴン (issue #199-A3)。属性 = miasma。 */
public class MiasmaDragonEntity extends ElementDragonEntity {
    public MiasmaDragonEntity(EntityType<? extends MiasmaDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "miasma";
    }
}
