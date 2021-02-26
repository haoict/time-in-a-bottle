package com.haoict.tiab.common.datagen;

import com.haoict.tiab.common.registries.BlockRegistry;
import com.haoict.tiab.common.registries.ItemRegistry;
import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.function.Consumer;

public class DataGenRecipes extends RecipeProvider {
  public DataGenRecipes(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "timecharger");

    Block block = BlockRegistry.TIME_CHARGER.get();
    ConditionalRecipe.builder().addCondition(new BooleanCondition("enableTimeCharger")).addRecipe(
        ShapedRecipeBuilder
            .shapedRecipe(block)
            .key('I', Tags.Items.INGOTS_IRON)
            .key('R', Tags.Items.DUSTS_REDSTONE)
            .key('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
            .key('L', Tags.Items.GEMS_LAPIS)
            .patternLine("IRI")
            .patternLine("LRL")
            .patternLine("IDI")
            .addCriterion("has_diamonds", hasItem(Tags.Items.GEMS_DIAMOND))
            ::build
    ).build(consumer, ID);

    ID = new ResourceLocation(Constants.MOD_ID, "timeinabottlefe");
    Item timeInABottleFE = ItemRegistry.BOTTLE_FE.get();
    ConditionalRecipe.builder().addCondition(new BooleanCondition("enableTimeInABottleFE")).addRecipe(
        ShapedRecipeBuilder
            .shapedRecipe(timeInABottleFE)
            .key('G', Tags.Items.INGOTS_GOLD)
            .key('D', Tags.Items.GEMS_DIAMOND)
            .key('R', Tags.Items.DUSTS_REDSTONE)
            .key('C', Items.CLOCK)
            .key('B', Items.GLASS_BOTTLE)
            .patternLine("GGG")
            .patternLine("DCD")
            .patternLine("RBR")
            .addCriterion("has_diamonds", hasItem(Tags.Items.GEMS_DIAMOND))
            ::build
    ).build(consumer, ID);
  }
}
