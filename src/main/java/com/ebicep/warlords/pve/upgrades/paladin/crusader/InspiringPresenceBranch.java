package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilities.InspiringPresence;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class InspiringPresenceBranch extends AbstractUpgradeBranch<InspiringPresence> {

    int duration = ability.getTickDuration();
    int energyPerSecond = ability.getEnergyPerSecond();
    double radius = ability.getRadius();

    public InspiringPresenceBranch(AbilityTree abilityTree, InspiringPresence ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Energize - Tier I",
                "+2 Energy per second",
                5000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 2);
                }
        ));
        treeA.add(new Upgrade(
                "Energize - Tier II",
                "+4 Energy per second",
                10000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 4);
                }
        ));
        treeA.add(new Upgrade(
                "Energize - Tier III",
                "+6 Energy per second",
                15000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Energize - Tier IV",
                "+8 Energy per second",
                20000,
                () -> {
                    ability.setEnergyPerSecond(energyPerSecond + 8);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+1s Duration\n+1.5 Blocks hit radius",
                5000,
                () -> {
                    ability.setTickDuration(duration + 20);
                    ability.setRadius(radius + 1.5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+2s Duration\n+3 Blocks hit radius",
                10000,
                () -> {
                    ability.setTickDuration(duration + 40);
                    ability.setRadius(radius + 3);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+3s Duration\n+4.5 Blocks hit radius",
                15000,
                () -> {
                    ability.setTickDuration(duration + 60);
                    ability.setRadius(radius + 4.5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+4s Duration\n+6 Blocks hit radius",
                20000,
                () -> {
                    ability.setTickDuration(duration + 80);
                    ability.setRadius(radius + 6);
                }
        ));

        masterUpgrade = new Upgrade(
                "Transcendent Presence",
                "Inspiring Presence - Master Upgrade",
                """
                        -20% Cooldown reduction

                        Reduce the cooldown on all caster's and nearby allies' abilities by 10 seconds (excluding Inspiring Presence.) Additionally, allies gain 10% cooldown reduction for the duration of Inspiring Presence""",
                50000,
                () -> {
                    ability.setCooldown(ability.getCooldown() * 0.8f);
                    ability.setPveMasterUpgrade(true);
                }
        );
    }
}
