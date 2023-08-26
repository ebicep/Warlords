package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public interface TracksDuringGame {

    static void applyToGame(Game game, List<TracksDuringGame> trackers) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                trackers.forEach(tracker -> tracker.onFinalDamageHeal(event));
            }

        });
    }

    void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event);

    void reset();

}
