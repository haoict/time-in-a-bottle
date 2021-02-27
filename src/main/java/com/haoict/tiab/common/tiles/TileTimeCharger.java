package com.haoict.tiab.common.tiles;

import com.haoict.tiab.common.blocks.BlockTimeCharger;
import com.haoict.tiab.common.capability.TileEnergyStorage;
import com.haoict.tiab.common.capability.TileTimeChargerInventoryHandler;
import com.haoict.tiab.common.container.ContainerTimeCharger;
import com.haoict.tiab.common.items.ItemTimeInABottle;
import com.haoict.tiab.common.items.ItemTimeInABottleFE;
import com.haoict.tiab.common.registries.BlockRegistry;
import com.haoict.tiab.common.utils.SetBlockStateFlag;
import com.haoict.tiab.config.NBTKeys;
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
import java.util.concurrent.atomic.AtomicInteger;

public class TileTimeCharger extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
  private final LazyOptional<TileEnergyStorage> energy;
  private final LazyOptional<ItemStackHandler> inventory = LazyOptional.of(() -> new TileTimeChargerInventoryHandler(this));
  private final TileEnergyStorage energyStorage;

  public static final int TIME_CHARGER_DATA_SIZE = 2;

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
   *
   * Container#trackIntArray only supports 16-bit short values. So we have to divide it to 32
   * If you need more data:
   * Override detectAndSendChanges in your Container. Check if the data has changed since the last time it was called.
   * If so, send a custom packet to all listeners (Container#listeners, you need reflection to access this field; check if the listener is a player to send them a packet).
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
      return TIME_CHARGER_DATA_SIZE;
    }
  };
  private boolean isCreative;

  public TileTimeCharger() {
    super(BlockRegistry.TIME_CHARGER_TILE.get());
    this.energyStorage = new TileEnergyStorage(this, 0, TiabConfig.COMMON.timeChargerMaxFE.get());
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
    if (world == null || world.isRemote)
      return;

    inventory.ifPresent(handler -> {
      ItemStack stack = handler.getStackInSlot(0);

      if (!stack.isEmpty()) {
        chargeItem(stack);
      } else {
        updateActiveBlockState(false);
      }
    });
  }

  private void chargeItem(ItemStack stack) {
    this.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyStorage -> {
      Item item = stack.getItem();
      int maxIO = Math.min(energyStorage.getEnergyStored(), TiabConfig.COMMON.timeChargerMaxIO.get());
      AtomicInteger energyToConsume = new AtomicInteger(maxIO);

      if (item instanceof ItemTimeInABottle) {
        ItemTimeInABottle itemTiab = (ItemTimeInABottle) item;
        if (!isChargingItemTime(energyStorage, stack)) {
          updateActiveBlockState(false);
          return;
        }
        updateActiveBlockState(true);
        itemTiab.setStoredEnergy(stack, itemTiab.getStoredEnergy(stack) + maxIO / TiabConfig.COMMON.equivalentFeForATick.get());
      } else if (item instanceof ItemTimeInABottleFE) {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(itemEnergy -> {
          if (!isChargingItemFE(energyStorage, itemEnergy)) {
            updateActiveBlockState(false);
            energyToConsume.set(0);
            return;
          }
          updateActiveBlockState(true);
          energyToConsume.set(itemEnergy.receiveEnergy(maxIO, false));
        });
      }

      if (!this.isCreative) {
        ((TileEnergyStorage) energyStorage).consumeEnergy(energyToConsume.get(), false);
      }
    });
  }

  public boolean isChargingItemTime(IEnergyStorage sourceEnergy, ItemStack stack) {
    int storedTime = ((ItemTimeInABottle) stack.getItem()).getStoredEnergy(stack);
    return sourceEnergy.getEnergyStored() > 0 && storedTime < TiabConfig.COMMON.maxStoredTime.get();
  }

  public boolean isChargingItemFE(IEnergyStorage sourceEnergy, IEnergyStorage targetEnergy) {
    return sourceEnergy.getEnergyStored() > 0 && targetEnergy.getEnergyStored() < targetEnergy.getMaxEnergyStored();
  }

  private void updateActiveBlockState(boolean isActive) {
    assert world != null;
    BlockState currentBlockState = world.getBlockState(this.pos);
    BlockState newBlockState = currentBlockState.with(BlockTimeCharger.ACTIVE_STATE, isActive);

    if (!newBlockState.equals(currentBlockState)) {
      final int FLAGS = SetBlockStateFlag.get(SetBlockStateFlag.BLOCK_UPDATE, SetBlockStateFlag.SEND_TO_CLIENTS);
      world.setBlockState(this.pos, newBlockState, FLAGS);
    }
  }

  /* The name is misleading; createMenu has nothing to do with creating a Screen, it is used to create the Container on the server only */
  @Nullable
  @Override
  public Container createMenu(int windowID, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
    assert world != null;
    return new ContainerTimeCharger(this, timeChargerData, windowID, playerInventory, this.inventory.orElse(new ItemStackHandler(1)));
  }

  /* This is where you load the data that you saved in writeToNBT */
  @Override
  public void read(@Nonnull BlockState stateIn, @Nonnull CompoundNBT compound) {
    super.read(stateIn, compound);
    inventory.ifPresent(h -> h.deserializeNBT(compound.getCompound(NBTKeys.INVENTORY)));
    energy.ifPresent(h -> h.deserializeNBT(compound.getCompound(NBTKeys.ENERGY)));
    this.isCreative = compound.getBoolean(NBTKeys.CREATIVE_MARKER);
  }

  /* This is where you save any data that you don't want to lose when the tile entity unloads
   * In this case, it saves the state of the furnace (burn time etc) and the itemstacks stored in the fuel, input, and output slots
   */
  @Override
  @Nonnull
  public CompoundNBT write(@Nonnull CompoundNBT compound) {
    inventory.ifPresent(h -> compound.put(NBTKeys.INVENTORY, h.serializeNBT()));
    energy.ifPresent(h -> compound.put(NBTKeys.ENERGY, h.serializeNBT()));
    compound.putBoolean(NBTKeys.CREATIVE_MARKER, this.isCreative);
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
  @Nonnull
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

  @Override
  public void remove() {
    energy.invalidate();
    inventory.invalidate();
    super.remove();
  }

  public TileEnergyStorage getEnergyStorage() {
    return energyStorage;
  }

  @Override
  @Nonnull
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
