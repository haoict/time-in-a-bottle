package com.haoict.tiab.items;

import com.haoict.tiab.capabilities.CapabilityProviderEnergy;
import com.haoict.tiab.capabilities.ItemCapabilityWrapper;
import com.haoict.tiab.capabilities.ItemEnergyForge;
import com.haoict.tiab.config.NBTKeys;
import com.haoict.tiab.config.TiabConfig;
import com.haoict.tiab.utils.Helpers;
import com.haoict.tiab.utils.lang.Styles;
import com.haoict.tiab.utils.lang.Translation;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TimeInABottleFEItem extends AbstractTiabItem {

    public TimeInABottleFEItem() {
        super();
    }

    @Override
    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tag) {
        return new ItemCapabilityWrapper(new CapabilityProviderEnergy(stack, TiabConfig.COMMON.maxStoredFE.get(), TiabConfig.COMMON.maxFEInput.get(), TiabConfig.COMMON.maxStoredFE.get()));
    }

    @Override
    public void fillItemCategory(CreativeModeTab creativeModeTab, @Nonnull NonNullList<ItemStack> items) {
        if (!TiabConfig.COMMON.enableTimeInABottleFE.get()) {
            return;
        }

        super.fillItemCategory(creativeModeTab, items);
        if (!allowdedIn(creativeModeTab)) return;

        ItemStack charged = new ItemStack(this);
        charged.getOrCreateTag().putDouble(NBTKeys.ENERGY, TiabConfig.COMMON.maxStoredFE.get());
        items.add(charged);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
        if (!cap.isPresent()) return super.getBarWidth(stack);

        return cap.map(e -> Math.round(13F * ((float) e.getEnergyStored() / e.getMaxEnergyStored()))).orElse(super.getBarWidth(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
        if (!cap.isPresent()) return super.getBarColor(stack);

        Pair<Integer, Integer> energyStorage = cap.map(e -> Pair.of(e.getEnergyStored(), e.getMaxEnergyStored())).orElse(Pair.of(0, 0));
        return Mth.hsvToRgb(Math.max(0.0F, energyStorage.getLeft() / (float) energyStorage.getRight()) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
        if (!cap.isPresent()) {
            return super.isDamaged(stack);
        }

        return getStoredEnergy(stack) != getMaxEnergy(stack);
    }

    @Override
    public int getDamage(ItemStack stack) {
        if (!stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
            return super.getDamage(stack);
        }

        return getMaxEnergy(stack) - getStoredEnergy(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        // return stack.getCapability(CapabilityEnergy.ENERGY).map(e -> e.getEnergyStored() != e.getMaxEnergyStored()).orElse(super.isBarVisible(stack));
        return true;
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(itemStack, world, tooltip, flag);

        int storedEnergy = this.getStoredEnergy(itemStack);

        tooltip.add(Translation.TOOLTIP_STORED_ENERGY.componentTranslation(String.valueOf(storedEnergy), Helpers.withSuffix(storedEnergy)).setStyle(Styles.GREEN));
        tooltip.add(Translation.TOOLTIP_TIAB_FE.componentTranslation().setStyle(Styles.GRAY_ITALIC));
    }

    public int getMaxEnergy(ItemStack stack) {
        LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
        return cap.isPresent() ? cap.map(IEnergyStorage::getMaxEnergyStored).orElse(0) : 0;
    }

    @Override
    public int getStoredEnergy(ItemStack stack) {
        LazyOptional<IEnergyStorage> cap = stack.getCapability(CapabilityEnergy.ENERGY);
        return cap.isPresent() ? cap.map(IEnergyStorage::getEnergyStored).orElse(0) : 0;
    }

    @Override
    public void setStoredEnergy(ItemStack stack, int energy) {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> {
            ((ItemEnergyForge) e).setEnergy(e.getEnergyStored() + energy);
            ((ItemEnergyForge) e).writeEnergyToNBT();
        });
    }

    @Override
    public void applyDamage(ItemStack stack, int damage) {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((ItemEnergyForge) e).consumeEnergy(damage, false));
    }

    @Override
    public int getEnergyCost(int timeRate) {
        // 20,000 RF = 1 Coal (as most common mods fuel levels)
        // 30 seconds = 600 ticks
        // make the items costs more FE, to make it balance
        // 30 seconds => 6000 FE
        return super.getEnergyCost(timeRate) * TiabConfig.COMMON.equivalentFeForATick.get();
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
