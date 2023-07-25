package com.haoict.tiab.api;

import com.haoict.tiab.config.Constants;
import com.haoict.tiab.registries.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

/**
  * Used to get The Registry Object for TIAB without needing to try to get it
  * This is for mods wanting it without worrying about anything changing
 *  And for anything useful mods may need.
 */
public class TimeInABottleAPI {
    public static RegistryObject<Item> getRegistryItem() {
        return ItemRegistry.timeInABottleItem;
    }

    public static String getModID() {
        return Constants.MOD_ID;
    }
}
