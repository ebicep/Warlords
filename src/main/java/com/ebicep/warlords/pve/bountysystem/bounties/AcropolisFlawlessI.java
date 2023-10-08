package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;

public class AcropolisFlawlessI extends AbstractBounty implements EventCost, GardenOfHesperides1 {

    @Override
    public String getName() {
        return "Acropolis Flawless";
    }

    @Override
    public String getDescription() {
        return "Complete Acropolis without dying.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ACROPOLIS_FLAWLESS_I;
    }

}
