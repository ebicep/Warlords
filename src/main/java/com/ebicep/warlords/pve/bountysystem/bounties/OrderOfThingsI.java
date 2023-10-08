package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;

public class OrderOfThingsI extends AbstractBounty implements EventCost, GardenOfHesperides2 {

    @Override
    public String getName() {
        return "Order of Things";
    }

    @Override
    public String getDescription() {
        return "Complete Tartarus in the effective order.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ORDER_OF_THINGS_I;
    }

}
