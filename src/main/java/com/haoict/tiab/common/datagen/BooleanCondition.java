package com.haoict.tiab.common.datagen;

import com.haoict.tiab.config.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class BooleanCondition implements ICondition {
  public static ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "booleancondition");
  public boolean test;

  private String configName;

  public BooleanCondition(String configName) {
    this.configName = configName;
  }

  public String getConfigName() {
    return configName;
  }

  public void setConfigName(String configName) {
    this.configName = configName;
  }

  @Override
  public ResourceLocation getID() {
    return ID;
  }

  @Override
  public boolean test() {
    return this.test;
  }
}
