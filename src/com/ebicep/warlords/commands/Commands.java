package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Game.State;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.util.Classes;
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

import static com.ebicep.warlords.maps.Game.State.GAME;
import static com.ebicep.warlords.menu.GameMenu.openMainMenu;

public class Commands implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("start")) {
            if (!sender.isOp()) {
                sender.sendMessage("§cYou do not have permission to do that.");
                return true;
            }
            Game game = Warlords.game; // In the future allow the user to select a game player
            GameMap map;

            if (args.length == 0) {
                map = null;
            } else if (args[0].equalsIgnoreCase("random")) {
                Random random = new Random();
                GameMap[] values = GameMap.values();
                map = values[random.nextInt(values.length)];
            } else {
                try {
                    map = GameMap.valueOf(args[0].toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + args[0] + " was not found, valid maps: " + Arrays.toString(GameMap.values()));
                    return true;
                }
            }

            if(game.getState() != State.PRE_GAME) {
                sender.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }

            for (Player player : game.clearAllPlayers()) {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
            game.resetTimer();
            if (map != null) {
                if (game.getMap() != map) {
                    game.changeMap(map);
                }
                sender.sendMessage(ChatColor.GREEN + "Changing map to " + map.getMapName());
            }
            Collection<? extends Player> online = Bukkit.getOnlinePlayers();
            if (online.size() < game.getMap().getMinPlayers()) {
                sender.sendMessage(ChatColor.RED + "The map '" + game.getMap().getMapName() + "' requires " + game.getMap().getMinPlayers() + " players to start");
                return true;
            }

            List<Player> people = new ArrayList<>(online);
            //Collections.shuffle(people);
            boolean teamBlueAssessment = true;
            for (Player player : people) {
                player.getInventory().setItem(5, new ItemBuilder(Material.NOTE_BLOCK)
                        .name(ChatColor.GREEN + "Team Selector " + ChatColor.GRAY + "(Right-Click)")
                        .lore(ChatColor.YELLOW + "Click to select your team!")
                        .get());
                player.getInventory().setItem(6, new ItemBuilder(Material.NETHER_STAR)
                        .name(ChatColor.AQUA + "Pre-game Menu ")
                        .lore(ChatColor.GRAY + "Allows you to change your class, select a\n" + ChatColor.GRAY + "weapon, and edit your settings.")
                        .get());
                Warlords.game.addPlayer(player, teamBlueAssessment);
                game.giveLobbyScoreboard(player);
                teamBlueAssessment = !teamBlueAssessment;
            }


        } else if (command.getName().equalsIgnoreCase("endgame")) {

            if (!sender.isOp()) {
                sender.sendMessage("§cYou do not have permission to do that.");
                return true;
            }
            if (Warlords.game.getState() != GAME) {
                sender.sendMessage(ChatColor.RED + "There are no games currently running!");
                return true;
            }

            Warlords.game.forceDraw();
            sender.sendMessage(ChatColor.RED + "Game has been terminated. Warping back to lobby...");

        } else if (command.getName().equalsIgnoreCase("class")) {

            if (Warlords.game.getState() == GAME) {
                sender.sendMessage(ChatColor.RED + "You cannot do that while the game is running!");
                return true;
            }

            if (!(sender instanceof Player)) {
                return true;
            }

            Player player = (Player) sender;

            if (args.length != 0) {
                try {
                    Classes selectedClass = Classes.valueOf(args[0].toUpperCase(Locale.ROOT));
                    Classes.setSelected(player, selectedClass);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + args[0] + " was not found, valid classes: " + Arrays.toString(Classes.values()));
                    return true;
                }
            }

            Classes selected = Classes.getSelected(player);
            player.sendMessage(ChatColor.BLUE + "Your selected class: §7" + selected);
        } else if (command.getName().equalsIgnoreCase("menu")) {
            if (!(sender instanceof Player)) {
                return true;
            }

            Player player = (Player) sender;
            openMainMenu(player);
        } else if (command.getName().equalsIgnoreCase("shout") && args.length > 0) {
            if (!(sender instanceof Player)) {
                return true;
            }
            if (Warlords.game.getState() == GAME && Warlords.hasPlayer((Player) sender)) {
                String message;
                if (Warlords.game.isBlueTeam((Player) sender)) {
                    message = ChatColor.BLUE + "[SHOUT] ";
                } else {
                    message = ChatColor.RED + "[SHOUT] ";
                }
                message += ChatColor.AQUA + sender.getName() + ChatColor.WHITE + ": ";
                for (String arg : args) {
                    message += arg + " ";
                }

                for (Player player : Warlords.getPlayers().keySet()) {
                    player.sendMessage(message);
                }
            }
        } else if (command.getName().equals("hotkeymode")) {
            if (Warlords.game.getState() == GAME) {
                WarlordsPlayer warlordsPlayer = Warlords.getPlayer((Player) sender);
                if (warlordsPlayer.isHotKeyMode()) {
                    sender.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.AQUA + "Classic " + ChatColor.GREEN + "enabled.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.YELLOW + "NEW " + ChatColor.GREEN + "enabled.");
                }
                warlordsPlayer.setHotKeyMode(!warlordsPlayer.isHotKeyMode());
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getName().equalsIgnoreCase("start")) {
            return Arrays
                    .stream(GameMap.values())
                    .map(Enum::name)
                    .filter(e -> e.startsWith(args[args.length - 1].toUpperCase(Locale.ROOT)))
                    .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());
        }

        if (command.getName().equalsIgnoreCase("class")) {
            return Arrays
                    .stream(Classes.values())
                    .map(Enum::name)
                    .filter(e -> e.startsWith(args[args.length - 1].toUpperCase(Locale.ROOT)))
                    .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}