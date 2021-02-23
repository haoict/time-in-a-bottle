package com.haoict.tiab.common.capability;

import com.haoict.tiab.common.items.AbstractItemTiab;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class TileTimeChargerInventoryHandler extends AbstractTileInventoryHandler {
  public static final int SLOT_SIZE = 1;

  public TileTimeChargerInventoryHandler(TileEntity tile) {
    super(SLOT_SIZE, tile);
  }

  @Override
  public boolean canInsert(int slot, @Nonnull ItemStack stack, boolean simulate) {
    return (stack.getItem() instanceof AbstractItemTiab) && getStackInSlot(slot).getCount() == 0;
  }

  @Override
  public boolean canExtract(int slot, int amount, boolean simulate) {
    return true;
  }
}
