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
                        Remove energy loss.
                        +5s duration.
                        +20% damage bonus.
                        +20 Energy.
                        """,
                50000,
                () -> {
                    ability.setEpsDecrease(0);
                    ability.setTickDuration(ability.getTickDuration() + 100);
                    ability.setDamageIncrease(ability.getDamageIncrease() + 20);
                    ability.setEnergyRestore(ability.getEnergyRestore() + 20);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Replicating Sight",
                "Energy Seer - Master Upgrade",
                """
                        Remove energy loss.
                        +5s duration.
                        When nearby allies within a 10 block radius expend energy while Energy Seer is active, gain 7.5% of their energy spent.
                        """,
                50000,
                () -> {
                    ability.setEpsDecrease(0);
                    ability.setTickDuration(ability.getTickDuration() + 100);
                }
        );
    }

}
