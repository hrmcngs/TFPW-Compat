package com.hrmcngs.tfpw_compat.entity;

import com.hrmcngs.tfpw_compat.compat.TfpwHostCompat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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

    // ── 成長段階 (tier) 1..5 ──────────────────────────────────────────
    /** 成長段階 (1=幼体 〜 5=成体)。描画サイズ・ヒットボックス・攻撃力・体力に反映。 */
    private static final EntityDataAccessor<Integer> DATA_STAGE =
            SynchedEntityData.defineId(ElementDragonEntity.class, EntityDataSerializers.INT);
    /** 個体差シード ( 色・大きさの個体差の元 )。 spawn 時に確定。 */
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(ElementDragonEntity.class, EntityDataSerializers.INT);
    public static final int MAX_STAGE = 5;
    /** 1 段階あたりの成長 tick ( 約 8 分 )。 給餌で短縮できる。 */
    private static final int TICKS_PER_STAGE = 20 * 60 * 8;

    private int growthAge;

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

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STAGE, MAX_STAGE);
        this.entityData.define(DATA_VARIANT, 0);
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            net.minecraft.world.entity.MobSpawnType reason,
            @javax.annotation.Nullable net.minecraft.world.entity.SpawnGroupData data,
            @javax.annotation.Nullable net.minecraft.nbt.CompoundTag tag) {
        if (getVariant() == 0) {
            // 0 は「未設定」なので必ず非 0 の乱数を割り当てる。
            this.entityData.set(DATA_VARIANT, this.random.nextInt() | 1);
        }
        return super.finalizeSpawn(level, difficulty, reason, data, tag);
    }

    // ── 個体差 ( 本家 DragonColor に倣い、 属性ごとに 4 種の離散カラー + 大きさ差 ) ──
    /** 属性 → 4 色バリアント ( 中立グレー肌に乗算する色 )。 */
    private static final java.util.Map<String, int[]> COLOR_VARIANTS = java.util.Map.of(
            "corrosion", new int[]{0xFF55FF, 0xE838C0, 0xFF7BE0, 0xC94FE8},
            "miasma",    new int[]{0x9B30E8, 0x7B4FC0, 0xB050D0, 0x6E8F2A},
            "soul",      new int[]{0x5CE1E6, 0x88F0FF, 0x3AB0C0, 0x9CFFE0},
            "soul_fire", new int[]{0x2CE8F5, 0x00C0A0, 0x50FFE0, 0x2090FF},
            "wind",      new int[]{0xC8EAF0, 0xE8F8FF, 0xA0D0E0, 0xDDF5D8},
            "water",     new int[]{0x2E9BFF, 0x1560BF, 0x50C8FF, 0x2ED9C0},
            "dark",      new int[]{0x5B2E86, 0x30203A, 0x7A45B0, 0x402060},
            "holy",      new int[]{0xFFF3C4, 0xFFFFFF, 0xFFE080, 0xFFF8E0});
    private static final int[] DEFAULT_VARIANTS = {0xFFFFFF};

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    /** カラーバリアント番号 ( 0..3 )。 */
    public int getColorVariant() {
        int[] palette = COLOR_VARIANTS.getOrDefault(getElementName(), DEFAULT_VARIANTS);
        return Math.floorMod(getVariant(), palette.length);
    }

    /** この個体の色 ( 0xRRGGBB )。描画の肌色乗算・ブレスのパーティクル色に共通で使う。 */
    public int getTintColor() {
        int[] palette = COLOR_VARIANTS.getOrDefault(getElementName(), DEFAULT_VARIANTS);
        return palette[Math.floorMod(getVariant(), palette.length)];
    }

    /** 大きさの個体差 ( 0.85 〜 1.15 倍 )。色バリアントとは別ビットから。 */
    public float getSizeVariance() {
        return 0.85f + ((getVariant() >> 8) & 0xFF) / 255.0f * 0.30f;
    }

    // ── 成長段階 API ──────────────────────────────────────────────────
    public int getStage() {
        return this.entityData.get(DATA_STAGE);
    }

    public void setStage(int stage) {
        int s = Math.max(1, Math.min(MAX_STAGE, stage));
        this.entityData.set(DATA_STAGE, s);
        this.refreshDimensions();
        applyStageStats(s);
    }

    /** 段階 → スケール係数 ( 1 段階=0.4 〜 5 段階=1.0 )。描画とヒットボックスで共有。 */
    public static float scaleForStage(int stage) {
        int s = Math.max(1, Math.min(MAX_STAGE, stage));
        return 0.4f + 0.6f * (s - 1) / (MAX_STAGE - 1);
    }

    /** 成長段階 × 個体差の総合スケール ( 描画・ヒットボックス共通 )。 */
    public float getScale() {
        return scaleForStage(getStage()) * getSizeVariance();
    }

    /** 成長を進める ( 給餌用 )。段階が上がったら true。 */
    public boolean addGrowth(int ticks) {
        if (getStage() >= MAX_STAGE) {
            return false;
        }
        this.growthAge += ticks;
        boolean grew = false;
        while (this.growthAge >= TICKS_PER_STAGE && getStage() < MAX_STAGE) {
            this.growthAge -= TICKS_PER_STAGE;
            setStage(getStage() + 1);
            grew = true;
        }
        return grew;
    }

    private void applyStageStats(int stage) {
        float t = (float) (stage - 1) / (MAX_STAGE - 1); // 0..1
        setAttr(Attributes.MAX_HEALTH, 30.0 + 50.0 * t);   // 30..80
        setAttr(Attributes.ATTACK_DAMAGE, 3.0 + 5.0 * t);  // 3..8
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private void setAttr(Attribute attr, double value) {
        AttributeInstance inst = this.getAttribute(attr);
        if (inst != null) {
            inst.setBaseValue(value);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(getScale());
    }

    /**
     * 眠っているか ( 描画で睡眠ポーズ/肌に使う )。
     * <p>夜間で、 攻撃対象が無く、 地上で静止しているとき眠る ( Ice and Fire の睡眠に倣った簡易版 )。
     */
    public boolean isDragonSleeping() {
        if (this.getTarget() != null || this.isVehicle() || this.isInWaterOrBubble()) {
            return false;
        }
        if (!this.onGround() || this.getDeltaMovement().horizontalDistanceSqr() > 1.0e-4) {
            return false;
        }
        return this.level().isNight();
    }

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
        this.goalSelector.addGoal(1, new DragonBreathGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, true));
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

    /** 属性ブレスのダメージ ( DragonBreathGoal から使う )。段階でスケール。 */
    void breatheAt(LivingEntity target) {
        if (this.level().isClientSide() || target == null) {
            return;
        }
        String element = getElementName();
        if (TfpwHostCompat.isElementNullifiedByBook(target, element)) {
            return;
        }
        float base = BREATH_BASE * (0.5f + 0.5f * getScale());
        float total = TfpwHostCompat.applyElementalDamage(this, target, element, BREATH_LEVEL, base);
        target.hurt(this.damageSources().mobAttack(this), Math.max(base, total));
    }

    /** ブレスのパーティクル色 ( この個体のカラーバリアント )。 */
    public int getElementColor() {
        return getTintColor();
    }

    // ── 成長 (server) ─────────────────────────────────────────────
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide() && getStage() < MAX_STAGE) {
            addGrowth(1);
        }
    }

    // ── 給餌: 手持ちが対応属性の餌なら回復 + 成長 ──────────────────
    @Override
    public net.minecraft.world.InteractionResult mobInteract(Player player,
                                                             net.minecraft.world.InteractionHand hand) {
        net.minecraft.world.item.ItemStack held = player.getItemInHand(hand);
        if (DragonFeedItem.isFeedFor(held, getElementName())) {
            if (!this.level().isClientSide()) {
                this.heal(this.getMaxHealth() * 0.25f);
                addGrowth(20 * 60 * 2); // 給餌 1 回で約 2 分ぶん成長
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                this.level().broadcastEntityEvent(this, (byte) 7); // ハート表示
            }
            return net.minecraft.world.InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
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

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Stage", getStage());
        tag.putInt("GrowthAge", this.growthAge);
        tag.putInt("Variant", getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.growthAge = tag.getInt("GrowthAge");
        this.entityData.set(DATA_VARIANT, tag.getInt("Variant"));
        if (tag.contains("Stage")) {
            setStage(tag.getInt("Stage"));
        }
    }
}
