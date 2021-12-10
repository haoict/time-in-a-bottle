package com.haoict.tiab.entities;

import com.haoict.tiab.config.NBTKeys;
import com.haoict.tiab.config.TiabConfig;
import com.haoict.tiab.registries.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class TimeAcceleratorEntity extends Entity {
    private static final EntityDataAccessor<Integer> timeRate = SynchedEntityData.defineId(TimeAcceleratorEntity.class, EntityDataSerializers.INT);
    private int remainingTime;
    private BlockPos pos;

    public TimeAcceleratorEntity(EntityType entityType, Level worldIn) {
        super(entityType, worldIn);
        entityData.set(timeRate, 1);
    }

    public TimeAcceleratorEntity(Level worldIn, BlockPos pos, double posX, double posY, double posZ) {
        this(EntityTypeRegistry.timeAcceleratorEntityType.get(), worldIn);
        this.pos = pos;
        this.setPos(posX, posY, posZ);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(timeRate, 1);
    }

    @Override
    public void tick() {
        super.tick();

        if (pos == null) {
            if (!level.isClientSide) {
                this.remove(RemovalReason.KILLED);
            }
            return;
        }

        BlockState blockState = level.getBlockState(pos);
        ServerLevel serverWorld = level.getServer().getLevel(level.dimension());
        BlockEntity targetTE = level.getBlockEntity(pos);

        for (int i = 0; i < getTimeRate(); i++) {
            if (targetTE != null) {
                // if is TileEntity (furnace, brewing stand, ...)
                BlockEntityTicker<BlockEntity> ticker = targetTE.getBlockState().getTicker(level, (BlockEntityType<BlockEntity>) targetTE.getType());
                if (ticker != null) {
                    ticker.tick(level, pos, targetTE.getBlockState(), targetTE);
                }
            } else if (serverWorld != null && blockState.isRandomlyTicking()) {
                // if is random ticket block (grass block, sugar cane, wheat or sapling, ...)
                if (level.random.nextInt(TiabConfig.COMMON.averageUpdateRandomTick.get()) == 0) {
                    blockState.randomTick(serverWorld, pos, level.random);
                }
            } else {
                // block entity broken
                this.remove(RemovalReason.KILLED);
                break;
            }
        }

        this.remainingTime -= 1;
        if (this.remainingTime <= 0 && !this.level.isClientSide) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        entityData.set(timeRate, compound.getInt(NBTKeys.ENTITY_TIME_RATE));
        setRemainingTime(compound.getInt(NBTKeys.ENTITY_REMAINING_TIME));
        this.pos = NbtUtils.readBlockPos(compound.getCompound(NBTKeys.ENTITY_POS));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt(NBTKeys.ENTITY_TIME_RATE, getTimeRate());
        compound.putInt(NBTKeys.ENTITY_REMAINING_TIME, getRemainingTime());
        compound.put(NBTKeys.ENTITY_POS, NbtUtils.writeBlockPos(this.pos));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public int getTimeRate() {
        return entityData.get(timeRate);
    }

    public void setTimeRate(int rate) {
        entityData.set(timeRate, rate);
    }

    public int getRemainingTime() {
        return this.remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }
}
