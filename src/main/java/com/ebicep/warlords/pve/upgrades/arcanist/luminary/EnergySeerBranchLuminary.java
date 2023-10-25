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
                .addUpgradeDuration(ability::setBonusDuration, ability::getBonusDuration, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Energizing Oracle",
                "Energy Seer - Master Upgrade",
                """
                        When your Energy Seer ends, add an additional 20% healing bonus and triple energy restored.
                        """,
                50000,
                () -> {
                    ability.setHealingIncrease(ability.getHealingIncrease() + 20);
                    ability.setEnergyRestore(ability.getEnergyRestore() * 3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Benevolent Gaze",
                "Energy Seer - Master Upgrade",
                """
                        +20% Additional Cooldown Reduction
                                                
                        When Energy Seer expires, all allies within a 10 block radius gain 1 stack of Merciful Hex.
                        """,
                50000,
                () -> {
                    ability.getCooldown().addMultiplicativeModifierMult("Benevolent Gaze", 0.8f);
                }
        );
    }

}
