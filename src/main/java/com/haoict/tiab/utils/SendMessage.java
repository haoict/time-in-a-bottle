package com.haoict.tiab.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class SendMessage {
  public static ClientPlayerEntity player = Minecraft.getInstance().player;

  public static void sendStatusMessage(String message) {
    player.sendStatusMessage(new StringTextComponent(message), true);
  }

  public static void sendMessage(String message) {
    player.sendMessage(new StringTextComponent(message), player.getUniqueID());
  }
}
