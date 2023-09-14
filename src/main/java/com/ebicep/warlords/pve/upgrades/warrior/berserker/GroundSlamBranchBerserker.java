package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.GroundSlamBerserker;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.warrior.AbstractGroundSlamBranch;

public class GroundSlamBranchBerserker extends AbstractGroundSlamBranch<GroundSlamBerserker> {

    public GroundSlamBranchBerserker(AbilityTree abilityTree, GroundSlamBerserker ability) {
        super(abilityTree, ability);

        masterUpgrade2 = new Upgrade(
                "Reverberation",
                "Ground Slam - Master Upgrade",
                """
                        Casting Ground Slam will leap you in the air for a short duration. Upon landing, activate a second Ground Slam that will increase your damage by 5% per enemy hit for 5 seconds, max 25%.
                        """,
                50000,
                () -> {
                }
        );
    }

}
