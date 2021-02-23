package com.haoict.tiab.common.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public abstract class AbstractTileInventoryHandler extends ItemStackHandler {
  private final TileEntity tileEntity;

  public AbstractTileInventoryHandler(int size, TileEntity tile) {
    super(size);
    this.tileEntity = tile;
  }

  @Override
  protected void onContentsChanged(int slot) {
    tileEntity.markDirty();
  }

  @Nonnull
  @Override
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (!canInsert(slot, stack, simulate)) {
      return stack;
    }

    return super.insertItem(slot, stack, simulate);
  }

  @Nonnull
  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (!canExtract(slot, amount, simulate)) {
      return ItemStack.EMPTY;
    }

    return super.extractItem(slot, amount, simulate);
  }

  public abstract boolean canInsert(int slot, @Nonnull ItemStack stack, boolean simulate);

  public abstract boolean canExtract(int slot, int amount, boolean simulate);
}
