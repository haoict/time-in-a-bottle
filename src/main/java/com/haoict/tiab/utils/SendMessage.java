package com.haoict.tiab.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class SendMessage {
  public static void sendStatusMessage(String message) {
    Minecraft.getInstance().player.sendStatusMessage(new StringTextComponent(message), true);
  }

  public static void sendMessage(String message) {
    Minecraft.getInstance().player.sendMessage(new StringTextComponent(message), Minecraft.getInstance().player.getUniqueID());
  }

  public static void sendMessage(ServerPlayerEntity serverPlayer, String message) {
    serverPlayer.sendMessage(new StringTextComponent(message), serverPlayer.getUniqueID());
  }
}
