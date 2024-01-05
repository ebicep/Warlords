package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabasePlayerPvEEventLibraryArchivesDifficultyStats;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodex;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodexEarnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class CodexCollectorI extends AbstractBounty implements TracksOutsideGame, EventCost, LibraryArchives1 {

    @Override
    protected void register() {
        super.register();
        updateBountiesCollected();
    }

    private void updateBountiesCollected() {
        if (!DatabaseGameEvent.eventIsActive()) {
            return;
        }
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats()).get(currentGameEvent.getStartDateSecond());
        if (!(eventMode instanceof DatabasePlayerPvEEventLibraryArchivesDifficultyStats stats)) {
            return;
        }
        value = stats.getCodexesEarned().keySet().size();
    }

    @Override
    public String getName() {
        return "Codex Collector";
    }

    @Override
    public String getDescription() {
        return "Collect all of the Codexes.";
    }

    @Override
    public int getTarget() {
        return PlayerCodex.values().length;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CODEX_COLLECTOR_I;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCodexCollected(PlayerCodexEarnEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        updateBountiesCollected();
    }

}
