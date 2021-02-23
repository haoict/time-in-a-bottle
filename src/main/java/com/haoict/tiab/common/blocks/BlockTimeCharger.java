package com.haoict.tiab.common.blocks;

import com.haoict.tiab.common.items.ItemBlockTimeCharger;
import com.haoict.tiab.common.registries.BlockRegistry;
import com.haoict.tiab.common.tiles.TileTimeCharger;
import com.haoict.tiab.config.NBTKeys;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class BlockTimeCharger extends Block {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public BlockTimeCharger() {
    super(Properties.create(Material.ROCK).hardnessAndResistance(5.0f, 6.0f).harvestLevel(1).harvestTool(ToolType.PICKAXE));
    setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    super.fillStateContainer(builder);
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return BlockRegistry.TIME_CHARGER_TILE.get().create();
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  @SuppressWarnings("deprecation")
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    // Only execute on the server
    if (worldIn.isRemote)
      return ActionResultType.SUCCESS;

    TileEntity te = worldIn.getTileEntity(pos);
    if (!(te instanceof TileTimeCharger))
      return ActionResultType.FAIL;

    NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
    return ActionResultType.SUCCESS;
  }

  @Override
  @SuppressWarnings("deprecation")
  public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);

    List<ItemStack> drops = super.getDrops(state, builder);
    if (te instanceof TileTimeCharger) {
      TileTimeCharger tileEntity = (TileTimeCharger) te;
      drops.stream()
          .filter(e -> e.getItem() instanceof ItemBlockTimeCharger)
          .findFirst()
          .ifPresent(e -> {
            e.getOrCreateTag().putInt(NBTKeys.ENERGY, tileEntity.getEnergyStorage().getEnergyStored());
            if (tileEntity.isCreative()) {
              e.getOrCreateTag().putBoolean(NBTKeys.CREATIVE_MARKER, true);
              e.setDisplayName(new TranslationTextComponent("block.tiab.timecharger.creative"));
            }
          });
    }

    return drops;
  }

  @Override
  @SuppressWarnings("deprecation")
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (newState.getBlock() != this) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity != null) {
        LazyOptional<IItemHandler> cap = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        cap.ifPresent(handler -> {
          for (int i = 0; i < handler.getSlots(); i++)
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
        });
      }
      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }
}
