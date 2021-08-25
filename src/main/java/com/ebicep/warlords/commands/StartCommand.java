package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.player.ArmorManager;
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

public class StartCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

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
            sender.sendMessage("§cDEV: §aChanging map to " + map.getMapName());
            //Bukkit.broadcastMessage(ChatColor.GRAY + "§lThe map has been changed to §6§l" + map.getMapName() + " §7§lby §c§l" + sender.getName());
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
            DatabaseManager.addPlayer(player);
            player.getInventory().clear();

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
            ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass(), teamBlueAssessment ? Team.BLUE : Team.RED);
            Warlords.getPlayerSettings(player.getUniqueId()).setWantedTeam(teamBlueAssessment ? Team.BLUE : Team.RED);
            teamBlueAssessment = !teamBlueAssessment;
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
