package com.ebicep.warlords.pve.upgrades.arcanist.guardian;

import com.ebicep.warlords.abilties.SpiritualShield;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SpiritualShieldBranch extends AbstractUpgradeBranch<SpiritualShield> {

    public SpiritualShieldBranch(AbilityTree abilityTree, SpiritualShield ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+15% Healing",
                5000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+30% Healing",
                10000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+45% Healing",
                15000,
                () -> {

                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+60% Healing",
                20000,
                () -> {

                }
        ));

        treeB.add(new Upgrade(
                "Scope - Tier I",
                "+1 Block rain radius",
                5000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier II",
                "+2 Blocks rain radius",
                10000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier III",
                "+3 Blocks rain radius",
                15000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier IV",
                "+4 Blocks rain radius\n+4s Duration",
                20000,
                () -> {

                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                """
                        """,
                50000,
                () -> {

                }
        );
    }

}
