package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilties.IceBarrier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class IceBarrierBranch extends AbstractUpgradeBranch<IceBarrier> {

    int duration = ability.getDuration();
    int damageReductionPercent = ability.getDamageReductionPercent();

    public IceBarrierBranch(AbilityTree abilityTree, IceBarrier ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "",
                5000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "",
                10000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "",
                15000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "",
                20000,
                () -> {

                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "",
                5000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "",
                10000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "",
                15000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "",
                20000,
                () -> {

                }
        ));

        masterUpgrade = new Upgrade(
                "",
                "",
                "",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
