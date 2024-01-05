package com.ebicep.warlords.pve.bountysystem.trackers;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.events.BountyCancelEvent;
import com.ebicep.warlords.pve.bountysystem.events.BountyClaimEvent;
import com.ebicep.warlords.pve.bountysystem.events.BountyStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

/**
 * Tracks events that happen outside of the game.
 * <p>
 * List of events that can be overriden by implementing this, there is a single static listener class that calls these methods, for all player bounties
 */
public interface TracksOutsideGame {

    static Listener getListener() {
        return new Listener() {

            @EventHandler
            public void onDatabasePlayerFirstLoad(DatabasePlayerFirstLoadEvent event) {
                refreshTracker(event.getDatabasePlayer(), true);
            }

            private void refreshTracker(DatabasePlayer databasePlayer, boolean register) {
                refreshTracker(databasePlayer, databasePlayer.getPveStats().getTrackableBounties(), register);
                if (!DatabaseGameEvent.eventIsActive()) {
                    return;
                }
                DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
                EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction
                        .apply(databasePlayer.getPveStats().getEventStats())
                        .get(currentGameEvent.getStartDateSecond());
                if (eventMode == null) {
                    return;
                }
                refreshTracker(databasePlayer, eventMode.getTrackableBounties(), register);
            }

            private void refreshTracker(DatabasePlayer databasePlayer, List<AbstractBounty> trackableBounties, boolean register) {
                trackableBounties.stream().filter(TracksOutsideGame.class::isInstance).forEach(bounty -> {
                    if (register) {
                        bounty.init(databasePlayer);
                    } else {
                        bounty.unregister();
                    }
                });
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                DatabaseManager.getPlayer(event.getPlayer(), databasePlayer -> refreshTracker(databasePlayer, false));
            }

            @EventHandler
            public void onBountyStart(BountyStartEvent event) {
                event.getBounty().init(event.getDatabasePlayer());
            }

            @EventHandler
            public void onBountyCancel(BountyCancelEvent event) {
                event.getBounty().unregister();
            }

            @EventHandler
            public void onBountyClaim(BountyClaimEvent event) {
                event.getBounty().unregister();
            }

        };
    }

}
