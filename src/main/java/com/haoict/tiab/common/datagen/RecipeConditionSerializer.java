package com.haoict.tiab.common.datagen;

import com.google.gson.JsonObject;
import com.haoict.tiab.config.TiabConfig;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.lang.reflect.Field;

public class RecipeConditionSerializer implements IConditionSerializer<BooleanCondition> {
  @Override
  public void write(JsonObject json, BooleanCondition booleanCondition) {
    json.addProperty("configName", booleanCondition.getConfigName());
  }

  @Override
  public BooleanCondition read(JsonObject json) {
    String configName = JSONUtils.getString(json, "configName");

    Boolean isOk = false;
    try {
      Field f1 = TiabConfig.COMMON.getClass().getField(configName);
      isOk = ((ForgeConfigSpec.BooleanValue) f1.get(TiabConfig.COMMON)).get();
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }

    BooleanCondition con = new BooleanCondition(configName);
    con.test = isOk;
    return con;
  }

  @Override
  public ResourceLocation getID() {
    return BooleanCondition.ID;
  }
}
