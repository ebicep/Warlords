package com.ebicep.warlords.commands.debugcommands.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnstuckCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);
        if (player != null) {
            if (Warlords.getPlayer(player) != null && Warlords.getPlayer(player).getGame().isFrozen()) {
                sender.sendMessage(ChatColor.RED + "You cannot use this command while the game is frozen!");
                return true;
            }
            player.teleport(player.getLocation().add(0, 1, 0));
            sender.sendMessage(ChatColor.GREEN + "You were teleported 1 block upwards.");
            System.out.println(ChatColor.RED + "[DEBUG] " + sender.getName() + " used unstuck command.");
            return true;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("unstuck").setExecutor(this);
    }
}
