package com.haoict.tiab.common.registries;

import com.haoict.tiab.common.blocks.BlockTimeCharger;
import com.haoict.tiab.common.container.ContainerTimeCharger;
import com.haoict.tiab.common.tiles.TileTimeCharger;
import com.haoict.tiab.config.Constants;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);
  public static final DeferredRegister<TileEntityType<?>> TILES_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Constants.MOD_ID);
  public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Constants.MOD_ID);

  public static final RegistryObject<Block> TIME_CHARGER = BLOCKS.register("timecharger", BlockTimeCharger::new);
  public static final RegistryObject<ContainerType<ContainerTimeCharger>> TIME_CHARGER_CONTAINER = CONTAINERS.register("timecharger_container", () -> IForgeContainerType.create(ContainerTimeCharger::new));
  public static RegistryObject<TileEntityType<TileTimeCharger>> TIME_CHARGER_TILE = TILES_ENTITIES.register("timecharger_tile", () -> TileEntityType.Builder.create(TileTimeCharger::new, TIME_CHARGER.get()).build(null));

  public static void init() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    BLOCKS.register(eventBus);
    TILES_ENTITIES.register(eventBus);
    CONTAINERS.register(eventBus);
  }
}
