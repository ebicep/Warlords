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
                .addUpgradeHealing(ability.getHealValues().getOrbHealing(), 12.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Orbs of Relics",
                "Orbs of Life - Master Upgrade",
                "Spawn 1 additional orb on active, double orbs healing increase over time, and orbs last twice as long.",
                50000,
                () -> {
                    ability.setOrbTickMultiplier(2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Orbs of Time",
                "Orbs of Life - Master Upgrade",
                """
                        Orbs of Life can now Overheal, double orbs healing increase over time, and orbs last twice as long.
                        """,
                50000,
                () -> {
                    ability.setOrbTickMultiplier(2);
                }
        );
    }
}
