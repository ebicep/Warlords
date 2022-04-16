package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MuteCommand implements CommandExecutor {

    public static HashMap<UUID, Boolean> mutedPlayers = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Insufficient Permissions");
            return true;
        }

        if (s.equalsIgnoreCase("mute") || s.equalsIgnoreCase("unmute")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Enter player name");
                return true;
            }
        }

        switch (s.toLowerCase()) {
            case "mute": {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                String name = offlinePlayer.getName();
                UUID uuid = offlinePlayer.getUniqueId();
                if (mutedPlayers.getOrDefault(uuid, false)) {
                    sender.sendMessage(ChatColor.RED + name + " is already muted");
                    return true;
                }
                mutedPlayers.put(uuid, true);
                sender.sendMessage(ChatColor.GREEN + "Muted " + name);
                return true;
            }
            case "unmute": {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                String name = offlinePlayer.getName();
                UUID uuid = offlinePlayer.getUniqueId();
                if (!mutedPlayers.getOrDefault(uuid, false)) {
                    sender.sendMessage(ChatColor.RED + name + " is not muted");
                    return true;
                }
                mutedPlayers.put(uuid, false);
                sender.sendMessage(ChatColor.GREEN + "Unmuted " + name);
                return true;
            }
            case "mutelist": {
                String mutedList = mutedPlayers.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(uuidBooleanEntry -> Bukkit.getOfflinePlayer(uuidBooleanEntry.getKey()).getName())
                        .collect(Collectors.joining(","));
                if (mutedList.isEmpty()) {
                    sender.sendMessage(ChatColor.GREEN + "There are no muted players");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Muted Players: " + ChatColor.AQUA + mutedList);
                }
                return true;
            }
        }

        return true;
    }


    public void register(Warlords instance) {
        instance.getCommand("mute").setExecutor(this);
    }
}
