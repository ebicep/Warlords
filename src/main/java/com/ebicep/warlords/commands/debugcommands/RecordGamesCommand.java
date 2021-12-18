package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RecordGamesCommand implements CommandExecutor {

    public static boolean recordGames = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.recordgames")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        recordGames = !recordGames;
        if(recordGames) {
            sender.sendMessage(ChatColor.GREEN + "All games from now on will be recorded!");
        } else {
            sender.sendMessage(ChatColor.RED + "All games from now on will not be recorded!");
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("recordgames").setExecutor(this);
    }

}
