package com.haoict.tiab.common.tiles;

import com.haoict.tiab.common.capability.TileEnergyStorage;
import com.haoict.tiab.common.capability.TileTimeChargerInventoryHandler;
import com.haoict.tiab.common.container.ContainerTimeCharger;
import com.haoict.tiab.common.items.ItemTimeInABottle;
import com.haoict.tiab.common.items.ItemTimeInABottleFE;
import com.haoict.tiab.common.registries.BlockRegistry;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileTimeCharger extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
  private final LazyOptional<TileEnergyStorage> energy;
  private final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new TileTimeChargerInventoryHandler(this));
  private final TileEnergyStorage energyStorage;

  /*
   * This is used to store some state data for the tile entity (eg energy, maxEnergy, etc)
   * 1) The Server TileEntity uses it to store the data permanently, including NBT serialisation and deserialisation
   * 2) The server container uses it to
   *    a) read/write permanent data back into the TileEntity
   *    b) synchronise the server container data to the client container using the IIntArray interface (via Container::trackIntArray)
   * 3) The client container uses it to store a temporary copy of the data, for rendering / GUI purposes
   * The TileEntity and the client container both use it by poking directly into its member variables.  That's not good
   *   practice but it's easier to understand than the vanilla method which uses an anonymous class/lambda functions
   *
   *  The IIntArray interface collates all the separate member variables into a single array for the purposes of transmitting
   *     from server to client (handled by Vanilla)
   */
  private final IIntArray timeChargerData = new IIntArray() {
    @Override
    public int get(int index) {
      switch (index) {
        case 0:
          return TileTimeCharger.this.energyStorage.getEnergyStored() / 32;
        case 1:
          return TileTimeCharger.this.energyStorage.getMaxEnergyStored() / 32;
        default:
          throw new IllegalArgumentException("Invalid index: " + index);
      }
    }

    @Override
    public void set(int index, int value) {
      throw new IllegalStateException("Cannot set values through IIntArray");
    }

    @Override
    public int size() {
      return 2;
    }
  };
  private boolean isCreative;

  public TileTimeCharger() {
    super(BlockRegistry.TIME_CHARGER_TILE.get());
    this.energyStorage = new TileEnergyStorage(0, TiabConfig.COMMON.timeChargerMaxFE.get());
    this.energy = LazyOptional.of(() -> this.energyStorage);
    this.isCreative = false;
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
      return inventory.cast();

    if (cap == CapabilityEnergy.ENERGY)
      return energy.cast();

    return super.getCapability(cap, side);
  }

  @Override
  public void tick() {
    if (getWorld() == null)
      return;

    inventory.ifPresent(handler -> {
      ItemStack stack = handler.getStackInSlot(0);
      if (!stack.isEmpty())
        chargeItem(stack);
    });
  }

  private void chargeItem(ItemStack stack) {
    this.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
      Item item = stack.getItem();
      int maxIO = Math.min(energyStorage.getEnergyStored(), TiabConfig.COMMON.timeChargerMaxIO.get());
      if (item instanceof ItemTimeInABottle) {
        ItemTimeInABottle itemTiab = (ItemTimeInABottle) item;
        if (!isChargingItemTime(stack)) {
          return;
        }
        itemTiab.setStoredEnergy(stack, itemTiab.getStoredEnergy(stack) + maxIO / TiabConfig.COMMON.feCostMultiply.get());
        if (!this.isCreative) {
          ((TileEnergyStorage) energyStorage).consumeEnergy(maxIO, false);
        }
      } else if (item instanceof ItemTimeInABottleFE) {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(itemEnergy -> {
          if (!isChargingItemFE(itemEnergy)) {
            return;
          }
          int energyRemoved = itemEnergy.receiveEnergy(maxIO, false);
          if (!this.isCreative) {
            ((TileEnergyStorage) energyStorage).consumeEnergy(energyRemoved, false);
          }
        });
      }
    });
  }

  public boolean isChargingItemTime(ItemStack stack) {
    int storedTime = ((ItemTimeInABottle) stack.getItem()).getStoredEnergy(stack);
    return storedTime >= 0 && storedTime < TiabConfig.COMMON.maxStoredTime.get();
  }

  public boolean isChargingItemFE(IEnergyStorage energy) {
    return energy.getEnergyStored() >= 0 && energy.receiveEnergy(energy.getEnergyStored(), true) >= 0;
  }

  /* The name is misleading; createMenu has nothing to do with creating a Screen, it is used to create the Container on the server only */
  @Nullable
  @Override
  public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    assert world != null;
    return new ContainerTimeCharger(this, timeChargerData, windowID, playerInventory, this.inventory.orElse(new ItemStackHandler(1)));
  }

  /* This is where you load the data that you saved in writeToNBT */
  @Override
  public void read(BlockState stateIn, CompoundNBT compound) {
    super.read(stateIn, compound);
    inventory.ifPresent(h -> h.deserializeNBT(compound.getCompound("inventory")));
    energy.ifPresent(h -> h.deserializeNBT(compound.getCompound("energy")));
  }

  /* This is where you save any data that you don't want to lose when the tile entity unloads
   * In this case, it saves the state of the furnace (burn time etc) and the itemstacks stored in the fuel, input, and output slots
   */
  @Override
  public CompoundNBT write(CompoundNBT compound) {
    inventory.ifPresent(h -> compound.put("inventory", h.serializeNBT()));
    energy.ifPresent(h -> compound.put("energy", h.serializeNBT()));
    return super.write(compound);
  }

  /*
   * When the world loads from disk, the server needs to send the TileEntity information to the client
   * it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this
   */
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    // Vanilla uses the type parameter to indicate which type of tile entity (command block, skull, or beacon?) is receiving the packet, but it seems like Forge has overridden this behavior
    return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return write(new CompoundNBT());
  }

  @Override
  public void handleUpdateTag(BlockState stateIn, CompoundNBT tag) {
    read(stateIn, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
    read(this.getBlockState(), pkt.getNbtCompound());
  }

  public TileEnergyStorage getEnergyStorage() {
    return energyStorage;
  }

  @Override
  public ITextComponent getDisplayName() {
    return new StringTextComponent("Time Charger Tile Entity");
  }

  public boolean isCreative() {
    return isCreative;
  }

  public void setCreative(boolean creative) {
    isCreative = creative;
  }
}
