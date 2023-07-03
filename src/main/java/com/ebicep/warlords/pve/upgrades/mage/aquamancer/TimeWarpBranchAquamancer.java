package com.ebicep.warlords.pve.upgrades.mage.aquamancer;

import com.ebicep.warlords.abilities.TimeWarpAquamancer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class TimeWarpBranchAquamancer extends AbstractUpgradeBranch<TimeWarpAquamancer> {

    float cooldown = ability.getCooldown();
    int healing = ability.getWarpHealPercentage();

    public TimeWarpBranchAquamancer(AbilityTree abilityTree, TimeWarpAquamancer ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+5% Healing",
                5000,
                () -> {
                    ability.setWarpHealPercentage(healing + 5);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+10% Healing",
                10000,
                () -> {
                    ability.setWarpHealPercentage(healing + 10);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+15% Healing",
                15000,
                () -> {
                    ability.setWarpHealPercentage(healing + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+20% Healing",
                20000,
                () -> {
                    ability.setWarpHealPercentage(healing + 20);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Monsoon Leap",
                "Time Warp - Master Upgrade",
                "Time Warp can now be re-activated to teleport you back early. Additionally, double the duration of Time Warp and now leave a water altar " +
                        "that grants allies immunity to debuffs and slowness effects while near it. " +
                        "The altar is removed upon warping back.",
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 2);

                }
        );
    }
}
