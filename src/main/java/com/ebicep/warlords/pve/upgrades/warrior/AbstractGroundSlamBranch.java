package com.ebicep.warlords.pve.upgrades.warrior;

import com.ebicep.warlords.abilities.internal.AbstractGroundSlam;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class AbstractGroundSlamBranch<T extends AbstractGroundSlam> extends AbstractUpgradeBranch<T> {


    public AbstractGroundSlamBranch(AbilityTree abilityTree, T ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getSlamDamage(), 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeHitBox(ability, 1f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Earthen Tremor",
                "Ground Slam - Master Upgrade",
                "Casting Ground Slam will leap you in the air for a short duration. Upon landing, activate a second Ground Slam for 150% of the original damage.",
                50000,
                () -> {

                }
        );
    }
}
