package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TracksDuringGame {

    static Listener getListener(Map<UUID, List<TracksDuringGame>> trackers) {
        trackers.values().stream().flatMap(List::stream).forEach(TracksDuringGame::reset);
        return new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                trackers.forEach((uuid, tracksDuringGame) -> tracksDuringGame.forEach(track -> track.onFinalDamageHeal(uuid, event)));
            }

            @EventHandler(priority = EventPriority.MONITOR)
            public void onAbilityUsed(WarlordsAbilityActivateEvent event) {
                trackers.forEach((uuid, tracksDuringGame) -> tracksDuringGame.forEach(track -> track.onAbilityUsed(uuid, event)));
            }

        };
    }

    /**
     * @param game The game that is being tracked
     * @return Whether this tracker should track given game, some trackers are gamemode specific etc.
     */
    default boolean trackGame(Game game) {
        return true;
    }

    default void onFinalDamageHeal(UUID uuid, WarlordsDamageHealingFinalEvent event) {

    }

    default void onAbilityUsed(UUID uuid, WarlordsAbilityActivateEvent event) {

    }

    void apply();

    void reset();

}
