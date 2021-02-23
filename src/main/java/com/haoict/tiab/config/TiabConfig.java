package com.haoict.tiab.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/*
 * IMPORTANT: DON'T Reformat and rearrange code on this file
 */
// @formatter:off
@Mod.EventBusSubscriber
public class TiabConfig {
  public static final Builder COMMON_BUILDER = new Builder();

  public static final Common COMMON = new Common();

  public static void init() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TiabConfig.COMMON_CONFIG);
  }

  public static final class Common {
    public final IntValue maxTimeRatePower;
    public final IntValue eachUseDuration;
    public final IntValue maxStoredTime;
    public final IntValue maxStoredFE;
    public final IntValue maxFEInput;
    public final IntValue feCostMultiply;
    public final IntValue averageUpdateRandomTick;
    public final IntValue timeChargerMaxFE;
    public final IntValue timeChargerMaxIO;

    private Common() {
      COMMON_BUILDER.push("Time In A Bottle");

      maxTimeRatePower = COMMON_BUILDER
          .comment("Define maximum time the items can be used continuously (0 means 1 time, 7 means 8 times). Corresponding to maximum times faster: Eg. 2^8=256")
          .defineInRange("Max Time Rate Power", 7, 0, 11);

      eachUseDuration = COMMON_BUILDER
          .comment("Define duration for each use - in second")
          .defineInRange("Each Use Duration", 30, 1, 60);

      maxStoredTime = COMMON_BUILDER
          .comment("Define max time the items can store")
          .defineInRange("Max Stored Time", 622080000, 30 * 20, 622080000);

      maxStoredFE = COMMON_BUILDER
          .comment("Define max FE/RF the items can store")
          .defineInRange("Max Stored Energy", 622080000, 30 * 20 * 10, 622080000);

      maxFEInput = COMMON_BUILDER
          .comment("Define max FE/RF input")
          .defineInRange("Max FE Input", 2500, 10, 622080000);

      feCostMultiply = COMMON_BUILDER
          .comment("Multiply cost for FE, to make game balance. 20,000 RF = 1 Coal (as most common mods fuel levels). 30 seconds = 600 ticks. So we set multiply by 10 means 10FE=1 tick, 30 seconds = 6000 FE")
          .defineInRange("Energy Cost Multiply", 10, 1, 1000);

      averageUpdateRandomTick = COMMON_BUILDER
          .comment("Define Average Update Random Tick on block in chunk (eg: sapling growth). On average, blocks are updated every 68.27 seconds (1365.33 game ticks)... https://minecraft.gamepedia.com/Tick#Random_tick")
          .defineInRange("Average Update Random Tick", 1365, 600, 2100);

      COMMON_BUILDER.pop();

      COMMON_BUILDER.push("Time Charger");

      timeChargerMaxFE = COMMON_BUILDER
          .comment("Define max FE/RF it can store")
          .defineInRange("Max FE", 1000000, 1000, 622080000);

      timeChargerMaxIO = COMMON_BUILDER
          .comment("Define max FE/RF input/output")
          .defineInRange("Max FE Input/Output", 2500, 10, 622080000);

      COMMON_BUILDER.pop();
    }
  }

  public static final ForgeConfigSpec COMMON_CONFIG = COMMON_BUILDER.build();
}
// @formatter:on