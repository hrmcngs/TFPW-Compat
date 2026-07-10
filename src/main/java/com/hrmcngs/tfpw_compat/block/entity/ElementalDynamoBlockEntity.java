package com.hrmcngs.tfpw_compat.block.entity;

import com.hrmcngs.tfpw_compat.block.ModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/**
 * 属性発電機 (issue #199-B)。電気/雷属性のエネルギーを <b>Forge Energy (FE)</b> として蓄え、
 * 隣接する FE 受入先 (Mekanism のケーブル/機械は FE 互換) へ供給する。
 *
 * <p>発電源:
 * <ul>
 *   <li>雷雨中に空が見える設置 → 継続発電 (storm dynamo)</li>
 *   <li>近くで電気/雷属性攻撃が命中 → {@link #addEnergy(int)} で注入
 *       ({@link com.hrmcngs.tfpw_compat.event.ElementalGenerationHandler})</li>
 * </ul>
 *
 * <p>FE を公開するだけなので Mekanism 専用 Joule API には触れない (最も疎結合)。
 */
public class ElementalDynamoBlockEntity extends BlockEntity {
    public static final int CAPACITY = 200_000;
    public static final int MAX_EXTRACT = 5_000;
    private static final int STORM_FE_PER_TICK = 40;

    /** 発電専用バッファ: 外部からの受電は不可 (maxReceive=0)、 抽出のみ。内部発電は {@link #generate}。 */
    private static final class Buffer extends EnergyStorage {
        Buffer() {
            super(CAPACITY, 0, MAX_EXTRACT);
        }

        int generate(int amount) {
            int add = Math.min(this.capacity - this.energy, Math.max(0, amount));
            this.energy += add;
            return add;
        }

        void setStored(int value) {
            this.energy = Math.max(0, Math.min(this.capacity, value));
        }
    }

    private final Buffer energy = new Buffer();
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);

    public ElementalDynamoBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELEMENTAL_DYNAMO.get(), pos, state);
    }

    /** 内部発電: バッファへ FE を加える (上限まで)。実際に加算できた量を返す。 */
    public int addEnergy(int amount) {
        int added = energy.generate(amount);
        if (added > 0) {
            setChanged();
        }
        return added;
    }

    public int getEnergyStored() {
        return energy.getEnergyStored();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ElementalDynamoBlockEntity be) {
        // 雷雨 + 空が見える → 発電。
        if (level.isThundering() && level.canSeeSky(pos.above())) {
            be.addEnergy(STORM_FE_PER_TICK);
        }
        // 蓄電を隣接 FE 受入先へ能動 push (Mekanism ケーブル/機械が受け取る)。
        be.pushEnergy(level, pos);
    }

    private void pushEnergy(Level level, BlockPos pos) {
        if (energy.getEnergyStored() <= 0) {
            return;
        }
        for (Direction dir : Direction.values()) {
            if (energy.getEnergyStored() <= 0) {
                break;
            }
            BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
            if (neighbor == null) {
                continue;
            }
            neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(dest -> {
                if (dest.canReceive()) {
                    int toSend = Math.min(MAX_EXTRACT, energy.getEnergyStored());
                    int accepted = dest.receiveEnergy(toSend, false);
                    if (accepted > 0) {
                        energy.extractEnergy(accepted, false);
                        setChanged();
                    }
                }
            });
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energy.setStored(tag.getInt("Energy"));
    }

    /** BlockEntityType 生成ヘルパ。 */
    public static BlockEntityType<ElementalDynamoBlockEntity> makeType(net.minecraft.world.level.block.Block block) {
        return BlockEntityType.Builder.of(ElementalDynamoBlockEntity::new, block).build(null);
    }
}
