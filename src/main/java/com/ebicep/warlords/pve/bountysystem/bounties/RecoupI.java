package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.player.SupplyDropCallEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.DailyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class RecoupI extends AbstractBounty implements TracksOutsideGame, DailyCost, DailyRewardSpendable4 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSupplyDropCall(SupplyDropCallEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        value += event.getAmount();
    }

    @Override
    public String getName() {
        return "Recoup";
    }

    @Override
    public String getDescription() {
        return "Call " + getTarget() + " Supply Drops.";
    }

    @Override
    public int getTarget() {
        return 10;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.RECOUP_I;
    }


}
