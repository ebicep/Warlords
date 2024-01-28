package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.events.StarPieceSynthesizedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class SynthesizerI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onStarPieceSynthesized(StarPieceSynthesizedEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        if (event.getStarPiece() == StarPieces.LEGENDARY) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Synthesizer";
    }

    @Override
    public String getDescription() {
        return "Synthesize " + getTarget() + " Legendary Star Pieces.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SYNTHESIZER_I;
    }


}
