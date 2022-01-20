package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.customentities.npc.traits.GameStartTrait;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.maps.*;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.Weapons;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.OfflinePlayer;

public class GameStartCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if (!sender.hasPermission("warlords.game.start")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        GameMap map;
        MapCategory category;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Game category required!");
            return true;
        } else {
            category = args[0].equals("any") ? null : MapCategory.valueOf(args[0].toUpperCase(Locale.ROOT));
            if (args.length == 1) {
                map = null;
            } else {
                try {
                    map = GameMap.valueOf(args[1].toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + args[1] + " was not found, valid maps: " + Arrays.toString(GameMap.values()));
                    return true;
                }
                if(category != null && !map.getCategories().contains(category)) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not part of the category " + args[0] + " : " + Arrays.toString(GameMap.values()));
                }
            }
        }

        Optional<Party> party = Warlords.partyManager.getPartyFromAny(((Player) sender).getUniqueId());
        List<Player> people = party.map(value -> value.getAllPartyPeoplePlayerOnline()).orElseGet(() -> new ArrayList<>(Bukkit.getOnlinePlayers()));
        if (party.isPresent()) {
            if (!party.get().getPartyLeader().getUuid().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are not the party leader");
                return true;
            } else if (!party.get().allOnlineAndNoAFKs()) {
                sender.sendMessage(ChatColor.RED + "All party members must be online or not afk");
                return true;
            }
        }
        
        GameManager.QueueResult result = Warlords.getGameManager().newEntry(people).setCategory(category).setMap(map).setPriority(-10).queueNow();
        if (!result.isSuccess()) {
            sender.sendMessage(ChatColor.RED + "Failed to join game: " + result.toString());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        return Arrays
                .stream(GameMap.values())
                .map(Enum::name)
                .filter(e -> e.startsWith(args[args.length - 1].toUpperCase(Locale.ROOT)))
                .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

    }

    public void register(Warlords instance) {
        instance.getCommand("start").setExecutor(this);
        instance.getCommand("start").setTabCompleter(this);
    }
}
