package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.events.EventShopPurchaseEvent;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Objects;

public class SpreeI extends AbstractBounty implements TracksOutsideGame, EventCost, GardenOfHesperides1 {

    @Override
    public String getName() {
        return "Spree";
    }

    @Override
    public String getDescription() {
        return "Spend 500,000 event coins in the event shop.";
    }

    @Override
    public int getTarget() {
        return 500_000;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SPREE_I;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEventShopPurchase(EventShopPurchaseEvent event) {
        if (!Objects.equals(event.getUUID(), uuid)) {
            return;
        }
        value += event.getBought().price();
    }

}
