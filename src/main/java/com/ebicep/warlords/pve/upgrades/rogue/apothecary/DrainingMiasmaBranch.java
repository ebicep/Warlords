package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.DrainingMiasma;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class DrainingMiasmaBranch extends AbstractUpgradeBranch<DrainingMiasma> {

    int duration = ability.getDuration();
    int hitRadius = ability.getEnemyHitRadius();

    public DrainingMiasmaBranch(AbilityTree abilityTree, DrainingMiasma ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setDuration(duration + 1);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setDuration(duration + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setDuration(duration + 3);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+4s Duration\n+4s Leech duration",
                20000,
                () -> {
                    ability.setDuration(duration + 4);
                    ability.setLeechDuration(ability.getLeechDuration() + 4);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+2 Blocks hit radius",
                5000,
                () -> {
                    ability.setEnemyHitRadius(hitRadius + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+4 Blocks hit radius",
                10000,
                () -> {
                    ability.setEnemyHitRadius(hitRadius + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+6 Blocks hit radius",
                15000,
                () -> {
                    ability.setEnemyHitRadius(hitRadius + 6);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+8 Blocks hit radius",
                20000,
                () -> {
                    ability.setEnemyHitRadius(hitRadius + 8);
                }
        ));

        masterUpgrade = new Upgrade(
                "Liquidizing Miasma",
                "Draining Miasma - Master Upgrade",
                "For each enemy within Draining Miasma,\ngain a 4% damage boost for Impaling Strike for\nthe duration of Draining Miasma",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
