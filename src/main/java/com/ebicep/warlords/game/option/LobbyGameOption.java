package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class LobbyGameOption implements Option {

    public static void start() {
        new BukkitRunnable() {
            long ticksElapsed = 0;
            Game game;

            @Override
            public void run() {
                if (ticksElapsed == 0) {
                    GameManager.QueueEntryBuilder entryBuilder = Warlords.getGameManager().newEntry(Collections.emptyList());
                    entryBuilder.setRequestedGameAddons(GameAddon.CUSTOM_GAME);
                    entryBuilder.setGameMode(GameMode.LOBBY);
                    entryBuilder.setMap(GameMap.MAIN_LOBBY);
                    Pair<GameManager.QueueResult, Game> resultGamePair = entryBuilder.queueNow();
                    if (resultGamePair.getB() != null) {
                        game = resultGamePair.getB();
                        ChatUtils.MessageType.WARLORDS.sendMessage("Lobby game started");
                    } else {
                        ChatUtils.MessageType.WARLORDS.sendMessage("Lobby game failed to start");
                        cancel();
                        return;
                    }
                }
                Bukkit.getOnlinePlayers()
                      .stream()
                      .filter(player -> player.getWorld().getName().equals("MainLobby"))
                      .forEach(player -> {
                          LocationBuilder locationToCheck = new LocationBuilder(player.getLocation()).y(49);
                          boolean inPlayerZone = player.getWorld().getBlockAt(locationToCheck).getType() == Material.BEDROCK && player.getLocation().getY() < 60;
                          Map<UUID, Team> players = game.getPlayers();
                          UUID uniqueId = player.getUniqueId();
                          if (players.containsKey(uniqueId) && !inPlayerZone) {
                              Warlords.SPAWN_POINTS.put(uniqueId, player.getLocation());
                              game.removePlayer(uniqueId);
                              Warlords.SPAWN_POINTS.remove(uniqueId);
                          } else if (!players.containsKey(uniqueId) && inPlayerZone) {
                              PlayerSettings.getPlayerSettings(uniqueId).setWantedTeam(Team.BLUE);
                              Warlords.SPAWN_POINTS.put(uniqueId, player.getLocation());
                              Warlords.addPlayer(new WarlordsPlayer(
                                      player,
                                      game,
                                      Team.BLUE
                              ));
                              game.addPlayer(player, false);
                          }
                      });
                ticksElapsed++;
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 10);
    }


}
