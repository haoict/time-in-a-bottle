package com.haoict.tiab.common.registries;

import com.haoict.tiab.Tiab;
import com.haoict.tiab.common.items.ItemTimeInABottle;
import com.haoict.tiab.config.Constants;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
  // create DeferredRegister object
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
  // register items
  public static final RegistryObject<Item> BOTTLE = ITEMS.register("timeinabottle", () -> new ItemTimeInABottle());
  public static void init() {
    // attach DeferredRegister to the event bus
    ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
  }
}
