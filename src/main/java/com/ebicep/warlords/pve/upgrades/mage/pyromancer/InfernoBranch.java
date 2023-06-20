package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.Inferno;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class InfernoBranch extends AbstractUpgradeBranch<Inferno> {

    int critMultiplierIncrease = ability.getCritMultiplierIncrease();
    int duration = ability.getTickDuration();

    public InfernoBranch(AbilityTree abilityTree, Inferno ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+15% Crit multiplier bonus",
                5000,
                () -> {
                    ability.setCritMultiplierIncrease(critMultiplierIncrease + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+30% Crit multiplier bonus",
                10000,
                () -> {
                    ability.setCritMultiplierIncrease(critMultiplierIncrease + 30);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+45% Crit multiplier bonus",
                15000,
                () -> {
                    ability.setCritMultiplierIncrease(critMultiplierIncrease + 45);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+60% Crit multiplier bonus",
                20000,
                () -> {
                    ability.setCritMultiplierIncrease(critMultiplierIncrease + 60);
                }
        ));

        treeB.add(new Upgrade(
                "Chronos - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setTickDuration(duration + 20);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setTickDuration(duration + 40);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setTickDuration(duration + 60);
                }
        ));
        treeB.add(new Upgrade(
                "Chronos - Tier IV",
                "+4s Duration",
                20000,
                () -> {
                    ability.setTickDuration(duration + 80);
                }
        ));

        masterUpgrade = new Upgrade(
                "Dante’s Inferno",
                "Inferno - Master Upgrade",
                "Inferno's cooldown gets reduced by 0.5 seconds and duration gets increased by 0.25 seconds for each critical hit (max 40 hits)",
                50000,
                () -> {
                    ability.setPveMasterUpgrade(true);
                }
        );
    }

}
