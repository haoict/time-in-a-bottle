package com.haoict.tiab.common;

public class Config {
  public static final String MOD_ID = "tiab";
  public static final String MOD_NAME = "Time In A Bottle";
  public static final String MOD_VERSION = "0.0.1";
  public static final String MOD_AUTHOR = "Hao Nguyen";
  public static final int TICK_CONST = 20; // 20 ticks per sec
  public static final int EFFECTIVE_EACH_USE_DURATION = 30; // each use lasts 30 seconds
  public static final int MAX_TIME_RATE_POWER = 7; // corresponding to times faster: 2^5=32, 2^11=2048
  public static final int AVERAGE_UPDATE_RANDOM_TICK = 1365; // On average, blocks are updated every 68.27 seconds (1365.33 game ticks)... https://minecraft.gamepedia.com/Tick#Random_tick
  public static final int MAX_STORED_TIME = 622080000; // max stored time in tick
  public static final int MAX_STORED_FE = 622080000;
}
