package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.mobs.events.libraryarchives.EventTheArchivist;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.springframework.data.annotation.Transient;

public class AcropolisFlawlessI extends AbstractBounty implements TracksDuringGame, EventCost, LibraryArchives1 {

    @Transient
    private int newKills = 0;

    @Override
    public String getName() {
        return "Acropolis Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Acropolis without dying.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ACROPOLIS_FLAWLESS_I;
    }

    @Override
    public void reset() {
        newKills = 0;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKill(WarlordsDeathEvent event) {
        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventTheArchivist) {
            newKills++;
        }
    }

    @Override
    public long getNewValue() {
        return newKills;
    }

}
