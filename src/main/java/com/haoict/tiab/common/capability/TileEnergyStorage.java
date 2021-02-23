package com.haoict.tiab.common.capability;

import com.haoict.tiab.config.NBTKeys;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class TileEnergyStorage extends AbstractEnergyStorage implements INBTSerializable<CompoundNBT> {
  public TileEnergyStorage(int energy, int capacity) {
    super(capacity, TiabConfig.COMMON.timeChargerMaxIO.get());
    this.setEnergy(energy);
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT tag = new CompoundNBT();
    tag.putInt(NBTKeys.ENERGY, energy);
    return tag;
  }

  @Override
  public void deserializeNBT(CompoundNBT nbt) {
    this.energy = nbt.getInt(NBTKeys.ENERGY);
  }

  @Override
  protected void writeEnergyToNBT() {
  }

  @Override
  protected void updateEnergyFromNBT() {
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canExtract() {
    return false;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  @Override
  public String toString() {
    return "TimeChargerEnergyStorage{" +
        "energy=" + energy +
        ", capacity=" + getMaxEnergyStored() +
        ", maxInOut=" + TiabConfig.COMMON.timeChargerMaxFE.get() +
        '}';
  }
}
