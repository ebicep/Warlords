package com.ebicep.warlords.pve.upgrades.warrior.berserker;

import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.warrior.AbstractSeismicWaveBranch;

public class SeismicWaveBranchBerserker extends AbstractSeismicWaveBranch {

    public SeismicWaveBranchBerserker(AbilityTree abilityTree, AbstractSeismicWave ability) {
        super(abilityTree, ability);

        masterUpgrade2 = new Upgrade(
                "Wild Wave",
                "Seismic Wave - Master Upgrade",
                """
                        Increase the size of Seismic Wave by 150% amd knockback is reduced by 50%.
                        
                        After seismic wave ends the area affected becomes unstable causing enemies to take 723-906 damage per second within the area for 4s.
                        """,
                50000,
                () -> {
                    ability.setWaveLength((int) (ability.getWaveLength() * 2.5f));
                    ability.setWaveWidth((int) (ability.getWaveWidth() * 2.5f));
                    ability.setVelocity(ability.getVelocity() * 0.6f);
                }
        );
    }

}
