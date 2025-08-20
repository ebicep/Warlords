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
                        Increase the size of Seismic Wave by 150%. Additionally, enemies hit by Seismic Wave will reduce the cooldown of Last Stand by 0.25s (max 2s).
                        """,
                50000,
                () -> {
                    ability.setWaveLength((int) (ability.getWaveLength() * 2.5f));
                    ability.setWaveWidth((int) (ability.getWaveWidth() * 2.5f));
                }
        );
    }

}
