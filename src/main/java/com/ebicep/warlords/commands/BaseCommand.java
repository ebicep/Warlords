package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BaseCommand {

    @Nullable
    public static Player requirePlayerOutsideGame(@Nonnull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command requires a player!");
            return null;
        }
        if (Warlords.hasPlayer((Player)sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot use this command inside a game!");
            return null;
        }
        return (Player)sender;
    }

    @Nullable
    public static Player requirePlayer(@Nonnull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command requires a player!");
            return null;
        }
        return (Player)sender;
    }

    @Nullable
    public static WarlordsPlayer requireWarlordsPlayer(@Nonnull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command requires a player!");
            return null;
        }
        WarlordsPlayer player = Warlords.getPlayer((Player)sender);
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "You are not in an active game!");
        }
        return player;
    }
}