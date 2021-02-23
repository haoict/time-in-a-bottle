package com.haoict.tiab.common.items;

import com.haoict.tiab.common.entities.EntityTimeAccelerator;
import com.haoict.tiab.common.utils.PlaySound;
import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class AbstractItemTiab extends Item {
  private static final int THIRTY_SECONDS = Constants.TICK_CONST * TiabConfig.COMMON.eachUseDuration.get();
  private static final String[] NOTES = {"C", "D", "E", "F", "G2", "A2", "B2", "C2", "D2", "E2", "F2"};

  public AbstractItemTiab() {
    super(new Properties().group(ItemGroup.MISC).maxStackSize(1));
  }

  @Override
  @Nonnull
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();

    if (world.isRemote) {
      return ActionResultType.PASS;
    }

    BlockPos pos = context.getPos();
    BlockState blockState = world.getBlockState(pos);
    TileEntity targetTE = world.getTileEntity(pos);
    ItemStack stack = context.getItem();
    PlayerEntity player = context.getPlayer();

    if (!blockState.ticksRandomly() && (targetTE == null || !(targetTE instanceof ITickableTileEntity))) {
      return ActionResultType.FAIL;
    }

    int nextRate = 1;
    int energyRequired = getEnergyCost(nextRate);
    boolean isCreativeMode = player != null && player.abilities.isCreativeMode;

    Optional<EntityTimeAccelerator> o = context.getWorld().getEntitiesWithinAABB(EntityTimeAccelerator.class, new AxisAlignedBB(pos).shrink(0.2)).stream().findFirst();

    if (o.isPresent()) {
      EntityTimeAccelerator entityTA = o.get();
      int currentRate = entityTA.getTimeRate();
      int usedUpTime = THIRTY_SECONDS - entityTA.getRemainingTime();

      if (currentRate >= Math.pow(2, TiabConfig.COMMON.maxTimeRatePower.get())) {
        return ActionResultType.SUCCESS;
      }

      nextRate = currentRate * 2;
      int timeAdded = usedUpTime / 2;
      energyRequired = getEnergyCost(nextRate);

      if (!canUse(stack, isCreativeMode, energyRequired)) {
        return ActionResultType.SUCCESS;
      }

      entityTA.setTimeRate(nextRate);
      entityTA.setRemainingTime(entityTA.getRemainingTime() + timeAdded);
    } else {
      // First use
      if (!canUse(stack, isCreativeMode, energyRequired)) {
        return ActionResultType.SUCCESS;
      }

      EntityTimeAccelerator entityTA = new EntityTimeAccelerator(context.getWorld(), pos, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      entityTA.setRemainingTime(THIRTY_SECONDS);
      world.addEntity(entityTA);
    }

    if (!isCreativeMode) {
      this.applyDamage(stack, energyRequired);
    }
    playSound(world, pos, nextRate);

    return ActionResultType.SUCCESS;
  }

  public int getEnergyCost(int timeRate) {
    if (timeRate <= 1) return THIRTY_SECONDS;
    return timeRate / 2 * THIRTY_SECONDS;
  }

  public boolean canUse(ItemStack stack, boolean isCreativeMode, int energyRequired) {
    return getStoredEnergy(stack) >= energyRequired || isCreativeMode;
  }

  protected abstract int getStoredEnergy(ItemStack stack);

  protected abstract void setStoredEnergy(ItemStack stack, int energy);

  protected abstract void applyDamage(ItemStack stack, int damage);

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
      default:
        PlaySound.playNoteBlockHarpSound(world, pos, NOTES[10]);
    }
  }

  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return !ItemStack.areItemsEqual(oldStack, newStack);
  }
}
