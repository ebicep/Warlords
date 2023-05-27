package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilties.Repentance;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RepentanceBranch extends AbstractUpgradeBranch<Repentance> {

    int duration = ability.getTickDuration();
    int healthRestore = ability.getHealthRestore();
    int energyRestore = ability.getEnergyRestore();

    public RepentanceBranch(AbilityTree abilityTree, Repentance ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+25 Healing",
                5000,
                () -> {
                    ability.setHealthRestore(healthRestore + 25);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+50 Healing",
                10000,
                () -> {
                    ability.setHealthRestore(healthRestore + 50);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+75 Healing",
                15000,
                () -> {
                    ability.setHealthRestore(healthRestore + 75);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+100 Healing",
                20000,
                () -> {
                    ability.setHealthRestore(healthRestore + 100);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1s Duration\n+1 Energy given",
                5000,
                () -> {
                    ability.setTickDuration(duration + 20);
                    ability.setEnergyRestore(energyRestore + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration\n+2 Energy given",
                10000,
                () -> {
                    ability.setTickDuration(duration + 40);
                    ability.setEnergyRestore(energyRestore + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration\n+3 Energy given",
                15000,
                () -> {
                    ability.setTickDuration(duration + 60);
                    ability.setEnergyRestore(energyRestore + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration\n+4 Energy given",
                20000,
                () -> {
                    ability.setTickDuration(duration + 80);
                    ability.setEnergyRestore(energyRestore + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Revengeance",
                "Repentance - Master Upgrade",
                "Increase the max procs possible of Repentance by 5.",
                50000,
                () -> {
                    ability.setMaxProcs(ability.getMaxProcs() + 54);
                }
        );
    }
}
