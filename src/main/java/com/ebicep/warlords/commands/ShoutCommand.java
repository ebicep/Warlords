package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShoutCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);
        if (player != null) { // We only have a warlords player if the game is running
            String message = player.getTeam().teamColor() + "[SHOUT] ";
            message += ChatColor.AQUA + sender.getName() + ChatColor.WHITE + ": ";
            for (String arg : args) {
                message += arg + " "; // TODO use a stringbuilder
            }

            for (WarlordsPlayer p : PlayerFilter.playingGame(player.getGame()).aliveTeammatesOf(player)) {
                p.sendMessage(message);
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("shout").setExecutor(this);
    }
}
