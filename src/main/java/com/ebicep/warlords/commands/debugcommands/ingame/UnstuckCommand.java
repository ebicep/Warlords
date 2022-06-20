package com.ebicep.warlords.commands.debugcommands.ingame;

import com.ebicep.warlords.Warlords;
import net.citizensnpcs.npc.ai.speech.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UnstuckCommand implements CommandExecutor {

    private boolean onCooldown = true;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        if (onCooldown) {
            if (player != null) {
                player.teleport(player.getLocation().add(0, 1, 0));
                sender.sendMessage(ChatColor.GREEN + "You were teleported 1 block upwards.");
                System.out.println(ChatColor.RED + "[DEBUG] " + sender.getName() + " used unstuck command.");
                resetCooldown();
                return true;
            } else {
                System.out.print("This command requires a player.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Please wait 5 seconds before using the command again!");
        }

        return true;
    }

    private void resetCooldown() {
        onCooldown = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                onCooldown = true;
            }
        }.runTaskLater(Warlords.getInstance(), 100);
    }

    public void register(Warlords instance) {
        instance.getCommand("unstuck").setExecutor(this);
    }
}
