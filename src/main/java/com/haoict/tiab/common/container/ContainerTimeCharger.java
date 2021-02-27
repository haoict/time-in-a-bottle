package com.haoict.tiab.common.container;

import com.haoict.tiab.common.items.AbstractItemTiab;
import com.haoict.tiab.common.registries.BlockRegistry;
import com.haoict.tiab.common.tiles.TileTimeCharger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/*
 * This is used to link the client side gui to the server side inventory.  It collates the various different
 * inventories into one place (using Slots)
 * It is also used to send server side data such as progress bars to the client for use in guis
 *
 * Vanilla automatically detects changes in the server side Container (the Slots and the trackedInts) and
 * sends them to the client container.
 */
public class ContainerTimeCharger extends Container {
  private static final int SLOTS = 1;

  public final IIntArray data;
  private final TileTimeCharger tile;
  public ItemStackHandler itemStackHandler;

  // constructor for client side, used for TIME_CHARGER_CONTAINER registry
  public ContainerTimeCharger(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
    this((TileTimeCharger) playerInventory.player.world.getTileEntity(extraData.readBlockPos()), new IntArray(TileTimeCharger.TIME_CHARGER_DATA_SIZE), windowId, playerInventory, new ItemStackHandler(SLOTS));
  }

  // constructor for server side
  public ContainerTimeCharger(TileTimeCharger tileEntity, IIntArray data, int windowId, PlayerInventory playerInventory, ItemStackHandler itemStackHandler) {
    super(BlockRegistry.TIME_CHARGER_CONTAINER.get(), windowId);

    this.itemStackHandler = itemStackHandler;
    this.tile = tileEntity;
    this.data = data;

    setup(playerInventory);
    trackIntArray(data);
  }

  public void setup(PlayerInventory inventory) {
    addSlot(new RestrictedSlot(itemStackHandler, 89, 40));

    // Slots for the hotbar
    for (int row = 0; row < 9; ++row) {
      int x = 8 + row * 18;
      int y = 56 + 86;
      addSlot(new Slot(inventory, row, x, y));
    }
    // Slots for the main inventory
    for (int row = 1; row < 4; ++row) {
      for (int col = 0; col < 9; ++col) {
        int x = 8 + col * 18;
        int y = row * 18 + (56 + 10);
        addSlot(new Slot(inventory, col + row * 9, x, y));
      }
    }
  }

  /*
   * This is where you specify what happens when a player shift clicks a slot in the gui
   * (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
   * player inventory. When you you shift-click a hotbar or player inventory items, it moves it to the first available
   * position in the TileEntity inventory - either input or fuel as appropriate for the items you clicked)
   * At the very least you must override this and return ItemStack.EMPTY or the game will crash when the player shift clicks a slot.
   * returns ItemStack.EMPTY if the source slot is empty, or if none of the source slot items could be moved.
   * otherwise, returns a copy of the source stack
   * Code copied & refactored from vanilla furnace AbstractFurnaceContainer
   */
  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);

    if (slot != null && slot.getHasStack()) {
      ItemStack currentStack = slot.getStack();
      itemstack = currentStack.copy();

      if (index < SLOTS) {
        if (!this.mergeItemStack(currentStack, SLOTS, this.inventorySlots.size(), false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(currentStack, 0, SLOTS, false)) {
        return ItemStack.EMPTY;
      }

      if (currentStack.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }
    }

    return itemstack;
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    BlockPos pos = this.tile.getPos();
    return this.tile != null && !this.tile.isRemoved() && playerIn.getDistanceSq(new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5D, 0.5D, 0.5D)) <= 64D;
  }

  public int getEnergy() {
    return this.data.get(0) * 32; // why 32? already explained in TileTimeCharger IIntArray
  }

  public int getMaxPower() {
    return this.data.get(1) * 32;
  }

  static class RestrictedSlot extends SlotItemHandler {
    public RestrictedSlot(IItemHandler itemHandler, int xPosition, int yPosition) {
      super(itemHandler, 0, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
      return stack.getItem() instanceof AbstractItemTiab;
    }
  }
}
