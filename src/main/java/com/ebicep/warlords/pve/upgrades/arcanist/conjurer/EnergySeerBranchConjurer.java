package com.ebicep.warlords.pve.upgrades.arcanist.conjurer;

import com.ebicep.warlords.abilities.EnergySeerConjurer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class EnergySeerBranchConjurer extends AbstractUpgradeBranch<EnergySeerConjurer> {

    public EnergySeerBranchConjurer(AbilityTree abilityTree, EnergySeerConjurer ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Energizing Oracle",
                "Energy Seer - Master Upgrade",
                """
                        Add an additional 15% damage bonus and double energy restored.
                        """,
                50000,
                () -> {
                    ability.setDamageIncrease(ability.getDamageIncrease() + 15);
                    ability.setEnergyRestore(ability.getEnergyRestore() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Replicating Sight",
                "Energy Seer - Master Upgrade",
                """
                        When nearby allies within a 10 block radius expend energy while Energy Seer is active, gain 10% of their energy spent when Energy Seer ends. Increase the duration of Energy Seer by 5s.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 100);
                }
        );
    }

}
