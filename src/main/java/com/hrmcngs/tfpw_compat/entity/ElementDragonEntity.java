package com.hrmcngs.tfpw_compat.entity;

import com.hrmcngs.tfpw_compat.compat.TfpwHostCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * 属性ドラゴン (issue #199-A3) の共通基底。
 *
 * <p>Ice and Fire の {@code EntityDragonBase} は Architectury/Yarn + uranus 依存で Forge/official
 * からの継承が現実的でないため、挙動を参考にした <b>Forge ネイティブの飛行ドラゴン</b>として実装。
 * iceandfire 非依存でロード成功する。属性は Ice and Fire に倣い<b>属性ごとに独立サブクラス</b>で
 * 表現し ({@link #getElementName()})、攻撃時に本体 the_four_primitives_and_weapons の属性ダメージ
 * 処理へ橋渡しする ({@link TfpwHostCompat})。本体が無ければ通常の飛行モブとして成立する。
 */
public abstract class ElementDragonEntity extends PathfinderMob implements Enemy {
    private static final int BREATH_LEVEL = 5;
    private static final float BREATH_BASE = 4.0f;

    protected ElementDragonEntity(EntityType<? extends ElementDragonEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0f);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
        this.xpReward = 30;
    }

    /** 本体 ElementType 名 (小文字, 例 "corrosion")。属性別サブクラスが返す。 */
    public abstract String getElementName();

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FLYING_SPEED, 0.6)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    // ── 属性ブレス = 本体の属性ダメージ処理へ橋渡し ───────────────
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && !this.level().isClientSide() && target instanceof LivingEntity living) {
            String element = getElementName();
            if (!TfpwHostCompat.isElementNullifiedByBook(living, element)) {
                float total = TfpwHostCompat.applyElementalDamage(this, living, element, BREATH_LEVEL, BREATH_BASE);
                float bonus = total - BREATH_BASE;
                if (bonus > 0.0f) {
                    living.hurt(this.damageSources().mobAttack(this), bonus);
                }
            }
        }
        return hurt;
    }

    // ── 飛行モブ: 落下ダメージ無し ────────────────────────────────
    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // no-op
    }
}
