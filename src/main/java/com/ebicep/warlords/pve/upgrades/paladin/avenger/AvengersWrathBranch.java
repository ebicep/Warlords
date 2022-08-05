package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilties.AvengersWrath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class AvengersWrathBranch extends AbstractUpgradeBranch<AvengersWrath> {

    float energyPerSecond = ability.getEnergyPerSecond();
    float cooldown = ability.getCooldown();
    int duration = ability.getDuration();

    public AvengersWrathBranch(AbilityTree abilityTree, AvengersWrath ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Energize - Tier I",
                "+2.5 Energy per second",
                5000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 2.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Energize - Tier II",
                "+5 Energy per second",
                10000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 5);
                }
        ));
        treeA.add(new Upgrade(
                "Energize - Tier III",
                "+7.5 Energy per second",
                15000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 7.5f);
                }
        ));
        treeA.add(new Upgrade(
                "Energize - Tier IV",
                "+10 Energy per second",
                20000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 10);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1s Duration",
                5000,
                () -> {
                    ability.setDuration(duration + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration",
                10000,
                () -> {
                    ability.setDuration(duration + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration",
                15000,
                () -> {
                    ability.setDuration(duration + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration",
                20000,
                () -> {
                    ability.setDuration(duration + 4);
                }
        ));

        masterUpgrade = new Upgrade(
                "Avenger’s Armageddon",
                "Avenger's Wrath - Master Upgrade",
                "Avenger's Wrath hits 3 additional targets.",
                50000,
                () -> {
                    ability.setMaxTargets(ability.getMaxTargets() + 3);
                }
        );
    }
}
