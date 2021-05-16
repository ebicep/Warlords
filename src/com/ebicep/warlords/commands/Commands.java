package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Game.State;
import com.ebicep.warlords.maps.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.ebicep.warlords.maps.Game.State.GAME;
import static com.ebicep.warlords.maps.Game.State.PRE_GAME;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (command.getName().equalsIgnoreCase("start")) {
            Game game = Warlords.game; // In the future allow the user to select a game player)
            GameMap map;
            if (args.length == 0) {
                map = null;
            } else if(args[0].equalsIgnoreCase("random")) {
                Random random = new Random();
                GameMap[] values = GameMap.values();
                map = values[random.nextInt(values.length)];
            } else {
                try {
                    map = GameMap.valueOf(args[0].toUpperCase(Locale.ROOT));
                } catch(IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + args[0] + " was not found, valid maps: " + Arrays.toString(GameMap.values()));
                    return true;
                }
            }

            if(game.getState() != State.PRE_GAME) {
                sender.sendMessage(ChatColor.RED + "The game has already started!");
                return true;
            }

            for (Player player : game.getTeamBlue()) {
                if (player != null) {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
            }
            for (Player player : game.getTeamRed()) {
                if (player != null) {
                    player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                }
            }
            game.getTeamBlue().clear();
            game.getTeamRed().clear();
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
                Warlords.game.addPlayer(player, teamBlueAssessment);
                teamBlueAssessment = !teamBlueAssessment;
            }

        } else if (command.getName().equalsIgnoreCase("endgame")) {
            if (Warlords.game.getState() != GAME) {
                sender.sendMessage(ChatColor.RED + "There are no games currently running!");
                return true;
            }

            Warlords.game.forceDraw();
            sender.sendMessage(ChatColor.RED + "Game has been terminated. Warping back to lobby...");
        }

        return true;
    }

}