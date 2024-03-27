package com.ebicep.warlords.pve.upgrades.warrior.defender;

import com.ebicep.warlords.abilities.Intervene;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class InterveneBranch extends AbstractUpgradeBranch<Intervene> {

    float maxDamagePrevented = ability.getMaxDamagePrevented();

    @Override
    public void runOnce() {
        ability.setMaxTargets(2);
    }

    public InterveneBranch(AbilityTree abilityTree, Intervene ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Intersection",
                "Intervene - Master Upgrade",
                """
                        +1 Chain Target
                                                
                        Remove the damage, cast and break range limit on Intervene, and reduce damage taken by 35%.""",
                50000,
                () -> {
                    ability.setDamageReduction(ability.getDamageReduction() - 35);
                    ability.setMaxDamagePrevented(10000000);
                    ability.setRadius(300);
                    ability.setBreakRadius(300);
                    ability.setMaxTargets(ability.getMaxTargets() + 1);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Interference",
                "Intervene - Master Upgrade",
                """
                        +1 Chain Target
                                                
                        Remove the damage, cast and break range limit on Intervene. Additionally, Intervene gives a 25% speed increase as well as knockback immunity.
                        """,
                50000,
                () -> {
                    ability.setMaxDamagePrevented(10000000);
                    ability.setRadius(300);
                    ability.setBreakRadius(300);
                    ability.setMaxTargets(ability.getMaxTargets() + 1);
                }
        );
    }
}
