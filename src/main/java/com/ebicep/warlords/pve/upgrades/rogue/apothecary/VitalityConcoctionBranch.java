
package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.VitalityConcoction;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class VitalityConcoctionBranch extends AbstractUpgradeBranch<VitalityConcoction> {


    public VitalityConcoctionBranch(AbilityTree abilityTree, VitalityConcoction ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDuration(ability, 2f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Concoction Zone",
                "Vitality Concoction - Master Upgrade",
                """
                        Vitality Concoction now deals 1245 - 1625 damage to all enemies you pass through.
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Concoction Zone",
                "Vitality Concoction - Master Upgrade",
                """
                        Vitality Concoction now splashes on the ground, affecting all nearby allies.
                        """,
                50000,
                () -> {

                }
        );
    }
}
