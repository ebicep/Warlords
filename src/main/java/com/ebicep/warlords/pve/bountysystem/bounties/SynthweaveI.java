package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.events.StarPieceSynthesizedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class SynthweaveI extends AbstractBounty implements TracksOutsideGame, DailyCost, DailyRewardSpendable4 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStarPieceSynthesized(StarPieceSynthesizedEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        if (event.getStarPiece() == StarPieces.COMMON) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Synthweave";
    }

    @Override
    public String getDescription() {
        return "Synthesize " + getTarget() + " Common Star Pieces.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SYNTHWEAVE_I;
    }


}
