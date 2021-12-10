package com.haoict.tiab.items;

import com.haoict.tiab.registries.ItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class TiabCreativeTab extends CreativeModeTab {

    public static TiabCreativeTab TAB = new TiabCreativeTab();

    private TiabCreativeTab() {
        super("tiab_creative_tab_name");
    }

    @Override
    @Nonnull
    public ItemStack makeIcon() {
        return new ItemStack(ItemRegistry.timeInABottleItem.get());
    }
}
