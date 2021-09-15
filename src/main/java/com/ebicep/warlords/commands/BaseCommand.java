package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.abilties.FallenSouls;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.maps.state.TimerDebugAble;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.GameMenu.openMainMenu;

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