package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.LifetimeCost;
import com.ebicep.warlords.pve.bountysystem.rewards.LifetimeRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

public class ArtisanI extends AbstractBounty implements TracksOutsideGame, LifetimeCost, LifetimeRewardSpendable1 {

    @Override
    public void onLegendaryWeaponCraft(AbstractLegendaryWeapon weapon) {
        value++;
    }

    @Override
    public String getName() {
        return "Artisan";
    }

    @Override
    public String getDescription() {
        return "Craft " + getTarget() + " Legendary Weapons.";
    }

    @Override
    public int getTarget() {
        return 3;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.ARTISAN_I;
    }


}
