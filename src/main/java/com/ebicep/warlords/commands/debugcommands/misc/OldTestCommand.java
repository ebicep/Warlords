package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OldTestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        Quests quest = Quests.DAILY_300_KA;
        ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                .appendHoverText(ChatColor.GREEN + quest.name, quest.getHoverText())
                .create()
        );

        return true;
    }

}
