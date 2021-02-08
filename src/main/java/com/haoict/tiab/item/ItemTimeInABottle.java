package com.haoict.tiab.item;

import com.haoict.tiab.Config;
import com.haoict.tiab.entities.EntityTimeAccelerator;
import com.haoict.tiab.utils.PlaySound;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemTimeInABottle extends Item {
  // Happy birthday notes
  private static final String[] NOTES = {"G", "G", "A", "G", "C", "B", "G", "G", "A", "G", "D", "C"};
  private static final int THIRTY_SECONDS = Config.TICK_CONST * Config.EFFECTIVE_EACH_USE_DURATION;
  private static final String TIME_DATA_TAG = "timeData";
  private static final String STORED_TIME_KEY = "storedTime";

  public ItemTimeInABottle() {
    super(new Properties().group(ItemGroup.MISC).maxStackSize(1));
  }

  public static int getStoredTime(ItemStack is) {
    CompoundNBT timeData = is.getChildTag(TIME_DATA_TAG);
    int timeAvailable = timeData.getInt(STORED_TIME_KEY);
    return timeAvailable;
  }

  public static void setStoredTime(ItemStack is, int time) {
    CompoundNBT timeData = is.getChildTag(TIME_DATA_TAG);
    int newStoredTime = time > 622080000 ? 622080000 : time;
    timeData.putInt(STORED_TIME_KEY, newStoredTime);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    CompoundNBT nbtTagCompound = stack.getOrCreateChildTag(TIME_DATA_TAG);

    int storedTime = nbtTagCompound.getInt(STORED_TIME_KEY);
    int storedSeconds = storedTime / Config.TICK_CONST;
    int hours = storedSeconds / 3600;
    int minutes = (storedSeconds % 3600) / 60;
    int seconds = storedSeconds % 60;

    tooltip.add(new StringTextComponent(I18n.format("item.tiab.timeinabottle.desc", hours, String.format("%02d", minutes), String.format("%02d", seconds))));
  }

  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    if (worldIn.isRemote) {
      return;
    }

    if (worldIn.getWorldInfo().getGameTime() % Config.TICK_CONST == 0) {
      CompoundNBT nbtTagCompound = stack.getOrCreateChildTag(TIME_DATA_TAG);
      int storedTime = nbtTagCompound.getInt(STORED_TIME_KEY);
      if (storedTime < 622080000) {
        nbtTagCompound.putInt(STORED_TIME_KEY, storedTime + 20);
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
            setStoredTime(stack, 0);
          }
        }
      }
    }
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();

    if (world.isRemote) {
      return ActionResultType.PASS;
    }

    BlockPos pos = context.getPos();
    BlockState blockState = world.getBlockState(pos);
    TileEntity targetTE = world.getTileEntity(pos);

    if (!blockState.ticksRandomly() && (targetTE == null || !(targetTE instanceof ITickableTileEntity))) {
      return ActionResultType.FAIL;
    }

    int nextRate = 1;
    int timeRequired = THIRTY_SECONDS;
    int timeAvailable = getStoredTime(context.getItem());
    boolean isCreativeMode = context.getPlayer().abilities.isCreativeMode;

    Optional<EntityTimeAccelerator> o = context.getWorld().getEntitiesWithinAABB(EntityTimeAccelerator.class, new AxisAlignedBB(pos).shrink(0.2)).stream().findFirst();

    if (o.isPresent()) {
      EntityTimeAccelerator entityTA = o.get();
      int currentRate = entityTA.getTimeRate();
      int usedUpTime = THIRTY_SECONDS - entityTA.getRemainingTime();

      if (currentRate >= Math.pow(2, Config.MAX_TIME_RATE_POWER)) {
        return ActionResultType.SUCCESS;
      }

      nextRate = currentRate * 2;
      int timeAdded = usedUpTime / 2;
      timeRequired = nextRate / 2 * THIRTY_SECONDS;

      if (timeAvailable < timeRequired && !isCreativeMode) {
        return ActionResultType.SUCCESS;
      }

      entityTA.setTimeRate(nextRate);
      entityTA.setRemainingTime(entityTA.getRemainingTime() + timeAdded);
    } else {
      // First use
      if (timeAvailable < THIRTY_SECONDS && !isCreativeMode) {
        return ActionResultType.SUCCESS;
      }

      EntityTimeAccelerator entityTA = new EntityTimeAccelerator(context.getWorld(), pos, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      entityTA.setRemainingTime(THIRTY_SECONDS);
      world.addEntity(entityTA);
    }

    if (!isCreativeMode) {
      setStoredTime(context.getItem(), timeAvailable - timeRequired);
    }
    playSound(world, pos, nextRate);

    return ActionResultType.SUCCESS;
  }

  public void playSound(World world, BlockPos pos, int nextRate) {
    switch (nextRate) {
      case 1:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[0]);
        break;
      case 2:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[1]);
        break;
      case 4:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[2]);
        break;
      case 8:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[3]);
        break;
      case 16:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[4]);
        break;
      case 32:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[5]);
        break;
      case 64:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[6]);
        break;
      case 128:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[7]);
        break;
      case 256:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[8]);
        break;
      case 512:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[9]);
        break;
      case 1024:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[10]);
        break;
      case 2048:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[11]);
        break;
    }
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return !ItemStack.areItemsEqual(oldStack, newStack);
  }
}
