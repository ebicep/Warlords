package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.events.ItemCraftEvent;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.events.NewItemCraftEvent;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class MasonryI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemCraft(NewItemCraftEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        if (event.getItem().getTier() == NewItemTier.LEGENDARY) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Masonry";
    }

    @Override
    public String getDescription() {
        return "Craft " + getTarget() + " Legendary items.";
    }

    @Override
    public int getTarget() {
        return 2;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.MASONRY_I;
    }


}
