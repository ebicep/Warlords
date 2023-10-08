package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;

public class StateOfMindI extends AbstractBounty implements EventCost, GardenOfHesperides1 {

    @Override
    public String getName() {
        return "State of Mind";
    }

    @Override
    public String getDescription() {
        return "Defeat Cronus while he is in his ground slam state.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.STATE_OF_MIND_I;
    }

}
