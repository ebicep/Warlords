package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.OrbsOfLife;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class OrbsOfLifeBranch extends AbstractUpgradeBranch<OrbsOfLife> {

    public OrbsOfLifeBranch(AbilityTree abilityTree, OrbsOfLife ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHealing(ability.getHealValues().getOrbHealing(), 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Orbs of Relics",
                "Orbs of Life - Master Upgrade",
                """
                        Double orbs healing increase over time, and orbs last twice as long.
                        
                        For each active orb, increase your damage by 1% (max 30%).""",
                50000,
                () -> {
                    ability.setOrbTickDuration(ability.getOrbTickDuration() * 2);
                    ability.setHealingIncrease(ability.getHealingIncrease() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Orbs of Time",
                "Orbs of Life - Master Upgrade",
                """
                        Double orbs healing increase over time, and orbs last twice as long.
                        
                        +30% Healing
                        Orbs of Life can now Overheal.
                        Upon reactivation, orbs will now retain its healing increase.
                        """,
                50000,
                () -> {
                    ability.getHealValues().getOrbHealing().value().addMultiplicativeModifierAdd("Orbs of Time", .3f);
                    ability.setOrbTickDuration(ability.getOrbTickDuration() * 2);
                    ability.setHealingIncrease(ability.getHealingIncrease() * 2);
                }
        );
    }
}
