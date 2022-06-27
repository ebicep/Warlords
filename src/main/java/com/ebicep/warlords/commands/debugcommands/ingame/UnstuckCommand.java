package com.ebicep.warlords.commands.debugcommands.ingame;

import com.ebicep.warlords.Warlords;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnstuckCommand implements CommandExecutor {

    private boolean onCooldown = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        if (player != null) {
            player.teleport(player.getLocation().add(0, 1, 0));
            sender.sendMessage(ChatColor.GREEN + "You were teleported 1 block upwards.");
            System.out.println(ChatColor.RED + "[DEBUG] " + sender.getName() + " used unstuck command.");
            return true;
        } else {
            System.out.print("This command requires a player.");
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("unstuck").setExecutor(this);
    }
}
