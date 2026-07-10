package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** 消滅ドラゴン (issue #199-A3)。属性 = erasure。 */
public class ErasureDragonEntity extends ElementDragonEntity {
    public ErasureDragonEntity(EntityType<? extends ErasureDragonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public String getElementName() {
        return "erasure";
    }
}
