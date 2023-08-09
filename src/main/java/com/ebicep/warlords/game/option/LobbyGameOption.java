package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class LobbyGameOption implements Option {

    public static void start() {
        new BukkitRunnable() {

            Game game;

            @Override
            public void run() {
                if (Bukkit.getWorld("MainLobby") == null) {
                    ChatUtils.MessageType.WARLORDS.sendErrorMessage("MainLobby world is null");
                    cancel();
                    return;
                }
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
                }
            }
        }.runTaskLater(Warlords.getInstance(), 20);
    }

    @Override
    public void register(@Nonnull Game game) {
        new GameRunnable(game) {
            final World mainLobby = Bukkit.getWorld("MainLobby");

            @Override
            public void run() {
                assert mainLobby != null;
                mainLobby.getPlayers().forEach(player -> {
                    UUID uniqueId = player.getUniqueId();
                    LocationBuilder locationToCheck = new LocationBuilder(player.getLocation()).y(49);
                    boolean inPlayingArea = player.getWorld().getBlockAt(locationToCheck).getType() == Material.BEDROCK && player.getLocation().getY() < 60;
                    Map<UUID, Team> players = game.getPlayers();
                    if (players.containsKey(uniqueId) && !inPlayingArea) {
                        removePlayerFromGame(player, uniqueId, game);
                    } else if (!players.containsKey(uniqueId) && inPlayingArea) {
                        addPlayerToGame(player, uniqueId, game);
                    }
                });
            }
        }.runTaskTimer(0, 10);
        game.registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onPlayerQuit(PlayerQuitEvent e) {
                WarlordsEntity wp = Warlords.getPlayer(e.getPlayer());
                if (wp != null) {
                    removePlayerFromGame(e.getPlayer(), e.getPlayer().getUniqueId(), game);
                }
            }
        });
    }

    private static void addPlayerToGame(Player player, UUID uniqueId, @Nonnull Game game) {
        PlayerSettings.getPlayerSettings(uniqueId).setWantedTeam(Team.BLUE);
        Warlords.SPAWN_POINTS.put(uniqueId, player.getLocation());
        Warlords.addPlayer(new WarlordsPlayer(
                player,
                game,
                Team.BLUE
        ));
        game.addPlayer(player, false);
        game.setPlayerTeam(uniqueId, Team.BLUE);
        Utils.resetPlayerMovementStatistics(player);
    }

    private static void removePlayerFromGame(Player player, UUID uniqueId, @Nonnull Game game) {
        Warlords.SPAWN_POINTS.put(uniqueId, player.getLocation());
        game.removePlayer(uniqueId);
        Warlords.SPAWN_POINTS.remove(uniqueId);
    }
}
