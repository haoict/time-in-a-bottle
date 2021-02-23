package com.haoict.tiab.common.items;

import com.haoict.tiab.common.capability.CapabilityProviderEnergy;
import com.haoict.tiab.common.capability.ItemCapabilityWrapper;
import com.haoict.tiab.common.capability.ItemEnergyForge;
import com.haoict.tiab.common.utils.Helpers;
import com.haoict.tiab.config.NBTKeys;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTimeInABottleFE extends AbstractItemTiab {
  public ItemTimeInABottleFE() {
    super();
  }

  @Override
  @Nullable
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT tag) {
    return new ItemCapabilityWrapper(new CapabilityProviderEnergy(stack, TiabConfig.COMMON.maxStoredFE.get(), TiabConfig.COMMON.maxFEInput.get(), TiabConfig.COMMON.maxStoredFE.get()));
  }

  @Override
  public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
    super.fillItemGroup(group, items);
    if (!isInGroup(group))
      return;

    ItemStack charged = new ItemStack(this);
    charged.getOrCreateTag().putDouble(NBTKeys.ENERGY, TiabConfig.COMMON.maxStoredFE.get());
    items.add(charged);
  }

  @Override
  public boolean isDamaged(ItemStack stack) {
    if (!stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
      return super.isDamaged(stack);
    }

    return getStoredEnergy(stack) != getMaxEnergy(stack);
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    if (!stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
      return super.getDurabilityForDisplay(stack);
    }

    return 1D - (getStoredEnergy(stack) / (double) getMaxEnergy(stack));
  }

  @Override
  public int getRGBDurabilityForDisplay(ItemStack stack) {
    if (!stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
      return super.getRGBDurabilityForDisplay(stack);
    }

    return MathHelper.hsvToRGB(Math.max(0.0F, getStoredEnergy(stack) / (float) getMaxEnergy(stack)) / 3.0F, 1.0F, 1.0F);
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    if (stack.getTag() != null && stack.getTag().contains(NBTKeys.CREATIVE_MARKER))
      return false;

    if (!stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
      return super.showDurabilityBar(stack);
    }

    return getStoredEnergy(stack) != (getMaxEnergy(stack));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
    int storedEnergy = getStoredEnergy(stack);
    String storedEnergyStr = storedEnergy + " (" + Helpers.withSuffix(storedEnergy) + "FE)";
    tooltip.add(new StringTextComponent(I18n.format("item.tiab.energy.tooltip", storedEnergyStr)).mergeStyle(TextFormatting.GREEN));
    tooltip.add(new StringTextComponent(I18n.format("item.tiab.timeinabottlefe.tooltip")).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
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
      ((ItemEnergyForge)e).setEnergy(e.getEnergyStored() + energy);
      ((ItemEnergyForge)e).writeEnergyToNBT();
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
    // make the items costs more FE, to make it a little bit balance I guess
    // 30 seconds => 6000 FE
    return super.getEnergyCost(timeRate) * TiabConfig.COMMON.feCostMultiply.get();
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return true;
  }
}
