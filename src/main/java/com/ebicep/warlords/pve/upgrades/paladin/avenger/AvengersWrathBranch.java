package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilities.AvengersWrath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class AvengersWrathBranch extends AbstractUpgradeBranch<AvengersWrath> {

    float energyPerSecond = ability.getEnergyPerSecond();
    float cooldown = ability.getCooldown();
    int duration = ability.getTickDuration();

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
                "+2s Duration",
                5000,
                () -> {
                    ability.setTickDuration(duration + 40);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+4s Duration",
                10000,
                () -> {
                    ability.setTickDuration(duration + 80);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+6s Duration",
                15000,
                () -> {
                    ability.setTickDuration(duration + 120);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+8s Duration",
                20000,
                () -> {
                    ability.setTickDuration(duration + 160);
                }
        ));

        masterUpgrade = new Upgrade(
                "Avenger’s Armageddon",
                "Avenger's Wrath - Master Upgrade",
                "-10% Cooldown reduction\n\nAvenger's Wrath hits 3 additional targets and double the hit radius.",
                50000,
                () -> {
                    ability.setHitRadius(ability.getHitRadius() * 2);
                    ability.setMaxTargets(ability.getMaxTargets() + 3);
                    ability.setCooldown(ability.getCooldown() * 0.9f);
                }
        );
    }
}
