package com.haoict.tiab.common.datagen;

import com.haoict.tiab.config.Constants;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGen {
  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();

    if (event.includeServer()) {
      generator.addProvider(new DataGenRecipes(generator));
      //  generator.addProvider(new GeneratorLoots(generator));
    }

    if (event.includeClient()) {
      // generator.addProvider(new GeneratorLanguage(generator));
      // generator.addProvider(new GeneratorBlockStates(generator, event.getExistingFileHelper()));
      // generator.addProvider(new GeneratorItemModels(generator, event.getExistingFileHelper()));
    }
  }

}
