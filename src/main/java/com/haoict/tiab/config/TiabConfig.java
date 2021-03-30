package com.haoict.tiab.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
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
    public final IntValue averageUpdateRandomTick;
    public final BooleanValue enableTimeInABottleFE;
    public final IntValue maxStoredFE;
    public final IntValue maxFEInput;
    public final IntValue equivalentFeForATick;
    public final BooleanValue enableTimeCharger;
    public final IntValue timeChargerMaxFE;
    public final IntValue timeChargerMaxIO;

    private Common() {
      COMMON_BUILDER.push("Time In A Bottle");

      maxTimeRatePower = COMMON_BUILDER
          .comment("Define maximum time the items can be used continuously. Corresponding to maximum times faster: Eg. 2^8=256")
          .defineInRange("Max Time Rate Power", 8, 1, 12);

      eachUseDuration = COMMON_BUILDER
          .comment("Define duration for each use - in second")
          .defineInRange("Each Use Duration", 30, 1, 60);

      averageUpdateRandomTick = COMMON_BUILDER
          .comment("Define Average Update Random Tick on block in chunk (eg: sapling growth). On average, blocks are updated every 68.27 seconds (1365.33 game ticks)... https://minecraft.gamepedia.com/Tick#Random_tick")
          .defineInRange("Average Update Random Tick", 1365, 600, 2100);

      maxStoredTime = COMMON_BUILDER
          .comment("Define max time the items can store - in tick (1 second = 20 ticks)")
          .defineInRange("Max Stored Time", 622080000, 30 * 20, 622080000);

      COMMON_BUILDER.pop();

      COMMON_BUILDER.push("Time In A Bottle FE");

      enableTimeInABottleFE = COMMON_BUILDER
          .comment("Enable an item like the Time In A Bottle item but use FE/RF instead of time")
          .define("Enable Time In A Bottle FE", false);

      maxStoredFE = COMMON_BUILDER
          .comment("Define max FE/RF the items can store")
          .defineInRange("Max Stored Energy", 622080000, 30 * 20 * 10, 622080000);

      maxFEInput = COMMON_BUILDER
          .comment("Define max FE/RF input")
          .defineInRange("Max FE Input", 2500, 10, 622080000);

      equivalentFeForATick = COMMON_BUILDER
          .comment("Equivalent cost for FE per game tick, to make game balance. 1 tick = 10 FE -> 30 seconds = 600 ticks = 6000 FE")
          .defineInRange("Equivalent FE for a tick", 10, 1, 1000);

      COMMON_BUILDER.pop();

      COMMON_BUILDER.push("Time Charger Block");

      enableTimeCharger = COMMON_BUILDER
          .comment("Enable a Block to charge time for the Time In A Bottle item")
          .define("Enable Time Charger", false);

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