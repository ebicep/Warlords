package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.Inferno;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class InfernoBranch extends AbstractUpgradeBranch<Inferno> {

    int maxHits = ability.getMaxHits();

    public InfernoBranch(AbilityTree abilityTree, Inferno ability) {
        super(abilityTree, ability);

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "Inferno cooldown gets reduced by 0.25 seconds\nfor each critical hit (max 10 hits)",
                5000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "Inferno cooldown gets reduced by 0.25 seconds\nfor each critical hit (max 20 hits)",
                10000,
                () -> {
                    ability.setMaxHits(maxHits + 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "Inferno cooldown gets reduced by 0.25 seconds\nfor each critical hit (max 30 hits)",
                15000,
                () -> {
                    ability.setMaxHits(maxHits + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "Inferno cooldown gets reduced by 0.25 seconds\nfor each critical hit (max 40 hits)",
                20000,
                () -> {
                    ability.setMaxHits(maxHits + 30);
                }
        ));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER",
                50000
        );
    }

}
