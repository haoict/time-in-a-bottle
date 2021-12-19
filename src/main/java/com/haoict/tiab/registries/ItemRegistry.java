package com.haoict.tiab.registries;

import com.haoict.tiab.config.Constants;
import com.haoict.tiab.items.TimeInABottleFEItem;
import com.haoict.tiab.items.TimeInABottleItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<Item> timeInABottleItem = ITEMS.register("time_in_a_bottle", TimeInABottleItem::new);
    public static final RegistryObject<Item> timeInABottleFEItem = ITEMS.register("time_in_a_bottle_fe", TimeInABottleFEItem::new);
}
