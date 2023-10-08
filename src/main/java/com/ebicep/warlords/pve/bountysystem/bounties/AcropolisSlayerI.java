package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;

public class AcropolisSlayerI extends AbstractBounty implements EventCost, GardenOfHesperides1 {

    @Override
    public String getName() {
        return "Acropolis Slayer";
    }

    @Override
    public String getDescription() {
        return "Defeat 3 of the Lesser Gods in the Acropolis.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ACROPOLIS_SLAYER_I;
    }

}
