package com.haoict.tiab.capabilities;

import com.haoict.tiab.config.NBTKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class ItemEnergyForge extends AbstractEnergyStorage {
    private final ItemStack stack;

    public ItemEnergyForge(ItemStack stack, int capacity, int maxInput, int maxOutput) {
        super(capacity, maxInput, maxOutput);
        this.stack = stack;
    }

    public void writeEnergyToNBT() {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putInt(NBTKeys.ENERGY, getEnergyStoredCache());
    }

    public void updateEnergyFromNBT() {
        CompoundTag nbt = stack.getOrCreateTag();
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
