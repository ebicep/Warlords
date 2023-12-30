package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.SpecPrestigeEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class ReputationI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable3 {


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpecPrestige(SpecPrestigeEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        value++;
    }

    @Override
    public String getName() {
        return "Reputation";
    }

    @Override
    public String getDescription() {
        return "Prestige a spec.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.REPUTATION_I;
    }

}
