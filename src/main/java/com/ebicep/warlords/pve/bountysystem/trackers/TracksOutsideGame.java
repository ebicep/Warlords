package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.events.player.SupplyDropCallEvent;
import com.ebicep.warlords.events.player.WeaponSalvageEvent;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.events.BountyClaimEvent;
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

    Map<UUID, Set<TracksOutsideGame>> CACHED_ONLINE_PLAYER_TRACKERS = new HashMap<>();

    static Listener getListener() {
        return new Listener() {

            // private final Map<UUID, Set<TracksOutsideGame>> CACHED_ONLINE_PLAYER_TRACKERS = new HashMap<>();

            @EventHandler
            public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
                refreshTracker(event.getPlayer().getUniqueId());
            }

            private void refreshTracker(UUID uuid) {
                CACHED_ONLINE_PLAYER_TRACKERS.remove(uuid);
                for (PlayersCollections collection : BountyUtils.MAX_BOUNTIES.keySet()) {
                    DatabaseManager.getPlayer(uuid, collection, this::refreshTracker);
                }
            }

            private void refreshTracker(DatabasePlayer databasePlayer) {
                CACHED_ONLINE_PLAYER_TRACKERS.merge(
                        databasePlayer.getUuid(),
                        databasePlayer.getPveStats()
                                      .getTrackableBounties()
                                      .stream()
                                      .filter(TracksOutsideGame.class::isInstance)
                                      .map(TracksOutsideGame.class::cast)
                                      .collect(Collectors.toSet()),
                        (oldValue, newValue) -> {
                            oldValue.addAll(newValue);
                            return oldValue;
                        }
                );
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                CACHED_ONLINE_PLAYER_TRACKERS.remove(event.getPlayer().getUniqueId());
            }

            @EventHandler
            public void onBountyStart(BountyStartEvent event) {
                refreshTracker(event.getDatabasePlayer().getUuid());
            }

            @EventHandler
            public void onBountyClaim(BountyClaimEvent event) {
                refreshTracker(event.getDatabasePlayer().getUuid());
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

            private void runTracker(UUID uuid, Consumer<TracksOutsideGame> runTracker) {
                Set<TracksOutsideGame> tracksOutsideGames = CACHED_ONLINE_PLAYER_TRACKERS.get(uuid);
                if (tracksOutsideGames == null) {
                    ChatUtils.MessageType.BOUNTIES.sendErrorMessage("No tracks outside game for " + uuid);
                    return;
                }
                for (TracksOutsideGame tracksOutsideGame : tracksOutsideGames) {
                    runTracker.accept(tracksOutsideGame);
                }
            }

            private void fixOnlineDatabasePlayers() {
                ChatUtils.MessageType.BOUNTIES.sendMessage("Fixing online database players: " + CACHED_ONLINE_PLAYER_TRACKERS);
                CACHED_ONLINE_PLAYER_TRACKERS.clear();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ChatUtils.MessageType.BOUNTIES.sendMessage(" - " + onlinePlayer.getUniqueId());
                    refreshTracker(onlinePlayer.getUniqueId());
                }
                ChatUtils.MessageType.BOUNTIES.sendMessage("Fixed online database players");
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onSupplyDropCall(SupplyDropCallEvent event) {
                quicklyValidate();
                runTracker(event.getUUID(), tracker -> tracker.onSupplyDropCall(event.getAmount()));
            }

        };
    }

    default void onWeaponSalvage(AbstractWeapon weapon) {
    }

    default void onSupplyDropCall(long amount) {

    }

}
