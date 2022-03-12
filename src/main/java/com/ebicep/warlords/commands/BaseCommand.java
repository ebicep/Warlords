package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

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
        return requirePlayer(sender, null);
    }

    @Nullable
    public static Player requirePlayer(@Nonnull CommandSender sender, @Nullable String name) {
        if(name == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command requires a player!");
                return null;
            }
            return (Player)sender;
        } else {
            Player p = Bukkit.getPlayer(name);
            if (p == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
            }
            return p;
        }
    }

    @Nullable
    public static WarlordsPlayer requireWarlordsPlayer(@Nonnull CommandSender sender) {
        return requireWarlordsPlayer(sender, null);
    }
    @Nullable
    public static WarlordsPlayer requireWarlordsPlayer(@Nonnull CommandSender sender, @Nullable String name) {
        Player p = requirePlayer(sender, name);
        if (p == null) {
            return null;
        }
        WarlordsPlayer player = Warlords.getPlayer(p);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "You are not in an active game!");
        }
        return player;
    }

    @Nullable
    public static WarlordsPlayer requireWarlordsPlayerInPrivateGame(@Nonnull CommandSender sender) {
        return requireWarlordsPlayerInPrivateGame(sender, null);
    }
    @Nullable
    public static WarlordsPlayer requireWarlordsPlayerInPrivateGame(@Nonnull CommandSender sender, @Nullable String name) {
        WarlordsPlayer player = requireWarlordsPlayer(sender, name);
        if(player == null) {
            return null;
        }

        if(!player.getGame().getAddons().contains(GameAddon.PRIVATE_GAME)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used in a private game!");
            return null;
        }
        return player;
    }

    @Nullable
    public static Game requireGame(@Nonnull CommandSender sender) {
        return requireGame(sender, null);
    }
    @Nullable
    public static Game requireGame(@Nonnull CommandSender sender, @Nullable String name) {
        Player p = requirePlayer(sender, name);
        if (p == null) {
            return null;
        }
        Optional<Game> playerGame = Warlords.getGameManager().getPlayerGame(((Player)sender).getUniqueId());
        if (!playerGame.isPresent()) {
            sender.sendMessage(ChatColor.RED + "You are not in a game!");
            return null;
        }
        return playerGame.get();
    }

    @Nullable
    public static Game requirePrivateGame(@Nonnull CommandSender sender) {
        return requirePrivateGame(sender, null);
    }
    @Nullable
    public static Game requirePrivateGame(@Nonnull CommandSender sender, @Nullable String name) {
        Game g = requireGame(sender, name);
        if (g == null) {
            return null;
        }
        if(!g.getAddons().contains(GameAddon.PRIVATE_GAME)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used in a private game!");
            return null;
        }
        return g;
    }
}