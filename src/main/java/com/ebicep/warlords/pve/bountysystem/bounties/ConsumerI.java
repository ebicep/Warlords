package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

public class ConsumerI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {

    @Override
    public void onWeaponTitlePurchase(AbstractLegendaryWeapon weapon) {
        value++;
    }

    @Override
    public String getName() {
        return "Consumer";
    }

    @Override
    public String getDescription() {
        return "Purchase " + getTarget() + " Weapon Titles";
    }

    @Override
    public int getTarget() {
        return 6;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.CONSUMER_I;
    }


}
