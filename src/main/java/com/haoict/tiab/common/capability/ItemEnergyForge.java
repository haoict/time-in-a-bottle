package com.haoict.tiab.common.capability;

import com.haoict.tiab.config.NBTKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public final class ItemEnergyForge extends AbstractEnergyStorage {
  private final ItemStack stack;

  public ItemEnergyForge(ItemStack stack, int capacity, int maxInput, int maxOutput) {
    super(capacity, maxInput, maxOutput);
    this.stack = stack;
  }

  public void writeEnergyToNBT() {
    CompoundNBT nbt = stack.getOrCreateTag();
    nbt.putInt(NBTKeys.ENERGY, getEnergyStoredCache());
  }

  public void updateEnergyFromNBT() {
    CompoundNBT nbt = stack.getOrCreateTag();
    if (nbt.contains(NBTKeys.ENERGY)) {
      setEnergy(nbt.getInt(NBTKeys.ENERGY));
    }
    updateMaxEnergy();
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }
}
