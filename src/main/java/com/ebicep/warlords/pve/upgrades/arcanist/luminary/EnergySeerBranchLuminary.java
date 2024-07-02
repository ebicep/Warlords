package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.EnergySeerLuminary;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class EnergySeerBranchLuminary extends AbstractUpgradeBranch<EnergySeerLuminary> {

    public EnergySeerBranchLuminary(AbilityTree abilityTree, EnergySeerLuminary ability) {
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
                        Add an additional 20% healing bonus and double energy restored.
                        """,
                50000,
                () -> {
                    ability.setHealingIncrease(ability.getHealingIncrease() + 20);
                    ability.setEnergyRestore(ability.getEnergyRestore() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Benevolent Gaze",
                "Energy Seer - Master Upgrade",
                """
                        +20% Additional Cooldown Reduction
                        -5 Post effect EPS decrease
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Benevolent Gaze", 0.8f);
                    ability.setEpsDecrease(ability.getEpsDecrease() - 5);
                }
        );
    }

}
