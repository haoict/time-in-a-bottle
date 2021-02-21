package com.haoict.tiab.common.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import java.util.function.IntSupplier;

public final class ItemEnergyForge extends ConfigEnergyStorage implements IPrivateEnergy {
  private final ItemStack stack;

  public ItemEnergyForge(ItemStack stack, IntSupplier capacity) {
    super(capacity);
    this.stack = stack;
  }

  protected void writeEnergy() {
    CompoundNBT nbt = stack.getOrCreateTag();
    nbt.putInt("energy", getEnergyStoredCache());
  }

  protected void updateEnergy() {
    CompoundNBT nbt = stack.getOrCreateTag();
    if (nbt.contains("energy"))
      setEnergy(nbt.getInt("energy"));
    updateMaxEnergy();
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  /**
   * Do not use {@link #extractEnergy(int, boolean)} internally. This method
   * stops the gadgets from being used like batteries.
   */
  public int extractPower(int maxExtract, boolean simulate) {
    if (maxExtract < 0)
      return 0;

    int energyExtracted = evaluateEnergyExtracted(maxExtract, simulate);
    if (!simulate) {
      setEnergy(getEnergyStored() - energyExtracted);
      writeEnergy();
    }
    return energyExtracted;
  }
}
