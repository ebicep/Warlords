package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.warrior.AbstractSeismicWaveBranch;

public class SeismicWaveBranchDefender extends AbstractSeismicWaveBranch {

    public SeismicWaveBranchDefender(AbilityTree abilityTree, AbstractSeismicWave ability) {
        super(abilityTree, ability);

        masterUpgrade2 = new Upgrade(
                "Wild Wave",
                "Seismic Wave - Master Upgrade",
                """
                        Enemies that are WOUNDED will take 30% more damage and enemies killed by Seismic Wave will reduce the cooldown of Last Stand by 1s.
                        """,
                50000,
                () -> {

                }
        );
    }

}
