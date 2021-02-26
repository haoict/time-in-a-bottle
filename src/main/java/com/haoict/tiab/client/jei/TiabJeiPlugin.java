package com.haoict.tiab.client.jei;

import com.haoict.tiab.common.items.ItemBlockTimeCharger;
import com.haoict.tiab.common.items.ItemTimeInABottle;
import com.haoict.tiab.common.items.ItemTimeInABottleFE;
import com.haoict.tiab.common.registries.ItemRegistry;
import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class TiabJeiPlugin implements IModPlugin {
  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(Constants.MOD_ID, "jei_plugin");
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registration) {
    List<Item> gadgets = new ArrayList<Item>() {{
      add(ItemRegistry.BOTTLE_FE.get());
      add(ItemRegistry.TIME_CHARGER.get());
    }};

    for (Item gadget : gadgets) {
      registration.registerSubtypeInterpreter(gadget, itemStack -> {
        if (itemStack.getItem() instanceof ItemTimeInABottle)
          return ISubtypeInterpreter.NONE;

        if (itemStack.getItem() instanceof ItemTimeInABottleFE) {
          double energy = itemStack.getOrCreateTag().getDouble("energy");
          if (energy == 0)
            return "empty";
          else if (energy == TiabConfig.COMMON.maxStoredFE.get())
            return "charged";
        }

        if (itemStack.getItem() instanceof ItemBlockTimeCharger) {
          double energy = itemStack.getOrCreateTag().getDouble("energy");
          if (energy == 0)
            return "empty";
          else if (energy == TiabConfig.COMMON.timeChargerMaxFE.get())
            return "charged";
        }

        return ISubtypeInterpreter.NONE;
      });
    }
  }

}