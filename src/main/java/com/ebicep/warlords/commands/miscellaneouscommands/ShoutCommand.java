package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShoutCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);
        if (player != null) { // We only have a warlords player if the game is running
            StringBuilder message = new StringBuilder(player.getTeam().teamColor() + "[SHOUT] ");
            message.append(ChatColor.AQUA).append(sender.getName()).append(ChatColor.WHITE).append(": ");
            for (String arg : args) {
                message.append(arg).append(" ");
            }

            for (WarlordsPlayer p : PlayerFilter.playingGame(player.getGame())) {
                p.sendMessage(message.toString());
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("shout").setExecutor(this);
    }
}
