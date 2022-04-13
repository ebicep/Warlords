package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResourcepackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Download Link: https://bit.ly/3J1lGGn");
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("resource").setExecutor(this);
    }
}