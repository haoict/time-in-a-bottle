package com.haoict.tiab.common.items;

import com.haoict.tiab.common.tiles.TileTimeCharger;
import com.haoict.tiab.common.utils.Helpers;
import com.haoict.tiab.config.NBTKeys;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockTimeCharger extends BlockItem {

  public ItemBlockTimeCharger(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  @Override
  public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
    if (!TiabConfig.COMMON.enableTimeCharger.get()) {
      return;
    }
    super.fillItemGroup(group, items);
    if (!isInGroup(group))
      return;

    ItemStack charged = new ItemStack(this);
    charged.getOrCreateTag().putDouble(NBTKeys.ENERGY, TiabConfig.COMMON.timeChargerMaxFE.get());
    charged.getOrCreateTag().putBoolean(NBTKeys.CREATIVE_MARKER, true);
    charged.setDisplayName(new TranslationTextComponent("block.tiab.timecharger.creative"));
    items.add(charged);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);

    if (stack.getOrCreateTag().getBoolean(NBTKeys.CREATIVE_MARKER)) {
      tooltip.add(new TranslationTextComponent("screen.tiab.timecharger.energy", "Infinity", "Infinity").mergeStyle(TextFormatting.GREEN));
      return;
    }

    int power = stack.getOrCreateTag().getInt(NBTKeys.ENERGY);
    if (power == 0) {
      return;
    }

    tooltip.add(new TranslationTextComponent("screen.tiab.timecharger.energy", Helpers.withSuffix(power), Helpers.withSuffix(TiabConfig.COMMON.timeChargerMaxFE.get())).mergeStyle(TextFormatting.GREEN));
  }

  @Override
  protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof TileTimeCharger) {
      TileTimeCharger timeCharger = (TileTimeCharger) te;
      timeCharger.getEnergyStorage().setEnergy(stack.getOrCreateTag().getInt(NBTKeys.ENERGY));
      if (stack.getOrCreateTag().getBoolean(NBTKeys.CREATIVE_MARKER)) {
        timeCharger.setCreative(true);
      }
    }

    return super.onBlockPlaced(pos, worldIn, player, stack, state);
  }

}
