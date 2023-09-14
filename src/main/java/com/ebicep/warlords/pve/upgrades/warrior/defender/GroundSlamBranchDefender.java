package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.GroundSlamDefender;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.warrior.AbstractGroundSlamBranch;

public class GroundSlamBranchDefender extends AbstractGroundSlamBranch<GroundSlamDefender> {

    public GroundSlamBranchDefender(AbilityTree abilityTree, GroundSlamDefender ability) {
        super(abilityTree, ability);

        masterUpgrade2 = new Upgrade(
                "Reverberation",
                "Ground Slam - Master Upgrade",
                """
                        Casting Ground Slam will leap you in the air for a short duration. Upon landing, activate a second Ground Slam that will increase your damage reduction based on the number of enemies hit.
                        Max being 25% at 5 enemies hit for a duration of 5s.
                        """,
                50000,
                () -> {
                }
        );
    }

}
