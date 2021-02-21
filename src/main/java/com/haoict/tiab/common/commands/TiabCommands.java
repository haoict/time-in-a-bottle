package com.haoict.tiab.common.commands;

import com.haoict.tiab.common.items.ItemTimeInABottle;
import com.haoict.tiab.common.utils.SendMessage;
import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
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

                      if (timeToAdd > TiabConfig.COMMON.maxStoredTime.get() / 20) {
                        timeToAdd = TiabConfig.COMMON.maxStoredTime.get() / 20;
                      }
                      ItemStack currentItem = player.inventory.getCurrentItem();
                      Item item = currentItem.getItem();

                      if (item instanceof ItemTimeInABottle) {
                        ItemTimeInABottle itemTiab = (ItemTimeInABottle) item;
                        itemTiab.setStoredEnergy(currentItem, itemTiab.getStoredEnergy(currentItem) + timeToAdd * Constants.TICK_CONST);
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
