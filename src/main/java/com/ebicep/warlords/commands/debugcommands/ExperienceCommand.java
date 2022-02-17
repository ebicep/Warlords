package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ExperienceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.exp.give")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Invalid Arguments");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give": {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid Arguments");
                    return true;
                } else if (args.length == 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid Arguments");
                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid Player");
                    return true;
                }

                if (DatabaseManager.playerService == null) return true;

                Warlords.newChain()
                        .asyncFirst(() -> DatabaseManager.playerService.findByUUID(player.getUniqueId()))
                        .syncLast(databasePlayer -> {
                            databasePlayer.setExperience(databasePlayer.getExperience() + Integer.parseInt(args[2]));
                            DatabaseManager.updatePlayerAsync(databasePlayer);
                            sender.sendMessage(ChatColor.GREEN + "Gave " + player.getName() + " " + args[2] + " experience!");
                            if (player.isOnline()) {
                                player.getPlayer().sendMessage(ChatColor.GREEN + "You received " + args[2] + " experience!");
                            }
                        })
                        .execute();


                return true;
            }
            case "take": {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid Arguments");
                    return true;
                } else if (args.length == 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid Arguments");
                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid Player");
                    return true;
                }

                if (DatabaseManager.playerService == null) return true;

                Warlords.newChain()
                        .asyncFirst(() -> DatabaseManager.playerService.findByUUID(player.getUniqueId()))
                        .syncLast(databasePlayer -> {
                            databasePlayer.setExperience(databasePlayer.getExperience() - Integer.parseInt(args[2]));
                            DatabaseManager.updatePlayerAsync(databasePlayer);
                            sender.sendMessage(ChatColor.RED + "Took " + args[2] + " experience from " + player.getName());
                            if (player.isOnline()) {
                                player.getPlayer().sendMessage(ChatColor.RED + "You lost " + args[2] + " experience!");
                            }
                        })
                        .execute();


                return true;
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("experience").setExecutor(this);
    }

}
