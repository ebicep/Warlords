package com.ebicep.warlords.pve.upgrades.mage;

import com.ebicep.warlords.abilties.TimeWarp;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class TimeWarpBranch extends AbstractUpgradeBranch<TimeWarp> {

    float cooldown = ability.getCooldown();
    int healing = ability.getWarpHealPercentage();

    public TimeWarpBranch(AbilityTree abilityTree, TimeWarp ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+3% Healing",
                5000,
                () -> {
                    ability.setWarpHealPercentage(healing + 3);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+6% Healing",
                10000,
                () -> {
                    ability.setWarpHealPercentage(healing + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+9% Healing",
                15000,
                () -> {
                    ability.setWarpHealPercentage(healing + 9);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+12% Healing",
                20000,
                () -> {
                    ability.setWarpHealPercentage(healing + 12);
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
                "Dimensional Leap",
                "Time Warp - Master Upgrade",
                "Time Warp can now be re-activated to\nteleport you back early. Additionally, double the\nduration of Time Warp.",
                50000,
                () -> {
                    ability.setDuration(ability.getDuration() * 2);
                    ability.setPveUpgrade(true);
                }
        );
    }
}
