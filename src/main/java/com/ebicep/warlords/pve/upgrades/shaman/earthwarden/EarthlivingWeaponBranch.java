package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.Earthliving;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EarthlivingWeaponBranch extends AbstractUpgradeBranch<Earthliving> {
    public EarthlivingWeaponBranch(AbilityTree abilityTree, Earthliving ability) {
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
                50000,
                () -> {

                }
        );
    }

    int weaponDamage = ability.getWeaponDamage();
    int procChance = ability.getProcChance();
}
