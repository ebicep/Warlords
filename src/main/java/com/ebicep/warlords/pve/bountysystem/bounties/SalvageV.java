package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.costs.WeeklyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable5;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksOutsideGame;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;

public class SalvageV extends AbstractBounty implements TracksOutsideGame, WeeklyCost, WeeklyRewardSpendable5 {

    @Override
    public void onWeaponSalvage(AbstractWeapon weapon) {
        if (weapon.getRarity() == WeaponsPvE.RARE) {
            value++;
        }
    }

    @Override
    public String getName() {
        return "Salvage";
    }

    @Override
    public String getDescription() {
        return "Salvage " + getTarget() + " Rare weapons.";
    }

    @Override
    public int getTarget() {
        return 25;
    }

    @Override
    public Bounty getBounty() {
        return Bounty.SALVAGE_V;
    }


}
