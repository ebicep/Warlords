
package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.VitalityConcoction;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class VitalityConcoctionBranch extends AbstractUpgradeBranch<VitalityConcoction> {

    @Override
    public void runOnce() {
        ability.multiplyTickDuration(1.5f);
    }

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
                        Vitality Concoction now heals 1045 - 1425 to all allies in a 5 block radius.
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Concoction Party",
                "Vitality Concoction - Master Upgrade",
                """
                        During the duration of Vitality concoction reduce the energy cost of Impaling strike by 75%.
                        """,
                50000,
                () -> {

                }
        );
    }
}
