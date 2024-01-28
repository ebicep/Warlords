package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable5;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.events.ItemScrapEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class ScrapI extends AbstractBounty implements TracksOutsideGame, WeeklyCost, WeeklyRewardSpendable5 {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemScrap(ItemScrapEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        if (event.getItem().getTier() == ItemTier.ALPHA) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Scrap";
    }

    @Override
    public String getDescription() {
        return "Scrap " + getTarget() + " Alpha Items.";
    }

    @Override
    public int getTarget() {
        return 10;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SCRAP_I;
    }


}
