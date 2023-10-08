package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.EventCost;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;

public class TartarusSlayerI extends AbstractBounty implements EventCost, GardenOfHesperides2 {

    @Override
    public String getName() {
        return "Tartarus Slayer";
    }

    @Override
    public String getDescription() {
        return "Defeat the Gods of Tartarus 10 times.";
    }

    @Override
    public int getTarget() {
        return 10;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.TARTARUS_SLAYER_I;
    }

}
