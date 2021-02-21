package com.haoict.tiab.item;

import com.google.common.collect.ImmutableList;
import com.haoict.tiab.Config;
import com.haoict.tiab.capability.CapabilityProviderEnergy;
import com.haoict.tiab.capability.IPrivateEnergy;
import com.haoict.tiab.capability.MultiCapabilityProvider;
import com.haoict.tiab.utils.UnitDisplay;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    ImmutableList.Builder<ICapabilityProvider> providerBuilder = ImmutableList.builder();
    providerBuilder.add(new CapabilityProviderEnergy(stack, () -> Config.MAX_STORED_FE));
    return new MultiCapabilityProvider(providerBuilder.build());
  }

  @Override
  public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
    super.fillItemGroup(group, items);
    if (!isInGroup(group))
      return;

    ItemStack charged = new ItemStack(this);
    charged.getOrCreateTag().putDouble("energy", Config.MAX_STORED_FE);
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
    if (stack.getTag() != null && stack.getTag().contains("creative"))
      return false;

    if (!stack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
      return super.showDurabilityBar(stack);
    }

    return getStoredEnergy(stack) != (getMaxEnergy(stack));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
    String storedEnergyStr = UnitDisplay.format(getStoredEnergy(stack)) + "FE";
    tooltip.add(new StringTextComponent(I18n.format("item.tiab.timeinabottlefe.tooltip", storedEnergyStr)));
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
    stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> e.receiveEnergy(energy, false));
  }

  @Override
  public void applyDamage(ItemStack stack, int damage) {
    stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(e -> ((IPrivateEnergy) e).extractPower(damage, false));
  }

}
