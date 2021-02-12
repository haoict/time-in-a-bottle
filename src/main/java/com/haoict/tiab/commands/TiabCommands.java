package com.haoict.tiab.commands;

import com.haoict.tiab.Config;
import com.haoict.tiab.item.ItemTimeInABottle;
import com.haoict.tiab.utils.SendMessage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class TiabCommands {
  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    LiteralArgumentBuilder<CommandSource> tiabCommands
        = Commands.literal("tiab")
        .requires((commandSource) -> commandSource.hasPermissionLevel(2))
        .then(Commands.literal("addtime")
            .then(Commands.argument("timeToAdd", MessageArgument.message())
                .executes((ctx) -> {
                  ITextComponent messageValue = MessageArgument.getMessage(ctx, "timeToAdd");
                  CommandSource source = ctx.getSource();
                  ServerPlayerEntity player = source.asPlayer();

                  if (!messageValue.getString().isEmpty()) {
                    try {
                      int timeToAdd = Integer.parseInt(messageValue.getString());

                      if (timeToAdd > Config.MAX_STORED_TIME / 20) {
                        timeToAdd = Config.MAX_STORED_TIME / 20;
                      }
                      ItemStack currentItem = player.inventory.getCurrentItem();

                      if (currentItem.getItem() instanceof ItemTimeInABottle) {
                        ItemTimeInABottle.setStoredTime(currentItem, ItemTimeInABottle.getStoredTime(currentItem) + timeToAdd * Config.TICK_CONST);
                        SendMessage.sendMessage(player, "Added " + timeToAdd + " seconds");
                      } else {
                        SendMessage.sendMessage(player, "You need to hold Time in a bottle to use this command");
                      }
                      return 1;
                    } catch (NumberFormatException ex) {
                      SendMessage.sendMessage(player, "Invalid time parameter!");
                    }
                  } else {
                    SendMessage.sendMessage(player, "Empty time parameter!");
                  }
                  return 0;
                })
            ));

    dispatcher.register(tiabCommands);
  }
}
