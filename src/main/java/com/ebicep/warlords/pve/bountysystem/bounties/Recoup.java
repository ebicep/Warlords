package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;

public class Recoup extends AbstractBounty implements TracksOutsideGame, DailyRewardSpendable4 {

    @Override
    public void onSupplyDropCall(long amount) {
        value += amount;
    }

    @Override
    public int getTarget() {
        return 10;
    }

    @Override
    public String getName() {
        return "Recoup";
    }

    @Override
    public String getDescription() {
        return "Call " + getTarget() + "Supply Drops.";
    }

    @Override
    public Bounty getBounty() {
        return Bounty.RECOUP;
    }


}
