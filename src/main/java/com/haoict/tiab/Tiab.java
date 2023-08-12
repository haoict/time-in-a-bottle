package com.haoict.tiab;

import com.haoict.tiab.client.renderer.TimeAcceleratorEntityRenderer;
import com.haoict.tiab.commands.TiabCommands;
import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
import com.haoict.tiab.registries.EntityTypeRegistry;
import com.haoict.tiab.registries.ItemRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MOD_ID)
public class Tiab
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Tiab()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TiabConfig.COMMON_CONFIG);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::CreativeTab);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ItemRegistry.ITEMS.register(modEventBus);
        EntityTypeRegistry.TILE_ENTITIES.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        EntityRenderers.register(EntityTypeRegistry.timeAcceleratorEntityType.get(), TimeAcceleratorEntityRenderer::new);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        event.getServer().getCommands().getDispatcher().register(Commands.literal(Constants.MOD_ID).then(TiabCommands.addTimeCommand).then(TiabCommands.removeTimeCommand));
    }

    public void CreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.TOOLS_AND_UTILITIES) return;
        event.accept(ItemRegistry.timeInABottleItem);
    }
}
