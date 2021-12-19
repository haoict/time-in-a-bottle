package com.haoict.tiab.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProviderEnergy implements ICapabilityProvider {
    private final ItemEnergyForge energyItem;
    private final LazyOptional<ItemEnergyForge> energyCapability;

    public CapabilityProviderEnergy(ItemStack stack, int capacity, int maxInput, int maxOutput) {
        this.energyItem = new ItemEnergyForge(stack, capacity, maxInput, maxOutput);
        this.energyCapability = LazyOptional.of(() -> energyItem);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityEnergy.ENERGY ? energyCapability.cast() : LazyOptional.empty();
    }

}
