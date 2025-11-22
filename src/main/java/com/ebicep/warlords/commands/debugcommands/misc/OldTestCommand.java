package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.util.chat.ChatChannels;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OldTestCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender commandSender = commandSourceStack.getSender();
        if (commandSender instanceof Player player) {
            if (!player.isOp()) {
                return;
            }
        }
        ChatChannels.sendDebugMessage(commandSender instanceof Player player ? player : null, "Executed OldTestCommand");
    }

}
