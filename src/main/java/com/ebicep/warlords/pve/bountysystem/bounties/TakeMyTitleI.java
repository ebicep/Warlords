package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;

public class TakeMyTitleI extends AbstractBounty implements EventCost, GardenOfHesperides2 {

    @Override
    public String getName() {
        return "Take My Title";
    }

    @Override
    public String getDescription() {
        return "Complete Tartarus with a Legendary weapon equipped with a Garden of Hesperides title.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.TAKE_MY_TITLE_I;
    }

}
