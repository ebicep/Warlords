package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.events.player.WeaponSalvageEvent;
import com.ebicep.warlords.pve.bountysystem.events.BountyStartEvent;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Tracks events that happen outside of the game.
 * <p>
 * List of events that can be overriden by implementing this, there is a single static listener class that calls these methods, for all player bounties
 */
public interface TracksOutsideGame {

    static Listener getListener() {
        return new Listener() {

            private final Map<UUID, List<TracksOutsideGame>> CACHED_ONLINE_PLAYER_TRACKERS = new HashMap<>();

            @EventHandler
            public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
                refreshTracker(event.getDatabasePlayer());
            }

            private void refreshTracker(DatabasePlayer databasePlayer) {
                CACHED_ONLINE_PLAYER_TRACKERS.put(
                        databasePlayer.getUuid(),
                        databasePlayer.getPveStats()
                                      .getTrackableBounties()
                                      .stream()
                                      .filter(TracksOutsideGame.class::isInstance)
                                      .map(TracksOutsideGame.class::cast)
                                      .collect(Collectors.toList())
                );
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                CACHED_ONLINE_PLAYER_TRACKERS.remove(event.getPlayer().getUniqueId());
            }

            @EventHandler
            public void onBountyStart(BountyStartEvent event) {
                refreshTracker(event.getDatabasePlayer());
            }

            @EventHandler
            public void onBountyClaim(BountyStartEvent event) {
                refreshTracker(event.getDatabasePlayer());
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onWeaponSalvagePost(WeaponSalvageEvent.Post event) {
                quicklyValidate();
                runTracker(event.getUUID(), tracker -> tracker.onWeaponSalvage(event.getWeapon()));
            }

            private void quicklyValidate() {
                Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                int onlinePlayerCount = onlinePlayers.size();
                int cachedOnlinePlayerCount = CACHED_ONLINE_PLAYER_TRACKERS.size();
                if (onlinePlayerCount != cachedOnlinePlayerCount) {
                    ChatUtils.MessageType.BOUNTIES.sendErrorMessage("Online player count mismatch: " + onlinePlayerCount + " != " + cachedOnlinePlayerCount);
                    fixOnlineDatabasePlayers();
                    return;
                }
                for (Player onlinePlayer : onlinePlayers) {
                    UUID uniqueId = onlinePlayer.getUniqueId();
                    if (!CACHED_ONLINE_PLAYER_TRACKERS.containsKey(uniqueId)) {
                        ChatUtils.MessageType.BOUNTIES.sendErrorMessage("Online player mismatch: " + uniqueId);
                        fixOnlineDatabasePlayers();
                        return;
                    }
                }
            }

            private void fixOnlineDatabasePlayers() {
                ChatUtils.MessageType.BOUNTIES.sendMessage("Fixing online database players: " + CACHED_ONLINE_PLAYER_TRACKERS);
                CACHED_ONLINE_PLAYER_TRACKERS.clear();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ChatUtils.MessageType.BOUNTIES.sendMessage(" - " + onlinePlayer.getUniqueId());
                    DatabaseManager.getPlayer(onlinePlayer, this::refreshTracker);
                }
                ChatUtils.MessageType.BOUNTIES.sendMessage("Fixed online database players");
            }

            private void runTracker(UUID uuid, Consumer<TracksOutsideGame> runTracker) {
                List<TracksOutsideGame> tracksOutsideGames = CACHED_ONLINE_PLAYER_TRACKERS.get(uuid);
                if (tracksOutsideGames == null) {
                    ChatUtils.MessageType.BOUNTIES.sendErrorMessage("No tracks outside game for " + uuid);
                    return;
                }
                for (TracksOutsideGame tracksOutsideGame : tracksOutsideGames) {
                    runTracker.accept(tracksOutsideGame);
                }
            }

        };
    }

    default void onWeaponSalvage(AbstractWeapon weapon) {
    }

}
