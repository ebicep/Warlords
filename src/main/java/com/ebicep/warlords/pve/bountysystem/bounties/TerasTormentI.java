package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;

public class TerasTormentI extends AbstractBounty implements EventCost, GardenOfHesperides1 {

    @Override
    public String getName() {
        return "Teras Torment";
    }

    @Override
    public String getDescription() {
        return "Defeat 100 Teras Mobs in the Acropolis.";
    }

    @Override
    public int getTarget() {
        return 100;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.TERAS_TORMENT_I;
    }

}
