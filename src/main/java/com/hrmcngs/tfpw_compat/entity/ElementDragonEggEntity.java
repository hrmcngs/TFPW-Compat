package com.hrmcngs.tfpw_compat.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * 属性ドラゴンの卵 (issue #199-A3)。
 *
 * <p>Ice and Fire の {@code DragonEggItem} → {@code DragonEggEntity} と同じ流儀:
 * 卵アイテムを使うと卵エンティティが設置され、一定時間後に該当属性のドラゴンが孵化する。
 * ( スポーンエッグは即座に成体を出す。卵はこちらの遅延孵化ルート )
 */
public class ElementDragonEggEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_ELEMENT =
            SynchedEntityData.defineId(ElementDragonEggEntity.class, EntityDataSerializers.INT);

    /** 孵化までの tick 数 ( 2 分 )。 */
    public static final int HATCH_TICKS = 20 * 60 * 2;

    private int age;

    public ElementDragonEggEntity(EntityType<? extends ElementDragonEggEntity> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public int getElementId() {
        return this.entityData.get(DATA_ELEMENT);
    }

    public void setElementId(int id) {
        this.entityData.set(DATA_ELEMENT, Math.floorMod(id, ModEntities.ELEMENT_ORDER.length));
    }

    public String getElementName() {
        return ModEntities.ELEMENT_ORDER[Math.floorMod(getElementId(), ModEntities.ELEMENT_ORDER.length)];
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ELEMENT, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            return;
        }
        this.age++;
        if (this.age >= HATCH_TICKS) {
            hatch();
        }
    }

    private void hatch() {
        if (!(this.level() instanceof ServerLevel server)) {
            return;
        }
        EntityType<? extends ElementDragonEntity> type = ModEntities.dragonByElement(getElementId());
        ElementDragonEntity dragon = type.create(server);
        if (dragon != null) {
            dragon.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0f);
            dragon.finalizeSpawn(server, server.getCurrentDifficultyAt(this.blockPosition()),
                    MobSpawnType.NATURAL, null, null);
            dragon.setStage(1); // 卵から孵ったら幼体
            server.addFreshEntity(dragon);
            server.playSound(null, this.blockPosition(), SoundEvents.TURTLE_EGG_HATCH,
                    SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
        this.discard();
    }

    // ── 当たり判定 / 相互作用 ─────────────────────────────────────
    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        if (this.level().isClientSide() || this.isRemoved()) {
            return false;
        }
        // 攻撃すると卵を破壊する ( アイテムは落とさない )
        this.discard();
        return true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("ElementId", getElementId());
        tag.putInt("Age", this.age);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setElementId(tag.getInt("ElementId"));
        this.age = tag.getInt("Age");
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
