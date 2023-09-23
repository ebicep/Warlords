package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.OrbsOfLife;
import com.ebicep.warlords.pve.upgrades.*;

public class OrbsOfLifeBranch extends AbstractUpgradeBranch<OrbsOfLife> {
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    public OrbsOfLifeBranch(AbilityTree abilityTree, OrbsOfLife ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 12.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
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
