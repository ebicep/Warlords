package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.GroundSlamRevenant;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.warrior.AbstractGroundSlamBranch;

public class GroundSlamBranchRevenant extends AbstractGroundSlamBranch<GroundSlamRevenant> {

    public GroundSlamBranchRevenant(AbilityTree abilityTree, GroundSlamRevenant ability) {
        super(abilityTree, ability);

        masterUpgrade2 = new Upgrade(
                "Reverberation",
                "Ground Slam - Master Upgrade",
                """
                        Casting Ground Slam will leap you in the air for a short duration. Upon Landing, active a second Ground Slam that will increase your healing output based on number of enemies hit.
                        Max being 25% at 5 enemies hit for a duration of 5s.
                        """,
                50000,
                () -> {
                }
        );
    }

}
