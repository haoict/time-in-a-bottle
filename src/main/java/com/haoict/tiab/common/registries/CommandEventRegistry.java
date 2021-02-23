package com.haoict.tiab.common.registries;

import com.haoict.tiab.common.commands.TiabCommands;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandEventRegistry {
  @SubscribeEvent
  public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
    CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
    TiabCommands.register(commandDispatcher);
  }
}
