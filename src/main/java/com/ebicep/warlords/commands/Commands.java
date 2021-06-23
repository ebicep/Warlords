package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.abilties.FallenSouls;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.Settings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
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

            if (!(game.getState() instanceof PreLobbyState)) {
                sender.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }

            game.clearAllPlayers();
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
                Warlords.databaseManager.addPlayer(player);
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
                Game.State.updateTempPlayer(player);
                teamBlueAssessment = !teamBlueAssessment;
            }

        } else if (command.getName().equalsIgnoreCase("endgame")) {
            if (!sender.isOp()) {
                sender.sendMessage("§cYou do not have permission to do that.");
                return true;
            }
            Game game = Warlords.game; // In the future allow the user to select a game player
            if (game.getState() instanceof PreLobbyState) {
                sender.sendMessage(ChatColor.RED + "There are no games currently running!");
                return true;
            }

            if (game.getState() instanceof PlayingState) {
                PlayingState playingState = (PlayingState) game.getState();
                playingState.endGame();
            }

            sender.sendMessage(ChatColor.RED + "Game has been terminated. Warping back to lobby...");

        } else if (command.getName().equalsIgnoreCase("class")) {
            Player player = requirePlayerOutsideGame(sender);
            if (player != null) {
                PlayerSettings settings = Warlords.getPlayerSettings(player.getUniqueId());
                if (args.length != 0) {
                    try {
                        Classes selectedClass = Classes.valueOf(args[0].toUpperCase(Locale.ROOT));
                        settings.selectedClass(selectedClass);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.RED + args[0] + " was not found, valid classes: " + Arrays.toString(Classes.values()));
                        return true;
                    }
                }

                Classes selected = Classes.getSelected(player);
                player.sendMessage(ChatColor.BLUE + "Your selected class: §7" + selected);
            }
        } else if (command.getName().equalsIgnoreCase("menu")) {
            Player player = requirePlayerOutsideGame(sender);
            if (player != null) {
                openMainMenu(player);
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("shout") && args.length > 0) {
            WarlordsPlayer player = requireWarlordsPlayer(sender);
            if (player != null) { // We only have a warlords player if the game is running
                String message = player.getTeam().teamColor() + "[SHOUT] ";
                message += ChatColor.AQUA + sender.getName() + ChatColor.WHITE + ": ";
                for (String arg : args) {
                    message += arg + " "; // TODO use a stringbuilder
                }

                for (WarlordsPlayer p : PlayerFilter.playingGame(player.getGame()).aliveTeammatesOf(player)) {
                    p.sendMessage(message);
                }
            }
            return true;
        } else if (command.getName().equals("hotkeymode")) {
            Player player = requirePlayer(sender);
            if (player != null) {
                if (Settings.HotkeyMode.getSelected(player) == Settings.HotkeyMode.NEW_MODE) {
                    sender.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.AQUA + "Classic " + ChatColor.GREEN + "enabled.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.YELLOW + "NEW " + ChatColor.GREEN + "enabled.");
                }
            }
        } else if (command.getName().equals("hitbox")) {
            if (args.length != 0) {
                FallenSouls.setFallenSoulHitBox(Float.parseFloat(args[0]));
            }
            sender.sendMessage("hitbox is " + FallenSouls.getFallenSoulHitBox());
        } else if (command.getName().equals("speed")) {
            if (args.length != 0) {
                FallenSouls.setFallenSoulSpeed(Float.parseFloat(args[0]));
            }
            sender.sendMessage("speed is " + FallenSouls.getFallenSoulSpeed());
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

    @Nullable
    private Player requirePlayerOutsideGame(@Nonnull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command requires a player!");
            return null;
        }
        if (Warlords.hasPlayer((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot use this command inside a game!");
            return null;
        }
        return (Player) sender;
    }

    @Nullable
    private Player requirePlayer(@Nonnull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command requires a player!");
            return null;
        }
        return (Player) sender;
    }

    @Nullable
    private WarlordsPlayer requireWarlordsPlayer(@Nonnull CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command requires a player!");
            return null;
        }
        WarlordsPlayer player = Warlords.getPlayer((Player) sender);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "You are not in an active game!");
        }
        return player;
    }
}