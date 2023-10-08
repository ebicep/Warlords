package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;

public class WithinTheTimeI extends AbstractBounty implements EventCost, GardenOfHesperides2 {

    @Override
    public String getName() {
        return "Within the Time";
    }

    @Override
    public String getDescription() {
        return "Complete Tartarus within 10 minutes.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.WITHIN_THE_TIME_I;
    }

}
