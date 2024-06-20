package com.ebicep.warlords.pve.upgrades.warrior;

import com.ebicep.warlords.abilities.internal.AbstractSeismicWave;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public abstract class AbstractSeismicWaveBranch extends AbstractUpgradeBranch<AbstractSeismicWave> {


    public AbstractSeismicWaveBranch(AbilityTree abilityTree, AbstractSeismicWave ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getWaveDamage(), 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability, 0.0375f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Seismic Smash",
                "Seismic Wave - Master Upgrade",
                "Increase the size of Seismic Wave by 150% and deal increased damage the further away the enemy is. (Max 1.5x at 15 blocks).",
                50000,
                () -> {

                    ability.setWaveLength((int) (ability.getWaveLength() * 2.5f));
                    ability.setWaveWidth((int) (ability.getWaveWidth() * 2.5f));
                }
        );
    }
}
