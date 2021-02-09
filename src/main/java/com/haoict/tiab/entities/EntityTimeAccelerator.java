package com.haoict.tiab.entities;

import com.haoict.tiab.Config;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;

public class EntityTimeAccelerator extends Entity implements IEntityAdditionalSpawnData {
  private static final DataParameter<Integer> TIME_RATE = EntityDataManager.<Integer> createKey(EntityTimeAccelerator.class, DataSerializers.VARINT);
  private int remainingTime;
  private BlockPos pos;

  public EntityTimeAccelerator(EntityType entityType, World worldIn) {
    super(entityType, worldIn);
    this.noClip = true;
    this.dataManager.register(TIME_RATE, 1);
  }

  public EntityTimeAccelerator(World worldIn, BlockPos pos, double posX, double posY, double posZ) {
    this(TiabEntityTypes.timeAcceleratorEntityType, worldIn);
    this.pos = pos;
    this.setPosition(posX, posY, posZ);
  }

  @Override
  public void tick() {
    super.tick();

    if (pos == null) {
      if (!world.isRemote) {
        this.setDead();
      }
      return;
    }

    BlockState blockState = world.getBlockState(pos);
    ServerWorld serverWorld = world.getServer().getWorld(world.getDimensionKey());
    TileEntity targetTE = world.getTileEntity(pos);

    for (int i = 0; i < getTimeRate(); i++) {
      // if is tickable TileEntity (furnace, brewing stand, ...)
      if (targetTE != null && targetTE instanceof ITickableTileEntity) {
        ((ITickableTileEntity) targetTE).tick();
      } else {
        if (serverWorld != null && blockState.ticksRandomly() && world.rand.nextInt(Config.AVERAGE_UPDATE_RANDOM_TICK) == 0) {
          blockState.randomTick(serverWorld, pos, world.rand);
        }
      }
    }

    this.remainingTime -= 1;
    if (this.remainingTime <= 0 && !this.world.isRemote) {
      this.setDead();
    }
  }

  @Override
  public void writeSpawnData(PacketBuffer buffer) {
    /*buffer.writeInt(pos.getX());
    buffer.writeInt(pos.getY());
    buffer.writeInt(pos.getZ());*/
  }

  @Override
  public void readSpawnData(PacketBuffer additionalData) {
    /*this.pos = new BlockPos(additionalData.readInt(), additionalData.readInt(), additionalData.readInt());*/
  }

  @Override
  protected void registerData() {
  }

  @Override
  protected void readAdditional(CompoundNBT compound) {
    // this.pos = NBTUtil.readBlockPos(compound);
    /*setRemainingTime(compound.getInt("remainingTime"));
    setTimeRate(compound.getInt("timeRate"));*/

  }

  @Override
  protected void writeAdditional(CompoundNBT compound) {
    // NBTUtil.writeBlockPos(pos);
    /*compound.putInt("remainingTime", getRemainingTime());
    compound.putInt("timeRate", getTimeRate());*/
  }

  @Nonnull
  @Override
  public IPacket<?> createSpawnPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  public int getTimeRate() {
    return this.dataManager.get(TIME_RATE);
  }

  public void setTimeRate(int timeRate) {
    this.dataManager.set(TIME_RATE, timeRate);
  }

  public int getRemainingTime() {
    return this.remainingTime;
  }

  public void setRemainingTime(int remainingTime) {
    this.remainingTime = remainingTime;
  }

  public BlockPos getPos() {
    return pos;
  }

  public void setPos(BlockPos pos) {
    this.pos = pos;
  }
}
