package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;

public class ReputationI extends AbstractBounty implements TracksOutsideGame, LifetimeRewardSpendable3 {

    @Override
    public void onSpecPrestige() {
        value++;
    }

    @Override
    public String getName() {
        return "Reputation";
    }

    @Override
    public String getDescription() {
        return "Prestige a spec.";
    }

    @Override
    public int getTarget() {
        return 1;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.REPUTATION_I;
    }

}
