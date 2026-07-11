package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;

import java.util.EnumSet;

/**
 * ドラゴンの属性ブレス。ターゲットが射程内で視線が通るとき、溜め → 発射。
 * 発射時に口元からターゲットへ属性色のパーティクル列を吐き、着弾で属性ダメージを与える
 * ({@link ElementDragonEntity#breatheAt}, 本体の属性処理経由)。
 */
public class DragonBreathGoal extends Goal {
    private static final double MIN_RANGE = 3.0;
    private static final double MAX_RANGE = 20.0;
    private static final int CHARGE_TICKS = 20;   // 溜め
    private static final int FIRE_TICKS = 12;      // 吐く時間
    private static final int COOLDOWN = 70;

    private final ElementDragonEntity dragon;
    private LivingEntity target;
    private int ticks;
    private int cooldown;
    private boolean damaged;

    public DragonBreathGoal(ElementDragonEntity dragon) {
        this.dragon = dragon;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        this.target = dragon.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        double d = dragon.distanceToSqr(target);
        if (d < MIN_RANGE * MIN_RANGE || d > MAX_RANGE * MAX_RANGE) {
            return false;
        }
        return dragon.getSensing().hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive()
                && ticks < CHARGE_TICKS + FIRE_TICKS
                && dragon.distanceToSqr(target) <= MAX_RANGE * MAX_RANGE;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.damaged = false;
    }

    @Override
    public void stop() {
        this.cooldown = COOLDOWN;
        this.ticks = 0;
        this.target = null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }
        dragon.getLookControl().setLookAt(target, 30.0f, 30.0f);
        this.ticks++;

        if (this.ticks < CHARGE_TICKS) {
            return; // 溜め中
        }

        // 口元からターゲット中心へのレイ
        Vec3 mouth = dragon.getEyePosition().add(dragon.getForward().scale(dragon.getBbWidth() * 0.9));
        Vec3 to = target.getBoundingBox().getCenter().subtract(mouth);
        double dist = to.length();
        if (dist < 1.0e-4) {
            return;
        }
        Vec3 dir = to.scale(1.0 / dist);

        if (this.ticks == CHARGE_TICKS && this.dragon.level() instanceof ServerLevel sl) {
            sl.playSound(null, dragon.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.2f, 0.7f);
        }

        if (this.dragon.level() instanceof ServerLevel sl) {
            int rgb = dragon.getElementColor();
            DustParticleOptions dust = new DustParticleOptions(new Vector3f(
                    ((rgb >> 16) & 255) / 255.0f, ((rgb >> 8) & 255) / 255.0f, (rgb & 255) / 255.0f), 1.6f);
            int steps = (int) Math.min(48, dist * 2.5);
            for (int i = 0; i <= steps; i++) {
                Vec3 p = mouth.add(dir.scale(dist * i / Math.max(1, steps)));
                sl.sendParticles(dust, p.x, p.y, p.z, 1, 0.18, 0.18, 0.18, 0.0);
            }
        }

        // 着弾で一度だけダメージ
        if (!this.damaged && this.ticks >= CHARGE_TICKS + 2) {
            dragon.breatheAt(target);
            this.damaged = true;
        }
    }
}
