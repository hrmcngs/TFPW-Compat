package com.hrmcngs.tfpw_compat.event;

import com.hrmcngs.tfpw_compat.block.entity.ElementalDynamoBlockEntity;
import com.hrmcngs.tfpw_compat.compat.MekanismCompat;
import com.hrmcngs.tfpw_compat.compat.TfpwHostCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * issue #199-B: 電気/雷属性攻撃 → 近傍の属性発電機へ FE 注入。
 *
 * <p>攻撃者の手持ち武器が電気(ELECTRIC)/雷(THUNDER)属性なら、被弾地点の近くにある
 * {@link ElementalDynamoBlockEntity} に {@code feFromElementLevel(level)} 分の FE を注入する。
 * 本体 (属性システム) 未ロードなら武器属性が取れず no-op。
 */
public final class ElementalGenerationHandler {
    private static final int SEARCH_RADIUS = 6;

    private ElementalGenerationHandler() {}

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        Level level = target.level();
        if (level.isClientSide()) {
            return;
        }
        Entity attacker = event.getSource().getEntity();
        if (!(attacker instanceof LivingEntity living)) {
            return;
        }
        ItemStack weapon = living.getMainHandItem();
        String element = TfpwHostCompat.getWeaponElementName(weapon);
        if (element == null) {
            return;
        }
        if (!element.equalsIgnoreCase("electric") && !element.equalsIgnoreCase("thunder")) {
            return;
        }
        int level0 = TfpwHostCompat.getWeaponElementLevel(weapon);
        int fe = MekanismCompat.feFromElementLevel(level0);
        if (fe <= 0) {
            return;
        }
        ElementalDynamoBlockEntity dynamo = findNearestDynamo(level, target.blockPosition());
        if (dynamo != null) {
            dynamo.addEnergy(fe);
        }
    }

    /** 被弾地点周辺 (立方体 SEARCH_RADIUS) の最寄り発電機を返す。 無ければ null。 */
    private static ElementalDynamoBlockEntity findNearestDynamo(Level level, BlockPos center) {
        ElementalDynamoBlockEntity nearest = null;
        double best = Double.MAX_VALUE;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dy = -SEARCH_RADIUS; dy <= SEARCH_RADIUS; dy++) {
                for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (!level.isLoaded(cursor)) {
                        continue;
                    }
                    BlockEntity be = level.getBlockEntity(cursor);
                    if (be instanceof ElementalDynamoBlockEntity dynamo) {
                        double d = dx * dx + dy * dy + dz * dz;
                        if (d < best) {
                            best = d;
                            nearest = dynamo;
                        }
                    }
                }
            }
        }
        return nearest;
    }
}
