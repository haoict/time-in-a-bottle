package com.haoict.tiab.common.commands;

import com.haoict.tiab.common.Config;
import com.haoict.tiab.common.item.ItemTimeInABottle;
import com.haoict.tiab.common.item.ItemTimeInABottleFE;
import com.haoict.tiab.common.utils.SendMessage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
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
                      Item item = currentItem.getItem();

                      if (item instanceof ItemTimeInABottle) {
                        ItemTimeInABottle itemTiab = (ItemTimeInABottle) item;
                        itemTiab.setStoredEnergy(currentItem, itemTiab.getStoredEnergy(currentItem) + timeToAdd * Config.TICK_CONST);
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
            ))
        .then(Commands.literal("addFE")
            .then(Commands.argument("feToAdd", MessageArgument.message())
                    .executes((ctx) -> {
                      ITextComponent messageValue = MessageArgument.getMessage(ctx, "feToAdd");
                      CommandSource source = ctx.getSource();
                      ServerPlayerEntity player = source.asPlayer();

                      if (!messageValue.getString().isEmpty()) {
                        try {
                          int feToAdd = Integer.parseInt(messageValue.getString());

                          if (feToAdd > Config.MAX_STORED_FE) {
                            feToAdd = Config.MAX_STORED_FE;
                          }
                          ItemStack currentItem = player.inventory.getCurrentItem();
                          Item item = currentItem.getItem();

                          if (item instanceof ItemTimeInABottleFE) {
                            ItemTimeInABottleFE itemTiab = (ItemTimeInABottleFE) item;
                            itemTiab.setStoredEnergy(currentItem, feToAdd);
                            SendMessage.sendMessage(player, "Added " + feToAdd + " FE");
                          } else {
                            SendMessage.sendMessage(player, "You need to hold Time in a bottle FE to use this command");
                          }
                          return 1;
                        } catch (NumberFormatException ex) {
                          SendMessage.sendMessage(player, "Invalid FE parameter!");
                        }
                      } else {
                        SendMessage.sendMessage(player, "Empty FE parameter!");
                      }
                      return 0;
                    })
            ));

    dispatcher.register(tiabCommands);
  }
}
