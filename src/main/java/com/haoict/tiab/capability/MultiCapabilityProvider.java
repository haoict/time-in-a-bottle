package com.haoict.tiab.capability;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MultiCapabilityProvider implements ICapabilityProvider {

  private final ImmutableList<ICapabilityProvider> childProviders;

  public MultiCapabilityProvider(ICapabilityProvider... childProviders) {
    this(ImmutableList.copyOf(childProviders));
  }

  public MultiCapabilityProvider(ImmutableList<ICapabilityProvider> childProviders) {
    this.childProviders = childProviders;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    for (ICapabilityProvider provider : childProviders) {
      LazyOptional<T> optional = provider.getCapability(cap, side);
      if (optional.isPresent()) return optional;
    }
    return LazyOptional.empty();
  }
}
