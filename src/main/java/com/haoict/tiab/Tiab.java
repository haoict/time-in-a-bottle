package com.haoict.tiab;

import com.haoict.tiab.client.ClientProxy;
import com.haoict.tiab.common.registries.CommandEventRegistry;
import com.haoict.tiab.common.CommonProxy;
import com.haoict.tiab.common.registries.ItemRegistry;
import com.haoict.tiab.common.entities.TiabEntityTypes;
import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MOD_ID)
public class Tiab {
  // Directly reference a log4j logger.
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Register our creative tab. Notice that we're also modifying the NBT data of the
   * building gadget to remove the damage / energy indicator from the creative
   * tabs icon.
   */
  public static final ItemGroup TIAB_ITEM_GROUP = new ItemGroup(Constants.MOD_ID) {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(ItemRegistry.BOTTLE.get());
    }
  };

  public Tiab() {
    TiabConfig.init();

    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the enqueueIMC method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    // Register the processIMC method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
    // Register the doClientStuff method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);

    ItemRegistry.init();

    DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    MinecraftForge.EVENT_BUS.register(CommandEventRegistry.class);
  }

  private void setup(final FMLCommonSetupEvent event) {
    // some preinit code
    LOGGER.info("HELLO FROM PREINIT");
    LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    // do something that can only be done on the client
    LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
  }

  private void enqueueIMC(final InterModEnqueueEvent event) {
    // some example code to dispatch IMC to another mod
    InterModComms.sendTo(Constants.MOD_ID, "helloworld", () -> {
      LOGGER.info("Hello world from the MDK");
      return "Hello world";
    });
  }

  private void processIMC(final InterModProcessEvent event) {
    // some example code to receive and process InterModComms from other mods
    LOGGER.info("Got IMC {}", event.getIMCStream().
        map(m -> m.getMessageSupplier().get()).
        collect(Collectors.toList()));
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(FMLServerStartingEvent event) {
    // do something when the server starts
    LOGGER.info("HELLO from server starting");
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
      // register a new block here
      LOGGER.info("HELLO from Register Block");
    }

    @SubscribeEvent
    public static void onEntityTypeRegistration(RegistryEvent.Register<EntityType<?>> entityTypeRegisterEvent) {
      TiabEntityTypes.timeAcceleratorEntityType.setRegistryName("tiab:time_accelerator_entity_type");
      entityTypeRegisterEvent.getRegistry().register(TiabEntityTypes.timeAcceleratorEntityType);
    }
  }
}
