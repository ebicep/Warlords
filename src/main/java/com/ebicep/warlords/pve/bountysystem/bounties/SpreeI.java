package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;

public class SpreeI extends AbstractBounty implements EventCost, GardenOfHesperides1 {

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

}
