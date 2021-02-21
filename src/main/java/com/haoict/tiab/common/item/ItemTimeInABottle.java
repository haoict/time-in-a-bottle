package com.haoict.tiab.common.item;

import com.haoict.tiab.common.Config;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemTimeInABottle extends AbstractItemTiab {
  private static final String TIME_DATA_TAG = "timeData";
  private static final String STORED_TIME_KEY = "storedTime";

  public ItemTimeInABottle() {
    super();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
    CompoundNBT nbtTagCompound = stack.getOrCreateChildTag(TIME_DATA_TAG);

    int storedTime = nbtTagCompound.getInt(STORED_TIME_KEY);
    int storedSeconds = storedTime / Config.TICK_CONST;
    int hours = storedSeconds / 3600;
    int minutes = (storedSeconds % 3600) / 60;
    int seconds = storedSeconds % 60;

    tooltip.add(new StringTextComponent(I18n.format("item.tiab.timeinabottle.tooltip", hours, String.format("%02d", minutes), String.format("%02d", seconds))));
  }

  @Override
  public void inventoryTick(@Nonnull ItemStack stack,@Nonnull World worldIn, @Nonnull Entity entityIn, int itemSlot, boolean isSelected) {
    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    if (worldIn.isRemote) {
      return;
    }

    if (worldIn.getWorldInfo().getGameTime() % Config.TICK_CONST == 0) {
      CompoundNBT nbtTagCompound = stack.getOrCreateChildTag(TIME_DATA_TAG);
      int storedTime = nbtTagCompound.getInt(STORED_TIME_KEY);
      if (storedTime < Config.MAX_STORED_TIME) {
        nbtTagCompound.putInt(STORED_TIME_KEY, storedTime + Config.TICK_CONST);
      }
    }

    // remove time if player has other TIAB item in his inventory
    if (worldIn.getWorldInfo().getGameTime() % (Config.TICK_CONST * 10) == 0) {
      if (!(entityIn instanceof PlayerEntity)) {
        return;
      }

      PlayerEntity player = (PlayerEntity) entityIn;

      for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
        ItemStack invStack = player.inventory.getStackInSlot(i);
        if (invStack.getItem() == this && invStack != stack) {
          int otherTimeData = invStack.getOrCreateChildTag(TIME_DATA_TAG).getInt(STORED_TIME_KEY);
          int myTimeData = stack.getOrCreateChildTag(TIME_DATA_TAG).getInt(STORED_TIME_KEY);

          if (myTimeData < otherTimeData) {
            setStoredEnergy(stack, 0);
          }
        }
      }
    }
  }

  @Override
  public int getStoredEnergy(ItemStack stack) {
    CompoundNBT timeData = stack.getChildTag(TIME_DATA_TAG);
    return timeData == null ? 0 : timeData.getInt(STORED_TIME_KEY);
  }

  @Override
  public void setStoredEnergy(ItemStack stack, int energy) {
    CompoundNBT timeData = stack.getChildTag(TIME_DATA_TAG);
    if (timeData == null) {
      return;
    }
    int newStoredTime = Math.min(energy, Config.MAX_STORED_TIME);
    timeData.putInt(STORED_TIME_KEY, newStoredTime);
  }

  @Override
  public void applyDamage(ItemStack stack, int damage) {
    setStoredEnergy(stack, getStoredEnergy(stack) - damage);
  }

}