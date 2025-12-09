package com.ebicep.warlords.pve.upgrades.warrior.revenant;

import com.ebicep.warlords.abilities.OrbsOfLife;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

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
                "Orbs of Eruption",
                "Orbs of Life - Master Upgrade",
                """
                       -50% Healing

                       Picking up an orb of life will detonate it. Dealing 315-423 damage to nearby enemies.
                       """,
                50000,
                () -> {
                    ability.getHealValues().getOrbHealing().value().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Orbs of Eruption", -.5f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Orbs of Time",
                "Orbs of Life - Master Upgrade",
                """
                        +30% Healing
                        
                        Double orbs healing increase over time, and orbs last twice as long. Orbs of Life can now Overheal. Upon reactivation, orbs will now retain its healing increase.
                        """,
                50000,
                () -> {
                    ability.getHealValues().getOrbHealing().value().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Orbs of Time", .3f);
                    ability.setOrbTickDuration(ability.getOrbTickDuration() * 2);
                    ability.setHealingIncrease(ability.getHealingIncrease() * 2);
                }
        );
    }
}
