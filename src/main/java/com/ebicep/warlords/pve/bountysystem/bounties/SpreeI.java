package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.SpendableBuyShop;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;

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

    @Override
    public void onEventShopPurchase(SpendableBuyShop shop) {
        value += shop.price();
    }
}
