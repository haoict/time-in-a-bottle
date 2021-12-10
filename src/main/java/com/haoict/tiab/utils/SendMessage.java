package com.haoict.tiab.utils;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class SendMessage {
    public static void sendStatusMessage(ServerPlayer serverPlayer, String message) {
        serverPlayer.displayClientMessage(new TextComponent(message), true);
    }

    public static void sendMessageToPlayer(ServerPlayer serverPlayer, String message) {
        serverPlayer.sendMessage(new TextComponent(message), serverPlayer.getUUID());
    }
}
