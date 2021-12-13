package com.haoict.tiab.commands;

import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
import com.haoict.tiab.items.TimeInABottleItem;
import com.haoict.tiab.utils.SendMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationTargetException;

public class TiabCommands {
    private static final String ADD_TIME_COMMAND = "addTime";
    private static final String REMOVE_TIME_COMMAND = "removeTime";
    private static final String TIME_PARAM = "seconds";

    private static int processTimeCommand(CommandContext<CommandSourceStack> ctx, boolean isAdd) throws CommandSyntaxException, InvocationTargetException, IllegalAccessException {
        Component messageValue = MessageArgument.getMessage(ctx, TIME_PARAM);
        CommandSourceStack source = ctx.getSource();
        ServerPlayer player = source.getPlayerOrException();

        if (!messageValue.getString().isEmpty()) {
            try {
                int timeToAdd = Integer.parseInt(messageValue.getString());

                if (timeToAdd < 0) {
                    throw new NumberFormatException();
                }
                if (timeToAdd > TiabConfig.COMMON.maxStoredTime.get() / Constants.TICK_CONST) {
                    timeToAdd = TiabConfig.COMMON.maxStoredTime.get() / Constants.TICK_CONST;
                }

                boolean success = false;

                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack invStack = player.getInventory().getItem(i);
                    Item item = invStack.getItem();
                    if (item instanceof TimeInABottleItem) {
                        TimeInABottleItem itemTiab = (TimeInABottleItem) item;
                        int currentStoredEnergy = itemTiab.getStoredEnergy(invStack);

                        if (!isAdd) {
                            if (currentStoredEnergy / Constants.TICK_CONST < timeToAdd) {
                                timeToAdd = currentStoredEnergy / Constants.TICK_CONST;
                            }
                            timeToAdd = -timeToAdd;
                        }

                        itemTiab.setStoredEnergy(invStack, currentStoredEnergy + timeToAdd * Constants.TICK_CONST);
                        SendMessage.sendStatusMessage(player,  String.format("%s %d seconds", isAdd ? "Added" : "Removed ", timeToAdd));
                        success = true;
                    }
                }

                if (!success) {
                    SendMessage.sendStatusMessage(player, "No Time in a bottle item in inventory");
                }

                return 1;
            } catch (NumberFormatException ex) {
                SendMessage.sendStatusMessage(player, "Invalid time parameter! (is the number too big?)");
            }
        } else {
            SendMessage.sendStatusMessage(player, "Empty time parameter!");
        }
        return 0;
    }

    public static LiteralArgumentBuilder<CommandSourceStack> addTimeCommand = Commands.literal(ADD_TIME_COMMAND).requires(commandSource -> commandSource.hasPermission(2)).then(Commands.argument(TIME_PARAM, MessageArgument.message()).executes((ctx) -> {
        try {
            return processTimeCommand(ctx, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }));

    public static LiteralArgumentBuilder<CommandSourceStack> removeTimeCommand = Commands.literal(REMOVE_TIME_COMMAND).requires(commandSource -> commandSource.hasPermission(2)).then(Commands.argument(TIME_PARAM, MessageArgument.message()).executes((ctx) -> {
        try {
            return processTimeCommand(ctx, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }));

}
