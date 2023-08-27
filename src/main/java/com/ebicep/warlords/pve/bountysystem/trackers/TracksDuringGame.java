package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUsedEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface TracksDuringGame {

    static Listener getListener(Map<UUID, Set<TracksDuringGame>> trackers) {
        trackers.values().stream().flatMap(Set::stream).forEach(TracksDuringGame::reset);
        return new Listener() {

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                trackers.forEach((uuid, tracksDuringGame) -> tracksDuringGame.forEach(track -> track.onFinalDamageHeal(uuid, event)));
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onAbilityUsed(WarlordsAbilityActivateEvent event) {
                trackers.forEach((uuid, tracksDuringGame) -> tracksDuringGame.forEach(track -> track.onAbilityUsed(uuid, event)));
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            public void onEnergyUsed(WarlordsEnergyUsedEvent event) {
                trackers.forEach((uuid, tracksDuringGame) -> tracksDuringGame.forEach(track -> track.onEnergyUsed(uuid, event)));
            }
        };
    }

    /**
     * Resets the local cached tracker (the one used in apply())
     */
    void reset();

    default void onFinalDamageHeal(UUID uuid, WarlordsDamageHealingFinalEvent event) {

    }

    default void onAbilityUsed(UUID uuid, WarlordsAbilityActivateEvent event) {

    }

    default void onEnergyUsed(UUID uuid, WarlordsEnergyUsedEvent event) {

    }

    default void onPlayerJump() {

    }

    /**
     * @param game The game that is being tracked
     * @return Whether this tracker should track given game, some trackers are gamemode specific etc.
     */
    default boolean trackGame(Game game) {
        return true;
    }

    /**
     * Called when the game ends, this is where you should apply the changes to the bounty
     * <p>
     * Not auto added to the counter in the case of the game not counting
     */
    default void apply(AbstractBounty bounty) {
        bounty.setValue(bounty.getValue() + getNewValue());
    }

    int getNewValue();

}
